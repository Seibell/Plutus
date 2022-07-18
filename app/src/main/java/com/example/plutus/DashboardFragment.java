package com.example.plutus;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

import Model.Data;

public class DashboardFragment extends Fragment {

    //Floating button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_expense_btn;

    //FLoating button text
    private TextView fab_expense_txt;

    //Add boolean
    private boolean isOpen = false;

    //Animation class
    private Animation FadeOpen, FadeClose;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    //Total dashboard expense view
    private TextView totalExpenseResult;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dashboard, container, false);

        //Firebase auth for income and expense
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();

        String uid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        //Connect floating button to layout

        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn);
        fab_expense_btn = myview.findViewById(R.id.expense_Ft_btn);

        //Connect floating text

        fab_expense_txt = myview.findViewById(R.id.expense_ft_text);

        //Animation connect

        FadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        //Total expense
        totalExpenseResult = myview.findViewById(R.id.expense_set_result);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
                if (isOpen) {
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_expense_btn.setClickable(false);

                    fab_expense_txt.startAnimation(FadeClose);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;
                } else {
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_expense_btn.setClickable(true);

                    fab_expense_txt.startAnimation(FadeOpen);
                    fab_expense_txt.setClickable(true);
                    isOpen = true;
                }
            }
        });

        //Calculate total expense
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int expenseSum = 0;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Data data = snap.getValue(Data.class);
                    expenseSum += data.getAmount();

                    String result = String.valueOf(expenseSum);
                    totalExpenseResult.setText(result);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myview;
    }

    //Floating button animation :D
    private void ftAnimation(){
        if (isOpen) {
            fab_expense_btn.startAnimation(FadeClose);
            fab_expense_btn.setClickable(false);

            fab_expense_txt.startAnimation(FadeClose);
            fab_expense_txt.setClickable(false);
            isOpen = false;
        } else {
            fab_expense_btn.startAnimation(FadeOpen);
            fab_expense_btn.setClickable(true);

            fab_expense_txt.startAnimation(FadeOpen);
            fab_expense_txt.setClickable(true);
            isOpen = true;
        }
    }

    private void addData() {
        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });
    }


    public void expenseDataInsert() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.custom_layout_insertdata, null);
        myDialog.setView(view);
        final AlertDialog dialog = myDialog.create();

        dialog.setCancelable(false);
        final EditText edtAmount = view.findViewById(R.id.amount_edt);
        final EditText edtType = view.findViewById(R.id.type_edt);
        final EditText edtNote = view.findViewById(R.id.note_edt);

        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = edtType.getText().toString().trim();
                String amount = edtAmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    edtType.setError("Required Field!");
                    return;
                }

                if (TextUtils.isEmpty(amount)) {
                    edtAmount.setError("Required Field!");
                    return;
                }

                int intAmount = Integer.parseInt(amount);

                if (TextUtils.isEmpty(note)) {
                    edtNote.setError("Required Field!");
                    return;
                }

                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(intAmount, type, note, id, mDate);

                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Expenses have been added!", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}