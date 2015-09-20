package sachan.dheeraj.mebeerhu;

/**
 * Created by dheeraj on 18/2/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.login.LoginManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.util.HashSet;
import java.util.List;

import sachan.dheeraj.mebeerhu.events.ApiErrorEvent;
import sachan.dheeraj.mebeerhu.events.RefreshNeededEvent;

public final class HttpAgent {
    private static final String TAG = HttpAgent.class.getSimpleName();
    private static final String API_FAT_GAI = "apiFatGai";
    private static final String UNAUTHENTICATED = "unauthenticated";
    public static HashSet<Thread> THREAD_HASH_SET = new HashSet<>();
    public static final Object OBJECT = new Object();

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
        String data = getImplementation(url, activity);
        Log.i(TAG, "response to request " + url + " is " + data);
      /*  if (data == null) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppUtils.BUS.post(new RefreshNeededEvent());
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "caught exception in activity.runOnUiThread()", e);
            }
            THREAD_HASH_SET.add(Thread.currentThread());
            try {
                synchronized (OBJECT) {
                    OBJECT.wait();
                    return get(url, activity);
                }
            } catch (Exception e) {
                Log.e(TAG, "caught interrupted exception");
            }
        } else if (data.equals(API_FAT_GAI)) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppUtils.BUS.post(new ApiErrorEvent());
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "caught exception in activity.runOnUiThread()", e);
            }
            THREAD_HASH_SET.add(Thread.currentThread());
            try {
                synchronized (OBJECT) {
                    OBJECT.wait();
                    return get(url, activity);
                }
            } catch (Exception e) {
                Log.e(TAG, "caught interrupted exception");
            }
        } else if (data.equals(UNAUTHENTICATED)) {
            try {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                Log.e(TAG, "caught exception in thread interrupt", e);
            }
        } */
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
        Log.i(TAG, "making POST request to : " + url + "with params" + params);
        String data = postImplementationNameValuePair(url, params, activity);
        Log.i(TAG, "response to request " + url + " is " + data);
        /* if (data == null) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppUtils.BUS.post(new RefreshNeededEvent());
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "caught exception in activity.runOnUiThread()", e);
            }
            THREAD_HASH_SET.add(Thread.currentThread());
            try {
                synchronized (OBJECT) {
                    OBJECT.wait();
                    return post(url, params, activity);
                }
            } catch (Exception e) {
                Log.e(TAG, "caught interrupted exception");
            }
        } else if (data.equals(API_FAT_GAI)) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppUtils.BUS.post(new ApiErrorEvent());
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "caught exception in activity.runOnUiThread()", e);
            }
            THREAD_HASH_SET.add(Thread.currentThread());
            try {
                synchronized (OBJECT) {
                    OBJECT.wait();
                    return post(url, params, activity);
                }
            } catch (Exception e) {
                Log.e(TAG, "caught interrupted exception");
            }
        } else if (data.equals(UNAUTHENTICATED)) {
            try {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                Log.e(TAG, "caught exception in thread interrupt", e);
            }
        } */
        return data;
    }

    public static String postGenericData(String url,String postData, Activity activity) {
        String data = postImplementationRawString(url, postData, activity);
        /* if (data == null) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppUtils.BUS.post(new RefreshNeededEvent());
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "caught exception in activity.runOnUiThread()", e);
            }
            THREAD_HASH_SET.add(Thread.currentThread());
            try {
                synchronized (OBJECT) {
                    OBJECT.wait();
                    return postGenericData(url, postData, activity);
                }
            } catch (Exception e) {
                Log.e(TAG, "caught interrupted exception");
            }
        } else if (data.equals(API_FAT_GAI)) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppUtils.BUS.post(new ApiErrorEvent());
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "caught exception in activity.runOnUiThread()", e);
            }
            THREAD_HASH_SET.add(Thread.currentThread());
            try {
                synchronized (OBJECT) {
                    OBJECT.wait();
                    return postGenericData(url, postData, activity);
                }
            } catch (Exception e) {
                Log.e(TAG, "caught interrupted exception");
            }
        } else if (data.equals(UNAUTHENTICATED)) {
            try {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                Log.e(TAG, "caught exception in thread interrupt", e);
            }
        } */
        return data;
    }

    public static String postBackGroundAccessLogs(String url, String postRawData) {
        String response = null;
        Log.i(TAG, "sending post request = " + url + ", raw data = " + postRawData);
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, AppUtils.HTTP_TIME_OUT);
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
        HttpConnectionParams.setConnectionTimeout(httpParams, AppUtils.HTTP_TIME_OUT);
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
        HttpConnectionParams.setConnectionTimeout(httpParams, AppUtils.HTTP_TIME_OUT);
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
        } catch (Exception e) {
            Log.e(TAG, "HttpClient Exception POST : for request " + url, e);
           /* if (e instanceof org.apache.http.client.HttpResponseException && ((HttpResponseException) e).getStatusCode() == 401) {
                return UNAUTHENTICATED;
            } else if (e instanceof org.apache.http.client.HttpResponseException && (((HttpResponseException) e).getStatusCode() == 500 || ((HttpResponseException) e).getStatusCode() == 404)) {
                return API_FAT_GAI;
            }*/
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        Log.d(TAG, "response from api on post: " + response + ", url=" + url);
        return response;
    }

    private static String getImplementation(String url, Activity activity) {
        String response = null;
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, AppUtils.HTTP_TIME_OUT);
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
        } catch (Exception e) {
            Log.e(TAG, "HttpClient Exception GET : for request " + url, e);
            if (e instanceof HttpResponseException && ((HttpResponseException) e).getStatusCode() == 401) {
                deleteCredentials(activity);
                return UNAUTHENTICATED;
            } else if (e instanceof HttpResponseException && (((HttpResponseException) e).getStatusCode() == 500 || ((HttpResponseException) e).getStatusCode() == 404)) {
                return API_FAT_GAI;
            }
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        Log.d(TAG, "response from api on get: " + response + ", url=" + url);
        return response;
    }

    private static String postImplementationRawString(String url,String data, Activity activity) {
        String response = null;
        Log.i(TAG, "sending post request = " + url + ", params = " + data);
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, AppUtils.HTTP_TIME_OUT);
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
            httpPost.setHeader("Content-Type","application/json");
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpClient.execute(httpPost, responseHandler);
        } catch (Exception e) {
            Log.e(TAG, "HttpClient Exception POST : for request " + url, e);
            if (e instanceof HttpResponseException && ((HttpResponseException) e).getStatusCode() == 401) {
                deleteCredentials(activity);
                return UNAUTHENTICATED;
            } else if (e instanceof HttpResponseException && (((HttpResponseException) e).getStatusCode() == 500 || ((HttpResponseException) e).getStatusCode() == 404)) {
                return API_FAT_GAI;
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
        HttpConnectionParams.setConnectionTimeout(httpParams, AppUtils.HTTP_TIME_OUT);
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
                deleteCredentials(activity);
                return UNAUTHENTICATED;
            } else if (e instanceof HttpResponseException && (((HttpResponseException) e).getStatusCode() == 500 || ((HttpResponseException) e).getStatusCode() == 404)) {
                return API_FAT_GAI;
            }
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        Log.d(TAG, "response from api on post: " + response + ", url=" + url);
        return response;
    }

    private static void deleteCredentials(Activity activity) {
        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
        }
        boolean userDeleted = true;
        if (userDeleted) {
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();
        }
    }


    public static String checkNetConnection() {
        String response = null;
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, AppUtils.HTTP_TIME_OUT);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        try {
/*
            HttpGet httpGet = new HttpGet(UrlConstants.GET_EPOCH_TIME);
*/
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
/*
            response = httpClient.execute(httpGet, responseHandler);
*/
        } catch (Exception e) {
            response = null;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return response;
    }
}