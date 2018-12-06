package jack.com.jkutils.email;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jack.com.jkutils.R;

public class EmailHelper {

    public static void sendEmail(Context context, String[] to, String[] cc, String[] bcc, String subject , String content, ArrayList<File> attachments) {

        if (context == null) {
            return;
        }

        if (to == null && cc == null && bcc == null && subject == null && content == null && (attachments == null || attachments.size() == 0)) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);

        if (to != null) {
            intent.putExtra(Intent.EXTRA_EMAIL, to);
        }
        if (cc != null) {
            intent.putExtra(Intent.EXTRA_CC, cc);
        }
        if (bcc != null) {
            intent.putExtra(Intent.EXTRA_BCC, bcc);
        }
        if (content != null) {
            intent.putExtra(Intent.EXTRA_TEXT, content);
        }
        if (subject != null) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }

        if (attachments != null && attachments.size() > 0) {

            ArrayList<Uri> fileUris = new ArrayList<>();


            for (File f : attachments) {
                fileUris.add(Uri.parse(String.format("file://%s",f.getAbsolutePath())));
            }

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);

        }

        intent.setType("message/rfc882");
        Intent.createChooser(intent, "Choose Email Client");

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> appList = packageManager.queryIntentActivities(intent, 0);

        if (appList == null || appList.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.jk_email_no_email), Toast.LENGTH_LONG).show();
            return;
        }

        context.startActivity(intent);

    }

}
