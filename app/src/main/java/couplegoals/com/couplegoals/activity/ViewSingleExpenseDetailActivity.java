package couplegoals.com.couplegoals.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import couplegoals.com.couplegoals.HomeActivity;
import couplegoals.com.couplegoals.R;
import couplegoals.com.couplegoals.database.DatabaseValues;
import couplegoals.com.couplegoals.fragments.ViewExpenseFragment;
import couplegoals.com.couplegoals.model.Expense;

public class ViewSingleExpenseDetailActivity extends AppCompatActivity {

    TextView textViewAmount,textViewPaidBy,textViewWhen,textViewNotes;
    ImageView imageViewExpenseSingle;
    Button btnDeleteExpense,btnEditExpense;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_expense_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundleTransactionData = getIntent().getExtras();
        final String sExpenseAmount = bundleTransactionData.getString("expenseAmount");
        final String sExpenseNotes = bundleTransactionData.getString("expenseNotes");
        final String sExpensePaidBy = bundleTransactionData.getString("expensePaidBy");
        final String expenseWhen = bundleTransactionData.getString("expenseWhen");
        final String expenseImagePath = bundleTransactionData.getString("expenseImagePath");
        final String expenseId = bundleTransactionData.getString("expenseId");


        super.setTitle(sExpenseNotes);
        textViewAmount = (TextView) findViewById(R.id.textViewAmountSingleView);
        textViewPaidBy = (TextView) findViewById(R.id.textViewExpensePaidBySingleView);
        textViewWhen = (TextView) findViewById(R.id.textViewWhenSingleView);
        textViewNotes = (TextView) findViewById(R.id.textViewNotesSingleView);
        imageViewExpenseSingle = (ImageView) findViewById(R.id.imageViewExpenseSingle);
        btnDeleteExpense = (Button) findViewById(R.id.btnDeleteExpense);
        btnEditExpense = (Button) findViewById(R.id.btnEditExpense);

        textViewAmount.setText(Html.fromHtml("Amt.Rs.<b>"+sExpenseAmount+"</b>"));
        textViewPaidBy.setText(Html.fromHtml("Paid by.<b>"+sExpensePaidBy+"</b>"));
        textViewWhen.setText(Html.fromHtml("When.<b>"+expenseWhen+"</b>"));
        textViewNotes.setText(Html.fromHtml("Notes. <b>"+sExpenseNotes+"</b>"));
        Picasso.with(ViewSingleExpenseDetailActivity.this).load(expenseImagePath).into(imageViewExpenseSingle);

        btnDeleteExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReferenceDeleteExpense = DatabaseValues.getExpseDetailReference();

                final Query queryDeleteExpense = databaseReferenceDeleteExpense.orderByChild("sExpenseId").equalTo(expenseId);
                queryDeleteExpense.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot  expenseDetailSnapshot : dataSnapshot.getChildren()){
                            Expense expenseDetails = expenseDetailSnapshot.getValue(Expense.class);
                            if (expenseDetails.getsCoupleName()!= null){
                                if (expenseDetails.getsCoupleName().equalsIgnoreCase(DatabaseValues.getCOUPLENAME())){
                                    databaseReferenceDeleteExpense.child(expenseId).removeValue();

                                    Toast.makeText(ViewSingleExpenseDetailActivity.this,"Deleted",Toast.LENGTH_SHORT)
                                            .show();
                                    //startActivity(new Intent(ViewSingleExpenseDetailActivity.this, HomeActivity.class));
                                    finish();
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
        btnEditExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewSingleExpenseDetailActivity.this, EditExpenseActivity.class));
            }
        });
    }
}
