package com.example.camera1;

import android.hardware.display.DisplayManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SocketPush extends Thread {
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
            videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, width*height);
            videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20);
            videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC);
            codec.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            Surface inputSurface = codec.createInputSurface();
            mediaProjection.createVirtualDisplay("-display", width, height, 1, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, inputSurface, null, null);


        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }

    @Override
    public void run() {
        super.run();
        codec.start();
        while (true) {
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int index = codec.dequeueOutputBuffer(bufferInfo, 10000);
            while (index >= 0) {
                ByteBuffer outputBuffer = codec.getOutputBuffers()[index];
                byte[] bytes = new byte[bufferInfo.size];
                outputBuffer.get(bytes);
                System.out.println(Arrays.toString(bytes));
                codec.releaseOutputBuffer(index, false);
                index = codec.dequeueOutputBuffer(bufferInfo, 10000);
            }
        }
    }
}
