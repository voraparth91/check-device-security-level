package com.example.keystoredetails;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyInfo;
import android.security.keystore.KeyProperties;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isStrong = hasStrongBox();
        final RelativeLayout background = findViewById(R.id.bg);
        final TextView tv = findViewById(R.id.info);

        if(isStrong){
           background.setBackgroundResource(R.color.green);
           tv.setText("Your device has STRONG BOX");
        }else if(isTEE(this)){
            background.setBackgroundResource(R.color.orange);
            tv.setText("Your device is HARDWARE BACKED \nbut doesn't have STRONG BOX");
        }else{
            background.setBackgroundResource(R.color.red);
            tv.setText("Your device is NOT HARDWARE BACKED");
        }
    }

    boolean isTEE(Context context){
        try {

            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                kpg.initialize(new KeyGenParameterSpec.Builder("demo_key", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).build());

                KeyPair kp = kpg.generateKeyPair();

                Key k = kp.getPrivate();

                KeyFactory kf = KeyFactory.getInstance(k.getAlgorithm(),"AndroidKeyStore");

                KeyInfo ki = kf.getKeySpec(k, KeyInfo.class);

                return ki.isInsideSecureHardware();

            }
        } catch (Exception e) {
            Toast.makeText(context, "Exception Occured:: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        return false;
    }

    boolean hasStrongBox() {
        return this.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE);
    }
}