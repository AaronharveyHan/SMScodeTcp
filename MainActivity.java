package com.example.switch20210927v1;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private Socket socket;
    private ExecutorService mThreadPool;
    // 输出流对象
    OutputStream outputStream;
    // 显示消息
    private TextView login_et_sms_code;
    // ip,验证码长度输入框
    private EditText ipEdit, numEdit;
    private final class MyObserver extends ContentObserver {
        public MyObserver(Handler handler){
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange) {
            Cursor cursor = null;
            try {
                cursor=getContentResolver().query(
                        Uri.parse("content://sms/inbox"),
                        new String[] {"body"},
                        null,null,"date desc limit 1");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        System.out.println("收到的信息:" + cursor.getString(0));
                        login_et_sms_code.setText(cursor.getString(0));
                        String smscodenum = "\\d{"+numEdit.getText().toString()+"}";
                        Pattern pattern = Pattern.compile(smscodenum);
                        Matcher matcher = pattern.matcher(cursor.getString(0));
                        if (matcher.find()) {
                            String smsCodeStr = matcher.group(0);
                            mThreadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // 创建Socket对象 & 指定服务端的IP 及 端口号
                                        socket = new Socket(ipEdit.getText().toString(), 8989);
                                        if(socket.isConnected()){
                                            try {
                                                // 发送数据
                                                outputStream = socket.getOutputStream();
                                                outputStream.write((smsCodeStr+"\n").getBytes("utf-8"));
                                                outputStream.flush();
                                                try {
                                                    // 断开连接
                                                    outputStream.close();
                                                    socket.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                 }
                            });
                        }
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login_et_sms_code = (TextView) this
                .findViewById(R.id.login_et_sms_code);
        getContentResolver().registerContentObserver(Uri.parse("content://sms/"),true,
                new MyObserver(new Handler()));
        ipEdit = (EditText) findViewById(R.id.ipadd);
        numEdit = (EditText) findViewById(R.id.numcode);
        mThreadPool = Executors.newCachedThreadPool();
    }
}
