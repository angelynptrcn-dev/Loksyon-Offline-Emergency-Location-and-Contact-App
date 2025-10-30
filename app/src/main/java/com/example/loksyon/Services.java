package com.example.loksyon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class Services extends AppCompatActivity {

    private static final int REQUEST_CALL_PERMISSION = 1;
    private String phoneNumberToCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services); // Ensure this is the correct layout file

        setUpCallButton(R.id.callButton1, "+639218601111");
        setUpCallButton(R.id.callButton2, "+639218602222");
        setUpCallButton(R.id.callButton3, "+639218603333");
        setUpCallButton(R.id.callButton4, "+639218604444");
        setUpCallButton(R.id.callButton5, "+639218609332");

        setUpMessageButton(R.id.msgButton1, "+639218601111");
        setUpMessageButton(R.id.msgButton2, "+639218602222");
        setUpMessageButton(R.id.msgButton3, "+639218603333");
        setUpMessageButton(R.id.msgButton4, "+639218604444");
        setUpMessageButton(R.id.msgButton5, "+639218609332");

        Button backservice = findViewById(R.id.backservice);
        backservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Services.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setUpCallButton(int buttonId, final String phoneNumber) {
        ImageButton callButton = findViewById(buttonId);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumberToCall = phoneNumber;
                makePhoneCall();
            }
        });
    }

    private void setUpMessageButton(int buttonId, final String phoneNumber) {
        ImageButton messageButton = findViewById(buttonId);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMessagingApp(phoneNumber);
            }
        });
    }

    private void makePhoneCall() {
        if (ContextCompat.checkSelfPermission(Services.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Services.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            startCall(phoneNumberToCall);
        }
    }

    private void startCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    private void openMessagingApp(String phoneNumber) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        startActivity(smsIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCall(phoneNumberToCall);
            } else {

            }
        }
    }
}
