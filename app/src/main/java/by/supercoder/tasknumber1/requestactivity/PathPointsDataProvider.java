package by.supercoder.tasknumber1.requestactivity;

import android.location.Address;

/**
 * Created by user on 30.06.2017.
 */
public class PathPointsDataProvider {

    public static final String RESULT_USER_LATLNG = "by.supercoder.tasknumber1.requestactivity.RESULT_USER_LATLNG";
    public static final String RESULT_WITHOUT_USER_LOCATION = "by.supercoder.tasknumber1.requestactivity.RESULT_WITHOUT_USER_LOCATION";
    public static final String RESULT_WITH_USER_LOCATION = "by.supercoder.tasknumber1.requestactivity.RESULT_WITH_USER_LOCATION";
    public static final String RESULT_USER_LOCATION_STATUS = "by.supercoder.tasknumber1.requestactivity.RESULT_USER_LOCATION_STATUS";

    public static final String LATITUDE_FROM_RESULT_ADDRESS = "by.supercoder.tasknumber1.requestactivity.LATITUDE_FROM_RESULT_ADDRESS";
    public static final String LONGITUDE_FROM_RESULT_ADDRESS = "by.supercoder.tasknumber1.requestactivity.LONGITUDE_FROM_RESULT_ADDRESS";
    public static final String DESCRIPTION_FROM_RESULT_ADDRESS = "by.supercoder.tasknumber1.requestactivity.DESCRIPTION_FROM_RESULT_ADDRESS";
    public static final String LATITUDE_TO_RESULT_ADDRESS = "by.supercoder.tasknumber1.requestactivity.LATITUDE_TO_RESULT_ADDRESS";
    public static final String LONGITUDE_TO_RESULT_ADDRESS = "by.supercoder.tasknumber1.requestactivity.LONGITUDE_TO_RESULT_ADDRESS";
    public static final String DESCRIPTION_TO_RESULT_ADDRESS = "by.supercoder.tasknumber1.requestactivity.DESCRIPTION_TO_RESULT_ADDRESS";


    private Address mFromAddress;
    private Address mToAddress;


    public Address getFromAddress() {
        return mFromAddress;
    }

    public void setFromAddress(Address mFromAddress) {
        this.mFromAddress = mFromAddress;
    }

    public Address getToAddress() {
        return mToAddress;
    }

    public void setToAddress(Address mToAddress) {
        this.mToAddress = mToAddress;
    }

    public PathPointsDataProvider getPath() {
        PathPointsDataProvider pathPointsDataProvider = new PathPointsDataProvider();
        pathPointsDataProvider.setFromAddress(getFromAddress());
        pathPointsDataProvider.setToAddress(getToAddress());
        return pathPointsDataProvider;
    }
}
