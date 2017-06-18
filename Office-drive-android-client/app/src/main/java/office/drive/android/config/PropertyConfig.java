package office.drive.android.config;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import office.drive.android.R;

/**
 * Created by NPOST on 2017-06-16.
 */

public final class PropertyConfig {

    private static final String TAG = "PropertyConfig";

    public static String getConfigValue(Context context, String key) {

        Resources resources = context.getResources();

        try {

            InputStream rawResource = resources.openRawResource(R.raw.application);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(key);

        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the application file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open application file.");
        }

        return null;
    }
}
