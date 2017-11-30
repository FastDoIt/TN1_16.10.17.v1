package by.supercoder.tasknumber1.requestactivity;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import by.supercoder.tasknumber1.R;

/**
 * Created by user on 15.07.2017.
 */
public class DeviceLocationManager {

    private static final String TAG = DeviceLocationManager.class.getSimpleName();
    //  Provides the entry point to the Fused Location Provider API.
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng mUserLocation;
    private RequestActivity mParentActivity;

    interface OnDeviceLocationFoundListener {
        void onDeviceLocationFound(LatLng deviceLocation);

        void onNoLocationDetected();
    }

    private OnDeviceLocationFoundListener mOnDeviceLocationFoundListener;

    public DeviceLocationManager(RequestActivity activity) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity.getApplicationContext());
        mUserLocation = null;
        setParentActivity(activity);
    }

    //  Provides a simple way of getting a device's location and is well suited for
    //  applications that do not require a fine-grained location and that do not need location
    //  updates. Gets the best and most recent location currently available, which may be null
    //  in rare cases when a location is not available.
    //  <p>
    //  Note: this method should be called after location permission has been granted.
    @SuppressWarnings("MissingPermission")
    public void getLastLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(getParentActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    mUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    // Logic to handle location object
                    mOnDeviceLocationFoundListener.onDeviceLocationFound(mUserLocation);
                }else {
                    // Show: No location detected
                    showSnackbar(getParentActivity().getString(R.string.no_location_detected).toString());
                }
            }
        });
    }

    public void setDeviceLocationFoundListener(AppCompatActivity appCompatActivity) {
        mOnDeviceLocationFoundListener = (OnDeviceLocationFoundListener) appCompatActivity;
    }

    public void setParentActivity(RequestActivity parentActivity) {
        mParentActivity = parentActivity;
    }

    public RequestActivity getParentActivity() {
        return mParentActivity;
    }

    protected void showSnackbar(final String text) {
        View container = (View) (mParentActivity.findViewById(R.id.coordinatorLayout));
        if (container != null) {
            Snackbar snackbar = Snackbar.make(container, text, Snackbar.LENGTH_LONG);
            // To fit all the text into the snake bar
            TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            final int MAX_COUNT_LINES = 5;
            textView.setMaxLines(MAX_COUNT_LINES);
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT) {
                        mOnDeviceLocationFoundListener.onNoLocationDetected();
                    }
                }
            });
            snackbar.show();
        }
    }
}
