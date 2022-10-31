package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //setTitle("Login");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            StartActivityByStatus();
            return;
        }

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticateUser();
            }
        });

        TextView tvSwitchToRegister = findViewById(R.id.tvSwitchToRegister);
        tvSwitchToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToRegister();
            }
        });


    }

    private void authenticateUser() {
        pd = new ProgressDialog(LoginActivity.this);

        // Set progress dialog style spinner
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Set the progress dialog title and message
        pd.setTitle("Please wait");
        pd.setMessage("Loading.........");
        pd.setCancelable(false);

        // Set the progress dialog background color
        //pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));

        pd.setIndeterminate(false);

        // Finally, show the progress dialog
        pd.show();

        EditText etLoginEmail = findViewById(R.id.etLoginEmail);
        EditText etLoginPassword = findViewById(R.id.etLoginPassword);

        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"Please fill all fields", Toast.LENGTH_LONG).show();
            pd.hide();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            StartActivityByStatus();
                            pd.hide();
                        } else {
                            Toast.makeText(LoginActivity.this,"Authentication failed.",Toast.LENGTH_LONG).show();
                            pd.hide();
                        }

                    }
                });

    }



    private void StartActivityByStatus(){

        try {

            if (mAuth.getCurrentUser() != null) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                String userID  = mAuth.getCurrentUser().getUid();
                reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Config.user = snapshot.getValue(User.class);

                        if(Config.user != null) {

                            /*String UserStatus = Config.user.status;
                            Config.UserStatus = UserStatus;
                            Config.UserEmail = Config.user.email;*/

                            //if(UserStatus.equals("admin")) {
                             //   Toast.makeText(LoginActivity.this, "Signed in", Toast.LENGTH_LONG).show();
                            //    finishAffinity();
                             //   startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                            //} else {
                                Toast.makeText(LoginActivity.this, "Signed in", Toast.LENGTH_LONG).show();
                                finishAffinity();
                                startActivity(new Intent(LoginActivity.this, UserActivity.class));
                            //}

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Something went wrong! " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void switchToRegister() {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
        //finish();
    }
}