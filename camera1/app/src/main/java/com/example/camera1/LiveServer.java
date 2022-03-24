package com.example.camera1;

import android.media.MediaCodec;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Mrack
 * @version 1.0
 * @date 2022/3/24 15:54
 */
public class LiveServer extends WebSocketServer {

    byte[] vps_sps_pps_buf;
    public static final int VPS = 32;
    public static final int I = 19;
    private WebSocket webSocket;

    public void sendFrame(ByteBuffer outputBuffer, MediaCodec.BufferInfo bufferInfo) {
        int offset = 4;
        if (outputBuffer.get(2) == 0x01) {
            offset = 3;
        }
        int type = (outputBuffer.get(offset) & 0x7e) >> 1;
        if (type == VPS) {
            vps_sps_pps_buf = new byte[bufferInfo.size];
            outputBuffer.get(vps_sps_pps_buf);
        } else if (type == I) {
            final byte[] bytes = new byte[bufferInfo.size];
            outputBuffer.get(bytes);
            byte[] send_buf = new byte[bytes.length + vps_sps_pps_buf.length];
            System.arraycopy(vps_sps_pps_buf, 0, send_buf, 0, vps_sps_pps_buf.length);
            System.arraycopy(bytes, 0, send_buf, vps_sps_pps_buf.length, bytes.length);
            if (webSocket != null && webSocket.isOpen()) {
                webSocket.send(send_buf);
            }
        } else {
            final byte[] bytes = new byte[bufferInfo.size];
            outputBuffer.get(bytes);
            if (webSocket != null && webSocket.isOpen()) {
                webSocket.send(bytes);
                System.out.println(Arrays.toString(bytes));
            }
        }
    }
    public interface SocketCallback {
        void callBack();
    }

    public LiveServer(int port, SocketCallback callback) {
        super(new InetSocketAddress(port));
        this.callback = callback;
    }
    final SocketCallback callback;
    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println(webSocket);
        this.webSocket = webSocket;
        callback.callBack();

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println(s);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {

    }
}
