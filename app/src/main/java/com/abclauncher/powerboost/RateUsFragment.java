package com.abclauncher.powerboost;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;


/**
 * Created by hyy on 2016/6/28.
 */
public class RateUsFragment extends DialogFragment implements View.OnClickListener{
    private final static String TAG = RateUsFragment.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.rate_us_dialog_layout, null);
        initView(view);
        builder.setView(view);
        AlertDialog dialog = builder.show();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.DialogWindowAnim);
        return dialog;
    }

    private void initView(View view) {
        view.findViewById(R.id.rate_like_it).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAbcCoolerPage();
                getDialog().dismiss();
            }
        });
        view.findViewById(R.id.rate_dislike).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch (Exception e) {
                    Log.d(getClass().getSimpleName(), "onPreferenceClick: start activity failed");
                }
                getDialog().dismiss();
            }
        });
        view.findViewById(R.id.close_dialog).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        Log.d(TAG, "onClick: " + v.getId());
        switch (v.getId()) {
            case R.id.close_dialog:
                getDialog().dismiss();
                break;
        }
    }

    private void gotoAbcCoolerPage() {
        try {
            String gpUrl = "https://play.google.com/store/apps/details?id=com.batterysaver.powerplus";
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(gpUrl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.vending");
            startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "gotoAbcCoolerPage: web open abclauncher");
            String gpUrl = "https://play.google.com/store/apps/details?id=com.batterysaver.powerplus";
            Uri uri = Uri.parse(gpUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);
            }catch (Exception exception) {
                Log.d(TAG, "gotoAbcCoolerPage: "  + exception.getMessage());
            }

        }
    }

}
