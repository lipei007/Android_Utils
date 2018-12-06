package jack.com.jkutils.map;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.widget.Toast;

import jack.com.jkutils.R;

public class MapHelper {

    public static void navigate(Context context, String street) {

        if (context == null || street == null) {
            return;
        }

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + street + "&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            Toast.makeText(context, context.getString(R.string.jk_map_alert_no_google_map), Toast.LENGTH_LONG).show();
        }
    }

    public static void showMapOnLocation(Context context, Location location) {

        if (context == null || location == null) {
            return;
        }

        Uri gmmIntentUri = Uri.parse("geo:" + location.getLatitude() + "," + location.getLongitude() + "?mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            Toast.makeText(context, context.getString(R.string.jk_map_alert_no_google_map), Toast.LENGTH_LONG).show();
        }

    }

}
