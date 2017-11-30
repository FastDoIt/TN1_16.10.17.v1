package by.supercoder.tasknumber1.resultactivity;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import by.supercoder.tasknumber1.R;
import by.supercoder.tasknumber1.requestactivity.PathPointsDataProvider;


public class MapsActivity extends AppCompatActivity implements DirectionCreator.OnPathCreated, DirectionCreator.OnShowServerResponse {
    final String TAG_SHOW_PATH_ON_MAP_FRAGMENT = "by.supercoder.tasknumber1.resultactivity.TAG_SHOW_PATH_ON_MAP_FRAGMENT";
    private DirectionCreator mDirectionCreator;

    private final String SIS_SERVER_RESPONSE_TEXT = "by.supercoder.tasknumber1.resultactivity.SIS_SERVER_RESPONSE_TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ShowPathOnMapFragment mResultMap;
        if (savedInstanceState == null) {
            mResultMap = new ShowPathOnMapFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.map, mResultMap, TAG_SHOW_PATH_ON_MAP_FRAGMENT);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        } else {
            mResultMap = (ShowPathOnMapFragment) getSupportFragmentManager().findFragmentByTag(TAG_SHOW_PATH_ON_MAP_FRAGMENT);
        }
        if (savedInstanceState == null) {
            LatLng latLngAddressFrom;
            try {
                latLngAddressFrom = new LatLng((double) getIntent().getDoubleExtra(PathPointsDataProvider.LATITUDE_FROM_RESULT_ADDRESS, 0),
                        (double) getIntent().getDoubleExtra(PathPointsDataProvider.LONGITUDE_FROM_RESULT_ADDRESS, 0));
            } catch (NullPointerException e) {
                latLngAddressFrom = null;
            }
            mResultMap.setLatLngFrom(latLngAddressFrom);

            String descriptionAddressFrom = "";
            try {
                descriptionAddressFrom = (String) getIntent().getStringExtra(PathPointsDataProvider.DESCRIPTION_FROM_RESULT_ADDRESS);
            } catch (NullPointerException e) {
                descriptionAddressFrom = null;
            }
            mResultMap.setFromDescription(descriptionAddressFrom);
            LatLng latLngAddressTo;
            try {
                latLngAddressTo = new LatLng((double) getIntent().getDoubleExtra(PathPointsDataProvider.LATITUDE_TO_RESULT_ADDRESS, 0),
                        (double) getIntent().getDoubleExtra(PathPointsDataProvider.LONGITUDE_TO_RESULT_ADDRESS, 0));
            } catch (NullPointerException e) {
                latLngAddressTo = null;
            }
            mResultMap.setLatLngTo(latLngAddressTo);
            String descriptionAddressTo = "";
            try {
                descriptionAddressTo = (String) getIntent().getStringExtra(PathPointsDataProvider.DESCRIPTION_TO_RESULT_ADDRESS);
            } catch (NullPointerException e) {
                descriptionAddressTo = null;
            }
            mResultMap.setToDescription(descriptionAddressTo);

            String resultUserLocationStatus = "";
            resultUserLocationStatus = getIntent().getStringExtra(PathPointsDataProvider.RESULT_USER_LOCATION_STATUS);
            if (StringEquals.equals(resultUserLocationStatus, PathPointsDataProvider.RESULT_WITH_USER_LOCATION)) {
                boolean isUserLocationFounded = StringEquals.equals(resultUserLocationStatus, PathPointsDataProvider.RESULT_WITH_USER_LOCATION);
                if (isUserLocationFounded) {
                    LatLng userLatLng = null;
                    userLatLng = (LatLng) getIntent().getParcelableExtra(PathPointsDataProvider.RESULT_USER_LATLNG);
                    String userDescription = "";
                    userDescription = getString(R.string.hint_marker_of_user_location).toString();
                    mResultMap.setUserLocationVisible(true);
                    mResultMap.setUserLocationLatLng(userLatLng);
                    mResultMap.setUserLocationDescription(userDescription);
                } else {
                    mResultMap.setUserLocationVisible(false);
                }
            } else if (StringEquals.equals(resultUserLocationStatus, PathPointsDataProvider.RESULT_WITHOUT_USER_LOCATION)) {
                mResultMap.setUserLocationVisible(false);
            }

            if (latLngAddressFrom != null || latLngAddressTo != null) {
                mDirectionCreator = new DirectionCreator();
                mDirectionCreator.setOnPathListener(this);
                mDirectionCreator.setShowServerResponseListener(this);
                mDirectionCreator.setContext(getApplicationContext());
                String key = getString(R.string.google_maps_directions_key);
                mDirectionCreator.createPathArray(getApplicationContext(), latLngAddressFrom, latLngAddressTo, key);
                ShowWaitingPathProgressBar();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mDirectionCreator != null) {
            mDirectionCreator.setContext(null);
            mDirectionCreator.setOnPathListener(null);
            mDirectionCreator.setShowServerResponseListener(null);
            mDirectionCreator.clear();
            mDirectionCreator = null;
        }
        super.onDestroy();
    }

    @Override
    public void onPathCreated(ArrayList<LatLng> pathArray) {
        ShowPathOnMapFragment mResultMap = (ShowPathOnMapFragment) getSupportFragmentManager().findFragmentByTag(TAG_SHOW_PATH_ON_MAP_FRAGMENT);
        if (mResultMap != null) {
            mResultMap.setPathArray(pathArray);
        }
        HideWaitingPathProgressBar();
    }
    private void ShowWaitingPathProgressBar(){
        ProgressBar waitingPathProgressBar = null;
        waitingPathProgressBar = (ProgressBar)findViewById(R.id.waitingPathProgressBar);
        waitingPathProgressBar.setVisibility(View.VISIBLE);
    }

    private void HideWaitingPathProgressBar(){
        ProgressBar waitingPathProgressBar = null;
        waitingPathProgressBar = (ProgressBar)findViewById(R.id.waitingPathProgressBar);
        if(waitingPathProgressBar.getVisibility() == View.VISIBLE) {
            waitingPathProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onShowServerResponse(String serverResponse) {
        TextView serverResponseTextView = (TextView) findViewById(R.id.textServerResponse);
        if (serverResponseTextView != null) {
            serverResponseTextView.setText(serverResponse);
        }
        HideWaitingPathProgressBar();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        TextView serverResponseTextView = (TextView) findViewById(R.id.textServerResponse);
        savedInstanceState.putString(SIS_SERVER_RESPONSE_TEXT, serverResponseTextView.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        TextView serverResponseTextView = (TextView) findViewById(R.id.textServerResponse);
        String serverResponseText = null;
        try {
            serverResponseText = savedInstanceState.getString(SIS_SERVER_RESPONSE_TEXT);
        } catch (NullPointerException e) {
            serverResponseText = "";
        }
        serverResponseTextView.setText(serverResponseText);
        HideWaitingPathProgressBar();
    }
}
