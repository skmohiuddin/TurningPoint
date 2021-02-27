package com.turningpoint.contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class numberprofile extends AppCompatActivity {
    TextView nametxt;
    TextView numbertxt;
    String name;
    String  number;
    Toolbar toolbar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numberprofile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView nametxt = (TextView) findViewById(R.id.textView);
        TextView numbertxt = (TextView) findViewById(R.id.textView2);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("number");


            String[] separated = value.split(":");
             name = separated[0];
             number =separated[1];
            nametxt.setText(name);
            numbertxt.setText(number);
            //The key argument here must match that used in the other activity
        }

    }


    public void callnumber(View v){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        String temp = "tel:" + number;
        intent.setData(Uri.parse(temp));

        startActivity(intent);

    }
    public void save(View v){
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
        startActivity(intent);
    }


}