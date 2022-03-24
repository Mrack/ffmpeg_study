package com.example.camera1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LiveServer.SocketCallback {

    public static final int REQUEST_CODE = 10;
    private MediaProjectionManager projectionManager;
    private EditText etPort;
    private Button btStatus;
    private SocketPush socketPush;
    private TextView tv;

    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    public static String getLocalIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
        }
        // return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPort = findViewById(R.id.et_port);
        btStatus = findViewById(R.id.bt_status);
        tv = findViewById(R.id.ip);
        tv.setText(getLocalIpAddress(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_WIFI_STATE
            }, 1);
        }
        btStatus.setOnClickListener((v) -> {
            tv.setText(getLocalIpAddress(this));
            if (socketPush == null) {
                projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                startActivityForResult(projectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            } else {
                socketPush.stopPush();
                socketPush = null;
            }
            btStatus.setText(socketPush == null ? "开启服务器" : "关闭服务器");
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            MediaProjection mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            socketPush = new SocketPush(Integer.parseInt(etPort.getText().toString()),this);
            socketPush.start(mediaProjection);
            btStatus.setText(socketPush == null ? "开启服务器" : "关闭服务器");
        }
    }

    @Override
    public void callBack() {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, "连接成功!", Toast.LENGTH_SHORT).show());
    }
}