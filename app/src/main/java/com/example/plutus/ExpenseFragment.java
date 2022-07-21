package com.example.plutus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Model.Data;


public class ExpenseFragment extends Fragment {

    //Firebase database
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;

    private TextView expenseSumResult;

    //For updating expense data
    private EditText edtAmount;
    private EditText edtNote;
    private Spinner edtType;

    private Button btnUpdate;
    private Button btnRemove;

    //Data items
    private double amount; //Change to double later?
    private String type;
    private String note;

    private String pos_key;

    //Floating button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_expense_btn;

    //FLoating button text
    private TextView fab_expense_txt;

    //Add boolean
    private boolean isOpen = false;

    //Animation class
    private Animation FadeOpen, FadeClose;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        //Connect floating button to layout

        fab_main_btn = view.findViewById(R.id.fb_main_plus_btn);
        fab_expense_btn = view.findViewById(R.id.expense_Ft_btn);

        //Connect floating text

        fab_expense_txt = view.findViewById(R.id.expense_ft_text);

        //Animation connect

        FadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        expenseSumResult = view.findViewById(R.id.expense_txt_result);
        recyclerView = view.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true); // this makes the recycler go from top>bottom
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

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

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {

            double sum = 0.00;
            ArrayList<Data> recycleList = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                recycleList.clear();

                DecimalFormat df = new DecimalFormat("0.00");

                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        Data data = s.getValue(Data.class);
                        recycleList.add(data);
                        adapter.notifyDataSetChanged();
                    }

                    sum = 0.00;

                    for (int i = 0; i < recycleList.size(); i++) {
                        sum += recycleList.get(i).getAmount();
                    }
                }

                expenseSumResult.setText(df.format(sum));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //nothing here?
            }
        });



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(mExpenseDatabase,  Data.class).build();

        adapter = new FirebaseRecyclerAdapter<Data, ViewHolder>(options) {

            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false));
            }

            protected void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Data model) {
                holder.setAmount(model.getAmount());
                holder.setType(model.getType());
                holder.setNote(model.getRemark());
                holder.setDate(model.getDate());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        pos_key = getRef(holder.getAdapterPosition()).getKey();

                        amount = model.getAmount();
                        type = model.getType();
                        note = model.getRemark();

                        updateDataItem();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void updateDataItem() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.update_data_item, null);

        dialog.setView(view);

        edtType = view.findViewById(R.id.type_edt);
        String[] types = getResources().getStringArray(R.array.types);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtType.setAdapter(adapter);

        edtAmount = view.findViewById(R.id.amount_edt);
        edtNote = view.findViewById(R.id.note_edt);

        //shows editText data in updateDialog
        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtType.setSelection(adapter.getPosition(type.toString()));

        //Buttons
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnRemove = view.findViewById(R.id.btnRemove);

        AlertDialog updateDialog = dialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amountString = edtAmount.getText().toString().trim();
                type = edtType.getSelectedItem().toString();
                note = edtNote.getText().toString().trim();
                amount = Double.parseDouble(amountString);

                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(amount, type, note, pos_key, mDate);

                mExpenseDatabase.child(pos_key).setValue(data);

                updateDialog.dismiss();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpenseDatabase.child(pos_key).removeValue();
                updateDialog.dismiss();
            }
        });

        updateDialog.show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

            View mView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mView = itemView;
            }

            private void setDate(String date) {
                TextView mDate = mView.findViewById(R.id.date_txt_expense);
                mDate.setText(date);
            }

            private void setType (String type) {
                TextView mType = mView.findViewById(R.id.type_txt_expense);
                mType.setText(type);
            }

            private void setNote(String note) {
                TextView mNote = mView.findViewById(R.id.note_txt_expense);
                mNote.setText(note);
            }

            private void setAmount(double amount) {
                TextView mAmount = mView.findViewById(R.id.amount_txt_expense);

                String strAmount = String.valueOf(amount);
                mAmount.setText(strAmount);
            }
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
        final EditText edtNote = view.findViewById(R.id.note_edt);

        final Spinner spinnerTypes = view.findViewById(R.id.type_edt);
        String[] types = getResources().getStringArray(R.array.types);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypes.setAdapter(adapter);

        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = edtAmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();
                String type = spinnerTypes.getSelectedItem().toString();

                if (TextUtils.isEmpty(amount)) {
                    edtAmount.setError("Required Field!");
                    return;
                }

                double doubleAmount = Double.parseDouble(amount);

                if (TextUtils.isEmpty(note)) {
                    edtNote.setError("Required Field!");
                    return;
                }

                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(doubleAmount, type, note, id, mDate);

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