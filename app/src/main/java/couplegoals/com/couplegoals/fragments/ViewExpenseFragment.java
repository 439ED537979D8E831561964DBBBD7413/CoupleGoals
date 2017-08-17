package couplegoals.com.couplegoals.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import couplegoals.com.couplegoals.R;
import couplegoals.com.couplegoals.adapter.ExpenseListAdapter;
import couplegoals.com.couplegoals.database.DatabaseValues;
import couplegoals.com.couplegoals.model.Expense;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class ViewExpenseFragment extends Fragment {

    ListView listViewCoupleExpense;
    TextView textViewTotalExpense;
    ImageButton btnShare;

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
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createExcelSheet();
            }
        });
        return viewExpenseFragment;
    }

    private void createExcelSheet()
    {
        String Fnamexls="ExpenseDetails"+"_"+ DatabaseValues.getCOUPLENAME()+System.currentTimeMillis()+ ".xls";
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/"+DatabaseValues.getCOUPLENAME());
        directory.mkdirs();
        File file = new File(directory, Fnamexls);

        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook = null;
        try {
            int a = 1;
            try {
                workbook = Workbook.createWorkbook(file, wbSettings);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //workbook.createSheet("Report", 0);

            WritableSheet sheet = workbook.createSheet(DatabaseValues.getCOUPLENAME(), 0);
            Label labelAmount = new Label(0,0,"Amount");
            Label labelPaidBy = new Label(1,0,"PaidBy");
            Label labelNotes = new Label(2,0,"Notes");
            Label labelWhen = new Label(3,0,"Date");
            Double totalAmount = 0.0;

            try {
                sheet.addCell(labelAmount);
                sheet.addCell(labelPaidBy);
                sheet.addCell(labelNotes);
                sheet.addCell(labelWhen);
                for (int i =0;i<expenseList.size();i++){
                    expenseList.get(i).getsAmount();
                    //Toast.makeText(getActivity(),"Amount Name"+expenseList.get(i).getsAmount(),Toast.LENGTH_SHORT ).show();
                    labelAmount = new Label(0, i+1, expenseList.get(i).getsAmount());
                    labelPaidBy = new Label(1, i+1, expenseList.get(i).getsPaidBy());
                    labelNotes = new Label(2, i+1, expenseList.get(i).getsNotes());
                    labelWhen = new Label(3, i+1, expenseList.get(i).getsWhen());
                    sheet.addCell(labelAmount);
                    sheet.addCell(labelPaidBy);
                    sheet.addCell(labelNotes);
                    sheet.addCell(labelWhen);
                    totalAmount = totalAmount+Double.valueOf(expenseList.get(i).getsAmount());
                }
                labelAmount = new Label(0,expenseList.size()+1,totalAmount.toString());
                sheet.addCell(labelAmount);

            } catch (RowsExceededException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            workbook.write();

            try {
                workbook.close();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //createExcel(excelSheet);
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("*/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile( file.getAbsoluteFile()));
            startActivity(Intent.createChooser(sharingIntent, "Share excel"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void loadCoupleExpenseDetailsFromDb() {
        databaseReference = DatabaseValues.getExpseDetailReference();
        databaseReference.keepSynced(true);
        final ExpenseListAdapter expenseListAdapter = new ExpenseListAdapter(getActivity(),expenseList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    expenseList.clear();
                    totalExpenseAmount = 0;
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
        btnShare = (ImageButton) viewExpenseFragment.findViewById(R.id.btnShare); 
        expenseList = new ArrayList<>();
    }

}
