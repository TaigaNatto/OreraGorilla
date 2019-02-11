package natto.com.oreragorilla;

import android.annotation.SuppressLint;
import android.content.Context;

public class SystemRepository {
    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        return android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }
}
