package couplegoals.com.couplegoals.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import couplegoals.com.couplegoals.R;
import couplegoals.com.couplegoals.database.DatabaseValues;
import couplegoals.com.couplegoals.model.Expense;
import couplegoals.com.couplegoals.utility.Utility;

import static android.app.Activity.RESULT_OK;


public class PersonalExpenseFragment extends Fragment {

    //.........STATIC VARIABLE DECLARATION.......................//

    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_FILE = 2000;

    //.........UI VARIABLE DECLARATION.......................//

    Button btSavePersonalExpenseDetails;
    ImageButton btnPersonalUploadImage;
    ImageView imageViewPersonalExpense;
    EditText etPersonalAmount,etPersonalNotes;

    Uri imageViewPersonalExpenseUri;
    //.........VARIABLE DECLARATION.......................//

    String sExpensePersonalAmount,
            sExpensePersonalNotes,
            sExpensePersonalImageFilePath,
            sExpensePersonalId,sPersonalDateToday;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewPersonalExpenseFragment = inflater.inflate(R.layout.fragment_personal_expense, container, false);
        //.....................INITIALIZE UI COMPONENTS............................................//

        initializeUiComponents(viewPersonalExpenseFragment);

        //...................END...................................................................//

        //..................CLICK LISTENERS FOR UPLOADING IMAGES...................................//

        btnPersonalUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        //...................END...................................................................//

        //..................CLICK LISTENERS FOR SAVING EXPENSE DETAILS IN FIREBASE.................//
        btSavePersonalExpenseDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpenseDetailsToDatabase();
            }
        });
        //...................END...................................................................//
        return viewPersonalExpenseFragment;
    }

    private void saveExpenseDetailsToDatabase() {
        getUserEnteredValues();
        if(validateUserEnterData()){
            saveExpenseImageToDatabase();
        }
    }
    private void getUserEnteredValues() {
        sExpensePersonalAmount = etPersonalAmount.getText().toString().trim();
        sExpensePersonalNotes = etPersonalNotes.getText().toString().trim();
        if (sExpensePersonalNotes.isEmpty() || sExpensePersonalNotes.equalsIgnoreCase(null)){
            sExpensePersonalNotes = getString(R.string.No_notes_label);
        }
    }
    private void saveExpenseImageToDatabase() {
        if (imageViewPersonalExpenseUri!=null) {
            final StorageReference filePathCU = DatabaseValues.getStorageReference().child(DatabaseValues.getCOUPLENAME()).child(DatabaseValues.getCOUPLENAME()+"_"+Utility.getUniqueDateTime());
            filePathCU.putFile(imageViewPersonalExpenseUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sExpensePersonalImageFilePath = String.valueOf(taskSnapshot.getMetadata().getDownloadUrl());
                    //Toast.makeText(getActivity(),"Photo uploaded to db" + sExpenseImageFilePath,Toast.LENGTH_LONG).show();
                    processExpenseDataToDb();
                }
            });
        }
        else {
            sExpensePersonalImageFilePath = DatabaseValues.getProfilePicturePath();
            processExpenseDataToDb();
        }
    }
    private void processExpenseDataToDb() {
        sExpensePersonalId = Utility.getUniqueDateTime();
        Expense expense = new Expense(sExpensePersonalId,DatabaseValues.getCOUPLENAME(),sExpensePersonalAmount,sExpensePersonalNotes,DatabaseValues.getUserDisplayName(),sPersonalDateToday,sExpensePersonalImageFilePath);
        DatabaseReference databaseReference = DatabaseValues.getExpensePersonalDetailReference();
        databaseReference.child(sExpensePersonalId).setValue(expense).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getActivity(),"Added ",Toast.LENGTH_LONG).show();
                    //sendNotification();
                    resetUiComponents();
//                    fragment = new ViewExpenseFragment();
//                    if (fragment !=null){
//                        fragmentTransaction = getFragmentManager().beginTransaction();
//                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
//                        fragmentTransaction.replace(R.id.content_frame,fragment);
//                        fragmentTransaction.commit();
//                        fragmentTransaction.addToBackStack("base");
//
//                    }

                }else {
                    Toast.makeText(getActivity(),"Failed to update " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });

    }
    private void resetUiComponents() {
        etPersonalAmount.setText("");
        etPersonalNotes.setText("");
        imageViewPersonalExpense.setImageResource(android.R.color.transparent);
    }

    private boolean validateUserEnterData() {
        boolean isValid = true;
        View focusView = null;
        if(sExpensePersonalAmount.isEmpty()){
            etPersonalAmount.setError("Amount is required");
            isValid = false;
            focusView = etPersonalAmount;
        }
        else if(Double.parseDouble(sExpensePersonalAmount)<=0){
            etPersonalAmount.setError("Amount is zero");
            isValid = false;
            focusView = etPersonalAmount;
        }
        return isValid;
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
                imageViewPersonalExpenseUri = data.getData();
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageViewPersonalExpense.setImageBitmap(bm);
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
            imageViewPersonalExpenseUri = Uri.fromFile(destination);
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageViewPersonalExpense.setImageBitmap(thumbnail);
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

    private void initializeUiComponents(View viewPersonalExpenseFragment) {
        btSavePersonalExpenseDetails = (Button) viewPersonalExpenseFragment.findViewById(R.id.btSavePersonalExpenseDetails);
        btnPersonalUploadImage = (ImageButton) viewPersonalExpenseFragment.findViewById(R.id.btnPersonalUploadImage);
        imageViewPersonalExpense = (ImageView) viewPersonalExpenseFragment.findViewById(R.id.imageViewPersonalExpense);
        etPersonalAmount = (EditText) viewPersonalExpenseFragment.findViewById(R.id.etPersonalAmount);
        etPersonalNotes = (EditText) viewPersonalExpenseFragment.findViewById(R.id.etPersonalNotes);

        setFiltersForAmount();
        
    }
    private void setFiltersForAmount() {
        etPersonalAmount.setFilters(new InputFilter[]{new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
            int beforeDecimal = 5, afterDecimal = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                String temp = etPersonalAmount.getText() + source.toString();

                if (temp.equals(".")) {
                    return "0.";
                }
                else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        etPersonalAmount.setError("maximum 5 digits ");
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

}
