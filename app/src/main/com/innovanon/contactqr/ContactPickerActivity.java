package com.innovanon.contactqr;
  
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
  
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
  
public class ContactPickerActivity extends AppCompatActivity
{
    private static final int REQUEST_READ_CONTACTS_PERMISSION = 0;
    private static final int REQUEST_CONTACT = 1;
  
    private Button mContactPick;
    private Button mContactName;
  
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  
        if (requestCode == REQUEST_READ_CONTACTS_PERMISSION && grantResults.length > 0)
        {
            updateButton(grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }
  
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
  
        if (resultCode != Activity.RESULT_OK) return;
  
        if (requestCode == REQUEST_CONTACT && data != null)
        {
            Uri contactUri = data.getData();
  
            // Specify which fields you want your
            // query to return values for
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
  
            // Perform your query - the contactUri 
            // is like a "where" clause here
            Cursor cursor = this.getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            try
            {
                // Double-check that you 
                // actually got results
                if (cursor.getCount() == 0) return;
  
                // Pull out the first column 
                // of the first row of data 
                // that is your contact's name
                cursor.moveToFirst();
  
                String name = cursor.getString(0);
                mContactName.setText(name);
  
            }
            finally
            {
                cursor.close();
            }
        }
    }
  
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);
  
        // Intent to pick contacts
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
  
        mContactPick = findViewById(R.id.contact_pick);
        mContactName = findViewById(R.id.contact_name);
  
        mContactPick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
  
        requestContactsPermission();
        updateButton(hasContactsPermission());
    }
  
    public void updateButton(boolean enable)
    {
        mContactPick.setEnabled(enable);
        mContactName.setEnabled(enable);
    }
  
    private boolean hasContactsPermission()
    {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED;
    }
  
    // Request contact permission if it
    // has not been granted already
    private void requestContactsPermission()
    {
        if (!hasContactsPermission())
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS_PERMISSION);
        }
    }
}

