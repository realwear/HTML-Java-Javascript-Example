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