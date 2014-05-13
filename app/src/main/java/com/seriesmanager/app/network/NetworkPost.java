package com.seriesmanager.app.network;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.net.URI;

public class NetworkPost extends AsyncTask<Void, Integer, HttpResponse> {

    private String url;
    private ContentType type;
    private StringEntity se;

    public NetworkPost(String url, String content, ContentType type) {
        this.url = url;
        this.type = type;
        try {
            this.se = new StringEntity(content);
        } catch (Exception e) {
            Log.e("NetworkPost", "Couldn't build string from entry!");
        }
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
    protected HttpResponse doInBackground(Void... voids) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(new URI(url));
            if (type == ContentType.JSON) {
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");
            } else if (type == ContentType.XML) {
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/xml"));
                request.setHeader("Accept", "application/xml");
                request.setHeader("Content-type", "application/xml");
            }
            request.setEntity(se);
            HttpResponse response = client.execute(request); // tem que ser igual 200
            Log.w("HttpResponse", Integer.toString(response.getStatusLine().getStatusCode()));
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public enum ContentType {
        NONE,
        XML,
        JSON
    }
}
