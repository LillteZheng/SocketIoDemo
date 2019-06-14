package com.zhengsr.socketiodemo;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by zhengshaorui on 2019/6/14.
 */

public class SingleSocket {
    private Socket mSocket;
    public static final String IO_SERVER_URL = "https://socket-io-chat.now.sh/";
    private SingleSocket(){

    }

    private static class Holder{
        static SingleSocket SIGNAL = new SingleSocket();
    }
    public static SingleSocket getInstance(){
        return Holder.SIGNAL;
    }

    public Socket getSocket(){
       if (mSocket == null) {
           try {
               mSocket = IO.socket(IO_SERVER_URL);
           } catch (URISyntaxException e) {
               e.printStackTrace();
           }
       }
        mSocket.connect();
        return mSocket;
    }

    public void disConnect(){
        if (mSocket != null){
            mSocket.disconnect();
        }
    }
}
