package couplegoals.com.couplegoals.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import couplegoals.com.couplegoals.HomeActivity;
import couplegoals.com.couplegoals.R;
import couplegoals.com.couplegoals.database.DatabaseValues;
import couplegoals.com.couplegoals.model.CoupleDetails;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //.........UI VARIABLE DECLARATION.......................//

    SignInButton btUserLogin;

    //.........GOOGLE SIGN IN VARIABLE DECLARATION.........//
    SignInButton googleSignInButon;
    GoogleApiClient mgoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    //.........GOOGLE SIGN IN VARIABLE DECLARATION.........//
    String sUserUid,sUserName;

    //.........VARIABLE RELATED TO PERSISTENCE.........//
    static boolean calledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //.....................DATABASE PERSISTENCE............................................//
        if(!calledAlready){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }
        if (FirebaseAuth.getInstance().getCurrentUser() !=null){
            checkIfCoupleDetailsUpdated(DatabaseValues.getUserUniqueId());
//            startActivity(new Intent(LoginActivity.this,HomeActivity.class) );
//            finish();
        }
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        //..................REQUEST RUN TIME PERMISSIONS..........................//
        checkRunTimePermission();

        //.....................INITIALIZE UI COMPONENTS............................................//
        initializeUiComponents();

        //...................END...................................................................//

        //..................CLICK LISTENERS FOR LOGIN..............................................//
        btUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();

            }
        });
        //...................END...................................................................//
    }

    private void initializeUiComponents() {
        btUserLogin = (SignInButton) findViewById(R.id.btUserLogin);
    }

    private void googleSignIn() {

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
                .build();
        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();
        btUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

    }
    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mgoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignResult(googleSignInResult);

        }
    }

    private void handleSignResult(GoogleSignInResult googleSignInResult) {

        if (googleSignInResult.isSuccess()){
            GoogleSignInAccount googleSignInAccount    = googleSignInResult.getSignInAccount();
            firebaseAuthWithGoogle(googleSignInAccount);
        }else{
            Toast.makeText(getApplicationContext(),"Problem with database please try after some time", Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {

        final AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        DatabaseValues.getFrirebaseInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Welcome " + DatabaseValues.getUserDisplayName(),
                                    Toast.LENGTH_SHORT).show();
                            //get user selected values

                            sUserUid = DatabaseValues.getUserUniqueId();
                            sUserName = DatabaseValues.getUserDisplayName();
                            checkIfCoupleDetailsUpdated(DatabaseValues.getUserUniqueId());
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkIfCoupleDetailsUpdated(final String sUid) {


        DatabaseReference databaseReference = DatabaseValues.getCoupleDetailReference();
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userExist = false;
                if (dataSnapshot.hasChildren()){
                    //dataSnapshot.get
                    //startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    //finish();
                    for (DataSnapshot  coupleDetails : dataSnapshot.getChildren()){

                        CoupleDetails coupleDetails1 = coupleDetails.getValue(CoupleDetails.class);
                        if (coupleDetails1.getsYourEmailId()!= null || coupleDetails1.getsPartnerEmailId()!=null ){

                            if (coupleDetails1
                                    .getsYourEmailId().
                                            equalsIgnoreCase(DatabaseValues.getUserLoginId())
                                    || coupleDetails1.getsPartnerEmailId().equalsIgnoreCase(DatabaseValues.getUserLoginId())){
                                OneSignal.sendTag("User_Id",DatabaseValues.getUserLoginId());
                                DatabaseValues.setCOUPLENAME(coupleDetails1.getsCoupleName());
                                DatabaseValues.setYOURNAME(coupleDetails1.getsYourEmailId());
                                DatabaseValues.setPARTNERNAME(coupleDetails1.getsPartnerEmailId());
                                DatabaseValues.setProfilePicturePath(coupleDetails1.getsCouplePicturePath());
                                sendNotification();
                                userExist = true;
                                break;
                            }
                            else
                            {
                                userExist = false;
                            }
                        }
                    }

                }
                if (userExist){
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this, "Update profile details.....",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, AddCoupleDetailsActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Database Error " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void checkRunTimePermission() {
        String[] permissionArrays = new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.SEND_SMS};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 11111);
        } else {
            // if already permition granted
            // PUT YOUR ACTION (Like Open cemara etc..)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermitted = false;
        boolean openActivityOnce = true;
        boolean openDialogOnce = true;
        if (requestCode == 11111) {

            for (int i = 0; i < grantResults.length; i++) {
                String permission = permissions[i];

                isPermitted = grantResults[i] == PackageManager.PERMISSION_GRANTED;

                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        //execute when 'never Ask Again' tick and permission dialog not show
                    } else {
                        if (openDialogOnce) {
                            //alertView();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
                    String send_email;

                    //This is a Simple Logic to Send Notification different Device Programmatically....
                    send_email = DatabaseValues.getUserLoginId();

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
                                + "\"contents\": {\"en\": \"Welcome to couple goals "+DatabaseValues.getUserDisplayName()+"\"}"
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
