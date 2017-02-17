package com.jogoler.jogolmapsunsri.other;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by RazorX on 12/16/2016.
 */

public class NotificationHandler {
    private Toast mToast;

    public void singleToast(Context context, String msg, int duration){

        if(mToast != null)
        {
            mToast.cancel();
            mToast = Toast.makeText(context, msg, duration);
            mToast.show();
        }
    }

    public void singleSnackBar(){

    }
}
