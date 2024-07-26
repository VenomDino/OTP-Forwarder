package android.service.sms.helpers;

import android.telephony.SmsManager;
import android.util.Log;

public class CustomMethods {

    private static final String TAG = "MADARA";

    public static boolean isNumericInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

//    ----------------------------------------------------------------------------------------------

}
