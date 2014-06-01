package net.simplyadvanced.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.NameValuePair;

import com.google.common.io.CharStreams;
import com.squareup.okhttp.OkHttpClient;

/** A simple helper class to help simplify accessing Internet resources.
 * 
 * This class uses the following two libraries: OkHttp, Guava
 * More info: http://square.github.io/okhttp/
 * More info: https://code.google.com/p/guava-libraries/
 * 
 * This class uses the following permissions in AndroidManifest.xml:
    <uses-permission android:name="android.permission.INTERNET" />
 * 
 *  */
public class HelperHttp {
//  private static final String LOG_TAG = "DEBUG: net.simplyadvanced.helper.HelperHttp";
//  private static final boolean IS_DEBUG = true;
//  private static final void log(String message) {
//      if (BuildConfig.DEBUG && IS_DEBUG) {
//          Log.d(LOG_TAG, message);
//      }
//  }
    
    /** Prevent instantiation of this class. */
    private HelperHttp() {}
    
    
    private static OkHttpClient client;
    
    static {
        client = new OkHttpClient();
    }
    
    
    /** Perform HTTP GET operation.
     * 
     * @param url the url to access. Ex: "http://..."
     * 
     * @return response from operation
     * */
    public static String get(URL url) throws IOException {
        HttpURLConnection connection = client.open(url);
        InputStream in = null;
        try {
            // Read the response.
            in = connection.getInputStream();
            return getStringFromInputStream(in);
        } finally {
            if (in != null)
                in.close();
        }
    }

    /** Perform HTTP POST operation.
     * 
     * @param url the url to access. Ex: "http://..."
     * @param body any additional properties or parameters to send
     * 
     * @return response from operation
     * */
    public static String post(URL url, byte[] body) throws IOException {
        HttpURLConnection connection = client.open(url);
        OutputStream out = null;
        InputStream in = null;
        try {
            // Write the request.
            connection.setRequestMethod("POST");
            out = connection.getOutputStream();
            out.write(body);
            out.close();

            // Read the response.
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Unexpected HTTP response: "
                        + connection.getResponseCode() + " "
                        + connection.getResponseMessage());
            }
            in = connection.getInputStream();
            return getStringFromInputStream(in);
        } finally {
            // Clean up.
            if (out != null) { out.close(); }
            if (in != null) { in.close(); }
        }
    }

    /** Perform HTTP POST operation.
     * 
     * The paramsList input can be formed by:
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("key1", value1));
            params.add(new BasicNameValuePair("key2", value2));
            params.add(new BasicNameValuePair("key3", value3));
     * 
     * @param url the url to access. Ex: "http://..."
     * @param paramsList any additional properties or parameters to send
     * 
     * @return response from operation
     * */
    public static String post(URL url, List<NameValuePair> paramsList) throws IOException {
        HttpURLConnection connection = client.open(url);
        OutputStream out = null;
        InputStream in = null;
        try {
            // Write the request.
            connection.setRequestMethod("POST");
            out = connection.getOutputStream();
            out.write(getStringFromList(paramsList).getBytes());
            out.flush();
            out.close();

            // Read the response.
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Unexpected HTTP response: "
                        + connection.getResponseCode() + " "
                        + connection.getResponseMessage());
            }
            in = connection.getInputStream();
            return getStringFromInputStream(in);
        } finally {
            // Clean up.
            if (out != null) { out.close(); }
            if (in != null) { in.close(); }
        }
    }
    
    /**
     * Not tested. Possibly has error.
     * 
     * Create JSONObject example:
            JSONObject params = new JSONObject();
            params.put("ID", "25");
            params.put("description", "Real");
            params.put("enable", "true");
     * 
     * More info: http://stackoverflow.com/questions/13911993/sending-a-json-http-post-request-from-android
     * 
     * */
//  public static String post(URL url, JSONObject params) throws IOException {
//      HttpURLConnection connection = client.open(url);
//      DataOutputStream out = null;
//      InputStream in = null;
//      try {
//          // Write the request.
//          connection.setRequestProperty("Content-Type", "application/json");
//          connection.setRequestMethod("POST");  
//          out = new DataOutputStream(connection.getOutputStream());
//          out.writeBytes(params.toString());
//          //out.write(URLEncoder.encode(params.toString(), "UTF-8"));
//          out.flush ();
//          out.close();
//
//          // Read the response.
//          if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//              throw new IOException("Unexpected HTTP response: "
//                      + connection.getResponseCode() + " "
//                      + connection.getResponseMessage());
//          }
//          in = connection.getInputStream();
//          return getStringFromInputStream(in);
//      } finally {
//          // Clean up.
//          if (out != null) { out.close(); }
//          if (in != null) { in.close(); }
//      }
//  }
    
    /** Returns string from input stream.
     * 
     *  No error checking is done on input. Currently, this method uses Guava, subject to change. */
    private static String getStringFromInputStream(final InputStream inputStream) throws UnsupportedEncodingException, IOException {
        return CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
    }
    
    
    /** Returns List input in the format of "key=value&key2=value2&key3=value3".
     * 
     *  This method also URL encodes the keys and values.
     *  
     *  @param paramsList 
     *  
     *  @return string in the format of "key=value&key2=value2&key3=value3". Or, if input is null or size 0, returns "".
     *  
     * @throws UnsupportedEncodingException */
    private static String getStringFromList(final List<NameValuePair> paramsList) throws UnsupportedEncodingException {
        if (paramsList.size() == 0 || paramsList == null) { return ""; }
        
        StringBuilder sb = new StringBuilder();
        for (NameValuePair o : paramsList) {
            sb.append(URLEncoder.encode(o.getName(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(o.getValue(), "UTF-8"));
            sb.append("&");
        }
        
        // Remove last character/ampersand by substracting one.
        return sb.substring(0, sb.length() - 1);
    }

}
