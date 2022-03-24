package cn.mrack.live.recv;

import android.util.Log;
import android.widget.Toast;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

public class SocketPull {
    private final String url;
    private SocketCallback socketCallback;
    MyWebSocketClient myWebSocketClient;
    private static final String TAG = "SocketPull";

    public SocketPull(SocketCallback socketCallback, String url) {
        this.url = url;
        this.socketCallback = socketCallback;
    }

    public void start() throws Exception {
        URI url = new URI("ws://" + this.url);
        myWebSocketClient = new MyWebSocketClient(url);
        myWebSocketClient.connect();

    }

    public void stop() {
        myWebSocketClient.close();

    }

    Timer timer = new Timer();


    private class MyWebSocketClient extends WebSocketClient {
        public MyWebSocketClient(URI serverURI) {
            super(serverURI);
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            Log.i(TAG, "打开 socket  onOpen: ");
        }

        @Override
        public void onMessage(String s) {
        }

        @Override
        public void onMessage(ByteBuffer bytes) {
            Log.i(TAG, "消息长度  : " + bytes.remaining());
            byte[] buf = new byte[bytes.remaining()];
            bytes.get(buf);
            socketCallback.callBack(buf);

        }

        @Override
        public void onClose(int i, String s, boolean b) {
            socketCallback.error("链接断开!");
        }

        @Override
        public void onError(Exception e) {
            Log.i(TAG, "onError: ");
            socketCallback.error(e.getMessage());
        }
    }

    public interface SocketCallback {
        void callBack(byte[] data);

        void error(String data);
    }
}
