package com.example.camera1;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.io.IOException;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {


    private Camera.Size previewSize;
    private byte[] buffer;

    public CameraSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    private void startCamera() {
        Camera camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        Camera.Parameters parameters = camera.getParameters();
        previewSize = parameters.getPreviewSize();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        try {
            camera.setPreviewDisplay(getHolder());
            camera.setDisplayOrientation(90);
            camera.startPreview();
            camera.setPreviewCallbackWithBuffer(this);
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {

                }
            });
            buffer = new byte[previewSize.width * previewSize.height * 3 / 2];
            camera.addCallbackBuffer(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void portraitData2Raw(byte[] data) {
        int width = previewSize.width;
        int height =previewSize.height;
//        旋转y
        int y_len = width * height;
//u   y/4   v  y/4
        int uvHeight = height/2;

        int k = 0;
        for (int j = 0; j < width; j++) {
            for (int i = height - 1; i >= 0; i--) {
//                存值  k++  0          取值  width * i + j
                buffer[k++] = data[width * i + j];
            }
        }
//        旋转uv

        for (int j = 0; j < width; j += 2) {
            for (int i = uvHeight - 1; i >= 0; i--) {
                buffer[k++] = data[y_len + width * i + j];
                buffer[k++] = data[y_len + width * i + j + 1];
            }
        }
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        startCamera();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {


    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        portraitData2Raw(data);
    }
}
