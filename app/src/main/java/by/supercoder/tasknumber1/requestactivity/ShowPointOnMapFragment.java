package by.supercoder.tasknumber1.requestactivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.location.Address;
//import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import by.supercoder.tasknumber1.R;

public class ShowPointOnMapFragment extends Fragment implements OnMapReadyCallback {
    private final static String SIS_BACKUP_ADDRESS = "by.supercoder.tasknumber1.requestactivity.SIS_BACKUP_ADDRESS";

    private GoogleMap mMap;
    private MapView mMapView;
    private Address mBackupAddress;


    public ShowPointOnMapFragment() {
        // Required empty public constructor
        super();
    }

    // TODO: Rename and change types and number of parameters
    public static ShowPointOnMapFragment newInstance(String param1, String param2) {
        return new ShowPointOnMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_point_on_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            Address backupAddress = null;
            try {
                backupAddress = savedInstanceState.getParcelable(SIS_BACKUP_ADDRESS);
            } catch (NullPointerException e) {
                backupAddress = null;
            }
            setBackupAddress(backupAddress);
        }
    }

    private void vOnViewCreated() {
        View view = null;
        view = getView();
        if (view != null) {
            mMapView = (MapView) view.findViewById(R.id.map);
            mMapView.onCreate(null);
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        vOnViewCreated();
        if (mMapView != null) {
            mMapView.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        mMap = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMapView = null;
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(SIS_BACKUP_ADDRESS, getBackupAddress());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            Context context = getContext();
            if (context != null) {
                MapsInitializer.initialize(context);
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                UiSettings uiSettings = mMap.getUiSettings();
                uiSettings.setZoomControlsEnabled(true);
                if (getBackupAddress() != null) {
                    showAddress(getBackupAddress());
                }
            }
        }
    }

    public void showAddress(Address address) {
        if (mMap != null) {
            setBackupAddress(address);
            String addressDescription = AddressListAdapter.getAddressDescription(address);
            mMap.clear();
            LatLng coordinatesMarker = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(coordinatesMarker).title(addressDescription));

            final int ZOOM = 16;
            final int TILT = 0;
            CameraPosition cameraPosition = CameraPosition.builder().target(coordinatesMarker).zoom(ZOOM).bearing(0).tilt(TILT).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void ClearMarker() {
        if (mMap != null) {
            mMap.clear();

            // Reset view of map
            LatLng coordinatesNullPoint = new LatLng(0, 0);
            final int ZOOM = 1;
            final int TILT = 0;
            CameraPosition cameraPosition = CameraPosition.builder().target(coordinatesNullPoint).zoom(ZOOM).bearing(0).tilt(TILT).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            setBackupAddress(null);
        }
    }

    public Address getBackupAddress() {
        return mBackupAddress;
    }

    public void setBackupAddress(Address backupAddress) {
        this.mBackupAddress = backupAddress;
    }
}
