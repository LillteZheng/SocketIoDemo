package com.zhengsr.socketiodemo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.zhengsr.socketiodemo.R;
import com.zhengsr.socketiodemo.SingleSocket;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by zhengshaorui on 2019/6/14.
 */

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private EditText mEditText;

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Socket mSocket;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.login_layout,container,false);

        mSocket = SingleSocket.getInstance().getSocket();
        //注册 login 事件
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "zsr call: ");
            }
        });
        mSocket.on("login",LoginListener);
        mEditText = view.findViewById(R.id.username_input);
        view.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEditText.getText().toString().trim();
                if (TextUtils.isEmpty(name)){
                    Toast.makeText(getContext(), "username cannot be null ", Toast.LENGTH_SHORT).show();
                    return;
                }
                //发送
                mSocket.emit("add user",name);
            }
        });
        return view;
    }

    private Emitter.Listener LoginListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }
           // Log.d(TAG, "zsr numbsers: "+numUsers);
            mSocket.off("login");
            mSocket.off("add user");
            String name = mEditText.getText().toString().trim();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content,MsgFragment.newInstance(name,numUsers))
                    .commit();
        }
    };


}
