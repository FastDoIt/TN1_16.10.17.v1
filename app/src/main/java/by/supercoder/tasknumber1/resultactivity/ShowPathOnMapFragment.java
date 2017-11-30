package by.supercoder.tasknumber1.resultactivity;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import by.supercoder.tasknumber1.R;
import by.supercoder.tasknumber1.requestactivity.AddressListAdapter;

public class ShowPathOnMapFragment extends Fragment implements OnMapReadyCallback {
    private final String SIS_LATLNG_FROM_RESULT_ADDRESS = "by.supercoder.tasknumber1.resultactivity.SIS_LATLNG_FROM_RESULT_ADDRESS";
    private final String SIS_DESCRIPTION_FROM_RESULT_ADDRESS = "by.supercoder.tasknumber1.resultactivity.SIS_DESCRIPTION_FROM_RESULT_ADDRESS";
    private final String SIS_LATLNG_TO_RESULT_ADDRESS = "by.supercoder.tasknumber1.resultactivity.SIS_LATLNG_TO_RESULT_ADDRESS";
    private final String SIS_DESCRIPTION_TO_RESULT_ADDRESS = "by.supercoder.tasknumber1.resultactivity.SIS_DESCRIPTION_TO_RESULT_ADDRESS";
    private final String SIS_RESULT_USER_LOCATION_VISIBLE = "by.supercoder.tasknumber1.resultactivity.SIS_RESULT_USER_LOCATION_VISIBLE";
    private final String SIS_RESULT_USER_LATLNG = "by.supercoder.tasknumber1.resultactivity.SIS_RESULT_USER_LATLNG";
    private final String SIS_RESULT_USER_LOCATION_DESCRIPTION = "by.supercoder.tasknumber1.resultactivity.SIS_RESULT_USER_LOCATION_DESCRIPTION";
    private final String SIS_IS_PATH_CREATED = "by.supercoder.tasknumber1.resultactivity.SIS_IS_PATH_CREATED";
    private final String SIS_COUNT_PATH_POINT = "by.supercoder.tasknumber1.resultactivity.SIS_COUNT_PATH_POINT";
    private final String SIS_PATH_POINT = "by.supercoder.tasknumber1.resultactivity.SIS_PATH_POINT";


    private LatLng mFromLatLng;
    private String mFromDescription;
    private LatLng mToLatLng;
    private String mToDescription;
    private LatLng mUserLocationLatLng;
    private String mUserLocationDescription;
    private Marker mFromMarker;
    private Marker mToMarker;
    private Marker mUserLocationMarker;
    private boolean mUserLocationVisible = false;

    private GoogleMap mMap;
    private MapView mMapView;

    private ArrayList<LatLng> mPathArray = null;

    public ShowPathOnMapFragment() {
        // Required empty public constructor
    }

    public static ShowPathOnMapFragment newInstance(String param1, String param2) {
        return new ShowPathOnMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPathArray = new ArrayList<LatLng>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_path_on_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) view.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            mMapView.onStart();

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mMapView != null) {
            mMapView.onStart();
            mMapView.getMapAsync(this);
        }
        refreshAllObjectsOnMap();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        mMap.clear();
        mMap = null;

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        refreshAllObjectsOnMap();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDetach() {
        mFromLatLng = null;
        mToLatLng = null;
        mUserLocationLatLng = null;
        mFromMarker = null;
        mToMarker = null;
        mUserLocationMarker = null;
        mMapView = null;
        mPathArray.clear();
        mPathArray = null;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mMap = googleMap;
        refreshAllObjectsOnMap();
    }

    private void refreshAllObjectsOnMap() {
        if (mFromLatLng != null && mToLatLng != null && mMap != null) {
            mFromMarker = createMarker(mFromLatLng, getFromDescription());
            mToMarker = createMarker(mToLatLng, getToDescription());
            if (isUserLocationVisible()) {
                mUserLocationMarker = createMarker(mUserLocationLatLng, getUserLocationDescription());
            }
            drawPath();
            ShowAllMarkersOnMap();
        }
    }

    public void ShowAddress(Address address) {
        String addressDescription = AddressListAdapter.getAddressDescription(address);
        mMap.clear();
        LatLng coordinatesMarker = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(coordinatesMarker).title(addressDescription));
    }

    public void clearAllMarkers() {
        mMap.clear();
    }

    public void setLatLngFrom(LatLng latLng) {
        mFromLatLng = latLng;
    }

    public void setLatLngTo(LatLng latLng) {
        mToLatLng = latLng;
    }

    public void setUserLocationLatLng(LatLng latLng) {
        mUserLocationLatLng = latLng;
    }

    Marker createMarker(LatLng markerCoordinates, String description) {
        return mMap.addMarker(new MarkerOptions().position(markerCoordinates).title(description));
    }

    private void ShowAllMarkersOnMap() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        builder.include(mFromMarker.getPosition());
        builder.include(mToMarker.getPosition());
        if (isUserLocationVisible()) {
            builder.include(mUserLocationMarker.getPosition());
        }

        LatLngBounds bounds = builder.build();
        int width = getView().getWidth();
        int height = getView().getHeight();
        CameraUpdate cameraUpdate = null;
        if(width != 0 && height != 0) {
            int padding = (int) (width * 0.20); // offset from edges of the map 20% of screen
            if (padding * 2 >= height) {
                padding = (int) (height * 0.20);
            }
            cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.moveCamera(cameraUpdate);
        }
    }

    public boolean isUserLocationVisible() {
        return mUserLocationVisible;
    }

    public void setUserLocationVisible(boolean userLocationVisible) {
        this.mUserLocationVisible = userLocationVisible;
    }

    public void setPathArray(ArrayList<LatLng> pathArray) {
        mPathArray = pathArray;
        refreshAllObjectsOnMap();
    }

    public String getFromDescription() {
        return mFromDescription;
    }

    public void setFromDescription(String fromDescription) {
        mFromDescription = fromDescription;
    }

    public String getToDescription() {
        return mToDescription;
    }

    public void setToDescription(String toDescription) {
        mToDescription = toDescription;
    }

    public String getUserLocationDescription() {
        return mUserLocationDescription;
    }

    public void setUserLocationDescription(String userLocationDescription) {
        mUserLocationDescription = userLocationDescription;
    }

    private void drawPath() {
        if (mMap == null) return;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        final int WIDTH_PATH = 10;
        if (mPathArray != null) {
            PolylineOptions polylineOptions = new PolylineOptions();
            for (LatLng point : mPathArray) {
                polylineOptions.add(point);
            }
            polylineOptions.width(WIDTH_PATH);
            mMap.addPolyline(polylineOptions);
        } else {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.add(mFromMarker.getPosition());
            polylineOptions.add(mToMarker.getPosition());
            polylineOptions.width(WIDTH_PATH);
            mMap.addPolyline(polylineOptions);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(SIS_LATLNG_FROM_RESULT_ADDRESS, mFromLatLng);
        savedInstanceState.putString(SIS_DESCRIPTION_FROM_RESULT_ADDRESS, mFromDescription.toString());
        savedInstanceState.putParcelable(SIS_LATLNG_TO_RESULT_ADDRESS, mToLatLng);
        savedInstanceState.putString(SIS_DESCRIPTION_TO_RESULT_ADDRESS, mToDescription.toString());
        savedInstanceState.putBoolean(SIS_RESULT_USER_LOCATION_VISIBLE, mUserLocationVisible);
        if (mUserLocationVisible) {
            savedInstanceState.putParcelable(SIS_RESULT_USER_LATLNG, mUserLocationLatLng);
            savedInstanceState.putString(SIS_RESULT_USER_LOCATION_DESCRIPTION, mUserLocationDescription);
        }
        boolean isPathCreated = false;
        final int PATH_POINT_MINIMAL = 2;
        if (mPathArray != null && mPathArray.size() >= PATH_POINT_MINIMAL) {
            isPathCreated = true;
        }
        savedInstanceState.putBoolean(SIS_IS_PATH_CREATED, isPathCreated);
        if (isPathCreated) {
            int countPathPoint = 0;
            countPathPoint = mPathArray.size();
            savedInstanceState.putInt(SIS_COUNT_PATH_POINT, countPathPoint);
            for (int i = 0; i < countPathPoint; i++) {
                savedInstanceState.putParcelable(SIS_PATH_POINT + i, mPathArray.get(i));
            }
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            try {
                mFromLatLng = savedInstanceState.getParcelable(SIS_LATLNG_FROM_RESULT_ADDRESS);
            } catch (NullPointerException e) {
                mFromLatLng = null;
            }

            try {
                mFromDescription = savedInstanceState.getString(SIS_DESCRIPTION_FROM_RESULT_ADDRESS);
            } catch (NullPointerException e) {
                mFromDescription = null;
            }

            try {
                mToLatLng = savedInstanceState.getParcelable(SIS_LATLNG_TO_RESULT_ADDRESS);
            } catch (NullPointerException e) {
                mToLatLng = null;
            }

            try {
                mToDescription = savedInstanceState.getString(SIS_DESCRIPTION_TO_RESULT_ADDRESS);
            } catch (NullPointerException e) {
                mToDescription = null;
            }

            try {
                mUserLocationVisible = savedInstanceState.getBoolean(SIS_RESULT_USER_LOCATION_VISIBLE);
            } catch (NullPointerException e) {
                mUserLocationVisible = false;
            }

            if (mUserLocationVisible) {
                try {
                    mUserLocationLatLng = savedInstanceState.getParcelable(SIS_RESULT_USER_LATLNG);
                } catch (NullPointerException e) {
                    mUserLocationLatLng = null;
                }
                try {
                    mUserLocationDescription = savedInstanceState.getString(SIS_RESULT_USER_LOCATION_DESCRIPTION);
                } catch (NullPointerException e) {
                    mUserLocationDescription = "";
                }
            }
            boolean isPathCreated = false;
            try {
                isPathCreated = savedInstanceState.getBoolean(SIS_IS_PATH_CREATED);
            } catch (NullPointerException e) {
                isPathCreated = false;
            }
            if (isPathCreated) {
                int countPathPoint = 0;
                try {
                    countPathPoint = savedInstanceState.getInt(SIS_COUNT_PATH_POINT);
                } catch (NullPointerException e) {
                    countPathPoint = 0;
                }
                for (int i = 0; i < countPathPoint; i++) {
                    LatLng currentPoint = null;
                    try {
                        currentPoint = savedInstanceState.getParcelable(SIS_PATH_POINT + i);
                    } catch (NullPointerException e) {
                        currentPoint = null;
                    }
                    mPathArray.add(currentPoint);
                }
            }
            refreshAllObjectsOnMap();
        }
    }
}
