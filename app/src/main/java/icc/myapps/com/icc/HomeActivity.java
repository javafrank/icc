package icc.myapps.com.icc;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

/**
 * Created by frank on 10/23/16.
 */

public class HomeActivity extends AppCompatActivity {
    private TextView connectedText;
    private Button verifyButton;

    NotificationCompat.Builder mBuilder;

    // Create the Handler object (on the main thread by default)
    Handler handler = new Handler();
    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            verifyInternetConnection();
            // Repeat this the same runnable code block again another 2 seconds
            handler.postDelayed(runnableCode, 10000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        connectedText = (TextView) findViewById(R.id.connectedText);
        verifyButton = (Button) findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyInternetConnection();
            }
        });

        createAndDisplayNotification();

        // Start the initial runnable task by posting through the handler
        handler.post(runnableCode);

//        launchTestService();
        launchICCService();
    }

    public void launchTestService() {
        // Construct our Intent specifying the Service
        Intent i = new Intent(this, MyTestService.class);
        // Add extras to the bundle
        i.putExtra("foo", "bar");
        // Start the service
        startService(i);
    }

    public void launchICCService() {
        // Construct our Intent specifying the Service
        Intent i = new Intent(this, ICCService.class);
        // Add extras to the bundle
        i.putExtra("foo", "bar");
        // Start the service
        startService(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelNotification();
    }

    private void verifyInternetConnection() {
        connectedText.setText(getResources().getString(R.string.verifying));
        connectedText.setTextColor(getResources().getColor(R.color.verifying));
        new MyAsyncTask().execute();
    }

    private void createAndDisplayNotification() {
        mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_on)
                        .setContentTitle(getResources().getString(R.string.icc_status))
                        .setContentText(getResources().getString(R.string.connected))
                        .setOngoing(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, HomeActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(HomeActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void updateNotification(boolean connected) {
        int textId = (connected) ? R.string.connected : R.string.disconnected;
        int iconId = (connected) ? R.drawable.ic_on : R.drawable.ic_off;

        mBuilder.setContentText(getString(textId));
        mBuilder.setSmallIcon(iconId);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(
                0,
                mBuilder.build());
    }

    private void cancelNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);
    }

    class MyAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return isOnline();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                connectedText.setText(getResources().getString(R.string.connected));
                connectedText.setTextColor(getResources().getColor(R.color.connected));
            } else {
                connectedText.setText(getResources().getString(R.string.disconnected));
                connectedText.setTextColor(getResources().getColor(R.color.disconnected));
            }
            updateNotification(aBoolean);
        }

        public boolean isOnline() {
            try {
                final URL url = new URL("http://www.google.com");
                final URLConnection conn = url.openConnection();
                conn.connect();
                return true;
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                return false;
            }
        }
    }
}
