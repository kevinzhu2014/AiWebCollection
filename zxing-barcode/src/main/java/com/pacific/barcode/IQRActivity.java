package com.pacific.barcode;

import android.view.SurfaceHolder;

/**
 * Created by zhushengui on 2017/4/21.
 */

public interface IQRActivity {
    void onSurfaceCreated(SurfaceHolder surfaceHolder);

    void onSurfaceDestroyed();

    void restartCapture();

    void setHook(boolean hook);
}
