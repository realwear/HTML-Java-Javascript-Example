package com.realwear.htmljavajavascript;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.webkit.WebView;

public class WearWebView extends WebView {

    private boolean mCloseKeyboard = false;

    public WearWebView(Context context) {
        super(context);
    }

    public WearWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WearWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WearWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = EditorInfo.IME_ACTION_NEXT;
        InputConnection b = super.onCreateInputConnection(outAttrs);

        if(b != null)
            return new WearInputConnection(b, true); //this is needed for #dispatchKeyEvent() to be notified.

        return b;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        boolean isOnKeyPre = super.onKeyPreIme(keyCode, event);
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            mCloseKeyboard = true;
        }
        return isOnKeyPre;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public class WearInputConnection extends InputConnectionWrapper {
        /**
         * Initializes a wrapper.
         *
         * <p><b>Caveat:</b> Although the system can accept {@code (InputConnection) null} in some
         * places, you cannot emulate such a behavior by non-null {@link InputConnectionWrapper} that
         * has {@code null} in {@code target}.</p>
         *
         * @param target  the {@link InputConnection} to be proxied.
         * @param mutable set {@code true} to protect this object from being reconfigured to target
         *                another {@link InputConnection}.  Note that this is ignored while the target is {@code null}.
         */
        public WearInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean finishComposingText() {
            if(mCloseKeyboard){
                mCloseKeyboard = false;
                handleKeyboardEvent();
            }
            return super.finishComposingText();
        }

        @Override
        public boolean performEditorAction(int editorAction) {
            handleKeyboardEvent();
            return super.performEditorAction(editorAction);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            int keyCode = event.getKeyCode();

            if(keyCode == KeyEvent.KEYCODE_ENTER){
                handleKeyboardEvent();
            }
            return super.sendKeyEvent(event);
        }

        /**
         * This function runs the keyboard access on the ui thread
         */
        private void handleKeyboardEvent(){
            Handler mainHandler = new Handler(Looper.getMainLooper());

            Runnable myRunnable = WearWebView.this::clearFocus;
            mainHandler.post(myRunnable);
        }
    }
}