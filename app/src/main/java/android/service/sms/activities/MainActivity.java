package android.service.sms.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.sms.R;
import android.service.sms.helpers.SharedPrefHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

                new Handler(Looper.getMainLooper()).postDelayed(this::finishAndRemoveTask, 2000);
            } else {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}