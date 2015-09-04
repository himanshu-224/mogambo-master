package sachan.dheeraj.mebeerhu;

import android.content.Context;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

/**
 * Created by agarwalh on 9/4/2015.
 */
public class GoogleHelper {
    //private static GoogleHelper googleHelperInstance = null;
    private static GoogleApiClient mGoogleApiClient = null;

    /*private GoogleHelper() {

    }*/

    /*public static GoogleHelper getInstance() {
        if (googleHelperInstance == null) {
            googleHelperInstance = new GoogleHelper();
        }
        return googleHelperInstance;
    }*/

    public static void setGoogleApiClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    public static GoogleApiClient getGoogleApiClient()
    {
        return mGoogleApiClient;
    }


}
