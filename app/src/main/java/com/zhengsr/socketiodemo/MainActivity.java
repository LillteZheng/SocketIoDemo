package com.zhengsr.socketiodemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.zhengsr.socketiodemo.R;
import com.zhengsr.socketiodemo.fragment.LoginFragment;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //自行判断翻转的问题，这里只是个demo
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, LoginFragment.newInstance())
                .commit();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SingleSocket.getInstance().disConnect();
    }


}
