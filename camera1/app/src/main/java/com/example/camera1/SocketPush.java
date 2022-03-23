package com.example.camera1;

import android.hardware.display.DisplayManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.view.Surface;

import java.io.IOException;

public class SocketPush {
    private MediaProjection mediaProjection;
    private MediaCodec codec;
    private int width = 720;
    private int height = 1280;

    private final int port;

    public SocketPush(int port) {
        this.port = port;

    }

    void start(MediaProjection mediaProjection) {
        this.mediaProjection = mediaProjection;
        try {
            MediaFormat videoFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, width, height);
            videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height);
            videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20);
            videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            codec = MediaCodec.createEncoderByType("video/hevc");
            codec.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            Surface inputSurface = codec.createInputSurface();
            mediaProjection.createVirtualDisplay("ttt", width, height, 1, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, inputSurface, null, null);
            codec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
