package com.creation.android.folkapp2019;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity {

    Button login,verify;
    EditText userID,otp;
    TextView signup;
    String enteredUserID,codeSent, phoneNumber;
    ObjectAnimator objectAnimator; //To decrease or increase the opacity of the button
    int flag = 0;    // 0 => folk member , 1 => guest
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    int progressStatus = 0;
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signup = findViewById(R.id.sign_up);
        login = findViewById(R.id.btn_login);
        userID = findViewById(R.id.et_user_id);
        verify = findViewById(R.id.btn_verify);
        otp = findViewById(R.id.et_otp);
        progressBar = findViewById(R.id.progress_login);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                enteredUserID = userID.getText().toString().trim();

                if (TextUtils.isDigitsOnly(enteredUserID) && !enteredUserID.isEmpty()){
                    enteredUserID = "+91 " + enteredUserID;
                    flag = 1;
                }

                // Folk member
                if (flag == 0) {


                    //Progress bar spinner
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (progressStatus<100){
                                progressStatus+=1;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setProgress(progressStatus);
                                        progressBar.setVisibility(View.VISIBLE);

                                    }
                                });
                                try {
                                    Thread.sleep(500);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();


                    Toast.makeText(getApplicationContext(),"FOLK member",Toast.LENGTH_SHORT).show();

                    // Access a Cloud Firestore instance from Activity
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    //Folk Boy is the collection which is simulated because folk member data should be obtained from the original folk database
                    db.collection("FolkBoy")
                            .whereEqualTo("folk_id",enteredUserID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Toast.makeText(getApplicationContext(),"Successful login! Hare Krishna "+' ' + document.get("name"),Toast.LENGTH_LONG).show();
                                            Log.d("Check the data", document.getId() + " => " + document.getData());

                                            phoneNumber = document.get("phone").toString();
                                            sendVerificationCode(phoneNumber);

                                            verify.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    VerifySignInCode();
                                                }
                                            });
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Error getting documents: " + task.getException(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                }else {

                    // Guest member

                    //Progress bar spinner
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (progressStatus<100){
                                progressStatus+=1;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setProgress(progressStatus);
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                });
                                try {
                                    Thread.sleep(500);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();


                    // Access a Cloud Firestore instance from Activity
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("Users")
                            .whereEqualTo("phone",enteredUserID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("Check the data", document.getId() + " => " + document.getData());

                                            Toast.makeText(getApplicationContext(),"Guest user",Toast.LENGTH_SHORT).show();

                                            // Move to Home activity with the successful login
                                            phoneNumber = document.get("phone").toString();
                                            sendVerificationCode(phoneNumber);

                                            verify.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    VerifySignInCode();
                                                }
                                            });

                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Error getting documents: " + task.getException(),Toast.LENGTH_LONG).show();
                                    }
                                }


                            });
                }

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, Guest_signup.class);
                startActivity(i);
            }
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // In case of auto verification of the code
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(),"Verification Failed" + ' ' + e,Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //Enabling the click event for the verify button and the editText
            verify.setEnabled(true);
            verify.setFocusable(true);
            objectAnimator = ObjectAnimator.ofFloat(verify,"alpha",1f);
            otp.setEnabled(true);
            otp.setFocusable(true);
            objectAnimator = ObjectAnimator.ofFloat(otp,"alpha",1f);

            Toast.makeText(getApplicationContext(),"OTP has been sent to your registered mobile number",Toast.LENGTH_SHORT).show();
            //OTP that has been sent to the user's phone is 's'
            codeSent = s;
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(String s) {
            super.onCodeAutoRetrievalTimeOut(s);

            Toast.makeText(getApplicationContext(),"Your session has timed out",Toast.LENGTH_SHORT).show();
        }
    };


    private void VerifySignInCode() {
        String codeEntered = otp.getText().toString().trim();
        if (codeEntered.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please enter the otp sent to your registered mobile number before clicking on verify",Toast.LENGTH_SHORT).show();
            return;
        }else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, codeEntered);
            signInWithPhoneAuthCredential(credential);
        }
    }




    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Successful login!",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LoginActivity.this,Splash_screen.class);
                            startActivity(i);

                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "An error occurred while signing in",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(getApplicationContext(), currentUser.getPhoneNumber(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,Splash_screen.class));
        }

    }
}