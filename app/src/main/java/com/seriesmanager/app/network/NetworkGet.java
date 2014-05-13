package com.seriesmanager.app.network;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.net.URI;
import java.util.zip.GZIPInputStream;

public class NetworkGet extends AsyncTask<Void, Integer, InputStream> {

    private String url;

    public NetworkGet(String url) {
        this.url = url;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    /*private InputStream getFromServer() throws Exception {
        Log.w("NetworkGet", url);
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(new URI(url));
        request.addHeader("Accept-Encoding","gzip");
        HttpResponse response = client.execute(request); // tem que ser igual 200
        InputStream content = response.getEntity().getContent();
        Header contentEncoding = response.getFirstHeader("Content-Encoding");
        if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")){
            content = new GZIPInputStream(content);
        }
        Log.w("NetworkGet", "finished execution");
        return content;
    }*/

    @Override
    protected InputStream doInBackground(Void... voids) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(new URI(url));
            request.addHeader("Accept-Encoding", "gzip");
            HttpResponse response = client.execute(request); // tem que ser igual 200
            Log.w("HttpResponse", Integer.toString(response.getStatusLine().getStatusCode()));
            InputStream content = response.getEntity().getContent();
            Header contentEncoding = response.getFirstHeader("Content-Encoding");
            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                content = new GZIPInputStream(content);
            }
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
