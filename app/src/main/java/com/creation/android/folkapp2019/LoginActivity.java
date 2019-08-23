package com.creation.android.folkapp2019;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    Button signup, login, verify;
    EditText userID, otp;
    String enteredUserID, codeSent, phoneNumber;
    ObjectAnimator objectAnimator; //To decrease or increase the opacity of the button
    int flag = 0;    // 0 => folk member , 1 => guest

    /*@Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Toast.makeText(getApplicationContext(), currentUser.getPhoneNumber(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "not null user", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(LoginActivity.this, Splash_screen.class));

        }

    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
//        password = findViewById(R.id.et_password);
        signup = findViewById(R.id.sign_up);
        login = findViewById(R.id.btn_login);
        userID = findViewById(R.id.et_user_id);
        verify = findViewById(R.id.btn_verify);
        otp = findViewById(R.id.et_otp);

        db = FirebaseFirestore.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                enteredUserID = userID.getText().toString().trim();

                if (TextUtils.isDigitsOnly(enteredUserID) && !enteredUserID.isEmpty()) {
                    enteredUserID = "+91 " + enteredUserID;
                    flag = 1;
                }

//                try {
//                    int num = Integer.parseInt(enteredUserID);
//                    enteredUserID = "+91 " + enteredUserID;
//                }catch (Exception e){
//                    Toast.makeText(getApplicationContext(),"Exception!!!!!!!",Toast.LENGTH_SHORT).show();
//                    flag = 1;
//                }

//                enteredPassword = password.getText().toString().trim();


                if (flag == 0) {

                    //Folk member
                    Toast.makeText(getApplicationContext(), "FOLK member", Toast.LENGTH_SHORT).show();

                    // Access a Cloud Firestore instance from Activity

                    db.collection("folk_app_users")
                            .whereEqualTo("folk_id", enteredUserID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Toast.makeText(getApplicationContext(), "Successful login! Hare Krishna " + ' ' + document.get("name"), Toast.LENGTH_LONG).show();
                                            Log.d("Check the data", document.getId() + " => " + document.getData());

                                            // Move to profile activity with the successful login
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
                                        Toast.makeText(getApplicationContext(), "Error getting documents: " + task.getException(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                } else {

                    //Guest
                    Toast.makeText(getApplicationContext(), "Guest user", Toast.LENGTH_SHORT).show();


                    // Access a Cloud Firestore instance from Activity

                   /* String token_id = FirebaseInstanceId.getInstance().getToken();
                    String current_id = mAuth.getCurrentUser().getUid();

                    Map<String, Object> tokenMap = new HashMap<>();
                    tokenMap.put("token_id", token_id);*/

                   /* db.collection("FolkMember")
                            .document(current_id)
                            .update(tokenMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(LoginActivity.this, Splash_screen.class);

                                    // Move to profile activity with the successful login
                                    startActivity(intent);

                                    //mProgressBar.setVisibility(View.INVISIBLE);

                                }
                            });*/

                    db.collection("FolkMember")
                            .whereEqualTo("phone", enteredUserID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Toast.makeText(getApplicationContext(), "Successful login!", Toast.LENGTH_LONG).show();
                                            Log.d("Check the data", document.getId() + " => " + document.getData());


                                            // Move to Home activity with the successful login
                                            phoneNumber = document.get("phone").toString();
                                            sendVerificationCode(phoneNumber);

                                            verify.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    VerifySignInCode();
                                                }
                                            });
                                           /* String token_id = FirebaseInstanceId.getInstance().getToken();
                                            String current_id = mAuth.getCurrentUser().getUid();

                                            Map<String, Object> tokenMap = new HashMap<>();
                                            tokenMap.put("token_id", token_id);

                                            db.collection("FolkMember").document(current_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //mProgressBar.setVisibility(View.INVISIBLE);

                                                }
                                            });*/



                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error getting documents: " + task.getException(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, Signup_method.class);
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
            Toast.makeText(getApplicationContext(), "Verification Failed" + ' ' + e, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //Enabling the click event for the verify button and the editText
            verify.setEnabled(true);
            verify.setFocusable(true);
            objectAnimator = ObjectAnimator.ofFloat(verify, "alpha", 1f);
            otp.setEnabled(true);
            otp.setFocusable(true);
            objectAnimator = ObjectAnimator.ofFloat(otp, "alpha", 1f);

            Toast.makeText(getApplicationContext(), "OTP has been sent to your registered mobile number", Toast.LENGTH_SHORT).show();
            //OTP that has been sent to the user's phone is 's'
            codeSent = s;
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(String s) {
            super.onCodeAutoRetrievalTimeOut(s);

            Toast.makeText(getApplicationContext(), "Your session has timed out", Toast.LENGTH_SHORT).show();
        }
    };


    private void VerifySignInCode() {
        String codeEntered = otp.getText().toString().trim();
        if (codeEntered.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter the otp sent to your registered mobile number before clicking on verify", Toast.LENGTH_SHORT).show();
            return;
        } else {
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
                            Toast.makeText(getApplicationContext(), "Successful login!", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(LoginActivity.this, Splash_screen.class);
                            startActivity(i);

                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), task.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

//        FirebaseAuth.AuthStateListener mAuthListener;

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(getApplicationContext(), currentUser.getPhoneNumber(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, Splash_screen.class));
        }

    }


}
