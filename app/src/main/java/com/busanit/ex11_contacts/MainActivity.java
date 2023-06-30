package com.busanit.ex11_contacts;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
        checkPermissions(permissions);

        textView = findViewById(R.id.textView);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseContacts();
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode()==RESULT_OK){
                            Intent intent = result.getData();
                            Uri contactsUri = intent.getData();
                            String id = contactsUri.getLastPathSegment();
                            getContacts(id);
                        }
                    }
                });
    }

    @SuppressLint("Range")
    private void getContacts(String id) {
        Cursor cursor = null;
        String name = "";
//        query(데이터를 가져올 주소, 가져올 컬럼, where절, where절 값, 정렬)
        cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,null,ContactsContract.Data.CONTACT_ID+"=?",
                new String[]{id},null);

        if (cursor.moveToFirst()){
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            String tel = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA1));
            println("Name : "+name+", Tel : "+tel);

            String[] columns = cursor.getColumnNames();
            for(String column:columns){
                int index = cursor.getColumnIndex(column);
                String columnOutput = ("#"+index+" -> ["+column+"] "+cursor.getString(index));
                println(columnOutput);
            }
            cursor.close();
        }
    }

    public void println(String data){
        textView.append(data+"\n");
    }

    private void chooseContacts() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        activityResultLauncher.launch(contactPickerIntent);

    }

    //위험 권한 부여
    private void checkPermissions(String[] permissions) {
        ArrayList<String> targetList = new ArrayList<String>();
        for(int i=0; i<permissions.length;i++){
            String curPermission = permissions[i];
            int permissionCheck = ContextCompat.checkSelfPermission(this,curPermission);
            if(permissionCheck== PackageManager.PERMISSION_GRANTED){
            } else {
                targetList.add(curPermission);
            }
        }
        if (targetList!=null && targetList.size()!=0){
            String[] targets = new String[targetList.size()];
            targetList.toArray(targets);
            ActivityCompat.requestPermissions(this, targets,101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}