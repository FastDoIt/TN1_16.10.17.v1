package by.supercoder.tasknumber1.requestactivity;



/**
 * Created by user on 11.08.2017.
 */
public class UserInterfaceManager implements SelectedPathPointsObserver {
    private boolean mAddressFromSelected = false;
    private boolean mAddressToSelected = false;
    private OnEnabledShowPathButtonListener mOnEnabledShowPathButton;

    public interface OnEnabledShowPathButtonListener {

        void OnEnabledShowPathButton(boolean enabledButton);
    }

    public UserInterfaceManager() {
        init();
    }

    public void init() {
        setAddressFromSelected(false);
        setAddressToSelected(false);
    }

    public void init(boolean addressFromSelected, boolean addressToSelected) {
        mAddressFromSelected = addressFromSelected;
        mAddressToSelected = addressToSelected;
    }

    public boolean hasPathSelected() {
        boolean pathSelected = false;
        if (isAddressFromSelected() && isAddressToSelected()) {
            pathSelected = true;
        }
        return pathSelected;
    }

    public boolean isAddressFromSelected() {
        return mAddressFromSelected;
    }

    public void setAddressFromSelected(boolean addressFromSelected) {
        this.mAddressFromSelected = addressFromSelected;
        refreshEnabledShowPathButton();
    }

    public boolean isAddressToSelected() {
        return mAddressToSelected;
    }

    public void setAddressToSelected(boolean addressToSelected) {
        this.mAddressToSelected = addressToSelected;
        refreshEnabledShowPathButton();
    }

    private void refreshEnabledShowPathButton() {
        if (getOnEnabledShowPathButton() != null) {
            getOnEnabledShowPathButton().OnEnabledShowPathButton(hasPathSelected());
        }
    }

    @Override
    public void update(int tagAdress, boolean selectedPoint) {
        switch (tagAdress) {
            case AddressToListResultsFragment.ShowPointOnMapListener.TAG_ADDRESS_FROM:
                setAddressFromSelected(selectedPoint);
                break;
            case AddressToListResultsFragment.ShowPointOnMapListener.TAG_ADDRESS_TO:
                setAddressToSelected(selectedPoint);
                break;
        }
    }

    public OnEnabledShowPathButtonListener getOnEnabledShowPathButton() {
        return mOnEnabledShowPathButton;
    }

    public void setOnEnabledShowPathButton(OnEnabledShowPathButtonListener onEnabledShowPathButton) {
        this.mOnEnabledShowPathButton = onEnabledShowPathButton;
    }
}
