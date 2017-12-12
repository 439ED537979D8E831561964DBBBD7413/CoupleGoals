package couplegoals.com.couplegoals.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import couplegoals.com.couplegoals.utility.Utility;

public class ViewDividedCoupleExpenseFragment extends Fragment {

    //UI componenets

    Spinner spinnerSelectDateRange;
    ArrayAdapter<String> adapterSelectedDateRange;
    private static final String[] DATE_SELECTION_TYPE = new String[]{"All","Today","Current Month","Previous Month"};

    TextView expenseSummary,tvPaidNyOneTotal,tvPaidByTwoTotal,tvDifference,tvToBePaid;
    ListView listViewPaidByOne,listViewPaidByTwo;

    DatabaseReference databaseReference;
    List<Expense> expenseListPaidByOne;
    List<Expense> expenseListPaidByTwo;
    double totalExpenseAmountOne = 0,totalExpenseAmountTwo = 0;
    double totalDifference = 0;
    double totalToBePaid = 0;
    double dividebyTwo = 2;

    String sUserSelectedDateRange;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
           
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ViewDividedCoupleExpenseFragment = inflater.inflate(R.layout.fragment_view_divided_couple_expense, container, false);
        intializeUiComponents(ViewDividedCoupleExpenseFragment);
        spinnerSelectDateRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sUserSelectedDateRange = spinnerSelectDateRange.getSelectedItem().toString().trim();
                setExpenseSummary();
                loadCoupleExpenseDetailsFromDbforOne();
                loadCoupleExpenseDetailsFromDbforTwo();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return ViewDividedCoupleExpenseFragment;
    }

    private void setExpenseSummary() {
        if (sUserSelectedDateRange.equalsIgnoreCase("All")){
            expenseSummary.setText("Expense details : All");
        }
        else if(sUserSelectedDateRange.equalsIgnoreCase("Today")){
            expenseSummary.setText("Expense details :" + Utility.getCurrentDateForUserDisplay());
        }
        else if(sUserSelectedDateRange.equalsIgnoreCase("Current Month")){
            expenseSummary.setText("Expense details :" + Utility.getCurrentMonthYear());
        }
        else if(sUserSelectedDateRange.equalsIgnoreCase("Previous Month")){
            expenseSummary.setText("Expense details :" + Utility.getPreviousMonthYear());
        }
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
                                if (!expenseDetails.getsPaidBy().equalsIgnoreCase(DatabaseValues.getUserLoginId())){
                                    if (sUserSelectedDateRange.equalsIgnoreCase(getString(R.string.all)))
                                    {
                                        expenseListPaidByTwo.add(0, expenseDetails);
                                        expenseListAdapter.notifyDataSetChanged();
                                        totalExpenseAmountTwo = totalExpenseAmountTwo + Double.parseDouble(expenseDetails.getsAmount());

                                    }
                                    else if (sUserSelectedDateRange.equalsIgnoreCase("Today")){
                                        if (expenseDetails.getsWhen().equalsIgnoreCase(Utility.getCurrentDateForUserDisplay())){
                                            expenseListPaidByTwo.add(0, expenseDetails);
                                            expenseListAdapter.notifyDataSetChanged();
                                            totalExpenseAmountTwo = totalExpenseAmountTwo + Double.parseDouble(expenseDetails.getsAmount());
                                        }
                                    }
                                    else if (sUserSelectedDateRange.equalsIgnoreCase("Current Month")){
                                        if (expenseDetails.getsWhen().contains(Utility.getCurrentMonthYear())){
                                            expenseListPaidByTwo.add(0, expenseDetails);
                                            expenseListAdapter.notifyDataSetChanged();
                                            totalExpenseAmountTwo = totalExpenseAmountTwo + Double.parseDouble(expenseDetails.getsAmount());
                                        }

                                    }
                                    else if (sUserSelectedDateRange.equalsIgnoreCase("Previous Month")){
                                        if (expenseDetails.getsWhen().contains(Utility.getPreviousMonthYear())){
                                            expenseListPaidByTwo.add(0, expenseDetails);
                                            expenseListAdapter.notifyDataSetChanged();
                                            totalExpenseAmountTwo = totalExpenseAmountTwo + Double.parseDouble(expenseDetails.getsAmount());
                                        }
                                    }
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
                    tvPaidByTwoTotal.setVisibility(View.VISIBLE);
                    tvPaidByTwoTotal.setText(Html.fromHtml("Total (PARTNER) Rs.<b> " + new DecimalFormat("##.##").format(totalExpenseAmountTwo)+"</b>") );
                    if (totalExpenseAmountTwo>totalExpenseAmountOne){
                        totalDifference = totalExpenseAmountTwo -totalExpenseAmountOne;
                    } else {
                        totalDifference = totalExpenseAmountOne -totalExpenseAmountTwo;
                    }
                    tvDifference.setVisibility(View.VISIBLE);
                    tvDifference.setText(Html.fromHtml("Difference Rs.<b> " + new DecimalFormat("##.##").format(totalDifference)+"</b>") );
                    totalToBePaid = totalDifference/dividebyTwo;
                    tvToBePaid.setVisibility(View.VISIBLE);
                    tvToBePaid.setText(Html.fromHtml("To be paid Rs.<b> " + new DecimalFormat("##.##").format(totalToBePaid)+"</b>") );
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
                                if (expenseDetails.getsPaidBy().equalsIgnoreCase(DatabaseValues.getUserLoginId())){
                                    if (sUserSelectedDateRange.equalsIgnoreCase(getString(R.string.all)))
                                    {
                                        expenseListPaidByOne.add(0, expenseDetails);
                                        expenseListAdapter.notifyDataSetChanged();
                                        totalExpenseAmountOne = totalExpenseAmountOne + Double.parseDouble(expenseDetails.getsAmount());
                                    }
                                    else if(sUserSelectedDateRange.equalsIgnoreCase("Today")){
                                        if (expenseDetails.getsWhen().equalsIgnoreCase(Utility.getCurrentDateForUserDisplay())){
                                            expenseListPaidByOne.add(0, expenseDetails);
                                            expenseListAdapter.notifyDataSetChanged();
                                            totalExpenseAmountOne = totalExpenseAmountOne + Double.parseDouble(expenseDetails.getsAmount());
                                        }
                                    }
                                    else if(sUserSelectedDateRange.equalsIgnoreCase("Current Month")){
                                        if (expenseDetails.getsWhen().contains(Utility.getCurrentMonthYear())){
                                            expenseListPaidByOne.add(0, expenseDetails);
                                            expenseListAdapter.notifyDataSetChanged();
                                            totalExpenseAmountOne = totalExpenseAmountOne + Double.parseDouble(expenseDetails.getsAmount());
                                        }
                                    }
                                    else if (sUserSelectedDateRange.equalsIgnoreCase("Previous Month")){
                                        if (expenseDetails.getsWhen().contains(Utility.getPreviousMonthYear())){
                                            expenseListPaidByOne.add(0, expenseDetails);
                                            expenseListAdapter.notifyDataSetChanged();
                                            totalExpenseAmountOne = totalExpenseAmountOne + Double.parseDouble(expenseDetails.getsAmount());
                                        }
                                    }

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
                    tvPaidNyOneTotal.setVisibility(View.VISIBLE);
                    tvPaidNyOneTotal.setText(Html.fromHtml("Total (YOU) Rs.<b> " + new DecimalFormat("##.##").format(totalExpenseAmountOne)+"</b>") );
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
        tvToBePaid = (TextView) viewDividedCoupleExpenseFragment.findViewById(R.id.tvToBePaid);
        listViewPaidByOne = (ListView) viewDividedCoupleExpenseFragment.findViewById(R.id.listViewPaidByOne);
        listViewPaidByTwo = (ListView) viewDividedCoupleExpenseFragment.findViewById(R.id.listViewPaidByTwo);

        expenseListPaidByOne = new ArrayList<>();
        expenseListPaidByTwo = new ArrayList<>();
    }
}
