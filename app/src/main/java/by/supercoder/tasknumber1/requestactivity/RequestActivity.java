package by.supercoder.tasknumber1.requestactivity;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.location.Address;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import by.supercoder.tasknumber1.R;
import by.supercoder.tasknumber1.resultactivity.MapsActivity;
import by.supercoder.tasknumber1.resultactivity.StringEquals;

public class RequestActivity extends AppCompatActivity
        implements AddressToListResultsFragment.ShowPointOnMapListener,
        AddressToListResultsFragment.ClearMarkerOnMapListener,
        View.OnClickListener,
        DeviceLocationManager.OnDeviceLocationFoundListener,
        PermissionManager.OnResultOfRequestPermissionsListener,
        UserInterfaceManager.OnEnabledShowPathButtonListener {


    private static final String FROM_TAG_FINDER_POINTS_FRAGMENT = "by.supercoder.tasknumber1.requestactivity.FROM_TAG_FINDER_POINTS_FRAGMENT";
    private static final String TO_TAG_FINDER_POINTS_FRAGMENT = "by.supercoder.tasknumber1.requestactivity.TO_TAG_FINDER_POINTS_FRAGMENT";
    private static final String FROM_TAG_TAB_SPEC = "by.supercoder.tasknumber1.requestactivity.FROM_TAG_TAB_SPEC";
    private static final String TO_TAG_TAB_SPEC = "by.supercoder.tasknumber1.requestactivity.TO_TAG_TAB_SPEC";

    private final static String SIS_CURRENT_TAB = "by.supercoder.tasknumber1.requestactivity.SIS_CURRENT_TAB";
    private final static String SIS_IS_ADDRESS_FROM_SELECTED = "by.supercoder.tasknumber1.requestactivity.SIS_IS_ADDRESS_FROM_SELECTED";
    private final static String SIS_IS_ADDRESS_TO_SELECTED = "by.supercoder.tasknumber1.requestactivity.SIS_IS_ADDRESS_TO_SELECTED";
    private final static String SIS_ADDRESS_FROM_PATH_POINT_DATA_PROVIDER = "by.supercoder.tasknumber1.requestactivity.SIS_ADDRESS_FROM_PATH_POINT_DATA_PROVIDER";
    private final static String SIS_ADDRESS_TO_PATH_POINT_DATA_PROVIDER = "by.supercoder.tasknumber1.requestactivity.SIS_ADDRESS_TO_PATH_POINT_DATA_PROVIDER";

    private static int FROM_TAB_INDEX = 0;
    private static int TO_TAB_INDEX = 1;
    private PathPointsDataProvider pathPointsDataProvider;
    private PermissionManager mPermissionManager;
    private DeviceLocationManager mDeviceLocationManager;
    private UserInterfaceManager mUserInterfaceManager;

    private Button buttonShowPath;

    private int mBackupSelectedTab;


    //----------------------------------------------------------------------------------------------
    //                              Methods of lifecycle activity
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Remove splash theme
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        setTitle(getString(R.string.title_request_activity));

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec = null;
        tabHost.getTabWidget().getTabCount();
        tabHost.getTabWidget().getTag();

        tabSpec = tabHost.newTabSpec(FROM_TAG_TAB_SPEC);
        tabSpec.setContent(R.id.tab_from);
        tabSpec.setIndicator(getString(R.string.tab_from_name).toString());
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec(TO_TAG_TAB_SPEC);
        tabSpec.setContent(R.id.tab_to);
        tabSpec.setIndicator(getString(R.string.tab_to_name).toString());

        tabHost.addTab(tabSpec);

        mUserInterfaceManager = new UserInterfaceManager();
        pathPointsDataProvider = new PathPointsDataProvider();
        if (savedInstanceState != null) {
            int backupSelectedTab = FROM_TAB_INDEX;
            try {
                backupSelectedTab = savedInstanceState.getInt(SIS_CURRENT_TAB);
            } catch (NullPointerException e) {
                backupSelectedTab = FROM_TAB_INDEX;
            }
            setBackupSelectTab(backupSelectedTab);

            Address backupAddressFrom;
            try {
                backupAddressFrom = savedInstanceState.getParcelable(SIS_ADDRESS_FROM_PATH_POINT_DATA_PROVIDER);
            } catch (NullPointerException e) {
                backupAddressFrom = null;
            }
            pathPointsDataProvider.setFromAddress(backupAddressFrom);
            Address backupAddressTo;
            try {
                backupAddressTo = savedInstanceState.getParcelable(SIS_ADDRESS_TO_PATH_POINT_DATA_PROVIDER);
            } catch (NullPointerException e) {
                backupAddressTo = null;
            }
            pathPointsDataProvider.setToAddress(backupAddressTo);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        if (getBackupSelectedTab() == FROM_TAB_INDEX || getBackupSelectedTab() == TO_TAB_INDEX) {
            tabHost.setCurrentTab(getBackupSelectedTab());
        }
        if (tabHost.getCurrentTabTag() == FROM_TAG_TAB_SPEC) {
            FinderPointsFragment finderPointsFragmentFrom = null;
            finderPointsFragmentFrom = (FinderPointsFragment) getSupportFragmentManager().findFragmentByTag(FROM_TAG_FINDER_POINTS_FRAGMENT);
            if (finderPointsFragmentFrom == null) {
                finderPointsFragmentFrom = new FinderPointsFragment();
                finderPointsFragmentFrom.initFinderPointFragment(TAG_ADDRESS_FROM, mUserInterfaceManager);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerFinderPointsFrom, finderPointsFragmentFrom, FROM_TAG_FINDER_POINTS_FRAGMENT);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();
            }
        } else if (tabHost.getCurrentTabTag() == TO_TAG_TAB_SPEC) {
            FinderPointsFragment finderPointsFragmentTo = null;
            finderPointsFragmentTo = (FinderPointsFragment) getSupportFragmentManager().findFragmentByTag(TO_TAG_FINDER_POINTS_FRAGMENT);
            if (finderPointsFragmentTo == null) {
                finderPointsFragmentTo = new FinderPointsFragment();
                finderPointsFragmentTo.initFinderPointFragment(TAG_ADDRESS_TO, mUserInterfaceManager);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerFinderPointsTo, finderPointsFragmentTo, TO_TAG_FINDER_POINTS_FRAGMENT);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        boolean isAddressFromSelected = false;
        try {
            isAddressFromSelected = savedInstanceState.getBoolean(SIS_IS_ADDRESS_FROM_SELECTED);
        } catch (NullPointerException e) {
            isAddressFromSelected = false;
        }
        boolean isAddressToSelected = false;
        try {
            isAddressToSelected = savedInstanceState.getBoolean(SIS_IS_ADDRESS_TO_SELECTED);
        } catch (NullPointerException e) {
            isAddressToSelected = false;
        }
        mUserInterfaceManager.init(isAddressFromSelected, isAddressToSelected);

        buttonShowPath = (Button) findViewById(R.id.buttonShowPath);
        FinderPointsFragment finderPointsFragmentFrom = null;
        finderPointsFragmentFrom = (FinderPointsFragment) getSupportFragmentManager().findFragmentByTag(FROM_TAG_FINDER_POINTS_FRAGMENT);
        if (finderPointsFragmentFrom != null) {
            finderPointsFragmentFrom.restoreSelectedPoint(isAddressFromSelected);
        }
        FinderPointsFragment finderPointsFragmentTo = null;
        finderPointsFragmentTo = (FinderPointsFragment) getSupportFragmentManager().findFragmentByTag(TO_TAG_FINDER_POINTS_FRAGMENT);
        if (finderPointsFragmentTo != null) {
            finderPointsFragmentTo.restoreSelectedPoint(isAddressToSelected);
        }

        boolean enabledShowPathButton = false;
        enabledShowPathButton = mUserInterfaceManager.hasPathSelected();
        buttonShowPath.setEnabled(enabledShowPathButton);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillAllReferences();
    }

    private void fillAllReferences() {

        buttonShowPath = (Button) findViewById(R.id.buttonShowPath);
        buttonShowPath.setOnClickListener(this);


        mUserInterfaceManager.setOnEnabledShowPathButton(
                    new UserInterfaceManager.OnEnabledShowPathButtonListener() {
                        @Override
                        public void OnEnabledShowPathButton(boolean enabledButton) {
                            if (buttonShowPath != null) {
                                buttonShowPath.setEnabled(enabledButton);
                            }
                        }
                    });


        // To deactivate a button when the list of addresses is empty or the item in the list is not selected
        FinderPointsFragment finderPointsFragmentFrom = null;
        finderPointsFragmentFrom = (FinderPointsFragment) getSupportFragmentManager().findFragmentByTag(FROM_TAG_FINDER_POINTS_FRAGMENT);
        if (finderPointsFragmentFrom != null) {
            finderPointsFragmentFrom.setTagAddressToListResultsFragment(TAG_ADDRESS_FROM);
            finderPointsFragmentFrom.setSelectedPathPointsObserverAddressToListResultsFragment(mUserInterfaceManager);

        }
        FinderPointsFragment finderPointsFragmentTo = null;
        finderPointsFragmentTo = (FinderPointsFragment) getSupportFragmentManager().findFragmentByTag(TO_TAG_FINDER_POINTS_FRAGMENT);
        if (finderPointsFragmentTo != null) {
            finderPointsFragmentTo.setTagAddressToListResultsFragment(TAG_ADDRESS_TO);
            finderPointsFragmentTo.setSelectedPathPointsObserverAddressToListResultsFragment(mUserInterfaceManager);

        }

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        tabHost.setCurrentTab(getBackupSelectedTab());
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tagTabSpec) {
                if (StringEquals.equals(tagTabSpec, FROM_TAG_TAB_SPEC)) {
                    setBackupSelectTab(FROM_TAB_INDEX);
                    FinderPointsFragment finderPointsFragmentFrom = null;
                    finderPointsFragmentFrom = (FinderPointsFragment) getSupportFragmentManager().findFragmentByTag(FROM_TAG_FINDER_POINTS_FRAGMENT);
                    if (finderPointsFragmentFrom == null) {
                        finderPointsFragmentFrom = new FinderPointsFragment();
                        finderPointsFragmentFrom.initFinderPointFragment(TAG_ADDRESS_FROM, mUserInterfaceManager);
                        FragmentTransaction fragmentTransaction = null;
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();

                        fragmentTransaction.replace(R.id.fragmentContainerFinderPointsFrom, finderPointsFragmentFrom, FROM_TAG_FINDER_POINTS_FRAGMENT);
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        fragmentTransaction.commit();
                    }

                } else if (StringEquals.equals(tagTabSpec, TO_TAG_TAB_SPEC)) {
                    setBackupSelectTab(TO_TAB_INDEX);
                    FinderPointsFragment finderPointsFragmentTo = null;
                    finderPointsFragmentTo = (FinderPointsFragment) getSupportFragmentManager().findFragmentByTag(TO_TAG_FINDER_POINTS_FRAGMENT);
                    if (finderPointsFragmentTo == null) {
                        finderPointsFragmentTo = new FinderPointsFragment();
                        finderPointsFragmentTo.initFinderPointFragment(TAG_ADDRESS_TO, mUserInterfaceManager);
                        FragmentTransaction fragmentTransaction = null;
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentContainerFinderPointsTo, finderPointsFragmentTo, TO_TAG_FINDER_POINTS_FRAGMENT);
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        fragmentTransaction.commit();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearAllReferences();
    }

    private void clearAllReferences() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setOnTabChangedListener(null);

        if (mUserInterfaceManager != null) {
            mUserInterfaceManager.setOnEnabledShowPathButton(null);
        }

        if (mDeviceLocationManager != null) {
            mDeviceLocationManager.setParentActivity(null);
            mDeviceLocationManager.setDeviceLocationFoundListener(null);
            mDeviceLocationManager = null;
        }

        if (buttonShowPath != null) {
            buttonShowPath.setOnClickListener(null);
            buttonShowPath = null;
        }

        // To deactivate a button when the list of addresses is empty or the item in the list is not selected
        FinderPointsFragment finderPointsFragmentFrom = null;
        finderPointsFragmentFrom = (FinderPointsFragment) getSupportFragmentManager().findFragmentByTag(FROM_TAG_FINDER_POINTS_FRAGMENT);
        if (finderPointsFragmentFrom != null) {
            finderPointsFragmentFrom.removeSelectedPathPointsObserverAddressToListResultsFragment();
        }
        FinderPointsFragment finderPointsFragmentTo = null;
        finderPointsFragmentTo = (FinderPointsFragment) getSupportFragmentManager().findFragmentByTag(TO_TAG_FINDER_POINTS_FRAGMENT);
        if (finderPointsFragmentTo != null) {
            finderPointsFragmentTo.removeSelectedPathPointsObserverAddressToListResultsFragment();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearAllReferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deletePermissionManager();
        mUserInterfaceManager.setOnEnabledShowPathButton(null);
        mUserInterfaceManager = null;
        clearAllReferences();
        pathPointsDataProvider = null;
    }

    //----------------------------------------------------------------------------------------------
    //                              Methods of implemented interfaces
    //----------------------------------------------------------------------------------------------

    @Override
    public void showPointOnMap(int tag, Address address) {

        if (tag == TAG_ADDRESS_FROM) {
            // Save address
            pathPointsDataProvider.setFromAddress(address);

            // Show address
            FinderPointsFragment finderPointsFragmentFrom = null;
            finderPointsFragmentFrom = (FinderPointsFragment) getSupportFragmentManager().findFragmentByTag(FROM_TAG_FINDER_POINTS_FRAGMENT);
            ShowPointOnMapFragment showerPointOnMapFragment = (ShowPointOnMapFragment) finderPointsFragmentFrom.getChildFragmentManager().findFragmentByTag(FinderPointsFragment.TAG_SHOW_POINT_ON_MAP_FRAGMENT);
            showerPointOnMapFragment.showAddress(address);

        }
        if (tag == TAG_ADDRESS_TO) {
            // Save address
            pathPointsDataProvider.setToAddress(address);

            // Show address
            FinderPointsFragment finderPointsFragmentTo = null;
            finderPointsFragmentTo = (FinderPointsFragment) getSupportFragmentManager().findFragmentByTag(TO_TAG_FINDER_POINTS_FRAGMENT);
            ShowPointOnMapFragment showerPointOnMapFragment = (ShowPointOnMapFragment) finderPointsFragmentTo.getChildFragmentManager().findFragmentByTag(FinderPointsFragment.TAG_SHOW_POINT_ON_MAP_FRAGMENT);
            showerPointOnMapFragment.showAddress(address);
        }
    }

    @Override
    public void ClearMarkerOnMap(int tag) {
        if (tag == TAG_ADDRESS_FROM) {
            // Save address
            pathPointsDataProvider.setFromAddress(null);

            // Show address
            FinderPointsFragment finderPointsFragmentFrom = null;
            finderPointsFragmentFrom = (FinderPointsFragment) getSupportFragmentManager().findFragmentByTag(FROM_TAG_FINDER_POINTS_FRAGMENT);
            ShowPointOnMapFragment showerPointOnMapFragment = (ShowPointOnMapFragment) finderPointsFragmentFrom.getChildFragmentManager().findFragmentByTag(FinderPointsFragment.TAG_SHOW_POINT_ON_MAP_FRAGMENT);
            showerPointOnMapFragment.ClearMarker();
        }
        if (tag == TAG_ADDRESS_TO) {
            // Save address
            pathPointsDataProvider.setToAddress(null);

            // Show address
            FinderPointsFragment finderPointsFragmentTo = null;
            finderPointsFragmentTo = (FinderPointsFragment) getSupportFragmentManager().findFragmentByTag(TO_TAG_FINDER_POINTS_FRAGMENT);
            ShowPointOnMapFragment showerPointOnMapFragment = (ShowPointOnMapFragment) finderPointsFragmentTo.getChildFragmentManager().findFragmentByTag(FinderPointsFragment.TAG_SHOW_POINT_ON_MAP_FRAGMENT);
            showerPointOnMapFragment.ClearMarker();
        }
    }

    @Override
    public void onDeviceLocationFound(LatLng deviceLocation) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra(PathPointsDataProvider.LATITUDE_FROM_RESULT_ADDRESS, pathPointsDataProvider.getFromAddress().getLatitude());
        intent.putExtra(PathPointsDataProvider.LONGITUDE_FROM_RESULT_ADDRESS, pathPointsDataProvider.getFromAddress().getLongitude());
        intent.putExtra(PathPointsDataProvider.DESCRIPTION_FROM_RESULT_ADDRESS, FormatAddress.getFormatDescription(pathPointsDataProvider.getFromAddress()));
        intent.putExtra(PathPointsDataProvider.LATITUDE_TO_RESULT_ADDRESS, pathPointsDataProvider.getToAddress().getLatitude());
        intent.putExtra(PathPointsDataProvider.LONGITUDE_TO_RESULT_ADDRESS, pathPointsDataProvider.getToAddress().getLongitude());
        intent.putExtra(PathPointsDataProvider.DESCRIPTION_TO_RESULT_ADDRESS, FormatAddress.getFormatDescription(pathPointsDataProvider.getToAddress()));

        intent.putExtra(PathPointsDataProvider.RESULT_USER_LOCATION_STATUS, PathPointsDataProvider.RESULT_WITH_USER_LOCATION);
        intent.putExtra(PathPointsDataProvider.RESULT_USER_LATLNG, deviceLocation);

        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view != null && view.getId() == R.id.buttonShowPath) {
            createPermissionManager();
            mPermissionManager.executeAction();
        }
    }

    @Override
    public void onNoLocationDetected() {
        onAvailableWork();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        deletePermissionManager();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPermissionManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFullWork() {
        createDeviceManager();
    }

    @Override
    public void onAvailableWork() {
        Toast.makeText(this, "Found", Toast.LENGTH_SHORT);
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra(PathPointsDataProvider.LATITUDE_FROM_RESULT_ADDRESS, pathPointsDataProvider.getFromAddress().getLatitude());
        intent.putExtra(PathPointsDataProvider.LONGITUDE_FROM_RESULT_ADDRESS, pathPointsDataProvider.getFromAddress().getLongitude());
        intent.putExtra(PathPointsDataProvider.DESCRIPTION_FROM_RESULT_ADDRESS, FormatAddress.getFormatDescription(pathPointsDataProvider.getFromAddress()));
        intent.putExtra(PathPointsDataProvider.LATITUDE_TO_RESULT_ADDRESS, pathPointsDataProvider.getToAddress().getLatitude());
        intent.putExtra(PathPointsDataProvider.LONGITUDE_TO_RESULT_ADDRESS, pathPointsDataProvider.getToAddress().getLongitude());
        intent.putExtra(PathPointsDataProvider.DESCRIPTION_TO_RESULT_ADDRESS, FormatAddress.getFormatDescription(pathPointsDataProvider.getToAddress()));
        intent.putExtra(PathPointsDataProvider.RESULT_USER_LOCATION_STATUS, PathPointsDataProvider.RESULT_WITHOUT_USER_LOCATION);
        startActivity(intent);
    }

    @Override
    public void OnEnabledShowPathButton(boolean enabledButton) {
        buttonShowPath.setEnabled(enabledButton);
    }


    //----------------------------------------------------------------------------------------------
    //                             Methods of RequestActivity class
    //----------------------------------------------------------------------------------------------


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(SIS_CURRENT_TAB, getBackupSelectedTab());
        savedInstanceState.putBoolean(SIS_IS_ADDRESS_FROM_SELECTED, mUserInterfaceManager.isAddressFromSelected());
        savedInstanceState.putBoolean(SIS_IS_ADDRESS_TO_SELECTED, mUserInterfaceManager.isAddressToSelected());
        savedInstanceState.putParcelable(SIS_ADDRESS_FROM_PATH_POINT_DATA_PROVIDER, pathPointsDataProvider.getFromAddress());
        savedInstanceState.putParcelable(SIS_ADDRESS_TO_PATH_POINT_DATA_PROVIDER, pathPointsDataProvider.getToAddress());
        super.onSaveInstanceState(savedInstanceState);
    }

    private void createPermissionManager() {
        mPermissionManager = null;
        mPermissionManager = new PermissionManager(this);
        mPermissionManager.setExplanationRunAvailableWork(this.getString(R.string.explanation_run_available_work_location).toString());
        mPermissionManager.setPermissionIsntGranted(this.getString(R.string.permission_isnt_granted_location).toString());
        mPermissionManager.setExplanationSettingsActivity(this.getString(R.string.explanation_settings_activity_location).toString());
        mPermissionManager.setPermissionExplanation(this.getString(R.string.location_permission_explanation).toString());
        mPermissionManager.setResultOfRequestPermissionsCallBack(this);
    }
    private void deletePermissionManager(){
        if(mPermissionManager != null){
            mPermissionManager.clearAllReferences();
            mPermissionManager = null;
        }
    }

    private void createDeviceManager() {
        mDeviceLocationManager = null;
        mDeviceLocationManager = new DeviceLocationManager(this);
        mDeviceLocationManager.setDeviceLocationFoundListener(this);
        mDeviceLocationManager.getLastLocation();
    }

    public UserInterfaceManager getUserInterfaceManager() {
        return mUserInterfaceManager;
    }

    public void setUserInterfaceManager(UserInterfaceManager userInterfaceManager) {
        this.mUserInterfaceManager = userInterfaceManager;
    }

    private int getBackupSelectedTab() {
        return mBackupSelectedTab;
    }

    private void setBackupSelectTab(int selectedTab) {
        this.mBackupSelectedTab = selectedTab;
    }
}




