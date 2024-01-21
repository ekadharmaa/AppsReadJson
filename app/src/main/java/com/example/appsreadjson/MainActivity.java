package com.example.appsreadjson;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    final int permissionInternetCode = 1001;
    Button btvAll, btSearchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showInternetPermission();
        btvAll = (Button) findViewById(R.id.btLihatData);
        btSearchData = (Button) findViewById(R.id.btSearchData);

        btvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainViewActivityAll.class);
                startActivity(intent);
            }
        });

        btSearchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivitySearchData.class);
                startActivity(intent);
            }
        });
    }

    private void requestPermission(String permissionName, int permissionRequestCode){
        ActivityCompat.requestPermissions(this, new String[]{
                permissionName
        }, permissionRequestCode);
    }
    private void showExplanation(String title, String message, final String permission, final int permissionCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
        .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission(permission,permissionCode);
                    }
                });
    }
    private void showInternetPermission(){
        String kindPermission = Manifest.permission.INTERNET;

        int permissionCheck = ContextCompat.checkSelfPermission(this,kindPermission);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,kindPermission)){
                showExplanation("Permission needed", "Application Need Permission " + "Please", kindPermission,permissionInternetCode);
            } else{
                requestPermission(kindPermission, permissionInternetCode);
            }
        }
    }

}