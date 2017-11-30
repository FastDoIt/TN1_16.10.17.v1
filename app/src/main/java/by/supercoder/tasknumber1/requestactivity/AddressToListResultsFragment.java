package by.supercoder.tasknumber1.requestactivity;


import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import by.supercoder.tasknumber1.R;
import by.supercoder.tasknumber1.resultactivity.StringEquals;


/**
 * A simple {@link Fragment} subclass.
 */


public class AddressToListResultsFragment extends Fragment
        implements View.OnKeyListener,
        AdapterView.OnItemClickListener,
        SelectedPathPointsObservable {

    // For restoreInstanceState of the fragment,
    private final static String SIS_ADDRESS_EDIT_TEXT = "by.supercoder.tasknumber1.requestactivity.ADDRESS_EDIT_TEXT";
    private final static String SIS_TAG_ADDRESS = "by.supercoder.tasknumber1.requestactivity.SIS_TAG_ADDRESS";

    private final static String SIS_SELECTED_INDEX = "by.supercoder.tasknumber1.requestactivity.SIS_SELECTED_INDEX";
    private final static String SIS_IS_SELECTED_POINT = "by.supercoder.tasknumber1.requestactivity.SIS_IS_SELECTED_POINT";
    private final static String SIS_COUND_ADDRESSES = "by.supercoder.tasknumber1.requestactivity.SIS_COUND_ADDRESSES";
    private final static String SIS_ADDRESS_NUMBER_ = "by.supercoder.tasknumber1.requestactivity.SIS_ADDRESS_NUMBER_";
    private final static String SIS_FIRST_VISIBLE_POSITION = "by.supercoder.tasknumber1.requestactivity.SIS_FIRST_VISIBLE_POSITION";

    interface ShowPointOnMapListener {
        // need set setTagAddress in TAG_ADDRESS_FROM or TAG_ADDRESS_TO
        int TAG_ADDRESS_FROM = 1;

        int TAG_ADDRESS_TO = 2;

        void showPointOnMap(int tag, Address address);
    }

    interface ClearMarkerOnMapListener {
        void ClearMarkerOnMap(int tag);
    }

    private int mTagAddress = 0;
    private ShowPointOnMapListener showPointOnMapListener;
    private ClearMarkerOnMapListener clearMarkerOnMapListener;
    private List<Address> mListFindAddresses;
    ListView foundPointsListView = null;
    private ArrayList<SelectedPathPointsObserver> mSelectedPathPointsObservers;
    private boolean mChanged = false;
    private boolean mSelectedPoint = false;
    private int mIndexSelectedPoint;
    private int mFirstVisiblePosition;


    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_to_list_results, container, false);
        if (view != null) {
            EditText addressEditText = (EditText) view.findViewById(R.id.addressEditText);
            addressEditText.setOnKeyListener(this);

            foundPointsListView = (ListView) view.findViewById(R.id.foundPointsListView);

            // Set of the empty view for listview
            View layoutListEmpty = inflater.inflate(R.layout.empty_list_result, container, false);
            layoutListEmpty.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayoutAddressToListResultFragment);
            linearLayout.addView(layoutListEmpty);
            foundPointsListView.setEmptyView(layoutListEmpty);
            foundPointsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            foundPointsListView.setOnItemClickListener(this);
        }
        if (savedInstanceState != null) {

            EditText addressEditText = (EditText) view.findViewById(R.id.addressEditText);
            String stringAddressEditText = "";
            try {
                stringAddressEditText = savedInstanceState.getString(SIS_ADDRESS_EDIT_TEXT);
            } catch (NullPointerException e) {
                stringAddressEditText = "";
            }
            addressEditText.setText(stringAddressEditText);

            int savedTagAdress = 0;
            try {
                savedTagAdress = savedInstanceState.getInt(SIS_TAG_ADDRESS);
            } catch (NullPointerException e) {
                savedTagAdress = 0;
            }
            setTagAddress(savedTagAdress);

            int countAddresses = 0;
            try {
                countAddresses = savedInstanceState.getInt(SIS_COUND_ADDRESSES);
            } catch (NullPointerException e) {
                countAddresses = 0;
            }
            mListFindAddresses = null;
            mListFindAddresses = new ArrayList<Address>();
            for (int i = 0; i < countAddresses; i++) {
                Address currentAddress = null;
                try {
                    currentAddress = savedInstanceState.getParcelable(SIS_ADDRESS_NUMBER_ + i);
                } catch (NullPointerException e) {
                    currentAddress = null;
                }
                if (currentAddress != null) {
                    mListFindAddresses.add(currentAddress);
                }
            }

            int selectedIndex = -1;
            try {
                selectedIndex = savedInstanceState.getInt(SIS_SELECTED_INDEX);
            } catch (NullPointerException e) {
                selectedIndex = -1;
            }
            setIndexSelectedPoint(selectedIndex);
            int mFirstVisiblePosition = 0;
            try {
                mFirstVisiblePosition = savedInstanceState.getInt(SIS_FIRST_VISIBLE_POSITION);
            } catch (NullPointerException e) {
                mFirstVisiblePosition = 0;
            }
            setFirstVisiblePosition(mFirstVisiblePosition);

        }
        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        Context context = null;
        context = getContext();
        extractListeners(context);
    }

    @Override
    public void onResume() {
        super.onResume();

        View view = null;
        view = getView();
        foundPointsListView = (ListView) getView().findViewById(R.id.foundPointsListView);
        if (view != null) {

            EditText addressEditText = (EditText) view.findViewById(R.id.addressEditText);
            addressEditText.setOnKeyListener(this);

            FragmentActivity activity = getActivity();
            AddressListAdapter addressListAdapter = null;

            boolean isFoundAddresses = true;
            if (mListFindAddresses == null) {
                mListFindAddresses = new ArrayList<Address>();
                isFoundAddresses = false;
            }
            addressListAdapter = new AddressListAdapter(activity, (ArrayList<Address>) mListFindAddresses);

            addressListAdapter.setSelectedItem(getIndexSelectedPoint());


            foundPointsListView.setAdapter(addressListAdapter);
            if (isFoundAddresses) {
                foundPointsListView.requestFocusFromTouch();
                foundPointsListView.setSelection(mFirstVisiblePosition);
            }

            if (isSelectedPoint()) {
                addressListAdapter.setSelectedItem(getIndexSelectedPoint());
            }

            RadioButton selectRadioButton = null;
            View currentView = foundPointsListView.getChildAt(getIndexSelectedPoint());
            if (currentView != null) {
                selectRadioButton = (RadioButton) currentView.findViewById(R.id.selectRadioButton);
                selectRadioButton.setChecked(true);
            }

            foundPointsListView.setItemChecked(getIndexSelectedPoint(), true);
        }
        if (foundPointsListView != null) {
            foundPointsListView.setOnItemClickListener(this);
            foundPointsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    setFirstVisiblePosition(firstVisibleItem);
                }

                public void onScrollStateChanged(AbsListView view, int scrollState) {

                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    }
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        showPointOnMapListener = null;
        foundPointsListView.setOnScrollListener(null);
        clearMarkerOnMapListener = null;
        foundPointsListView = null;

        EditText addressEditText = (EditText) getView().findViewById(R.id.addressEditText);
        addressEditText.setOnKeyListener(null);
        removeObservers();
    }

    @Override
    public void onStop() {
        super.onStop();
        showPointOnMapListener = null;
        if (foundPointsListView != null) {
            foundPointsListView.setOnScrollListener(null);
        }
        clearMarkerOnMapListener = null;
        foundPointsListView = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        showPointOnMapListener = null;
        clearMarkerOnMapListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        EditText addressEditText = (EditText) getView().findViewById(R.id.addressEditText);
        savedInstanceState.putInt(SIS_TAG_ADDRESS, getTagAddress());
        savedInstanceState.putString(SIS_ADDRESS_EDIT_TEXT, addressEditText.getText().toString());
        if (mListFindAddresses != null) {
            int countAddresses = 0;
            countAddresses = mListFindAddresses.size();
            savedInstanceState.putInt(SIS_COUND_ADDRESSES, countAddresses);
            for (int i = 0; i < countAddresses; i++) {
                savedInstanceState.putParcelable(SIS_ADDRESS_NUMBER_ + i, mListFindAddresses.get(i));
            }
            if (isSelectedPoint()) {
                savedInstanceState.putBoolean(SIS_IS_SELECTED_POINT, isSelectedPoint());
                savedInstanceState.putInt(SIS_SELECTED_INDEX, getIndexSelectedPoint());
            }
            savedInstanceState.putInt(SIS_FIRST_VISIBLE_POSITION, getFirstVisiblePosition());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        EditText editText = (EditText) view;
        String oldValue = editText.getText().toString();
        try {
            // If enter key
            if (editText.getId() == R.id.addressEditText && keyCode == KeyEvent.KEYCODE_ENTER &&
                    keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                final String EMPTY_TEXT = "";
                setSelectedPoint(false);
                // If text no empty
                if (!StringEquals.equals(oldValue, EMPTY_TEXT)) {
                    clearMarkerOnMapListener.ClearMarkerOnMap(getTagAddress());
                    ShowWaitingGeocodingProgressBar();
                    SearchLocationFrom(editText.getText().toString());
                } else {
                    // Clear list if empty text
                    foundPointsListView.setAdapter(null);
                    clearMarkerOnMapListener.ClearMarkerOnMap(getTagAddress());
                    mListFindAddresses = null;
                    mListFindAddresses = new ArrayList<Address>();
                }

                setChanged();
                notifyObservers();
                return true;
            }
        } catch (Exception e) {
            editText.setText(oldValue);
        }
        return false;
    }

    private void HideKeyboard() {
        View view = getView();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void SearchLocationFrom(String text) {

        FragmentActivity activity = getActivity();
        Geocoder geocoder = null;
        if (activity != null) {
            geocoder = new Geocoder(activity, Locale.getDefault());
            try {
                final int MAX_RESULT_LOCATIONS = 7;
                mListFindAddresses = geocoder.getFromLocationName(text, MAX_RESULT_LOCATIONS);
            } catch (IOException e) {
                e.printStackTrace();
                mListFindAddresses = null;
            }
            View view = getView();
            if (view != null) {
                foundPointsListView = (ListView) view.findViewById(R.id.foundPointsListView);
                AddressListAdapter addressListAdapter = null;
                foundPointsListView.setAdapter(addressListAdapter);

                ArrayList<Address> arrayListResultAddresses = null;
                if (mListFindAddresses == null) {
                    Toast.makeText(getContext(), getResources().getString(R.string.explanation_locations_not_found).toString(), Toast.LENGTH_LONG).show();
                    arrayListResultAddresses = new ArrayList<Address>();
                } else {
                    arrayListResultAddresses = (ArrayList<Address>) mListFindAddresses;
                }

                addressListAdapter = new AddressListAdapter(activity, arrayListResultAddresses);

                foundPointsListView.setAdapter(addressListAdapter);

                HideWaitingGeocodingProgressBar();

                // To lock the button when there are no results
                if (addressListAdapter.getCount() == 0) {
                    setSelectedPoint(false);
                    setChanged();
                    notifyObservers();
                }
                HideKeyboard();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        // Show point on the map
        Address selectedAddress = (Address) (adapterView.getItemAtPosition(position));
        showPointOnMapListener.showPointOnMap(getTagAddress(), selectedAddress);
        setIndexSelectedPoint(position);

        // Refresh selection of items
        RadioButton selectRadioButton = null;
        ListView listView = null;
        listView = (ListView) adapterView;
        AddressListAdapter addressListAdapter = (AddressListAdapter) ((ListView) adapterView).getAdapter();
        long countItems = adapterView.getCount();
        for (int i = 0; i < countItems; i++) {
            selectRadioButton = null;
            View currentView = listView.getChildAt(i);
            int indexSelectedPosition = listView.getCheckedItemPosition();

            addressListAdapter.setSelectedItem(indexSelectedPosition);
            if (currentView != null) {
                selectRadioButton = (RadioButton) currentView.findViewById(R.id.selectRadioButton);
                if (selectRadioButton != null) {
                    if (currentView == view) {
                        if (!selectRadioButton.isChecked()) {
                            selectRadioButton.setChecked(true);
                            setSelectedPoint(true);
                            setChanged();
                            notifyObservers();
                        }
                    } else {
                        if (selectRadioButton.isChecked()) {
                            selectRadioButton.setChecked(false);
                        }
                    }
                }
            }
        }
    }

    private void extractListeners(Context context) {
        if (context instanceof ShowPointOnMapListener) {
            showPointOnMapListener = (ShowPointOnMapListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ShowPointOnMapListener");
        }
        if (context instanceof ShowPointOnMapListener) {
            clearMarkerOnMapListener = (ClearMarkerOnMapListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ClearMarkerOnMapListener");
        }
    }

    public int getTagAddress() {
        return mTagAddress;
    }

    public void setTagAddress(int tagAddress) {
        this.mTagAddress = tagAddress;
    }

    public boolean isSelectedPoint() {
        return mSelectedPoint;
    }

    public void setSelectedPoint(boolean selectedPoint) {
        this.mSelectedPoint = selectedPoint;
    }

    public void createArrayListSelectedPathPointsObserver() {
        mSelectedPathPointsObservers = new ArrayList<SelectedPathPointsObserver>();
    }

    public void clearArrayListSelectedPathPointsObserver() {
        mSelectedPathPointsObservers.clear();
        mSelectedPathPointsObservers = null;
    }

    @Override
    public void addObserver(SelectedPathPointsObserver selectedPathPointsObserver) {
        if (mSelectedPathPointsObservers == null) {
            createArrayListSelectedPathPointsObserver();
        }
        mSelectedPathPointsObservers.add(selectedPathPointsObserver);
    }

    @Override
    public void removeObserver(SelectedPathPointsObserver selectedPathPointsObserver) {
        int observerIndex = mSelectedPathPointsObservers.indexOf(selectedPathPointsObserver);
        if (observerIndex >= 0) {
            mSelectedPathPointsObservers.remove(observerIndex);
        }
    }

    @Override
    public void removeObservers() {
        if (mSelectedPathPointsObservers != null) {
            for (int i = 0; i < mSelectedPathPointsObservers.size(); i++) {
                mSelectedPathPointsObservers.remove(i);
            }
        }
        mSelectedPathPointsObservers = null;
    }

    @Override
    public void setChanged() {
        mChanged = true;
    }

    @Override
    public void notifyObservers() {
        if (mChanged) {
            if (mSelectedPathPointsObservers != null) {
                // Delete null observers
                for (int i = 0; i < mSelectedPathPointsObservers.size(); i++) {
                    SelectedPathPointsObserver currentObserver = null;
                    currentObserver = mSelectedPathPointsObservers.get(i);
                    if (currentObserver == null) {
                        mSelectedPathPointsObservers.remove(i);
                    }
                }

                // Update observers
                for (int i = 0; i < mSelectedPathPointsObservers.size(); i++) {
                    SelectedPathPointsObserver currentObserver = null;
                    currentObserver = mSelectedPathPointsObservers.get(i);
                    if (currentObserver != null) {
                        currentObserver.update(getTagAddress(), isSelectedPoint());
                    }
                }
                mChanged = false;
            }
        }
    }

    public int getIndexSelectedPoint() {
        return mIndexSelectedPoint;
    }

    public void setIndexSelectedPoint(int indexSelectedPoint) {
        this.mIndexSelectedPoint = indexSelectedPoint;
    }

    public void setFirstVisiblePosition(int firstVisiblePosition) {
        this.mFirstVisiblePosition = firstVisiblePosition;
    }

    public int getFirstVisiblePosition() {
        return mFirstVisiblePosition;
    }

    private void ShowWaitingGeocodingProgressBar(){
        ProgressBar waitingGeocodingProgressBar = null;
        waitingGeocodingProgressBar = (ProgressBar)getView().findViewById(R.id.geocodingProgressBar);
        waitingGeocodingProgressBar.setVisibility(View.VISIBLE);
    }

    private void HideWaitingGeocodingProgressBar(){
        ProgressBar waitingGeocodingProgressBar = null;
        waitingGeocodingProgressBar = (ProgressBar)getView().findViewById(R.id.geocodingProgressBar);
        waitingGeocodingProgressBar.setVisibility(View.GONE);
    }
}