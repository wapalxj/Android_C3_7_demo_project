package vero.com.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Activity2 extends AppCompatActivity {
    private TextView mTv1;
    private TextView mTv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        initView();
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        String myChoices=bundle.getString("myChoices");
        String res=bundle.getString("res");
        mTv1.setText("我选择的答案是："+myChoices);
        mTv2.setText("参考答案是："+res);
    }

    private void initView() {
        mTv1= (TextView) findViewById(R.id.tv1);
        mTv2= (TextView) findViewById(R.id.tv2);
    }
}
