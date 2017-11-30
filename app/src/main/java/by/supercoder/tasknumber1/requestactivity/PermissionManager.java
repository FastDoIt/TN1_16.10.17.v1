package by.supercoder.tasknumber1.requestactivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import by.supercoder.tasknumber1.R;


/**
 * Created by user on 13.07.2017.
 */
public class PermissionManager {
    private RequestActivity mParentActivity;
    private String[] mNeededPermissions;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private String mExplanationRunAvailableWork;
    private String mPermissionIsntGranted;
    private String mExplanationSettingsActivity;
    private String mPermissionExplanation;

    private OnResultOfRequestPermissionsListener mOnResultOfRequestPermissionsListener;

    public interface OnResultOfRequestPermissionsListener {
        void onFullWork();

        void onAvailableWork();
    }

    public PermissionManager(RequestActivity activity) {
        mParentActivity = activity;
        setNeededPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        setExplanationRunAvailableWork(mParentActivity.getString(R.string.explanation_run_available_work).toString());
        setPermissionIsntGranted(mParentActivity.getString(R.string.permission_isnt_granted).toString());
        setExplanationSettingsActivity(mParentActivity.getString(R.string.explanation_settings_activity).toString());
        setPermissionExplanation(mParentActivity.getString(R.string.permission_explanation).toString());
    }

    // Here run act, which the needed of runtime permission.
    public void runFullWork() {
        if (mOnResultOfRequestPermissionsListener != null) {
            mOnResultOfRequestPermissionsListener.onFullWork();
        }
    }

    // Here run act, which the not needed of runtime permission.
    public void runAvailableWork() {
        if (mOnResultOfRequestPermissionsListener != null) {
            mOnResultOfRequestPermissionsListener.onAvailableWork();
        }
    }

    // This function call insteed of act in which the needed of runtime permission.
    public void executeAction() {
        if (hasPermissions()) {
            // App has permissions.
            runFullWork();
        } else {
            // App doesn't have permissions, so requesting permissions.
            requestPermissionWithRationale();
        }
    }

    // This function need of call in implemented mParentActivity.onRequestPermissionsResult
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                for (int res : grantResults) {
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed) {
            //user granted all permissions we can perform our task.
            runFullWork();
        } else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean shouldRationale = true;
                shouldRationale = ActivityCompat.shouldShowRequestPermissionRationale(mParentActivity, getNeededPermissions()[0]);
                if (shouldRationale) {
                    runAvailableWorkWithRationale();
                } else {
                    showNoPermissionSnackbar();
                }
            }
        }
    }

    // This function need of call in implemented mParentActivity.onActivityResult
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (hasPermissions()) {
                runFullWork();
            } else {
                runAvailableWorkWithRationale();
            }
        }
    }

    // Here need set of your permissions
    public void setNeededPermissions(String[] neededPermissions) {
        this.mNeededPermissions = neededPermissions;
    }

    public String[] getNeededPermissions() {
        return mNeededPermissions;
    }

    // Need set ParentActivity for Snackbar
    public void setParentActivity(RequestActivity parentActivity) {
        mParentActivity = parentActivity;
    }

    public RequestActivity getParentActivity() {
        return mParentActivity;
    }

    // Also, need set of your the text message in the constuctor code.
    // (ExplanationRunAvailableWork, PermissionIsntGranted, ExplanationSettingsActivity, PermissionExplanation)

    public String getExplanationRunAvailableWork() {
        return mExplanationRunAvailableWork;
    }

    public void setExplanationRunAvailableWork(String explanationRunAvailableWork) {
        mExplanationRunAvailableWork = explanationRunAvailableWork;
    }

    public String getPermissionIsntGranted() {
        return mPermissionIsntGranted;
    }

    public void setPermissionIsntGranted(String mPermissionIsntGranted) {
        this.mPermissionIsntGranted = mPermissionIsntGranted;
    }

    public String getExplanationSettingsActivity() {
        return mExplanationSettingsActivity;
    }

    public void setExplanationSettingsActivity(String mExplanationSettingsActivity) {
        this.mExplanationSettingsActivity = mExplanationSettingsActivity;
    }

    public String getPermissionExplanation() {
        return mPermissionExplanation;
    }

    public void setPermissionExplanation(String mPermissionExplanation) {
        this.mPermissionExplanation = mPermissionExplanation;
    }

    private boolean hasPermissions() {
        // Check of needed permissions,
        // if all permissions granted,
        // function will return true.
        String[] permissions = getNeededPermissions();
        final int GRANTED = PackageManager.PERMISSION_GRANTED;
        for (String currentPermission : permissions) {
            int statusCurrentPermission = PackageManager.PERMISSION_DENIED;
            statusCurrentPermission = ContextCompat.checkSelfPermission((Activity) mParentActivity, currentPermission);
            if (statusCurrentPermission != GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestNeededPermissions() {
        String[] neededPermissions = getNeededPermissions();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(mParentActivity, neededPermissions, PERMISSION_REQUEST_CODE);
        }
    }

    private void showNoPermissionSnackbar() {
        final String SETTINGS_BUTTON = mParentActivity.getString(R.string.settings_button).toString();
        Snackbar snackbar = Snackbar.make(mParentActivity.findViewById(R.id.coordinatorLayout), getPermissionIsntGranted(),
                Snackbar.LENGTH_LONG)
                .setAction(SETTINGS_BUTTON, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();

                        Toast.makeText(mParentActivity.getApplicationContext(), getExplanationSettingsActivity(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
        TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        final int MAX_COUNT_LINES = 5;
        textView.setMaxLines(MAX_COUNT_LINES);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    runAvailableWorkWithRationale();
                }
            }
        });
        snackbar.show();

    }

    private void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + mParentActivity.getPackageName()));
        mParentActivity.startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

    private void requestPermissionWithRationale() {
        boolean shouldRationale = true;
        shouldRationale = ActivityCompat.shouldShowRequestPermissionRationale(mParentActivity, getNeededPermissions()[0]);
        if (shouldRationale) {
            Snackbar snackbar = Snackbar.make((View) (mParentActivity.findViewById(R.id.coordinatorLayout)), getPermissionExplanation(), Snackbar.LENGTH_LONG)
                    .setAction(mParentActivity.getString(R.string.grant_button).toString(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestNeededPermissions();
                        }
                    });
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    // If permission ist't granted - we notify about this, and we perform the permitted actions.
                    if (event != DISMISS_EVENT_ACTION) {
                        runAvailableWorkWithRationale();
                    }
                }
            });

            snackbar.show();
        } else {
            requestNeededPermissions();
        }
    }

    protected void runAvailableWorkWithRationale() {
        View view = (View) (mParentActivity.findViewById(R.id.coordinatorLayout));
        Snackbar snackbarDenied = Snackbar.make(view, getExplanationRunAvailableWork(), Snackbar.LENGTH_LONG);
        TextView textView = (TextView) snackbarDenied.getView().findViewById(android.support.design.R.id.snackbar_text);
        final int MAX_COUNT_LINES = 5;
        textView.setMaxLines(MAX_COUNT_LINES);
        snackbarDenied.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                runAvailableWork();
            }
        });
        snackbarDenied.show();
    }

    protected void showSnackbar(final String text) {
        View container = (View) (mParentActivity.findViewById(R.id.coordinatorLayout));
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    public void removeOnResultOfRequestPermissionsCallBack() {
        setResultOfRequestPermissionsCallBack(null);
    }

    public PermissionManager.OnResultOfRequestPermissionsListener getOnResultOfRequestPermissionsListener() {
        return mOnResultOfRequestPermissionsListener;
    }

    public void setResultOfRequestPermissionsCallBack(PermissionManager.OnResultOfRequestPermissionsListener onResultOfRequestPermissions) {
        mOnResultOfRequestPermissionsListener = onResultOfRequestPermissions;
    }

    public void clearAllReferences() {
        mOnResultOfRequestPermissionsListener = null;
        mParentActivity = null;
    }
}

