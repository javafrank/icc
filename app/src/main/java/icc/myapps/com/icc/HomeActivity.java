package icc.myapps.com.icc;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by frank on 10/23/16.
 */

public class HomeActivity extends AppCompatActivity {
    private TextView connectedText;
    private Button verifyButton;
    ICCService iccService;
    boolean mBound = false;

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

        final Context context = this;
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                verifyInternetConnection();
                if (mBound) {
                    // Call a method from the LocalService.
                    // However, if this call were something that might hang, then this request should
                    // occur in a separate thread to avoid slowing down the activity performance.
                    Toast.makeText(context, "connection? " + iccService.isOnline(), Toast.LENGTH_SHORT).show();
                }
            }
        });

//        createAndDisplayNotification();

        // Start the initial runnable task by posting through the handler
//        handler.post(runnableCode);

//        launchTestService();
        launchICCService();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Bind to LocalService
        Intent intent = new Intent(this, ICCService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ICCService.LocalBinder binder = (ICCService.LocalBinder) service;
            iccService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

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
//            updateNotification(aBoolean);
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
