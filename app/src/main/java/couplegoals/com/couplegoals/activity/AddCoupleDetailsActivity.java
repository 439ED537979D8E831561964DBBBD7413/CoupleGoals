package couplegoals.com.couplegoals.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Date;

import couplegoals.com.couplegoals.HomeActivity;
import couplegoals.com.couplegoals.R;
import couplegoals.com.couplegoals.database.DatabaseValues;
import couplegoals.com.couplegoals.model.CoupleDetails;
import couplegoals.com.couplegoals.utility.Utility;

public class AddCoupleDetailsActivity extends AppCompatActivity {

    //.........STATIC VARIABLE DECLARATION.......................//

    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_FILE = 2000;

    //.........UI VARIABLE DECLARATION.......................//
    Button btSaveCoupleDetails;
    EditText etCoupleName,etYourMobileNo,etPartnerNumber,etPartnerEmailId;
    Button btnUploadCoupleImage;
    ImageView imageCouple;

    //.........USER ENTERED VALUES -VARIABLE DECLARATION.......................//
    String sCoupleName,sYourNumber,sPartnerNumber,sCoupleImageFilePath,sPartnerEmailId;
    Uri imageCoupleUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTitle("Invite your better half");
        setContentView(R.layout.activity_add_couple_details);

        //.....................INITIALIZE UI COMPONENTS............................................//
        initializeUiComponents();
        //...................END...................................................................//

        //..................CLICK LISTENERS FOR UPLOADING IMAGES...................................//
        btnUploadCoupleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        //...................END...................................................................//
        //..................CLICK LISTENERS FOR SAVING COUPLE DETAILS IN FIREBASE.................//
        btSaveCoupleDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCoupleDetailsToDatabase();
            }
        });
        //...................END...................................................................//
    }


    /*
    * Select image dailog box
    * */

    private void selectImage() {
        final CharSequence[] items = { getString(R.string.takephotolabel), getString(R.string.choosefromlibrarylabel),
                getString(R.string.cancellable) };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddCoupleDetailsActivity.this);
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
        if (!Utility.hasPermissions(AddCoupleDetailsActivity.this, PERMISSIONS)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(AddCoupleDetailsActivity.this, R.style.AppTheme);
            dialog.setTitle(PERMISSIONS+ "Permission Denied")
                    .setInverseBackgroundForced(true)
                    //.setIcon(R.drawable.ic_info_black_24dp)
                    .setMessage("Without this permission the app is unable to take picture from camera.Please give the necessary permissions by taping on OPEN SETTINGS")
                    .setPositiveButton("OPEN SETTINGS", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            dialoginterface.dismiss();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + AddCoupleDetailsActivity.this.getPackageName()));
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
        if (!Utility.hasPermissions(AddCoupleDetailsActivity.this, PERMISSIONS)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(AddCoupleDetailsActivity.this, R.style.AppTheme);
            dialog.setTitle("CAMERA Permission Denied")
                    .setInverseBackgroundForced(true)
                    //.setIcon(R.drawable.ic_info_black_24dp)
                    .setMessage("Without this permission the app is unable to take picture from camera.Please give the necessary permissions by taping on OPEN SETTINGS")
                    .setPositiveButton("OPEN SETTINGS", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            dialoginterface.dismiss();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + AddCoupleDetailsActivity.this.getPackageName()));
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }).show();
        }
        else
        {
            String[] PERMISSIONS1 = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!Utility.hasPermissions(AddCoupleDetailsActivity.this, PERMISSIONS1)) {
                Toast.makeText(AddCoupleDetailsActivity.this, "permission not givven", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder dialog = new AlertDialog.Builder(AddCoupleDetailsActivity.this, R.style.AppTheme);

                dialog.setTitle("STORAGE Permission Denied")
                        .setInverseBackgroundForced(true)
                        //.setIcon(R.drawable.ic_info_black_24dp)
                        .setMessage("Without this permission the app is unable to store images taken from the camera.Please give the necessary permissions by taping on OPEN SETTINGS")
                        .setPositiveButton("OPEN SETTINGS", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.parse("package:" + AddCoupleDetailsActivity.this.getPackageName()));
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
                imageCoupleUri = data.getData();
                bm = MediaStore.Images.Media.getBitmap(AddCoupleDetailsActivity.this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageCouple.setImageBitmap(bm);
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
            imageCoupleUri = Uri.fromFile(destination);
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageCouple.setImageBitmap(thumbnail);
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


    private void initializeUiComponents() {
        btSaveCoupleDetails = (Button) findViewById(R.id.btSaveCoupleDetails);
        etCoupleName = (EditText) findViewById(R.id.etCoupleName);
        etYourMobileNo = (EditText) findViewById(R.id.etYourMobileNo);
        etPartnerNumber = (EditText) findViewById(R.id.etPartnerNumber);
        btnUploadCoupleImage = (Button) findViewById(R.id.btnUploadCoupleImage);
        imageCouple = (ImageView) findViewById(R.id.imageCouple);
        etPartnerEmailId = (EditText) findViewById(R.id.etPartnerEmailId);
        setFiltersForYourMobile();
        setFiltersForPartnerMobile();
    }

    private void setFiltersForYourMobile() {
        etYourMobileNo.setFilters(new InputFilter[]{new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
            int beforeDecimal = 10, afterDecimal = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                String temp = etYourMobileNo.getText() + source.toString();

                if (temp.equals(".")) {
                    return "0.";
                }
                else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        etYourMobileNo.setError("maximum 10 digits ");
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
    private void setFiltersForPartnerMobile() {
        etPartnerNumber.setFilters(new InputFilter[]{new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
            int beforeDecimal = 10, afterDecimal = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                String temp = etPartnerNumber.getText() + source.toString();

                if (temp.equals(".")) {
                    return "0.";
                }
                else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        etPartnerNumber.setError("maximum 10 digits ");
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

    private void saveCoupleDetailsToDatabase() {
        getUserEnteredValues();
        if(validateUserEnterData()){
            Toast.makeText(AddCoupleDetailsActivity.this,"Save details to database" + sCoupleName+ "Notes" + sYourNumber + " "+ sPartnerNumber,Toast.LENGTH_SHORT).show();
            saveCoupleImageToDatabase();
            //processCoupleDetailsToDb();
        }
        //startActivity(new Intent(AddCoupleDetailsActivity.this, HomeActivity.class));
    }

    private void processCoupleDetailsToDb() {

        CoupleDetails coupleDetails = new CoupleDetails(sCoupleName,sYourNumber,sPartnerNumber,sCoupleImageFilePath,DatabaseValues.getUserLoginId(),sPartnerEmailId);
        DatabaseReference databaseReference = DatabaseValues.getCoupleDetailReference();
        databaseReference.child(sCoupleName).setValue(coupleDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AddCoupleDetailsActivity.this,"Updated Successfully ",Toast.LENGTH_LONG).show();
                    setReferenveValues();
                    startActivity(new Intent(AddCoupleDetailsActivity.this, HomeActivity.class));
                    finish();
                }else {
                    Toast.makeText(AddCoupleDetailsActivity.this,"Failed to update " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void setReferenveValues() {
        DatabaseValues.setCOUPLENAME(sCoupleName);
        DatabaseValues.setYOURNAME(DatabaseValues.getUserLoginId());
        DatabaseValues.setPARTNERNAME(sPartnerEmailId);
        DatabaseValues.setProfilePicturePath(sCoupleImageFilePath);
    }

    private void saveCoupleImageToDatabase() {
        if (imageCoupleUri!=null) {
            final StorageReference filePathCU = DatabaseValues.getStorageReference().child(sCoupleName).child(sCoupleName);
            filePathCU.putFile(imageCoupleUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sCoupleImageFilePath = String.valueOf(taskSnapshot.getMetadata().getDownloadUrl());
                    //Toast.makeText(AddCoupleDetailsActivity.this,"Photo uploaded to db" + sCoupleImageFilePath,Toast.LENGTH_LONG).show();
                    processCoupleDetailsToDb();
                }
            });
        }
        else {
            sCoupleImageFilePath = getString(R.string.noimagelable);
            processCoupleDetailsToDb();
        }
    }

    private boolean validateUserEnterData() {
        boolean isValid = true;
        View focusView = null;
        if(sCoupleName.isEmpty()){
            etCoupleName.setError("Couple name is required");
            isValid = false;
            focusView = etCoupleName;
        }
        else if(sCoupleName.length()<4){
            etCoupleName.setError("Enter more than 4 character");
            isValid = false;
            focusView = etCoupleName;
        }
        else if(sYourNumber.isEmpty()){
            etYourMobileNo.setError("Your number is required");
            isValid = false;
            focusView = etYourMobileNo;
        }
        else if(sYourNumber.length()<10){
            etYourMobileNo.setError("Enter valid number");
            isValid = false;
            focusView = etYourMobileNo;
        }
        else if(sPartnerNumber.isEmpty()){
            etPartnerNumber.setError("Partner number is required");
            isValid = false;
            focusView = etPartnerNumber;
        }
        else if(sPartnerNumber.length()<10){
            etPartnerNumber.setError("Enter valid number");
            isValid = false;
            focusView = etPartnerNumber;
        }
        else if(sPartnerEmailId.isEmpty()){
            etPartnerEmailId.setError("Email id required");
            isValid = false;
            focusView = etPartnerEmailId;
        }
        return isValid;
    }

    private void getUserEnteredValues() {
        sCoupleName = etCoupleName.getText().toString().trim();
        sYourNumber = etYourMobileNo.getText().toString().trim();
        sPartnerNumber = etPartnerNumber.getText().toString().trim();
        sPartnerEmailId = etPartnerEmailId.getText().toString().trim();
    }

}
