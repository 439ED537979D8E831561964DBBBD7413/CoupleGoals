package couplegoals.com.couplegoals.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import couplegoals.com.couplegoals.R;
import couplegoals.com.couplegoals.adapter.ExpenseListAdapter;
import couplegoals.com.couplegoals.database.DatabaseValues;
import couplegoals.com.couplegoals.model.Category;
import couplegoals.com.couplegoals.model.Expense;
import couplegoals.com.couplegoals.utility.Utility;

import static android.app.Activity.RESULT_OK;


public class AddExpenseFragment extends Fragment {

    //.........STATIC VARIABLE DECLARATION.......................//

    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_FILE = 2000;

    //.........UI VARIABLE DECLARATION.......................//

    Button btSaveExpenseDetails;
    ImageButton btnUploadImage;
    ImageView imageViewExpense;
    EditText etAmount,etNotes;
    Spinner spExpenseCategory;
    Switch switchPaidBy;

    //.........VARIABLE DECLARATION.......................//

    String sExpenseAmount,
            sExpenseNotes,
            sExpenseImageFilePath,
            sExpenseId,sDateToday,sExpenseCategory,sDisplayName,sPaidBy;
    Uri imageViewExpenseUri;

    //.........VARIABLE DECLARATION FOR FRAGMENTS.......................//
    Fragment fragment;
    FragmentTransaction fragmentTransaction;

    //......VIEW EXPENSES VARIABLES ....................................//

    ListView listViewCoupleExpense;
    TextView textViewTotalExpense;

    DatabaseReference databaseReference;
    List<Expense> expenseList;
    double totalExpenseAmount = 0;

    ArrayAdapter<String> adapterCategories;
    ProgressDialog progressBarAddExpense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //..............INFLATE THE LAYOUT FOR THIS FRAGMENT.......................................//

        View viewAddExpenseFragment = inflater.inflate(R.layout.fragment_add_expense, container, false);

        //...................END...................................................................//

        //.....................INITIALIZE UI COMPONENTS............................................//

        initializeUiComponents(viewAddExpenseFragment);

        //...................END...................................................................//

        //................SWITCH LISTERNER............................................................................
            switchPaidBy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked){

                        if (DatabaseValues.getUserLoginId().equals(DatabaseValues.getYOURMAILID())){
                            sDisplayName = DatabaseValues.getPARTNERDISPLAYNAME();
                            sPaidBy = DatabaseValues.getPARTNERMAILID();
                            switchPaidBy.setText(sDisplayName);
//                            Toast.makeText(getActivity(), "Paid By " + sPaidBy,
//                                    Toast.LENGTH_SHORT).show();
                        }
                        else if(DatabaseValues.getUserLoginId().equals(DatabaseValues.getPARTNERMAILID())){
                            sDisplayName = DatabaseValues.getYOURDISPLAYNAME();
                            sPaidBy = DatabaseValues.getYOURMAILID();
                            switchPaidBy.setText(sDisplayName);
                        }
                    }else {
//                        Toast.makeText(getActivity(), "The Switch is " + (isChecked ? "on" : "off"),
//                                Toast.LENGTH_SHORT).show();
                        if (DatabaseValues.getUserLoginId().equals(DatabaseValues.getYOURMAILID())){
                            sDisplayName = DatabaseValues.getYOURDISPLAYNAME();
                            sPaidBy = DatabaseValues.getYOURMAILID();
                            switchPaidBy.setText(sDisplayName);
                        }
                        else if(DatabaseValues.getUserLoginId().equals(DatabaseValues.getPARTNERMAILID())){
                            sDisplayName = DatabaseValues.getPARTNERDISPLAYNAME();
                            sPaidBy = DatabaseValues.getPARTNERMAILID();
                            switchPaidBy.setText(sDisplayName);
                        }
                    }
                }
            });

        //...................END...................................................................//

        //..................CLICK LISTENERS FOR UPLOADING IMAGES...................................//

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        //...................END...................................................................//

        //..................CLICK LISTENERS FOR SAVING EXPENSE DETAILS IN FIREBASE.................//
        btSaveExpenseDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpenseDetailsToDatabase();
            }
        });
        //...................END...................................................................//

        //..................METHOD TO SET ERROR TO NULL WHEN FOCUS RECEIVED........................//
        setErrorsToNullFocusedReceived();
        //...................END...................................................................//
        loadCoupleExpenseDetailsFromDb();
        return viewAddExpenseFragment;
    }

    private void setErrorsToNullFocusedReceived() {
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etAmount.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void saveExpenseDetailsToDatabase() {
        getUserEnteredValues();
        if(validateUserEnterData()){
            //Toast.makeText(getActivity(),"Save details to database" + sExpenseAmount+ "Notes" + sExpenseNotes,Toast.LENGTH_SHORT).show();
            //if image is selected
            saveExpenseImageToDatabase();

        }

    }

    private void processExpenseDataToDb() {
        progressBarAddExpense.setMessage("Adding expense....");
        progressBarAddExpense.show();
        DatabaseReference databaseReference = DatabaseValues.getExpseDetailReference();
        sExpenseId = databaseReference.push().getKey();
        Expense expense = new Expense(sExpenseId,DatabaseValues.getCOUPLENAME(),sExpenseAmount,sExpenseNotes,sPaidBy,sDateToday,sExpenseImageFilePath,sExpenseCategory,sDisplayName);
        databaseReference.child(sExpenseId).setValue(expense).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBarAddExpense.dismiss();
                    Toast.makeText(getActivity(),"Added ",Toast.LENGTH_LONG).show();
                    sendNotification();
                    resetUiComponents();

                }else {
                    progressBarAddExpense.dismiss();
                    Toast.makeText(getActivity(),"Failed to update " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });
        databaseReference = null;
    }
    private void resetUiComponents() {
        etAmount.setText("");
        etNotes.setText("");
        imageViewExpense.setImageResource(android.R.color.transparent);
    }


    private void saveExpenseImageToDatabase() {
        if (imageViewExpenseUri!=null) {
            final StorageReference filePathCU = DatabaseValues.getStorageReference().child(DatabaseValues.getCOUPLENAME()).child(DatabaseValues.getCOUPLENAME()+"_"+Utility.getUniqueDateTime());
            filePathCU.putFile(imageViewExpenseUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sExpenseImageFilePath = String.valueOf(taskSnapshot.getMetadata().getDownloadUrl());
                    //Toast.makeText(getActivity(),"Photo uploaded to db" + sExpenseImageFilePath,Toast.LENGTH_LONG).show();
                    processExpenseDataToDb();
                }
            });
        }
        else {
            sExpenseImageFilePath = DatabaseValues.getProfilePicturePath();
            processExpenseDataToDb();
        }
    }

    private void getUserEnteredValues() {
        sExpenseAmount = etAmount.getText().toString().trim();
        sExpenseNotes = etNotes.getText().toString().trim();
        if (sExpenseNotes.isEmpty() || sExpenseNotes.equalsIgnoreCase(null)){
            sExpenseNotes = getString(R.string.No_notes_label);
        }
        sExpenseCategory = spExpenseCategory.getSelectedItem().toString();
    }

    private boolean validateUserEnterData() {
        boolean isValid = true;
        View focusView = null;
        if(sExpenseAmount.isEmpty()){
            etAmount.setError("Amount is required");
            isValid = false;
            focusView = etAmount;
        }
        else if(Double.parseDouble(sExpenseAmount)<=0){
            etAmount.setError("Amount is zero");
            isValid = false;
            focusView = etAmount;
        }
        return isValid;
    }

    /*
    * Initialize UI components
    * */
    private void initializeUiComponents(View view){
        btnUploadImage = (ImageButton) view.findViewById(R.id.btnUploadImage);
        imageViewExpense = (ImageView) view.findViewById(R.id.imageViewExpense);
        etAmount = (EditText) view.findViewById(R.id.etAmount);
        etNotes = (EditText) view.findViewById(R.id.etNotes);
        btSaveExpenseDetails = (Button) view.findViewById(R.id.btSaveExpenseDetails);
        sDateToday = Utility.getCurrentDateForUserDisplay();
        listViewCoupleExpense = (ListView) view.findViewById(R.id.listViewCoupleExpense);
        textViewTotalExpense = (TextView) view.findViewById(R.id.textViewTotalExpense);
        expenseList = new ArrayList<>();
        switchPaidBy = (Switch) view.findViewById(R.id.switchPaidBy);

        List<String> categoryName = new ArrayList<>();

        for (int i = 0;i< DatabaseValues.getCategoryList().size();i++){
            categoryName.add(0,DatabaseValues.getCategoryList().get(i).getsCategoryName());
        }
        categoryName.add(0,getString(R.string.defaultcategory));
        adapterCategories = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,categoryName);
        spExpenseCategory = (Spinner) view.findViewById(R.id.spExpenseCategory);

        spExpenseCategory.setAdapter(adapterCategories);
        progressBarAddExpense = new ProgressDialog(getActivity());
        //Add filters for amount
        setFiltersForAmount();

        //Setting Default values
        if (DatabaseValues.getUserLoginId().equals(DatabaseValues.getYOURMAILID())){
            sDisplayName = DatabaseValues.getYOURDISPLAYNAME();
            sPaidBy = DatabaseValues.getYOURMAILID();
            switchPaidBy.setText(sDisplayName);
        }
        else if(DatabaseValues.getUserLoginId().equals(DatabaseValues.getPARTNERMAILID())){
            sDisplayName = DatabaseValues.getPARTNERDISPLAYNAME();
            sPaidBy = DatabaseValues.getPARTNERMAILID();
            switchPaidBy.setText(sDisplayName);
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
                    //Collections.reverse(expenseList);
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

    private void setFiltersForAmount() {
        etAmount.setFilters(new InputFilter[]{new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
            int beforeDecimal = 5, afterDecimal = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                String temp = etAmount.getText() + source.toString();

                if (temp.equals(".")) {
                    return "0.";
                }
                else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        etAmount.setError("maximum 5 digits ");
                        return "";
                    }
                } else {
                    temp = temp.substring(temp.indexOf(".") + 1);
                    if (temp.length() > afterDecimal) {
                        return "";
                    }
                }

                return super.filter(source, start, end, dest, dstart, dend);
            }
        }
        });
    }

    /*
    * Select image dailog box
    * */

    private void selectImage() {
        final CharSequence[] items = { getString(R.string.takephotolabel), getString(R.string.choosefromlibrarylabel),
                getString(R.string.cancellable) };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.addphotolable);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.takephotolabel))) {
                    checkPermissionForCamera();
                } else if (items[item].equals(getString(R.string.choosefromlibrarylabel))) {
                    checkPermissionForGallery();
                } else if (items[item].equals(getString(R.string.cancellable))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void checkPermissionForGallery() {
        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!Utility.hasPermissions(getActivity(), PERMISSIONS)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AppTheme);
            dialog.setTitle(PERMISSIONS+ "Permission Denied")
                    .setInverseBackgroundForced(true)
                    //.setIcon(R.drawable.ic_info_black_24dp)
                    .setMessage("Without this permission the app is unable to take picture from camera.Please give the necessary permissions by taping on OPEN SETTINGS")
                    .setPositiveButton("OPEN SETTINGS", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            dialoginterface.dismiss();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + getActivity().getPackageName()));
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }).show();
        }
        else {
            galleryIntent();
        }

    }

    private void checkPermissionForCamera() {
        String[] PERMISSIONS = {Manifest.permission.CAMERA};
        if (!Utility.hasPermissions(getActivity(), PERMISSIONS)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AppTheme);
            dialog.setTitle("CAMERA Permission Denied")
                    .setInverseBackgroundForced(true)
                    //.setIcon(R.drawable.ic_info_black_24dp)
                    .setMessage("Without this permission the app is unable to take picture from camera.Please give the necessary permissions by taping on OPEN SETTINGS")
                    .setPositiveButton("OPEN SETTINGS", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            dialoginterface.dismiss();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + getActivity().getPackageName()));
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }).show();
        }
        else
        {
            String[] PERMISSIONS1 = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!Utility.hasPermissions(getActivity(), PERMISSIONS1)) {
                Toast.makeText(getActivity(), "permission not givven", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AppTheme);

                dialog.setTitle("STORAGE Permission Denied")
                        .setInverseBackgroundForced(true)
                        //.setIcon(R.drawable.ic_info_black_24dp)
                        .setMessage("Without this permission the app is unable to store images taken from the camera.Please give the necessary permissions by taping on OPEN SETTINGS")
                        .setPositiveButton("OPEN SETTINGS", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.parse("package:" + getActivity().getPackageName()));
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).show();

            }
            else
            {
                cameraIntent();
            }
        }
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType(getString(R.string.imagetype));
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selectfilelabel)),SELECT_FILE);
    }
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                imageViewExpenseUri = data.getData();
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageViewExpense.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File root = new File(Environment.getExternalStorageDirectory()
                + File.separator + getString(R.string.CoupleGoalsLable) + File.separator);
        root.mkdirs();
        File destination = new File(root ,
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            imageViewExpenseUri = Uri.fromFile(destination);
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageViewExpense.setImageBitmap(thumbnail);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            onCaptureImageResult(data);
        }
        else if(requestCode == SELECT_FILE && resultCode == RESULT_OK){
            onSelectFromGalleryResult(data);
        }

    }
    private void sendNotification()
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
                                + "\"contents\": {\"en\": \"Amt. "+sExpenseAmount+" added by "+ DatabaseValues.getUserDisplayName()+" \"}"
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
