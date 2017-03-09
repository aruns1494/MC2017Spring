package com.group24.arun.group24;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * Created by Arun Subramanian on 06-03-2017.
 */

public class DownloadWeb extends AsyncTask<String, Integer, String> {

    private Context con;

    public DownloadWeb(Context con) {
        this.con = con;
    }

    @Override
    protected String doInBackground(String... Params) {
        String surl = "https://impact.asu.edu/CSE535Spring17Folder/Group24";

        InputStream is = null;
        OutputStream os = null;
        HttpsURLConnection con = null;

        TrustManager[] mgr = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, mgr, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException kme) {
            return kme.toString();
        } catch (NoSuchAlgorithmException nsae) {
            return nsae.toString();
        }


        try {
            URL url = new URL(surl);
            con = (HttpsURLConnection) url.openConnection();
            con.connect();

            if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + con.getResponseCode()
                        + " " + con.getResponseMessage();
            }

            int fileLength = con.getContentLength();

            // download the file
            is = con.getInputStream();
            String Location = "/data/data/com.group24.arun.group24/databases/Group24File";
            File file = new File(Location);
            os = new FileOutputStream(file);
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = is.read(data)) != -1) {
                total += count;
                os.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (os != null)
                    os.close();
                if (is != null)
                    is.close();
            } catch (IOException ex) {
                return ex.toString();
            }

            if (con != null)
                con.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Toast.makeText(con, "Error Occured! Error is:" + result, Toast.LENGTH_LONG);
        } else {
            Toast.makeText(con, "Download Successful", Toast.LENGTH_LONG);
        }
    }
}