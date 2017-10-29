package com.example.vero.testwriteexternalstorage70;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkePermission();
    }



    public void testWrite(View view){
        //写到文件
        long len=Environment.getExternalStorageDirectory().getFreeSpace();
        Log.e("len",""+len);

        File file=new File(Environment.getExternalStorageDirectory(),"test.txt");
        FileOutputStream fos= null;
        try {
            fos = new FileOutputStream(file);
            PrintWriter out=new PrintWriter(fos);
            out.println("test write"+len);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkePermission() {
        List<String> permissions=new ArrayList<>();

        int writeSDCard=ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeSDCard!= PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissions.size()>0){
            ActivityCompat.requestPermissions(this,permissions.toArray(new String[permissions.size()]),0);
        }

    }
}
