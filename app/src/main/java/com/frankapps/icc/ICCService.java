package com.frankapps.icc;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by frank on 11/12/16.
 */

public class ICCService extends IntentService {
    public static final String ONLINE_MESSAGE = "ONLINE_MESSAGE";
    public static final String ONLINE_RESULT = "ONLINE_RESULT";

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    NotificationCompat.Builder mBuilder;

    private boolean online;
    private LocalBroadcastManager broadcaster;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        private static final int MILISECONDS = 10 * 1000;

        private void reviewConnection() {
            online = isOnline();
            Log.i("ICCService", "hay conexion? " + online);
            updateNotification(online);
            sendResult(online);
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

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Create the Handler object (on the main thread by default)
            final Handler handler = new Handler();
            // Define the code block to be executed
            Runnable runnableCode = new Runnable() {
                @Override
                public void run() {
                    // Do something here on the main thread
                    //verifyInternetConnection();
                    reviewConnection();
                    // Repeat this the same runnable code block again another 2 seconds
                    handler.postDelayed(this, MILISECONDS);
                }
            };

            handler.post(runnableCode);
        }
    }

    // Must create a default constructor
    public ICCService() {
        // Used to name the worker thread, important only for debugging.
        super("icc-service");
    }

    @Override
    public void onCreate() {
//        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        broadcaster = LocalBroadcastManager.getInstance(this);

        createAndDisplayNotification();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ICCService", "servicio iniciado!");
//        Toast.makeText(this, "servicio iniciandose! yeiiii", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
//        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        Log.i("ICCService", "servicio finalizado!");
//        Toast.makeText(this, "servicio finalizado!", Toast.LENGTH_SHORT).show();
//        super.onDestroy();
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

    /** method for clients */
//    public boolean isOnline() {
//        return ConnectionVerify.isOnline();
//    }

    public void sendResult(boolean isOnline) {
        Intent intent = new Intent(ONLINE_RESULT);
        intent.putExtra(ONLINE_MESSAGE, isOnline);
        broadcaster.sendBroadcast(intent);
    }
}
