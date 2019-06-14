package com.zhengsr.socketiodemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;

import com.zhengsr.socketiodemo.R;
import com.zhengsr.socketiodemo.SingleSocket;
import com.zhengsr.socketiodemo.adapter.RBaseAdapter;
import com.zhengsr.socketiodemo.adapter.RBaseViewholder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by zhengshaorui on 2019/6/14.
 */

public class MsgFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MsgFragment";
    private String mUserName;

    public static MsgFragment newInstance(String name,int nums) {

        Bundle args = new Bundle();
        args.putString("name",name);
        args.putInt("nums",nums);
        MsgFragment fragment = new MsgFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private Socket mSocket;
    private List<User> mMsgs = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private MsgAdpater mAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.sendmsg_layout,container,false);

        mMsgs.clear();
        Bundle bundle = getArguments();
        if (bundle != null){
            int nums = bundle.getInt("nums",-1);
            mUserName = bundle.getString("name");
            String msg = " 加入房间  共有"+nums+" 在线";
            mMsgs.add(new User(mUserName,msg));

        }
        mSocket = SingleSocket.getInstance().getSocket();
        getSocketMsg();
        mRecyclerView = view.findViewById(R.id.messages);
        mEditText = view.findViewById(R.id.message_input);
        view.findViewById(R.id.send_button).setOnClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mAdapter = new MsgAdpater(R.layout.item_msg,mMsgs);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onClick(View v) {
        String message = mEditText.getText().toString().trim();
        mSocket.emit("new message", message);
        updateUI(new User(mUserName,": "+message));
        mEditText.setText("");
        mEditText.requestFocus();
    }


    class MsgAdpater extends RBaseAdapter<User>{

        public MsgAdpater(int layoutid, List<User> list) {
            super(layoutid, list);
        }

        @Override
        public void getConver(RBaseViewholder holder, User data) {

            if (!TextUtils.isEmpty(data.name) && !TextUtils.isEmpty(data.msg)){
                holder.setText(R.id.item_user,data.name);
                holder.setText(R.id.item_msg,data.msg);
            }
            if (!TextUtils.isEmpty(data.msg)){
                holder.setText(R.id.item_msg,data.msg);
            }
        }
    }

    private void getSocketMsg(){
        mSocket.on("user joined", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                String username;
                int numUsers;
                try {
                    username = data.getString("username");
                    numUsers = data.getInt("numUsers");
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    return;
                }
                updateUI(new User(username," 加入房间"));
            }
        });

        mSocket.on("new message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                String username;
                int message;
                try {
                    username = data.getString("username");
                    message = data.getInt("message");
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    return;
                }
                updateUI(new User(username,": "+message));
            }
        });
        mSocket.on("user left", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                String username;
                try {
                    username = data.getString("username");
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    return;
                }
                updateUI(new User(username," 离开了房间"));
            }
        });
    }


    private void updateUI(final User user){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMsgs.add(user);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    class User{
        public String name;
        public String msg;

        public User(String name, String msg) {
            this.name = name;
            this.msg = msg;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off();
        mSocket.disconnect();
    }
}
