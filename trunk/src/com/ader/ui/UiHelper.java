package com.ader.ui;

import android.app.AlertDialog;
import android.content.Context;

public class UiHelper {
    public static void alert(Context context, int msgId) {
        new AlertDialog.Builder(context).setMessage(msgId).show();
    }
}
