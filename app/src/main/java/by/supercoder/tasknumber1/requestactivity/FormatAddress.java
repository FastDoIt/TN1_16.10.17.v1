package by.supercoder.tasknumber1.requestactivity;

import android.location.Address;

import by.supercoder.tasknumber1.resultactivity.StringEquals;

/**
 * Created by user on 23.08.2017.
 */
public class FormatAddress {
    public static String getFormattedCoordinate(double coordinate){
        String formattedCoordinate = "";
        final String FORMAT = "%.6f";
        formattedCoordinate = String.format(FORMAT, coordinate);
        return formattedCoordinate;
    }
    public static String getFormatDescription(Address address){
        String formattedDescription = "";
        String countryName = "";
        countryName = prepareAddressDataFromOutput(address.getCountryName());
        String locality = "";
        locality = prepareAddressDataFromOutput(address.getLocality());
        String thoroughfare = "";
        thoroughfare = prepareAddressDataFromOutput(address.getThoroughfare());
        String subThoroughfare = "";
        subThoroughfare = prepareAddressDataFromOutput(address.getSubThoroughfare());

        formattedDescription = countryName + locality + thoroughfare + subThoroughfare;
        return formattedDescription;
    }
    private static String prepareAddressDataFromOutput(String value) {
        final String SPLITTER_DATA_DESCRIPTION = ", ";
        final String EMPTY_LINE = "";
        final String VALUE_NULL = null;
        String result = EMPTY_LINE;
        if (!StringEquals.equals(value, EMPTY_LINE) && !StringEquals.equals(value, VALUE_NULL)) {
            result = value + SPLITTER_DATA_DESCRIPTION;
        } else {
            result = EMPTY_LINE;
        }
        return result;
    }
}
