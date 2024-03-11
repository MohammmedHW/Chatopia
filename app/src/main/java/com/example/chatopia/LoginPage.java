package com.example.chatopia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

public class LoginPage extends AppCompatActivity {
    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.login_phone_number);
        sendOtp= findViewById(R.id.send_otp);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);

        sendOtp.setOnClickListener((v)->{
            if (!countryCodePicker.isValidFullNumber()) {
                phoneInput.setError("Phone Number Is Not Valid");
                return;

            }
            Intent intent =new Intent(LoginPage.this,LoginOTP.class);
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });
    }
}