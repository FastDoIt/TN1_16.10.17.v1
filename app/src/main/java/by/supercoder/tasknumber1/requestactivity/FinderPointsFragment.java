package by.supercoder.tasknumber1.requestactivity;


import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import by.supercoder.tasknumber1.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FinderPointsFragment extends Fragment {

    // For restoreInstanceState of the fragment,
    private final static String SIS_FINDER_POINTS_FRAGMENT = "SIS_FINDER_POINTS_FRAGMENT";

    public static final String TAG_ADDRESS_TO_LIST_RESULTS_FRAGMENT = "TAG_ADDRESS_TO_LIST_RESULTS_FRAGMENT";

    public static final String TAG_SHOW_POINT_ON_MAP_FRAGMENT = "TAG_SHOW_POINT_ON_MAP_FRAGMENT";
    private int mTagAddressToListResultsFragment = 0;
    private SelectedPathPointsObserver mSelectedPathPointsObserverAddressToListResultsFragment;


    public FinderPointsFragment() {
        super();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            AddressToListResultsFragment addressToListResultsFragment = null;
            addressToListResultsFragment = (AddressToListResultsFragment) getChildFragmentManager().findFragmentByTag(TAG_ADDRESS_TO_LIST_RESULTS_FRAGMENT);
            if (addressToListResultsFragment == null) {
                FragmentTransaction fragmentTransaction;
                addressToListResultsFragment = new AddressToListResultsFragment();
                addressToListResultsFragment.setTagAddress(getTagAddressToListResultsFragment());
                addressToListResultsFragment.addObserver(getSelectedPathPointsObserverAddressToListResultsFragment());
                fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerAddressToListResult, addressToListResultsFragment, TAG_ADDRESS_TO_LIST_RESULTS_FRAGMENT);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();
            } else {
                addressToListResultsFragment.setTagAddress(mTagAddressToListResultsFragment);
            }
            ShowPointOnMapFragment showPointOnMapFragment = null;
            showPointOnMapFragment = (ShowPointOnMapFragment) getChildFragmentManager().findFragmentByTag(TAG_SHOW_POINT_ON_MAP_FRAGMENT);
            if (showPointOnMapFragment == null) {
                showPointOnMapFragment = new ShowPointOnMapFragment();
                FragmentTransaction fragmentTransaction;
                fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerShowerOnMap, showPointOnMapFragment, TAG_SHOW_POINT_ON_MAP_FRAGMENT);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();
            }
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finder_points, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        AddressToListResultsFragment addressToListResultsFragment = null;
        addressToListResultsFragment = (AddressToListResultsFragment) getChildFragmentManager().findFragmentByTag(TAG_ADDRESS_TO_LIST_RESULTS_FRAGMENT);
        if (addressToListResultsFragment != null) {
            addressToListResultsFragment.setTagAddress(getTagAddressToListResultsFragment());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        setSelectedPathPointsObserverAddressToListResultsFragment(null);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(SIS_FINDER_POINTS_FRAGMENT, SIS_FINDER_POINTS_FRAGMENT);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void initFinderPointFragment(int tagAddressToListResultsFragment, SelectedPathPointsObserver selectedPathPointsObserver) {
        setTagAddressToListResultsFragment(tagAddressToListResultsFragment);
        mSelectedPathPointsObserverAddressToListResultsFragment = selectedPathPointsObserver;
    }

    public int getTagAddressToListResultsFragment() {
        return mTagAddressToListResultsFragment;
    }

    public void setTagAddressToListResultsFragment(int tagAddressToListResultsFragment) {
        this.mTagAddressToListResultsFragment = tagAddressToListResultsFragment;
    }

    public SelectedPathPointsObserver getSelectedPathPointsObserverAddressToListResultsFragment() {
        return mSelectedPathPointsObserverAddressToListResultsFragment;
    }

    public void setSelectedPathPointsObserverAddressToListResultsFragment(SelectedPathPointsObserver selectedPathPointsObserverAddressToListResultsFragment) {
        this.mSelectedPathPointsObserverAddressToListResultsFragment = selectedPathPointsObserverAddressToListResultsFragment;
        AddressToListResultsFragment addressToListResultsFragment = null;
        addressToListResultsFragment = (AddressToListResultsFragment) getChildFragmentManager().findFragmentByTag(TAG_ADDRESS_TO_LIST_RESULTS_FRAGMENT);
        if (addressToListResultsFragment != null) {
            addressToListResultsFragment.addObserver(selectedPathPointsObserverAddressToListResultsFragment);
            addressToListResultsFragment.setChanged();
            addressToListResultsFragment.notifyObservers();
        }
    }

    public void removeSelectedPathPointsObserverAddressToListResultsFragment() {
        AddressToListResultsFragment addressToListResultsFragment = null;
        addressToListResultsFragment = (AddressToListResultsFragment) getChildFragmentManager().findFragmentByTag(TAG_ADDRESS_TO_LIST_RESULTS_FRAGMENT);
        if (addressToListResultsFragment != null) {
            addressToListResultsFragment.removeObservers();
        }
    }

    public void restoreSelectedPoint(boolean restoreValue){
        AddressToListResultsFragment addressToListResultsFragment = null;
        addressToListResultsFragment = (AddressToListResultsFragment) getChildFragmentManager().findFragmentByTag(TAG_ADDRESS_TO_LIST_RESULTS_FRAGMENT);
        if (addressToListResultsFragment != null) {
            addressToListResultsFragment.setSelectedPoint(restoreValue);
        }
    }
}