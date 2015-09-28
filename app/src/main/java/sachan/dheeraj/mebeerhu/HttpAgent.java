package sachan.dheeraj.mebeerhu;

/**
 * Created by dheeraj on 18/2/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.facebook.login.LoginManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

import static sachan.dheeraj.mebeerhu.utils.Utils.deleteCredentials;

public final class HttpAgent {
    private static final String TAG = HttpAgent.class.getSimpleName();

    public static String tokenValue;

    private HttpAgent() {
    }

    /**
     * makes a get request to a url
     *
     * @param url URL
     * @return String response from the url
     */
    public static String get(String url, Activity activity) {
        Log.i(TAG, "making GET request to : " + url);

        if( !isOnline(activity) )
        {
            Log.e(TAG, "Cannot execute GET request, device not online");
            return AppConstants.NO_INTERNET_ACCESS;
        }
        String data = getImplementation(url, activity);
        Log.i(TAG, "response to request " + url + " is " + data);

        if(data == null)
        {
            Log.e(TAG, "Null response received from server");
        }
        else if (data.equals(AppConstants.UNAUTHENTICATED))
        {
            try {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                Log.e(TAG, "caught exception in thread interrupt", e);
            }
        }
        else if (data.equals(AppConstants.CONNECTION_TIME_OUT)) {
            if (checkNetConnection() == null)
            {
                Log.e(TAG, "No internet connection to server");
            }
        }
        return data;
    }

    /**
     * makes a post request to a url with some data
     *
     * @param url    String url
     * @param params List of name:value pairs to sent with the request as post data
     * @return String response of the post request
     */
    public static String post(String url, List<NameValuePair> params, Activity activity) {
        Log.i(TAG, "making POST request to : " + url + ", with params: " + params);

        if(!isOnline(activity))
        {
            Log.e(TAG, "Cannot execute POST request, device not online");
            return AppConstants.NO_INTERNET_ACCESS;
        }
        String data = postImplementationNameValuePair(url, params, activity);
        Log.i(TAG, "response to request " + url + " is " + data);

        if(data == null)
        {
            Log.e(TAG, "Null response received from server");
        }
        else if (data.equals(AppConstants.UNAUTHENTICATED)) {
            try {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                Log.e(TAG, "caught exception in thread interrupt", e);
            }
        }
        else if (data.equals(AppConstants.CONNECTION_TIME_OUT)) {
            if (checkNetConnection() == null)
            {
                Log.e(TAG, "No internet connection to server");
            }
        }
        return data;
    }

    public static String postGenericData(String url,String postData, Activity activity)
    {
        Log.i(TAG, "making POST request to : " + url + ", with params: " + postData);

        if(!isOnline(activity))
        {
            Log.e(TAG, "Cannot execute POST request, device not online");
            return AppConstants.NO_INTERNET_ACCESS;
        }
        String data = postImplementationRawString(url, postData, activity);

        if(data == null)
        {
            Log.e(TAG, "Null response received from server");
        }
        else if (data.equals(AppConstants.UNAUTHENTICATED)) {
            try {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                Log.e(TAG, "caught exception in thread interrupt", e);
            }
        }
        else if (data.equals(AppConstants.CONNECTION_TIME_OUT)) {
            if (checkNetConnection() == null)
            {
                Log.e(TAG, "No internet connection to server");
            }
        }
        return data;
    }

    public static String postBackGroundAccessLogs(String url, String postRawData) {
        String response = null;
        Log.i(TAG, "sending post request = " + url + ", raw data = " + postRawData);
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, AppConstants.HTTP_TIME_OUT);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        try {
            HttpPost post = new HttpPost(url);
            HttpEntity entity = new ByteArrayEntity(postRawData.getBytes("UTF-8"));
            post.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(post);
            response = EntityUtils.toString(httpResponse.getEntity());
        } catch (Exception e) {
            Log.e(TAG, "caught exception", e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        Log.d(TAG, "response from api on post: " + response + ", url=" + url);
        return response;
    }

    public static String getWithNoPopUps(String url,Context context){
        String response = null;
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, AppConstants.HTTP_TIME_OUT);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        try {
            HttpGet httpGet = new HttpGet(url);
            String bearer = "";
            if (bearer == null) {
                SharedPreferences settings = context.getSharedPreferences(Constants.BEARER, 0);
                bearer = settings.getString(Constants.BEARER, null);
            }
            String value = "Bearer " + bearer;
            httpGet.setHeader("Authorization", value);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpClient.execute(httpGet, responseHandler);
        } catch (Exception e) {
            Log.e(TAG, "HttpClient Exception GET : for request " + url, e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        Log.d(TAG, "response from api on get: " + response + ", url=" + url);
        return response;
    }


    public static String postNameValuePairWithNoPopup(String url, List<NameValuePair> params,Context context) {
        String response = null;
        Log.i(TAG, "sending post request = " + url + ", params = " + params);
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, AppConstants.HTTP_TIME_OUT);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        try {
            HttpPost httpPost = new HttpPost(url);
            String bearer = "";
            if (bearer == null) {
                SharedPreferences settings = context.getSharedPreferences(Constants.BEARER, 0);
                bearer = settings.getString(Constants.BEARER, null);
            }
            String value = "Bearer " + bearer;
            httpPost.setHeader("Authorization", value);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpClient.execute(httpPost, responseHandler);
        }
        catch (Exception e) {
            Log.e(TAG, "HttpClient Exception POST : for request " + url, e);
            if (e instanceof org.apache.http.client.HttpResponseException && ((HttpResponseException) e).getStatusCode() == 401) {
                return AppConstants.UNAUTHENTICATED;
            } else if (e instanceof org.apache.http.client.HttpResponseException && (((HttpResponseException) e).getStatusCode() == 500 || ((HttpResponseException) e).getStatusCode() == 404)) {
                return AppConstants.API_RESOURCE_ERROR;
            }
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        Log.d(TAG, "response from api on post: " + response + ", url=" + url);
        return response;
    }

    private static String getImplementation(String url, Activity activity) {
        String response = null;
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, AppConstants.HTTP_TIME_OUT);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        try {
            HttpGet httpGet = new HttpGet(url);
            String bearer = "";
            if (bearer == null) {
                SharedPreferences settings = activity.getSharedPreferences(Constants.BEARER, 0);
                bearer = settings.getString(Constants.BEARER, null);
            }
            String value = "Bearer " + bearer;
            httpGet.setHeader(Constants.X_ACCESS_TOKEN, tokenValue);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpClient.execute(httpGet, responseHandler);
        }
        catch (HttpResponseException e)
        {
            Log.e(TAG, "HttpClient Exception GET : for request " + url, e);
            e.printStackTrace();
            if (e.getStatusCode() == 401) {
                deleteCredentials(TAG, activity);
                return AppConstants.UNAUTHENTICATED;
            }
            else if (e.getStatusCode() == 500 ||  e.getStatusCode() == 404)
            {
                return AppConstants.API_RESOURCE_ERROR;
            }
        }
        catch (ClientProtocolException e)
        {
            Log.e(TAG, "HttpClient ClientProtocolException for url:" + url + ", error:" + e);
            e.printStackTrace();
            return AppConstants.API_RESOURCE_ERROR;
        }
        catch (ConnectTimeoutException e) {
            Log.e(TAG, "HttpClient ConnectTimeoutException for url:" + url + ", error:" + e);
            return AppConstants.CONNECTION_TIME_OUT;

        }
        catch (IOException e)
        {
            Log.e(TAG, "HttpClient IOException for url:" + url + ", error:" + e);
            e.printStackTrace();
            return AppConstants.API_RESOURCE_ERROR;
        }
        finally {
            httpClient.getConnectionManager().shutdown();
        }
        Log.d(TAG, "response from api on get: " + response + ", url=" + url);
        return response;
    }

    private static String postImplementationRawString(String url, String data, Activity activity) {
        String response = null;
        Log.i(TAG, "sending post request = " + url + ", params = " + data);
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, AppConstants.HTTP_TIME_OUT);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        try {
            HttpPost httpPost = new HttpPost(url);
            String bearer = "";
            if (bearer == null) {
                SharedPreferences settings = activity.getSharedPreferences(Constants.BEARER, 0);
                bearer = settings.getString(Constants.BEARER, null);
            }
            String value = "Bearer " + bearer;
            httpPost.setHeader(Constants.X_ACCESS_TOKEN, tokenValue);
            HttpEntity entity = new ByteArrayEntity(data.getBytes("UTF-8"));
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpClient.execute(httpPost, responseHandler);
        } catch (Exception e) {
            Log.e(TAG, "HttpClient Exception POST : for request " + url, e);
            if (e instanceof HttpResponseException && ((HttpResponseException) e).getStatusCode() == 401) {
                deleteCredentials(TAG, activity);
                return AppConstants.UNAUTHENTICATED;
            } else if (e instanceof HttpResponseException && (((HttpResponseException) e).getStatusCode() == 500 || ((HttpResponseException) e).getStatusCode() == 404)) {
                return AppConstants.API_RESOURCE_ERROR;
            }
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        Log.d(TAG, "response from api on post: " + response + ", url=" + url);
        return response;
    }

    private static String postImplementationNameValuePair(String url, List<NameValuePair> params, Activity activity) {
        String response = null;
        Log.i(TAG, "sending post request = " + url + ", params = " + params);
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, AppConstants.HTTP_TIME_OUT);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        try {
            HttpPost httpPost = new HttpPost(url);
            String bearer = "";
            if (bearer == null) {
                SharedPreferences settings = activity.getSharedPreferences(Constants.BEARER, 0);
                bearer = settings.getString(Constants.BEARER, null);
            }
            String value = "Bearer " + bearer;
            httpPost.setHeader("Authorization", value);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpClient.execute(httpPost, responseHandler);
        } catch (Exception e) {
            Log.e(TAG, "HttpClient Exception POST : for request " + url, e);
            if (e instanceof HttpResponseException && ((HttpResponseException) e).getStatusCode() == 401) {
                deleteCredentials(TAG, activity);
                return AppConstants.UNAUTHENTICATED;
            } else if (e instanceof HttpResponseException && (((HttpResponseException) e).getStatusCode() == 500 || ((HttpResponseException) e).getStatusCode() == 404)) {
                return AppConstants.API_RESOURCE_ERROR;
            }
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        Log.d(TAG, "response from api on post: " + response + ", url=" + url);
        return response;
    }

    public static String checkNetConnection() {
        String response = null;
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, AppConstants.HTTP_CHECK_CONNECTION_TIME_OUT);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        try {

            HttpGet httpGet = new HttpGet(UrlConstants.GET_EPOCH_TIME);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            response = httpClient.execute(httpGet, responseHandler);

        } catch (Exception e) {
            response = null;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return response;
    }

    public static boolean isOnline(Activity activity)
    {
        ConnectivityManager cm =
                (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
