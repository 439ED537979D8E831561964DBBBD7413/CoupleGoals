package couplegoals.com.couplegoals.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import couplegoals.com.couplegoals.R;
import couplegoals.com.couplegoals.adapter.ExpenseListAdapter;
import couplegoals.com.couplegoals.database.DatabaseValues;
import couplegoals.com.couplegoals.model.Expense;


public class ViewExpenseFragment extends Fragment {

    ListView listViewCoupleExpense;
    TextView textViewTotalExpense;

    DatabaseReference databaseReference;
    List<Expense> expenseList;
    double totalExpenseAmount = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewExpenseFragment = inflater.inflate(R.layout.fragment_view_expense, container, false);

        initializeUIComponents(viewExpenseFragment);

        loadCoupleExpenseDetailsFromDb();
        return viewExpenseFragment;
    }

    private void loadCoupleExpenseDetailsFromDb() {
        databaseReference = DatabaseValues.getExpseDetailReference();
        databaseReference.keepSynced(true);
        final ExpenseListAdapter expenseListAdapter = new ExpenseListAdapter(getActivity(),expenseList);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    expenseList.clear();
                    for (DataSnapshot  expenseDetailSnapshot : dataSnapshot.getChildren()){
                        Expense expenseDetails = expenseDetailSnapshot.getValue(Expense.class);
                        if (expenseDetails.getsCoupleName()!= null){
                            if (expenseDetails.getsCoupleName().equalsIgnoreCase(DatabaseValues.getCOUPLENAME())){
                                expenseList.add(0, expenseDetails);
                                expenseListAdapter.notifyDataSetChanged();
                                totalExpenseAmount = totalExpenseAmount + Double.parseDouble(expenseDetails.getsAmount());
                            }
                        }
                    }
                    Collections.reverse(expenseList);
                    listViewCoupleExpense.post(new Runnable() {
                        @Override
                        public void run() {
                            listViewCoupleExpense.smoothScrollToPosition(0);
                        }
                    });
                    textViewTotalExpense.setText(Html.fromHtml("Total expense Rs.<b> " + new DecimalFormat("##.##").format(totalExpenseAmount)+"</b>") );
                    listViewCoupleExpense.setAdapter(expenseListAdapter);
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

    private void initializeUIComponents(View viewExpenseFragment) {
        listViewCoupleExpense = (ListView) viewExpenseFragment.findViewById(R.id.listViewCoupleExpense);
        textViewTotalExpense = (TextView) viewExpenseFragment.findViewById(R.id.textViewTotalExpense);
        expenseList = new ArrayList<>();
    }

}
