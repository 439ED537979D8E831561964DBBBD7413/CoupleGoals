package couplegoals.com.couplegoals.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import couplegoals.com.couplegoals.R;
import couplegoals.com.couplegoals.adapter.ExpenseDividedListAdapter;
import couplegoals.com.couplegoals.adapter.ExpenseListAdapter;
import couplegoals.com.couplegoals.database.DatabaseValues;
import couplegoals.com.couplegoals.model.Expense;

public class ViewDividedCoupleExpenseFragment extends Fragment {

    //UI componenets

    Spinner spinnerSelectDateRange;
    ArrayAdapter<String> adapterSelectedDateRange;
    private static final String[] DATE_SELECTION_TYPE = new String[]{"All","Today","Current Month"};

    TextView expenseSummary,tvPaidNyOneTotal,tvPaidByTwoTotal,tvDifference;
    Button btnViewExpense;
    ListView listViewPaidByOne,listViewPaidByTwo;

    DatabaseReference databaseReference;
    List<Expense> expenseListPaidByOne;
    List<Expense> expenseListPaidByTwo;
    double totalExpenseAmountOne = 0,totalExpenseAmountTwo = 0;
    double totalDifference = 0;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
           
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ViewDividedCoupleExpenseFragment = inflater.inflate(R.layout.fragment_view_divided_couple_expense, container, false);
        intializeUiComponents(ViewDividedCoupleExpenseFragment);

        btnViewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCoupleExpenseDetailsFromDbforOne();
                loadCoupleExpenseDetailsFromDbforTwo();
            }
        });
        return ViewDividedCoupleExpenseFragment;
    }

    private void loadCoupleExpenseDetailsFromDbforTwo() {
        databaseReference = DatabaseValues.getExpseDetailReference();
        databaseReference.keepSynced(true);
        final ExpenseDividedListAdapter expenseListAdapter = new ExpenseDividedListAdapter(getActivity(),expenseListPaidByTwo);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    expenseListPaidByTwo.clear();
                    totalExpenseAmountTwo = 0;
                    for (DataSnapshot  expenseDetailSnapshot : dataSnapshot.getChildren()){
                        Expense expenseDetails = expenseDetailSnapshot.getValue(Expense.class);
                        if (expenseDetails.getsCoupleName()!= null){
                            if (expenseDetails.getsCoupleName().equalsIgnoreCase(DatabaseValues.getCOUPLENAME())){
                                if (!expenseDetails.getsPaidBy().equalsIgnoreCase(DatabaseValues.getUserDisplayName())){
                                    expenseListPaidByTwo.add(0, expenseDetails);
                                    expenseListAdapter.notifyDataSetChanged();
                                    totalExpenseAmountTwo = totalExpenseAmountTwo + Double.parseDouble(expenseDetails.getsAmount());
                                }
                            }
                        }
                    }
                    //Collections.reverse(expenseList);
                    listViewPaidByTwo.post(new Runnable() {
                        @Override
                        public void run() {
                            listViewPaidByTwo.smoothScrollToPosition(0);
                        }
                    });
                    tvPaidByTwoTotal.setText(Html.fromHtml("Total expense Rs.<b> " + new DecimalFormat("##.##").format(totalExpenseAmountTwo)+"</b>") );
                    if (totalExpenseAmountTwo>totalExpenseAmountOne){
                        totalDifference = totalExpenseAmountTwo -totalExpenseAmountOne;
                    } else {
                        totalDifference = totalExpenseAmountOne -totalExpenseAmountTwo;
                    }

                    tvDifference.setText(Html.fromHtml("Total expense Rs.<b> " + new DecimalFormat("##.##").format(totalDifference)+"</b>") );
                    listViewPaidByTwo.setAdapter(expenseListAdapter);
                    expenseListAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getActivity(),"No expense exist",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void loadCoupleExpenseDetailsFromDbforOne() {
        databaseReference = DatabaseValues.getExpseDetailReference();
        databaseReference.keepSynced(true);
        final ExpenseDividedListAdapter expenseListAdapter = new ExpenseDividedListAdapter(getActivity(),expenseListPaidByOne);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    expenseListPaidByOne.clear();
                    totalExpenseAmountOne = 0;
                    for (DataSnapshot  expenseDetailSnapshot : dataSnapshot.getChildren()){
                        Expense expenseDetails = expenseDetailSnapshot.getValue(Expense.class);
                        if (expenseDetails.getsCoupleName()!= null){
                            if (expenseDetails.getsCoupleName().equalsIgnoreCase(DatabaseValues.getCOUPLENAME())){
                                if (expenseDetails.getsPaidBy().equalsIgnoreCase(DatabaseValues.getUserDisplayName())){
                                    expenseListPaidByOne.add(0, expenseDetails);
                                    expenseListAdapter.notifyDataSetChanged();
                                    totalExpenseAmountOne = totalExpenseAmountOne + Double.parseDouble(expenseDetails.getsAmount());
                                }
                            }
                        }
                    }
                    //Collections.reverse(expenseList);
                    listViewPaidByOne.post(new Runnable() {
                        @Override
                        public void run() {
                            listViewPaidByOne.smoothScrollToPosition(0);
                        }
                    });
                    tvPaidNyOneTotal.setText(Html.fromHtml("Total expense Rs.<b> " + new DecimalFormat("##.##").format(totalExpenseAmountOne)+"</b>") );
                    listViewPaidByOne.setAdapter(expenseListAdapter);
                    expenseListAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getActivity(),"No expense exist",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void intializeUiComponents(View viewDividedCoupleExpenseFragment) {

        adapterSelectedDateRange = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,DATE_SELECTION_TYPE);
        spinnerSelectDateRange = (Spinner) viewDividedCoupleExpenseFragment.findViewById(R.id.spinnerSelectDateRange);
        spinnerSelectDateRange.setAdapter(adapterSelectedDateRange);

        expenseSummary = (TextView) viewDividedCoupleExpenseFragment.findViewById(R.id.expenseSummary);
        tvPaidNyOneTotal = (TextView) viewDividedCoupleExpenseFragment.findViewById(R.id.tvPaidNyOneTotal);
        tvPaidByTwoTotal = (TextView) viewDividedCoupleExpenseFragment.findViewById(R.id.tvPaidByTwoTotal);
        expenseSummary = (TextView) viewDividedCoupleExpenseFragment.findViewById(R.id.expenseSummary);
        tvDifference = (TextView) viewDividedCoupleExpenseFragment.findViewById(R.id.tvDifference);
        btnViewExpense = (Button) viewDividedCoupleExpenseFragment.findViewById(R.id.btnViewExpense);
        listViewPaidByOne = (ListView) viewDividedCoupleExpenseFragment.findViewById(R.id.listViewPaidByOne);
        listViewPaidByTwo = (ListView) viewDividedCoupleExpenseFragment.findViewById(R.id.listViewPaidByTwo);

        expenseListPaidByOne = new ArrayList<>();
        expenseListPaidByTwo = new ArrayList<>();
    }
}
