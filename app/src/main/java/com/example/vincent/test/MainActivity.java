package com.example.vincent.test;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vise.log.ViseLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    public static final String request_url = "http://shahe.rywl.cn/wex5App/queryInformation.do?params={%22appUserId%22:%22901%22}";

    private EditText url;
    private EditText number;
    private Button btnGo;
    private TextView tvException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url = (EditText)findViewById(R.id.et_url);
        number = (EditText)findViewById(R.id.et_number);
        btnGo = findViewById(R.id.btn_go);
        tvException = findViewById(R.id.tv);
        btnGo.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
                request(url.getText().toString().trim(),Integer.valueOf(number.getText().toString().trim()));
            }
        });
    }

    private void request(String url, final int j) {
        if(TextUtils.isEmpty(url)){
            Toast.makeText(MainActivity.this,"地址不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if(j == 0){
            Toast.makeText(MainActivity.this,"请输入循环次数",Toast.LENGTH_LONG).show();
            return;
        }
        final StringBuffer sb = new StringBuffer();
        btnGo.setClickable(false);
        btnGo.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.color_gray_646f7f));
        for (final int[] i = {0}; i[0] <j; i[0]++){
            final int[] error = {0};
            final int finalI = i[0];
            OkHttpUtils.post()
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ViseLog.d("在第"+finalI+"次请求时发生异常");
                            error[0]++;
                            e.printStackTrace();
                            sb.append("这是第"+finalI+"次请求时发生的异常\n"+e.toString()+"\n");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            ViseLog.d("当前请求次数"+ finalI +""+",响应结果:"+response);
                            if(finalI == j-1){
                                ViseLog.d("最后一次请求响应结束，请求错误次数："+error[0]);
                                btnGo.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent));
                                btnGo.setClickable(true);
                                btnGo.setText("开始循环请求");
                            }else {
                                btnGo.setText("正在进行第"+String.valueOf(finalI)+"次请求");
                            }
                        }
                    });
            if(error[0]!=0){
                tvException.setText(sb.toString());
            }
        }
    }
}
