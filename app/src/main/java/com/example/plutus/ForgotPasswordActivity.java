package com.example.plutus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class ForgotPasswordActivity extends AppCompatActivity {

    /*
    Forgot password menu execute
     */
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            finish();
            return;
        }

        Button btnResetPassword = (Button) findViewById(R.id.btnResetPassword);
        TextView textViewSwitchToLogin = (TextView) findViewById(R.id.tvSwitchToLogin);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { sendResetPasswordEmail(); }
        });

        textViewSwitchToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { switchToLogin(); }
        });
    }

    private void sendResetPasswordEmail() {
        EditText resetEmail = findViewById(R.id.etResetEmail);
        String email = resetEmail.getText().toString();

        if (email.isEmpty()) {
            resetEmail.setError("Please provide valid email!");
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Check your email to reset your password! (May be in spam)", Toast.LENGTH_LONG).show();
                    switchToLogin();
                }
            }
        });
    }
    private void switchToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}