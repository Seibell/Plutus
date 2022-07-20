package com.example.plutus;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mProfileDatabase;

    private TextView profileFirstName, profileLastName, profileEmail, profileWelcome;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mProfileDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        profileWelcome = view.findViewById(R.id.profileWelcome);
        profileFirstName = view.findViewById(R.id.profileFirstName);
        profileLastName = view.findViewById(R.id.profileLastName);
        profileEmail = view.findViewById(R.id.profileEmail);

        mProfileDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                if (user != null) {
                    profileWelcome.setText("Welcome " + user.firstName + "!");
                    profileFirstName.setText(user.firstName);
                    profileLastName.setText(user.lastName);
                    profileEmail.setText(user.email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}