package com.group24.arun.group24;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Created by Arun Subramanian on 06-03-2017.
 */

public class UploadWebService extends AsyncTask<String, Integer, String> {

    Context con;

    public UploadWebService(Context con) {
        this.con = con;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
    }

    @Override
    protected String doInBackground(String... params) {
        String dbURL = "https://impact.asu.edu/CSE535Spring17Folder/UploadToServer.php";
        String dbFileLocation = "/data/data/com.group24.arun.group24/databases/Group24";
        try {
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
            } catch (KeyManagementException kmex) {
                return kmex.toString();
            } catch (NoSuchAlgorithmException nsae) {
                return nsae.toString();
            }

            HttpsURLConnection con = null;

            int bRead, bAvail, bSize;
            byte[] buffer;
            int maxBufferSize = 1048576;

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            DataOutputStream dos = null;
            FileInputStream fis = null;
            File dbFile = null;
            try {
                dbFile = new File(dbFileLocation);
                fis = new FileInputStream(dbFile);
                URL url = new URL(dbURL);

                con = (HttpsURLConnection) url.openConnection();

                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);

                con.setRequestMethod("POST");
                con.setRequestProperty("Connection", "Keep-Alive");
                con.setRequestProperty("Cache-Control", "no-cache");
                con.setRequestProperty("ENCTYPE", "multipart/form-data");
                con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                dos = new DataOutputStream(con.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name='uploaded_file';fileName='" + dbFileLocation + "'" + lineEnd);
                dos.writeBytes(lineEnd);


                bAvail = fis.available();
                bSize = Math.min(bAvail, maxBufferSize);
                buffer = new byte[bSize];
                bRead = fis.read(buffer, 0, bSize);

                while (bRead > 0) {
                    dos.write(buffer, 0, bSize);
                    bAvail = fis.available();
                    bSize = Math.min(bAvail, maxBufferSize);
                    bRead = fis.read(buffer, 0, bSize);
                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                int serverResponseCode = con.getResponseCode();
                if (serverResponseCode != 200) {
                    return "server returned: " + con.getResponseCode() + " message: " + con.getResponseMessage();
                } else {
                    return "\n\n\n\n\n\nRESPONSE CODE: " + serverResponseCode + " RESPONSE MESSAGE: " + con.getResponseMessage();
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    dos.flush();
                    dos.close();
                    fis.close();
                } catch (IOException ioe) {
                    return ioe.toString();
                }
                if (con != null) {
                    con.disconnect();
                }
            }
        } catch (Exception e) {
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Toast.makeText(con,"Upload Error Occured! Error is: "+result,Toast.LENGTH_LONG);
        } else {
            Toast.makeText(con,"File Upload Successful!",Toast.LENGTH_LONG);
        }
    }


}
