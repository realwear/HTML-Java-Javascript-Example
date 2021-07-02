package com.realwear.htmljavajavascript

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings.LOAD_NO_CACHE
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout


class MainActivity : AppCompatActivity(){
    companion object {
        private lateinit var webView: WearWebView
        private val TAG = "webview"
    }

    // The action that WearHF will use for broadcasting when a voice command is spoken.
    private val ACTION_SPEECH_EVENT = "com.realwear.wearhf.intent.action.SPEECH_EVENT"

    // Identifier for the voice command that is/was spoken.
    private val EXTRA_RESULT = "command"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.hide()
        webView = findViewById(R.id.wearwebview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.settings.cacheMode = LOAD_NO_CACHE

        //This WebViewClient is restricted to the local content provided within the asset folder
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return run {
                    if (url.startsWith("file:///")) {
                        view.loadUrl(url)
                    }
                    true
                }
            }
        }

        //Necessary for alert function in JavaScript
        webView.webChromeClient = object : WebChromeClient() {
            // Need to accept permissions to use the camera
            override fun onPermissionRequest(request: PermissionRequest) {
                //request.grant(request.resources)
                runOnUiThread {
                    if (request.origin.toString() == "file:///") {
                        Log.d(TAG, "GRANTED")
                        request.grant(request.resources)
                    } else {
                        Log.d(TAG, "DENIED")
                        request.deny()
                    }
                }
            }
        }

        /**
         * Here is an important piece, the name you give the interface is the same name you will need to use
         * in the javascript side to call the java functions on the java side
         */
        webView.addJavascriptInterface(WebAppInterface(this), "WearHFNative")

        val myWebUrl = "file:///android_asset/sample/index.html"
        webView.loadUrl(myWebUrl)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        AlertDialog.Builder(this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Exit!")
                                .setMessage("Are you sure you want to close?")
                                .setPositiveButton("Yes") { _, _: Int -> finish() }
                                .setNegativeButton("No", null)
                                .show()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * Attaching receiver to the activity
     */
    override fun onResume() {
        super.onResume()
        registerReceiver(asrBroadcastReceiver, IntentFilter(ACTION_SPEECH_EVENT))
    }

    /**
     * Removing receiver when activity is not in foreground
     */
    override fun onPause() {
        super.onPause()
        if (asrBroadcastReceiver != null) {
            unregisterReceiver(asrBroadcastReceiver)
        }
    }

    /**
     * This broadcast receiver is the object that captures the spoken command given by the user
     */
    private var asrBroadcastReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == ACTION_SPEECH_EVENT) {
                val asrCommand : String? = intent.getStringExtra(EXTRA_RESULT)

                //Sending the command received from speech engine back to JavaScript
                webView.evaluateJavascript("onReceiveCommand('$asrCommand')", null)
            }
        }
    }

    /**
     * Here the new command string is being built with the commands sent from javascript array
     */
    fun buildCommands(commands: Array<String>){
        var newCommandString: String = "hf_scroll_none|hf_add_commands:"

        if(commands.isNotEmpty()){
            for(i in commands.indices){
                if(i == 0)
                    newCommandString += commands[i]
                else
                    newCommandString += "|" + commands[i]
            }
            clearCommands()
            setCommands(newCommandString)
        }
    }

    /**
     * Only the original thread that created a view hierarchy can touch its views.
     * This forces the ui thread to run the desired code snippet to clear contentDescription
     *
     * This function sets the new command string to the root view of the the application
     */
    private fun setCommands(newCommands: String){
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable { findViewById<ConstraintLayout>(R.id.root).contentDescription = newCommands }

        mainHandler.post(myRunnable)
    }

    /**
     * Only the original thread that created a view hierarchy can touch its views.
     * This forces the ui thread to run the desired code snippet to clear contentDescription
     *
     * This function clears the current commands that are set on the root view
     */
    private fun clearCommands(){
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable { findViewById<ConstraintLayout>(R.id.root).contentDescription = null }

        mainHandler.post(myRunnable)
    }
}


