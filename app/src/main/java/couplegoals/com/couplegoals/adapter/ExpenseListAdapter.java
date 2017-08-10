package couplegoals.com.couplegoals.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import couplegoals.com.couplegoals.R;
import couplegoals.com.couplegoals.activity.ViewSingleExpenseDetailActivity;
import couplegoals.com.couplegoals.database.DatabaseValues;
import couplegoals.com.couplegoals.model.Expense;

/**
 * Created by Brijesh on 8/8/2017.
 */

public class ExpenseListAdapter extends ArrayAdapter<Expense> {
    private Activity context;
    private List<Expense> expenseDetailsList;


    public ExpenseListAdapter(Activity context,List<Expense> expenseDetailsList){
        super(context, R.layout.card_layout_view_couple_expense,expenseDetailsList);
        this.context = context;
        this.expenseDetailsList = expenseDetailsList;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        final View listViewItems = layoutInflater.inflate(R.layout.card_layout_view_couple_expense,null,true);

        TextView textViewExpenseAmount = (TextView) listViewItems.findViewById(R.id.textViewExpenseAmount);
        TextView textViewExpenseNotes = (TextView) listViewItems.findViewById(R.id.textViewExpenseNotes);
        TextView textViewExpenseDate = (TextView) listViewItems.findViewById(R.id.textViewExpenseDate);
        TextView textViewExpensePaidBy = (TextView) listViewItems.findViewById(R.id.textViewExpensePaidBy);
        CardView cardViewExpenseCardList =(CardView) listViewItems.findViewById(R.id.cardViewExpenseCardList);
        ImageButton ibDeleteExpense = (ImageButton) listViewItems.findViewById(R.id.ibDeleteExpense);


        final Expense expenseDetails = expenseDetailsList.get(position);
        textViewExpenseAmount.setText(Html.fromHtml("Rs.<b>" + expenseDetails.getsAmount()+"</b>"));
        textViewExpenseNotes.setText(Html.fromHtml("Notes.<b>"+expenseDetails.getsNotes()+"</b>"));
        textViewExpenseDate.setText(Html.fromHtml("On.<b>"+expenseDetails.getsWhen()+"</b>"));
        textViewExpensePaidBy.setText(Html.fromHtml("Paid by : <b>" + expenseDetails.getsPaidBy()+"</b>"));

        cardViewExpenseCardList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentViewSingleExpenseDetail = new Intent(context.getApplicationContext(), ViewSingleExpenseDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("expenseId",expenseDetails.getsExpenseId());
                bundle.putString("expenseAmount",expenseDetails.getsAmount());
                bundle.putString("expenseNotes",expenseDetails.getsNotes());
                bundle.putString("expensePaidBy",expenseDetails.getsPaidBy());
                bundle.putString("expenseWhen",expenseDetails.getsWhen());
                bundle.putString("expenseImagePath",expenseDetails.getsExpenseImagePath());
                intentViewSingleExpenseDetail.putExtras(bundle);

                context.startActivity(intentViewSingleExpenseDetail);
            }
        });
        ibDeleteExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReferenceDeleteExpense = DatabaseValues.getExpseDetailReference();

                final Query queryDeleteExpense = databaseReferenceDeleteExpense.orderByChild("sExpenseId").equalTo(expenseDetails.getsExpenseId());
                queryDeleteExpense.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot  expenseDetailSnapshot : dataSnapshot.getChildren()){
                            Expense expenseDetails = expenseDetailSnapshot.getValue(Expense.class);
                            if (expenseDetails.getsCoupleName()!= null){
                                if (expenseDetails.getsCoupleName().equalsIgnoreCase(DatabaseValues.getCOUPLENAME())){
                                    databaseReferenceDeleteExpense.child(expenseDetails.getsExpenseId()).removeValue();

                                    Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
        return listViewItems;
    }

}
