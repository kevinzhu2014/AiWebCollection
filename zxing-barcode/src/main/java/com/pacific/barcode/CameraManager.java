package com.pacific.barcode;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * This class is for android targets below android 5.0 and it uses old camera api
 */
public class CameraManager extends BaseCameraManager implements Camera.AutoFocusCallback, Camera.PreviewCallback {
    public static final String TAG = CameraManager.class.getName();

    private Camera camera;

    public CameraManager(Context context) {
        super(context);
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (hook || isRelease) return;
        camera.setOneShotPreviewCallback(this);
    }

    @Override
    public void connectCamera(SurfaceHolder surfaceHolder) {
        if (!isRelease) return;
        try {
            camera = Camera.open();
            isRelease = false;
            camera.setPreviewDisplay(surfaceHolder);
            setCameraParameter();
            camera.startPreview();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void releaseCamera() {
        if (isRelease) return;
        isRelease = true;
        camera.cancelAutoFocus();
        camera.stopPreview();
        try {
            camera.setPreviewDisplay(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        camera.release();
        camera = null;
    }

    @Override
    public void startCapture() {
        if (hook || isRelease || executor.isShutdown()) return;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                camera.autoFocus(CameraManager.this);
            }
        });
    }

    @Override
    public void setCameraParameter() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(0, cameraInfo);
        int degrees = 0;
        switch (rotate) {
            case Surface.ROTATION_0: {
                degrees = 0;
                break;
            }
            case Surface.ROTATION_90: {
                degrees = 90;
                break;
            }
            case Surface.ROTATION_180: {
                degrees = 180;
                break;
            }
            case Surface.ROTATION_270: {
                degrees = 270;
                break;
            }
        }

        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayOrientation = (cameraInfo.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        }

        /** Warning : may throw exception with parameters not supported */
        Camera.Parameters parameters = camera.getParameters();

        /*List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size bestSize = previewSizes.get(0);
        for (int i = 1; i < previewSizes.size(); i++) {
            if (previewSizes.get(i).width * previewSizes.get(i).height > bestSize.width * bestSize.height) {
                bestSize = previewSizes.get(i);
            }
        }
        parameters.setPreviewSize(bestSize.width, bestSize.height);

        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        bestSize = pictureSizes.get(0);
        for (int i = 1; i < pictureSizes.size(); i++) {
            if (pictureSizes.get(i).width * pictureSizes.get(i).height > bestSize.width * bestSize.height) {
                bestSize = pictureSizes.get(i);
            }
        }
        parameters.setPictureSize(bestSize.width, bestSize.height);
        */
        initPreviewSize(parameters);
        camera.setParameters(parameters);
        camera.setDisplayOrientation(displayOrientation);
    }

    private void initPreviewSize(Camera.Parameters parameters){
        if(parameters == null){
            return;
        }
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point screenResolution = new Point(display.getWidth(), display.getHeight());
        Log.d(TAG, "Screen resolution: " + screenResolution);

        Point screenResolutionForCamera = new Point();
        screenResolutionForCamera.x = screenResolution.x;
        screenResolutionForCamera.y = screenResolution.y;

        // preview size is always something like 480*320, other 320*480
        if (screenResolution.x < screenResolution.y) {
            screenResolutionForCamera.x = screenResolution.y;
            screenResolutionForCamera.y = screenResolution.x;
        }

        Point cameraResolution = getCameraResolution(parameters, screenResolutionForCamera);

        parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
    }

    private static Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {

        String previewSizeValueString = parameters.get("preview-size-values");
        // saw this on Xperia
        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }

        Point cameraResolution = null;

        if (previewSizeValueString != null) {
            Log.d(TAG, "preview-size-values parameter: " + previewSizeValueString);
            cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
        }

        if (cameraResolution == null) {
            // Ensure that the camera resolution is a multiple of 8, as the
            // screen may not be.
            cameraResolution = new Point((screenResolution.x >> 3) << 3, (screenResolution.y >> 3) << 3);
        }

        return cameraResolution;
    }

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    private static Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenResolution) {
        int bestX = 0;
        int bestY = 0;
        int diff = Integer.MAX_VALUE;
        for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {

            previewSize = previewSize.trim();
            int dimPosition = previewSize.indexOf('x');
            if (dimPosition < 0) {
                Log.w(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            int newX;
            int newY;
            try {
                newX = Integer.parseInt(previewSize.substring(0, dimPosition));
                newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
            } catch (NumberFormatException nfe) {
                Log.w(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
            if (newDiff == 0) {
                bestX = newX;
                bestY = newY;
                break;
            } else if (newDiff < diff) {
                bestX = newX;
                bestY = newY;
                diff = newDiff;
            }

        }

        if (bestX > 0 && bestY > 0) {
            return new Point(bestX, bestY);
        }
        return null;
    }

    private static int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
        int tenBestValue = 0;
        for (String stringValue : COMMA_PATTERN.split(stringValues)) {
            stringValue = stringValue.trim();
            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException nfe) {
                return tenDesiredZoom;
            }
            int tenValue = (int) (10.0 * value);
            if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }
        return tenBestValue;
    }


    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {
        if (hook || executor.isShutdown()) return;
        Observable
                .just(camera.getParameters().getPreviewSize())
                .subscribeOn(Schedulers.from(executor))
                .map(new Func1<Camera.Size, QRResult>() {
                    @Override
                    public QRResult call(Camera.Size size) {
                        return getCodeValue(data, new Point(size.width, size.height));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<QRResult>() {
                    @Override
                    public void call(QRResult qrResult) {
                        if (qrResult == null) {
                            count++;
                            startCapture();
                            return;
                        }
                        QRUtils.vibrate(context);
                        if (onResultListener != null) {
                            onResultListener.onResult(qrResult);
                        }
                        count = 0;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("CameraManager", "getCodeValue() failed .");
                    }
                });
    }

    @Override
    public void switchLight(boolean on){
        if(on){
            turnLightOnCamera(camera);
        }else{
            turnLightOffCamera(camera);
        }
    }

    /**
     * 通过设置Camera打开闪光灯
     *
     * @param mCamera
     */
    public void turnLightOnCamera(Camera mCamera) {
        Camera.Parameters parameters = camera.getParameters();
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            // 开启闪光灯
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
            }
        }
    }

    /**
     * 通过设置Camera关闭闪光灯
     *
     * @param mCamera
     */
    public void turnLightOffCamera(Camera mCamera) {
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            // 关闭闪光灯
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            }
        }
    }
}
