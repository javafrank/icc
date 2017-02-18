package com.frankapps.icc;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by frank on 10/23/16.
 */

public class HomeActivity extends AppCompatActivity {
    private TextView connectedText;
    private Button verifyButton;
//    ICCService iccService;
    private BroadcastReceiver receiver;
    private NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isOnline = intent.getBooleanExtra(ICCService.ONLINE_MESSAGE, false);
                updateUI(isOnline);
            }
        };

        connectedText = (TextView) findViewById(R.id.connectedText);
        verifyButton = (Button) findViewById(R.id.verifyButton);
//        verifyButton.setEnabled(false);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyAsyncTask2().execute();
            }
        });

        launchICCService();
    }

    @Override
        protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(ICCService.ONLINE_RESULT)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    public void launchICCService() {
        // Construct our Intent specifying the Service
        Intent i = new Intent(this, ICCService.class);
        // Add extras to the bundle
//        i.putExtra("foo", "bar");
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
//        new MyAsyncTask().execute();
    }

    private void cancelNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);
    }

    class MyAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            updateUI(aBoolean);
//            updateNotification(aBoolean);
        }
    }

    class MyAsyncTask2 extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            verifyInternetConnection();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return reviewConnection();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            updateUI(aBoolean);
//            updateNotification(aBoolean);
        }
    }

    private void updateUI(Boolean aBoolean) {
        if (aBoolean) {
            connectedText.setText(getResources().getString(R.string.connected));
            connectedText.setTextColor(getResources().getColor(R.color.connected));
        } else {
            connectedText.setText(getResources().getString(R.string.disconnected));
            connectedText.setTextColor(getResources().getColor(R.color.disconnected));
        }
    }

    private boolean reviewConnection() {
        boolean online = isOnline();
        Log.i("HomeActivity", "hay conexion? (desde activity) " + online);
//        sendResult(online);
        return online;
    }

    private boolean isOnline() {
        try {
            final URL url = new URL("http://google.com/");
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);
                String strLine = null;
                StringBuilder response = new StringBuilder();
                while ((strLine = input.readLine()) != null)
                {
                    response.append(strLine);
                }
                input.close();

                if (response.toString().length() > 346) {
                    return true;
                }
            }
            return false;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
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
}
