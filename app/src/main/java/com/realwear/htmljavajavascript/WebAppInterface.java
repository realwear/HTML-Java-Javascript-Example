package com.realwear.htmljavajavascript;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebAppInterface {
    Context mContext;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /** Sends Broadcast to speech service to force update */
    @JavascriptInterface
    public void updateVoiceCommands() {
        Toast.makeText(mContext, "This is an example on how to call java functions from javascript", Toast.LENGTH_LONG).show();
        /**
         * Once here Im sure you can do all kinds of things from updating the ui for fresh voice commands to
         * a host of other things. With the global context from above you have access to the native android side.
         * Get creative!
         */

        /**
         * Below is the actual intent to refresh voice commands on the screen!
         * you don't need to use a button, you can set the title of any element and then
         * you can always access the ability to refresh
         */
        //mContext.sendBroadcast(new Intent("com.realwear.wearhf.intent.action.REFRESH_UI"));
    }


    /**
     * Overloaded function which takes an array of strings to use as the new set of voice commands.
     * Uses the context to access main activity and call the method to add/change the voice commands
     * @param commands
     */
    @JavascriptInterface
    public void updateVoiceCommands(String[] commands) {
        MainActivity activity = (MainActivity) mContext;
        activity.buildCommands(commands);
    }
}