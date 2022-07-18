package com.example.plutus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

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
    private EditText edtType;
    private EditText edtNote;

    private Button btnUpdate;
    private Button btnRemove;

    //Data items
    private int amount; //Change to double later?
    private String type;
    private String note;

    private String pos_key;

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

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        expenseSumResult = view.findViewById(R.id.expense_txt_result);
        recyclerView = view.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true); // this makes the recycler go from top>bottom
        layoutManager.setStackFromEnd(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {

            int sum = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot s : snapshot.getChildren()) {
                    Data data = s.getValue(Data.class);
                    sum += data.getAmount();

                    String finalSum = String.valueOf(sum);

                    expenseSumResult.setText(finalSum);
                }
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

                        pos_key = getRef(position).getKey();

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

        edtAmount = view.findViewById(R.id.amount_edt);
        edtType = view.findViewById(R.id.type_edt);
        edtNote = view.findViewById(R.id.note_edt);

        //shows editText data in updateDialog
        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        //Buttons
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnRemove = view.findViewById(R.id.btnRemove);

        AlertDialog updateDialog = dialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amountString = edtAmount.getText().toString().trim();
                type = edtType.getText().toString().trim();
                note = edtNote.getText().toString().trim();
                amount = Integer.parseInt(amountString);

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

            private void setAmount(int amount) {
                TextView mAmount = mView.findViewById(R.id.amount_txt_expense);

                String strAmount = String.valueOf(amount);
                mAmount.setText(strAmount);
            }
        }

}