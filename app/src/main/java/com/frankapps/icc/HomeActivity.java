package com.frankapps.icc;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by frank on 10/23/16.
 */

public class HomeActivity extends AppCompatActivity {
    private TextView connectedText;
    private Button verifyButton;
//    ICCService iccService;
    private BroadcastReceiver receiver;

//    private void reviewConnection() {
//        boolean online = iccService.isOnline();
//        updateUI(online);
//    }

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
        verifyButton.setEnabled(false);

//        verifyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            if (mBound) {
//                reviewConnection();
//            }
//            }
//        });

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
        new MyAsyncTask().execute();
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

    private void updateUI(Boolean aBoolean) {
        if (aBoolean) {
            connectedText.setText(getResources().getString(R.string.connected));
            connectedText.setTextColor(getResources().getColor(R.color.connected));
        } else {
            connectedText.setText(getResources().getString(R.string.disconnected));
            connectedText.setTextColor(getResources().getColor(R.color.disconnected));
        }
    }
}
