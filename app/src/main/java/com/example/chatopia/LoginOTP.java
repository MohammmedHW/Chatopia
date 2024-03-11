package com.example.chatopia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.chatopia.utils.androidutil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;


import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginOTP extends AppCompatActivity {
String phoneNumber;
long timeoutSeconds =60L;
EditText otpInput;
Button nextButton;
TextView resendOtpTextView;

String verificationCode;

PhoneAuthProvider.ForceResendingToken ResendingToken;

FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        otpInput =findViewById(R.id.login_otp);
        nextButton=findViewById(R.id.login_next);
        resendOtpTextView =findViewById(R.id.resend_otp);


        phoneNumber = getIntent().getExtras().getString("phone");
        senOtp(phoneNumber,false);
        nextButton.setOnClickListener((v)->{
            String EnteredOtp = otpInput.getText().toString();
           PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationCode,EnteredOtp);
           signIn(credential);

        }

        );
        resendOtpTextView.setOnClickListener((v)->{
            senOtp(phoneNumber,true);

        });

    }

    void senOtp(String phoneNumber, boolean isresend ){
        startResendTimer();
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential PhoneAuthCredential){
                        signIn(PhoneAuthCredential);
                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException a){
                        androidutil.ShowToast(getApplicationContext(),"OTP Verification Fail" );

                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        ResendingToken = forceResendingToken;
                        androidutil.ShowToast(getApplicationContext(),"OTP Send Successfully");
                    }
                });
        if (isresend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(ResendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }


    }
    void signIn(PhoneAuthCredential phoneAuthCredential){
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginOTP.this, LoginUsername.class);
                    intent.putExtra("phone",phoneNumber);
                    startActivity(intent);

                }else{
                    androidutil.ShowToast(getApplicationContext(),"Verification failed");
                }
            }
        });

    }
    void startResendTimer(){
        resendOtpTextView.setEnabled(false);
        Timer timer= new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                timeoutSeconds--;
                resendOtpTextView.setText("Resend OTP In"+ " "+timeoutSeconds+" "+"Seconds");
                if(timeoutSeconds<=0){
                    timeoutSeconds =60L;
                    timer.cancel();
                    runOnUiThread(()->{
                        resendOtpTextView.setEnabled(true);
                    });
                }

            }
        },0,100);
    }
}