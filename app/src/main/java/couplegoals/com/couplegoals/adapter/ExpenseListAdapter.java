package couplegoals.com.couplegoals.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import couplegoals.com.couplegoals.R;
import couplegoals.com.couplegoals.activity.EditExpenseActivity;
import couplegoals.com.couplegoals.activity.ViewSingleExpenseDetailActivity;
import couplegoals.com.couplegoals.database.DatabaseValues;
import couplegoals.com.couplegoals.model.Expense;

/**
 * Created by Brijesh on 8/8/2017.
 */

public class ExpenseListAdapter extends ArrayAdapter<Expense> {
    private Activity context;
    private List<Expense> expenseDetailsList;
    Expense expenseDetails;
    AlertDialog.Builder alertDailogForDeletingExpense;

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
        ImageButton ibEditExpense = (ImageButton) listViewItems.findViewById(R.id.ibEditExpense);
        ImageButton ibInfoExpense = (ImageButton) listViewItems.findViewById(R.id.ibInfoExpense);
        ImageButton ibShareExpense = (ImageButton) listViewItems.findViewById(R.id.ibShareExpense);

        alertDailogForDeletingExpense = new AlertDialog.Builder(context);
        alertDailogForDeletingExpense.setTitle(R.string.deleteconfirmation);
        alertDailogForDeletingExpense.setMessage(R.string.deleteexpenseconfirmation);

        expenseDetails = expenseDetailsList.get(position);
        textViewExpenseAmount.setText(Html.fromHtml("Rs.<b>" + expenseDetails.getsAmount()+"</b>"));
        textViewExpenseNotes.setText(Html.fromHtml("Notes.<b>"+expenseDetails.getsNotes()+"</b>"));
        textViewExpenseDate.setText(Html.fromHtml("On.<b>"+expenseDetails.getsWhen()+"</b>"));
        textViewExpensePaidBy.setText(Html.fromHtml("Paid by : <b>" + expenseDetails.getsDisplayName()+"</b>"));

        cardViewExpenseCardList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentViewSingleExpenseDetail = new Intent(context.getApplicationContext(), ViewSingleExpenseDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("expenseId",expenseDetailsList.get(position).getsExpenseId());
                bundle.putString("expenseAmount",expenseDetailsList.get(position).getsAmount());
                bundle.putString("expenseNotes",expenseDetailsList.get(position).getsNotes());
                bundle.putString("expensePaidBy",expenseDetailsList.get(position).getsDisplayName());
                bundle.putString("expenseWhen",expenseDetailsList.get(position).getsWhen());
                bundle.putString("expenseImagePath",expenseDetailsList.get(position).getsExpenseImagePath());
                intentViewSingleExpenseDetail.putExtras(bundle);

                context.startActivity(intentViewSingleExpenseDetail);
            }
        });
        ibDeleteExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDailogForDeletingExpense.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final DatabaseReference databaseReferenceDeleteExpense = DatabaseValues.getExpseDetailReference();

                        final Query queryDeleteExpense = databaseReferenceDeleteExpense.orderByChild("sExpenseId").equalTo(expenseDetailsList.get(position).getsExpenseId());
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
                                            sendNotification(expenseDetails.getsAmount());
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
                alertDailogForDeletingExpense.setNegativeButton("No",  new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(getActivity(),"You cancelled adding transaction",Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog alert = alertDailogForDeletingExpense.create();
                alert.show();

            }
        });
        ibEditExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentViewEditExpense = new Intent(context.getApplicationContext(), EditExpenseActivity.class);
                context.startActivity(intentViewEditExpense);
            }
        });
        ibInfoExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentViewSingleExpenseDetail = new Intent(context.getApplicationContext(), ViewSingleExpenseDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("expenseId",expenseDetailsList.get(position).getsExpenseId());
                bundle.putString("expenseAmount",expenseDetailsList.get(position).getsAmount());
                bundle.putString("expenseNotes",expenseDetailsList.get(position).getsNotes());
                bundle.putString("expensePaidBy",expenseDetailsList.get(position).getsPaidBy());
                bundle.putString("expenseWhen",expenseDetailsList.get(position).getsWhen());
                bundle.putString("expenseImagePath",expenseDetailsList.get(position).getsExpenseImagePath());
                intentViewSingleExpenseDetail.putExtras(bundle);

                context.startActivity(intentViewSingleExpenseDetail);
            }
        });
        ibShareExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "From Couple goals \n" +
                        "Couple name:"+DatabaseValues.getCOUPLENAME()+" \n"+
                        "Amount:"+expenseDetailsList.get(position).getsAmount()+" \n"+
                        "Notes:"+expenseDetailsList.get(position).getsNotes()+" \n"
                        +"Paid by:"+expenseDetailsList.get(position).getsPaidBy() +"\n"
                        +"On:"+expenseDetailsList.get(position).getsWhen() + "\n"
                        +"Category:"+expenseDetailsList.get(position).getsExpenseCategory());

                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }
        });
        return listViewItems;
    }

    private void sendNotification(final String expenseAount)
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String send_email = null;

                    //This is a Simple Logic to Send Notification different Device Programmatically....
                    if (DatabaseValues.getUserLoginId().equalsIgnoreCase(DatabaseValues.getYOURNAME())){
                        send_email = DatabaseValues.getPARTNERNAME();
                    }
                    else if(DatabaseValues.getUserLoginId().equalsIgnoreCase(DatabaseValues.getPARTNERNAME())){
                        send_email = DatabaseValues.getYOURNAME();
                    }


                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic MGVmMDE5MGMtYTdjNS00ODQ2LWJkYjEtYTNmYThhYzczMjI3");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"643ce42f-8baf-41f7-b158-a11beecb8c85\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_Id\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"Amt. "+expenseAount+" deleted by "+ DatabaseValues.getUserDisplayName()+" \"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }
}
