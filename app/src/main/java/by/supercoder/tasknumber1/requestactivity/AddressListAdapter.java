package by.supercoder.tasknumber1.requestactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.location.Address;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import by.supercoder.tasknumber1.R;

/**
 * Created by user on 22.06.2017.
 */
public class AddressListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater lInflater;
    ArrayList<Address> arrayListAddress;
    private final long NOT_SELECTED_ITEMS = -1;
    private long mSelectedItem;

    public AddressListAdapter(Context context, ArrayList<Address> addresses) {
        super();
        this.context = context;
        arrayListAddress = new ArrayList<Address>();
        for (Address address : addresses) {
            arrayListAddress.add(address);
        }
        lInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSelectedItem = NOT_SELECTED_ITEMS;
    }

    @Override
    public int getCount() {
        return arrayListAddress.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayListAddress.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public String getItemDescription(int position) {
        String itemDescription = "";
        if (position < arrayListAddress.size()) {
            itemDescription = getAddressDescription((Address) arrayListAddress.get(position));
        }
        return itemDescription;
    }

    public Object getItemCoordinates(int position) {
        final String SPLITTER_DATA_DESCRIPTION = ", ";
        String itemCoordinates = "";
        double latitude = 0;
        double longitude = 0;
        if (position < arrayListAddress.size()) {
            latitude = ((Address) arrayListAddress.get(position)).getLatitude();
            String stringLatitude = "";
            stringLatitude = FormatAddress.getFormattedCoordinate(latitude);
            longitude = ((Address) arrayListAddress.get(position)).getLongitude();
            String stringLongitude = "";
            stringLongitude = FormatAddress.getFormattedCoordinate(longitude);
            itemCoordinates = stringLatitude + SPLITTER_DATA_DESCRIPTION + stringLongitude;
        }
        return itemCoordinates;
    }

    public static String getAddressDescription(Address address) {
        String description = "";
        description = FormatAddress.getFormatDescription(address);
        return description;
    }

    public long getSelectedItem() {
        return mSelectedItem;
    }

    public void setSelectedItem(long selectedItem) {
        this.mSelectedItem = selectedItem;
    }

    static class ViewHolder {
        TextView textDescription;
        TextView textCoordinates;
        RadioButton selectRadioButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        // A created but not used view is used
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_address_xml, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textDescription = (TextView) convertView.findViewById(R.id.descriptionText);
            viewHolder.textCoordinates = (TextView) convertView.findViewById(R.id.coordinatesText);
            viewHolder.selectRadioButton = (RadioButton) convertView.findViewById(R.id.selectRadioButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textDescription.setText(getItemDescription(position).toString());
        viewHolder.textCoordinates.setText(getItemCoordinates(position).toString());
        if (getSelectedItem() >= 0) {
            if (position == getSelectedItem()) {
                if (!viewHolder.selectRadioButton.isChecked()) {
                    viewHolder.selectRadioButton.setChecked(true);
                }
            } else {
                if (viewHolder.selectRadioButton.isChecked()) {
                    viewHolder.selectRadioButton.setChecked(false);
                }
            }
        }
        return convertView;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
