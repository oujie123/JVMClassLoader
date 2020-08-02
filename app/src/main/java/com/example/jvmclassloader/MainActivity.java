package com.example.jvmclassloader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Demo.test();
//        Log.e(TAG, "====" + getClassLoader());
//        Log.e(TAG, "====" + String.class.getClassLoader());
//        Log.e(TAG, "====" + AppCompatActivity.class.getClassLoader());
    }
}
