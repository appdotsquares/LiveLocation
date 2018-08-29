package ds.com.livelocation.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import ds.com.livelocation.R;


/**
 * Created by shachindrap on 8/1/2016.
 */
public class Util {

    static Dialog mProgressDialog;

    private static boolean isLoadingVisible;

    public static Boolean isShowing(Context context) {
        return mProgressDialog.isShowing();
    }

    public static void showProgress(Context mContext) {

       /* if (isLoadingVisible) {
            hideProgress();
        }*/
        if (!isLoadingVisible) {
            isLoadingVisible = true;
            if (mContext == null) {
                return;
            }

            mProgressDialog = new Dialog(mContext);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setContentView(R.layout.progress_bar_dialog);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

    }

    public static void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            isLoadingVisible = false;
        }
    }


}


