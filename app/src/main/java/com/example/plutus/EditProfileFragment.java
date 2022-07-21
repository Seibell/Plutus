package com.example.plutus;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EditProfileFragment extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private DatabaseReference mProfileDatabase;

    EditText editProfileFirstName, editProfileLastName, editProfileEmail, editProfileIncome, editProfileSavingsGoal;
    Button btnSaveProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_update_profile);

        editProfileFirstName = findViewById(R.id.editProfileFirstName);
        editProfileLastName = findViewById(R.id.editProfileLastName);
        editProfileEmail = findViewById(R.id.editProfileEmail);
        editProfileIncome = findViewById(R.id.editProfileIncome);
        editProfileSavingsGoal = findViewById(R.id.editProfileSavingsGoal);


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mProfileDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);


        mProfileDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null) {
                    editProfileFirstName.setText(user.firstName);
                    editProfileLastName.setText(user.lastName);
                    editProfileEmail.setText(user.email);
                    editProfileIncome.setText(String.valueOf(user.income));
                    editProfileSavingsGoal.setText(String.valueOf(user.savingsGoal));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
                switchToViewProfile();
            }
        });
    }

    private void updateProfile() {
        String firstName = editProfileFirstName.getText().toString();
        String lastName = editProfileLastName.getText().toString();
        String email = editProfileEmail.getText().toString();
        double income = Double.parseDouble(editProfileIncome.getText().toString());
        double savingsGoal = Double.parseDouble(editProfileSavingsGoal.getText().toString());

        HashMap User = new HashMap();
        User.put("firstName", firstName);
        User.put("lastName", lastName);
        User.put("email", email);
        User.put("income", income);
        User.put("savingsGoal", savingsGoal);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mProfileDatabase = FirebaseDatabase.getInstance().getReference("users");

        mProfileDatabase.child(currentUser.getUid()).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileFragment.this, "Successfully updated!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(EditProfileFragment.this, "Failed to update", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void switchToViewProfile() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}


