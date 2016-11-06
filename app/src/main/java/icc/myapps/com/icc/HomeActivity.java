package icc.myapps.com.icc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        connectedText = (TextView) findViewById(R.id.connectedText);
        verifyButton = (Button) findViewById(R.id.verifyButton);

        verifyInternetConnection();
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyInternetConnection();
            }
        });
    }

    private void verifyInternetConnection() {
        connectedText.setText(getResources().getString(R.string.verifying));
        connectedText.setTextColor(getResources().getColor(R.color.verifying));
        new MyAsyncTask().execute();
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
        }

        public boolean isOnline() {
            /*Runtime runtime = Runtime.getRuntime();
            try {

                Process ipProcess = runtime.exec("ping -c 1 www.google.com");
                int     exitValue = ipProcess.waitFor();
                return (exitValue == 0);

            } catch (IOException e)          { e.printStackTrace(); }
            catch (InterruptedException e) { e.printStackTrace(); }*/

            /*try {
                if (InetAddress.getByName("www.google.com").isReachable(6000)) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;*/

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

        public class NetTask extends AsyncTask<String, Integer, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                InetAddress addr = null;
                try
                {
                    addr = InetAddress.getByName(params[0]);
                }

                catch (UnknownHostException e)
                {
                    e.printStackTrace();
                }
                return addr.getHostAddress();
            }
        }
    }
}
