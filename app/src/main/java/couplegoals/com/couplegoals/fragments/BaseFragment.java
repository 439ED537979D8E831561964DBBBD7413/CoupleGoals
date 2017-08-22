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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import couplegoals.com.couplegoals.R;
import couplegoals.com.couplegoals.adapter.PostListAdapter;
import couplegoals.com.couplegoals.database.DatabaseValues;
import couplegoals.com.couplegoals.model.Post;
import couplegoals.com.couplegoals.utility.Utility;

import static android.app.Activity.RESULT_OK;

public class BaseFragment extends Fragment {


    //.........STATIC VARIABLE DECLARATION.......................//

    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_FILE = 2000;

    EditText etTodaysPostMessage;
    Button btnPost;
    ImageButton imageBtnTodayPost;
    ImageView imageViewTodayPost;

    Uri imageViewTodayPostUri;

    String sTodayPostId,sDateToday,sTodaysPostMessage,sTodaysPostImagePath;

    //......VIEW EXPENSES VARIABLES ....................................//

    ListView listViewTodaysPost;
    DatabaseReference databaseReference;
    List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewBaseFragment = inflater.inflate(R.layout.fragment_base, container, false);

        initializeUIComponents(viewBaseFragment);

        //..................CLICK LISTENERS FOR UPLOADING IMAGES...................................//

        imageBtnTodayPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        //...................END...................................................................//

        //..................CLICK LISTENERS FOR SAVING EXPENSE DETAILS IN FIREBASE.................//
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePostDetailsToDatabase();
            }
        });
        loadTodaysPostDetailsFromDb();
        return viewBaseFragment;
    }

    private void savePostDetailsToDatabase() {
        getUserEnteredValues();
        saveTodayPostImageToDatabase();
    }

    private void saveTodayPostImageToDatabase() {
        if (imageViewTodayPostUri!=null) {
            final StorageReference filePathCU = DatabaseValues.getStorageReference().child(DatabaseValues.getCOUPLENAME()).child(DatabaseValues.getCOUPLENAME()+"_"+sDateToday);
            filePathCU.putFile(imageViewTodayPostUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sTodaysPostImagePath = String.valueOf(taskSnapshot.getMetadata().getDownloadUrl());
                    //Toast.makeText(getActivity(),"Photo uploaded to db" + sExpenseImageFilePath,Toast.LENGTH_LONG).show();
                    processTodayPostDataToDb();
                }
            });
        }
        else {
            sTodaysPostImagePath = DatabaseValues.getProfilePicturePath();
            processTodayPostDataToDb();
        }
    }

    private void processTodayPostDataToDb() {
        DatabaseReference databaseReference = DatabaseValues.getPostDetailReference();
        sTodayPostId = databaseReference.push().getKey();
        Post todayPost = new Post(sTodayPostId,DatabaseValues.getCOUPLENAME(),sTodaysPostMessage,DatabaseValues.getUserDisplayName(),sDateToday,sTodaysPostImagePath);

        databaseReference.child(sTodayPostId).setValue(todayPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getActivity(),"Posted ",Toast.LENGTH_LONG).show();
                    //sendNotification();
                    resetUiComponents();
                }else {
                    Toast.makeText(getActivity(),"Failed to update " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });
        databaseReference = null;
    }
    private void resetUiComponents() {
        etTodaysPostMessage.setText("");
        imageViewTodayPost.setImageResource(android.R.color.transparent);
    }



    private void getUserEnteredValues() {
        sTodaysPostMessage = etTodaysPostMessage.getText().toString().trim();
    }

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
                imageViewTodayPostUri = data.getData();
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageViewTodayPost.setImageBitmap(bm);
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
            imageViewTodayPostUri = Uri.fromFile(destination);
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageViewTodayPost.setImageBitmap(thumbnail);
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
    private void initializeUIComponents(View view) {
        listViewTodaysPost = (ListView) view.findViewById(R.id.listViewTodaysPost);
        etTodaysPostMessage = (EditText) view.findViewById(R.id.etTodaysPostMessage);
        btnPost = (Button) view.findViewById(R.id.btnPost);
        imageBtnTodayPost = (ImageButton) view.findViewById(R.id.imageBtnTodayPost);
        imageViewTodayPost = (ImageView) view.findViewById(R.id.imageViewTodayPost);

        postList = new ArrayList<>();

        sDateToday = Utility.getCurrentDateForUserDisplay();
    }
    private void loadTodaysPostDetailsFromDb() {
        databaseReference = DatabaseValues.getPostDetailReference();
        databaseReference.keepSynced(true);
        final PostListAdapter postListAdapter = new PostListAdapter(getActivity(),postList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    postList.clear();

                    for (DataSnapshot  expenseDetailSnapshot : dataSnapshot.getChildren()){
                        Post postDetails = expenseDetailSnapshot.getValue(Post.class);
                        if (postDetails.getsCoupleName()!= null){
                            if (postDetails.getsCoupleName().equalsIgnoreCase(DatabaseValues.getCOUPLENAME())){
                                postList.add(0, postDetails);
                                postListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    //Collections.reverse(postList);
                    listViewTodaysPost.post(new Runnable() {
                        @Override
                        public void run() {
                            listViewTodaysPost.smoothScrollToPosition(0);
                        }
                    });
                    listViewTodaysPost.setAdapter(postListAdapter);
                    postListAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getActivity(),"No post exist",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
