package com.frankapps.icc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by frank on 2/4/17.
 */

public class ConnectionVerify {
    public static boolean isOnline() {
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
}
