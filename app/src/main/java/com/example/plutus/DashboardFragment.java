package com.example.plutus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.text.Layout;
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

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import Model.Data;

public class DashboardFragment extends Fragment {

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;
    private DatabaseReference mProfileDatabase;

    //Total dashboard expense/savings view
    private TextView totalExpenseResult;
    private TextView totalSavingsResult;

    //Textviews for pie chart
    private TextView tvHousing, tvMedical, tvFood, tvEntertainment, tvUtilities, tvOthers;
    PieChart pieChart;

    //Recycler
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;


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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        //Firebase auth for income and expense
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();

        String uid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        //savings database (linked to user)
        mProfileDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        //Recycler
        recyclerView = view.findViewById(R.id.recycler_expense);

        //Total expense
        totalExpenseResult = view.findViewById(R.id.expense_set_result);

        //Total savings
        totalSavingsResult = view.findViewById(R.id.savings_set_result);

        //Textviews for graph
        tvHousing = view.findViewById(R.id.tvHousing);
        tvMedical = view.findViewById(R.id.tvMedical);
        tvFood = view.findViewById(R.id.tvFood);
        tvEntertainment = view.findViewById(R.id.tvEntertainment);
        tvUtilities = view.findViewById(R.id.tvUtilities);
        tvOthers = view.findViewById(R.id.tvOthers);
        pieChart = view.findViewById(R.id.pieChart);

        getData();

        //Calculate total expense
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                double expenseSum = 0.00;
                DecimalFormat df = new DecimalFormat("0.00");

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Data data = snap.getValue(Data.class);
                    expenseSum += data.getAmount();

                    totalExpenseResult.setText(df.format(expenseSum));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Compute savings
        mProfileDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                double savingsFinal = user.income - Double.parseDouble(totalExpenseResult.getText().toString());

                DecimalFormat df = new DecimalFormat("0.00");

                totalSavingsResult.setText(df.format(savingsFinal));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler View
        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManagerExpense);

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(mExpenseDatabase, Data.class).build();

        adapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(options) {

            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ExpenseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense, parent, false));
            }

            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Data model) {
                holder.setExpenseAmount(model.getAmount());
                holder.setExpenseType(model.getType());
                holder.setExpenseDate(model.getDate());
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    //Dashboard expense recyclerview
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        View mExpenseView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView = itemView;
        }

        public void setExpenseAmount(double amount) {
            TextView mAmount = mExpenseView.findViewById(R.id.amount_expense_ds);
            String stringAmount = String.valueOf(amount);
            mAmount.setText(stringAmount);
        }

        public void setExpenseType(String type) {
            TextView mType = mExpenseView.findViewById(R.id.type_expense_ds);
            mType.setText(type);
        }

        public void setExpenseDate(String date) {
            TextView mDate = mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }
    }

    private void getData() {
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<PieModel> pieModels = new ArrayList<>();
                double totalExpenseSum = 0.00;

                for (DataSnapshot s : snapshot.getChildren()) {
                    Data data = s.getValue(Data.class);
                    totalExpenseSum += data.getAmount();
                }

                for (DataSnapshot s : snapshot.getChildren()) {
                    Data data = s.getValue(Data.class);

                    Random random = new Random();

                    pieModels.add(new PieModel(data.getType(), (float) data.getAmount(), Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))));
                }

                for (int i = 0; i < pieModels.size(); i++) {
                    pieChart.addPieSlice(pieModels.get(i));
                }

                DecimalFormat df = new DecimalFormat("0.00");

                double housing = 0.0;
                double medical = 0.0;
                double food = 0.0;
                double entertainment = 0.0;
                double utilities = 0.0;
                double others = 0.0;

                for (DataSnapshot s : snapshot.getChildren()) {
                    Data data = s.getValue(Data.class);

                    if (data.getType().equalsIgnoreCase("housing")) {
                        housing += data.getAmount();
                    }
                    if (data.getType().equalsIgnoreCase("medical")) {
                        medical += data.getAmount();
                    }
                    if (data.getType().equalsIgnoreCase("food")) {
                        food += data.getAmount();
                    }
                    if (data.getType().equalsIgnoreCase("entertainment")) {
                        entertainment += data.getAmount();
                    }
                    if (data.getType().equalsIgnoreCase("utilities")) {
                        utilities += data.getAmount();
                    }
                    if (data.getType().equalsIgnoreCase("others")) {
                        others += data.getAmount();
                    }
                }

                double finalHousingResult = (housing / totalExpenseSum) * 100;
                tvHousing.setText(String.valueOf(df.format(finalHousingResult)));

                double finalMedicalResult = (medical / totalExpenseSum) * 100;
                tvMedical.setText(String.valueOf(df.format(finalMedicalResult)));

                double finalFoodResult = (food / totalExpenseSum) * 100;
                tvFood.setText(String.valueOf(df.format(finalFoodResult)));

                double finalEntertainmentResult = (entertainment / totalExpenseSum) * 100;
                tvEntertainment.setText(String.valueOf(df.format(finalEntertainmentResult)));

                double finalUtilitiesResult = (utilities / totalExpenseSum) * 100;
                tvUtilities.setText(String.valueOf(df.format(finalUtilitiesResult)));

                double finalOthersResult = (others / totalExpenseSum) * 100;
                tvOthers.setText(String.valueOf(df.format(finalOthersResult)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}