package cn.mrack.live.recv;

import android.content.Intent;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ConnectActivity extends AppCompatActivity {
    private EditText etPort;
    private Button btStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        etPort = findViewById(R.id.et_port);
        btStatus = findViewById(R.id.bt_status);
        btStatus.setOnClickListener((v) -> {
            Intent intent = new Intent(this, RemoteActivity.class);
            intent.putExtra("url", etPort.getText().toString().trim());
            startActivity(intent);
        });
    }

}