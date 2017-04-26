package com.pacific.barcode;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.pacific.mvc.Activity;
import com.pacific.mvc.ActivityView;

/**
 * Created by root on 16-5-8.
 */
public class QRView extends ActivityView<Activity> implements SurfaceHolder.Callback {
    private QRCodeView qrCodeView;
    private SurfaceView surfaceView;

    public QRView(Activity activity) {
        super(activity);
    }

    @Override
    protected void findView() {
        surfaceView = retrieveView(R.id.sv_preview);
        qrCodeView = retrieveView(R.id.qr_view);
    }

    @Override
    protected void setListener() {
        qrCodeView.setPickImageListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity instanceof IQRActivity) {
                    ((IQRActivity)activity).setHook(true);
                }
                Intent galleryIntent = new Intent();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    galleryIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                galleryIntent.setType("image/*");
                Intent wrapperIntent = Intent.createChooser(galleryIntent, "选择二维码图片");
                activity.startIntentForResult(wrapperIntent, QRActivity.CODE_PICK_IMAGE, null);
            }
        });
        qrCodeView.setLightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity instanceof IQRActivity) {
                    CheckBox checkBox = (CheckBox) v;
                    if(checkBox.isChecked()) {
                        //打开闪光灯
                        ((IQRActivity) activity).switchLight(true);
                    }else{
                        //关闭闪光灯
                        ((IQRActivity) activity).switchLight(false);
                    }
                }
            }
        });

        surfaceView.getHolder().addCallback(this);
    }

    @Override
    protected void setAdapter() {

    }

    @Override
    protected void initialize() {

    }

    @Override
    public void onClick(View v) {

    }

    public void resultDialog(QRResult qrResult) {
        if (qrResult == null) {
            new AlertDialog.Builder(activity)
                    .setTitle("No Barcode Result")
                    .setMessage("Can't decode barcode from target picture , \nplease confirm the picture has barcode value.")
                    .setPositiveButton("Ok", null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(activity instanceof IQRActivity) {
                                ((IQRActivity) activity).setHook(false);
                                ((IQRActivity) activity).restartCapture();
                            }
                        }
                    })
                    .create()
                    .show();
            return;
        }
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_result, null);
        if (!TextUtils.isEmpty(String.valueOf(qrResult.getResult()))) {
            ((TextView) view.findViewById(R.id.tv_value)).setText(String.valueOf(qrResult.getResult()));
        }
        if (qrResult.getBitmap() != null) {
            ((ImageView) view.findViewById(R.id.img_barcode)).setImageBitmap(qrResult.getBitmap());
        }
        new AlertDialog.Builder(activity)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(activity instanceof IQRActivity) {
                            ((IQRActivity) activity).setHook(false);
                            ((IQRActivity) activity).restartCapture();
                        }
                    }
                })
                .setView(view)
                .create()
                .show();
    }

    public void setEmptyViewVisible(boolean visible) {
        if (visible) {
            retrieveView(R.id.v_empty).setVisibility(View.VISIBLE);
        } else {
            retrieveView(R.id.v_empty).setVisibility(View.GONE);
        }
    }

    public void setSurfaceViewVisible(boolean visible) {
        if (visible) {
            surfaceView.setVisibility(View.VISIBLE);
        } else {
            surfaceView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(activity instanceof IQRActivity) {
            ((IQRActivity) activity).onSurfaceCreated(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        setEmptyViewVisible(true);
        if(activity instanceof IQRActivity) {
            ((IQRActivity) activity).onSurfaceDestroyed();
        }
    }
}
