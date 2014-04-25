package net.simplyadvanced.helper.old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/** Don't use this.
 * 
 *  A simple API to help simplify accessing Internet resource.
 * To use this class, you need to declare the following permissions in AndroidManifest.xml:
    <uses-permission android:name="android.permission.INTERNET" />
 * 
 * 
 * Each method in this class runs in the same thread that it is called in.
 * 
 * 
 * More info: http://developer.android.com/training/basics/network-ops/xml.html
 * 
 * @deprecated
 *  */
public class NaiveHttp {
	private static final String LOG_TAG = "DEBUG: NaiveHttp";
//	private static final boolean IS_DEBUG = true;
//	private static final void log(final String message) {
//		if (BuildConfig.DEBUG && IS_DEBUG) {
//			Log.d(LOG_TAG, message);
//		}
//	}
	

	/** Constant string to use for "POST" requests. */
	public static final String HTTP_POST = "POST";

	/** Constant string to use for "GET" requests. */
	public static final String HTTP_GET = "GET";
	
	


    /** Make a HTTP request with parameters already added in the URL (or no parameters at all).
     * Though, for HTTP_POST method, there shouldn't be params in the URL.
     
     * This request is performed in the same thread that it is called in.
     
     * @param url In the format of "http://..."
     * @param method either HelperNetwork.HTTP_POST or HelperNetwork.HTTP_GET
     * 
     * @return response from performing request */
    public static String makeHttpRequest(final String url, final String method) {
    	HttpResponse httpResponse = null;
    	
        if (method.equalsIgnoreCase(HTTP_POST)) {
        	httpResponse = makeHttpPostRequest(url);
        } else if (method.equalsIgnoreCase(HTTP_GET)) {
        	httpResponse = makeHttpGetRequest(url);
        }
        
        InputStream inputStream = getInputStreamFromHttpResponse(httpResponse);
        String responseString = getStringFromInputStream(inputStream);
    	
    	return responseString;
    }

    /** Make a HTTP request with parameters.
     * 
     * This request is performed in the same thread that it is called in.
     * 
     * @param url In the format of "http://..."
     * @param method either HelperNetwork.HTTP_POST or HelperNetwork.HTTP_GET
     * @param params The parameters to send with url
     * 
     * @return response from performing request */
    public static final String makeHttpRequest(final String url, final String method, final List<NameValuePair> params) {
    	HttpResponse httpResponse = null;
    	
        if (method.equalsIgnoreCase(HTTP_POST)) {
        	httpResponse = makeHttpPostRequest(url, params);
        } else if (method.equalsIgnoreCase(HTTP_GET)) {
        	httpResponse = makeHttpGetRequest(url, params);
        }
        
        InputStream inputStream = getInputStreamFromHttpResponse(httpResponse);
        String response = getStringFromInputStream(inputStream);
    	
    	return response;
    }

    /** Make a HTTP request with parameters already added in the URL (or no parameters at all).
     * This will return null if request isn't for a valid JSONObject.
     * @param url In the format of "http://..."
     * @param method either HelperNetwork.HTTP_POST or HelperNetwork.HTTP_GET
     * 
     * @return response from performing request */
    public static final JSONObject makeHttpRequestForJson(final String url, final String method) {
    	String responseString = makeHttpRequest(url, method);
    	
    	JSONObject responseJsonObject = null;
        try {
            responseJsonObject = new JSONObject(responseString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing data " + e.toString());
//        	throw new RuntimeException(e);
        }

//      log("makeHttpRequestForJson(" + url + "), " + responseJsonObject.toString());
    	return responseJsonObject;
    }

    /** Make a HTTP request with parameters.
     * This will return null if request isn't for a valid JSONObject.
     * @param url In the format of "http://..."
     * @param method Either "GET" or "POST"
     * @param params The parameters to send with url */
    public static final JSONObject makeHttpRequestForJson(final String url, final String method, final List<NameValuePair> params) {
    	String responseString = makeHttpRequest(url, method, params);
    	
    	JSONObject responseJsonObject = null;
        try {
            responseJsonObject = new JSONObject(responseString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing data " + e.toString());
//        	throw new RuntimeException(e);
        }

//      log("makeHttpRequestForJson(" + url + "), " + responseJsonObject.toString());
    	return responseJsonObject;
    }
    
    /** Returns the full response after calling the URL, which is typically in the format of "http://...". */
	private static final HttpResponse makeHttpPostRequest(final String url) {
		HttpResponse httpResponse = null;
		try {
	        HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(new URI(url));
//          httpPost.setEntity(new UrlEncodedFormEntity(params));
	        httpResponse = httpClient.execute(httpPost);
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return httpResponse;
	}
    
    /** Returns the full response after calling the URL, which is typically in the format of "http://...". */
	private static final HttpResponse makeHttpPostRequest(final String url, final List<NameValuePair> params) {
		HttpResponse httpResponse = null;
		try {
	        HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(new URI(url));
            httpPost.setEntity(new UrlEncodedFormEntity(params));
	        httpResponse = httpClient.execute(httpPost);
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return httpResponse;
	}
	
	/** Returns the full response after calling the URL, which is typically in the format of "http://...". */
	private static final HttpResponse makeHttpGetRequest(final String url) {
		HttpResponse httpResponse = null;
		try {
	        HttpClient httpClient = new DefaultHttpClient();
	        HttpGet httpGet = new HttpGet();
//          String paramString = URLEncodedUtils.format(params, "utf-8");
//          url += "?" + paramString;
	        httpGet.setURI(new URI(url));
	        httpResponse = httpClient.execute(httpGet);
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return httpResponse;
	}
	
	/** Returns the full response after calling the URL, which is typically in the format of "http://...". */
	private static final HttpResponse makeHttpGetRequest(String url, final List<NameValuePair> params) {
		HttpResponse httpResponse = null;
		try {
	        HttpClient httpClient = new DefaultHttpClient();
	        HttpGet httpGet = new HttpGet();
            String paramString = URLEncodedUtils.format(params, "utf-8");
            url += "?" + paramString;
	        httpGet.setURI(new URI(url));
	        httpResponse = httpClient.execute(httpGet);
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return httpResponse;
	}
	
	/** Returns an InputStream from HttpResponse. */
	private static final InputStream getInputStreamFromHttpResponse(final HttpResponse hr) {
    	InputStream inputStream = null;
        try {
			inputStream = hr.getEntity().getContent();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return inputStream;
	}
	
	/** Returns a String of characters from the InputStream given. */
	private static final String getStringFromInputStream(final InputStream is) {
		BufferedReader reader = null;
		StringBuilder sb = null;
		try {
			// InputStreamReader is used to read characters. Use just InputStream->DataInputStream for binary data.
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); // JSON is UTF-8 by default I believe.
//			reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
			sb = new StringBuilder();
			String line = null;
			
			while ((line = reader.readLine()) != null) {
			    sb.append(line).append("\n");
//			    log("convertInputStreamToString(), sb: " + sb.toString());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// Ignore. // Close quietly. // org.apache.commons.io.IOUtils.closeQuietly(InputStream).
//					e.printStackTrace();
				}
			}
		}
		
		return sb.toString();
	}

}
