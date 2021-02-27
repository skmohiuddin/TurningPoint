package com.turningpoint.contact;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shamanland.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserActivty extends AppCompatActivity {

    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    private static final String TAG = "token";
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    ListView listView;
    ImageView img,img2;
    ArrayList<String> StoreContacts;

    ArrayAdapter<String> arrayAdapter;
    Cursor cursor;
    String name, phonenumber;
    public  static final int RequestPermissionCode  = 1;
    Button num,nam;
    FloatingActionButton showPopupBtn;
    Button     closePopupBtn;
    PopupWindow popupWindow;
    RelativeLayout linearLayout1;
    EditText searchTo;
    FirebaseUser user;
    String android_id;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int editetype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
         user = FirebaseAuth.getInstance().getCurrentUser();
        android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        onTokenRefresh();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.teal_700);
        num = (Button)findViewById(R.id.button);
        nam = (Button)findViewById(R.id.button2);
       nam.setVisibility(View.INVISIBLE);
       num.setVisibility(View.INVISIBLE);
        searchTo = (EditText)findViewById(R.id.editTextPhone);
        img=(ImageView)findViewById(R.id.imageView2);
        img2=(ImageView)findViewById(R.id.imageView3);
        searchTo.setVisibility(View.INVISIBLE);
        img.setVisibility(View.INVISIBLE);
        img2.setVisibility(View.INVISIBLE);
RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mSwipeRefreshLayout.getLayoutParams();
        params.setMargins(0, -100, 0, 0);

        mSwipeRefreshLayout.setLayoutParams(params);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference(android_id);

                scoresRef.keepSynced(true);
                someMethod();
            }



        });







        listView = (ListView)findViewById(R.id.listview1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text or do whatever you need.

                Object o = listView.getItemAtPosition(position);

                Intent intent = new Intent(getBaseContext(), numberprofile.class);
                intent.putExtra("number", o.toString());
                startActivity(intent);


            }
        });
        searchTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                arrayAdapter.clear();
                String st = searchTo.getText().toString();
                doSomething(st);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchTo.getText().toString().matches("")) {
                    arrayAdapter.clear();
                    GetContactsIntoArrayList();

                }




            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                Object o = listView.getItemAtPosition(pos);


                Toast.makeText(getApplicationContext(), "item  Long click", Toast.LENGTH_SHORT).show();



                return true;
            }
        });
        linearLayout1 = (RelativeLayout) findViewById(R.id.linearLayout1);




        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();





        StoreContacts = new ArrayList<String>();

        EnableRuntimePermission();



                GetContactsIntoArrayList();

                arrayAdapter = new ArrayAdapter<String>(
                        UserActivty.this,
                        R.layout.contact_items_listview,
                        R.id.textView, StoreContacts
                );

                listView.setAdapter(arrayAdapter);


            }


    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

    }
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    void someMethod() {

        Runnable task = new Runnable() {
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference(android_id);

                scoresRef.keepSynced(false);
            }
        };
        worker.schedule(task, 5, TimeUnit.SECONDS);

    }
public void choice (View v){
    nam.setVisibility(View.VISIBLE);
    num.setVisibility(View.VISIBLE);
}

    public void number (View v){
        nam.setVisibility(View.INVISIBLE);
        num.setVisibility(View.INVISIBLE);
        searchTo.setInputType(InputType.TYPE_CLASS_PHONE);

         editetype = 1;
    }
    public void name (View v){
        nam.setVisibility(View.INVISIBLE);
        num.setVisibility(View.INVISIBLE);
        searchTo.setInputType(InputType.TYPE_CLASS_TEXT);
        editetype = 2;
    }
public void search(View v){
    arrayAdapter.clear();
    String s = searchTo.getText().toString();
    doSomething(s);
}
    private void doSomething(CharSequence s) {



        String ss = s.toString().toLowerCase();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
ref.addChildEventListener(new ChildEventListener() {
    @Override
    public void onChildAdded(DataSnapshot snapshot,  String s) {
        for (DataSnapshot userSnapshot: snapshot.getChildren()) {



            if(editetype==2){

                String k = userSnapshot.getKey().toLowerCase().toString();

                if((k.startsWith(ss))){

                    arrayAdapter.notifyDataSetChanged();
                    arrayAdapter = new ArrayAdapter<String>(
                            UserActivty.this,
                            R.layout.contact_items_listview,
                            R.id.textView, StoreContacts



                    );

                    StoreContacts.add(userSnapshot.getKey().toString() + ":" + userSnapshot.getValue().toString());
                    Log.w("myApp", userSnapshot.getKey());

                    listView.setAdapter(arrayAdapter);

                }

            }else if(editetype==1) {

                if (userSnapshot.getValue().toString().startsWith(ss)) {


                    arrayAdapter = new ArrayAdapter<String>(
                            UserActivty.this,
                            R.layout.contact_items_listview,
                            R.id.textView, StoreContacts
                    );
                    StoreContacts.add(userSnapshot.getKey().toString() + ":" + userSnapshot.getValue().toString());
                    Log.w("myApp", userSnapshot.getKey());

                    listView.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();

                }}










        }}

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});


    }


    public void GetContactsIntoArrayList(){

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            String n = name.replace(".", "");
            String phonenumber2 = phonenumber.replace(" ", "");

            if (! StoreContacts.contains(n + " "  + ":" + " " + phonenumber2))
            {


                StoreContacts.add(name + " "  + ":" + " " + phonenumber2);
                // Write a message to the database

                FirebaseDatabase database = FirebaseDatabase.getInstance();

                DatabaseReference myRef = database.getReference(android_id).child(n);

                myRef.setValue(phonenumber2);


            }



        }

        cursor.close();

    }

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                UserActivty.this,
                Manifest.permission.READ_CONTACTS))
        {



        } else {

            ActivityCompat.requestPermissions(UserActivty.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                    Toast.makeText(UserActivty.this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item3:
                if(searchTo.getVisibility()== View.VISIBLE){
                searchTo.setVisibility(View.INVISIBLE);
                    img.setVisibility(View.INVISIBLE);
                    img2.setVisibility(View.INVISIBLE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mSwipeRefreshLayout.getLayoutParams();
                    params.setMargins(0, -100, 0, 0);
                    mSwipeRefreshLayout.setLayoutParams(params);


            }
            else if(searchTo.getVisibility()== View.INVISIBLE){
                searchTo.setVisibility(View.VISIBLE);
                    img.setVisibility(View.VISIBLE);
                    img2.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mSwipeRefreshLayout.getLayoutParams();
                    params.setMargins(0, 0, 0, 0);
                    mSwipeRefreshLayout.setLayoutParams(params);

// load the animation
                    Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animrtol);

// start the animation
                    searchTo.startAnimation(animFadein);
                }
                return true;

            case R.id.item1:
                Toast.makeText(getApplicationContext(),"LOG OUT",Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().signOut();
                Intent I = new Intent(UserActivty.this, ActivityLogin.class);
                startActivity(I);
                return true;
            case R.id.item2:
                if(item.isChecked())
                {
                    DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference(android_id);
                    scoresRef.keepSynced(true);
                    //logic is it is checked
                    item.setChecked(false);
                }
                else
                {
                    DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference(android_id);
                    contactRef.keepSynced(false);
                    //logic is it is not checked
                    item.setChecked(true);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void speakButtonClicked(View v)
    {
        startVoiceRecognitionActivity();
    }
    private static final int REQUEST_CODE = 1234;
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "SPEAK NOW");
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            editetype = 2;
            searchTo.setText(matches.get(0).toString());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}