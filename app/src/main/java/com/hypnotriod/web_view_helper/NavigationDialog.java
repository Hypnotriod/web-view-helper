package com.hypnotriod.web_view_helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class NavigationDialog extends DialogFragment implements View.OnClickListener, URLListItemAdapter.OnItemClickListener {
    String currentUrlAddress = "";
    ArrayList<String> recentURLs;
    boolean fullScreen = false;
    boolean hideNavigation = false;
    boolean layoutNoLimits = false;

    Button openURLButton;
    EditText urlTextInput;
    ListView listViewRecentURLs;
    CheckBox fullscreenCheckbox;
    CheckBox hideNavigationCheckbox;
    CheckBox layoutNoLimitsCheckbox;

    Activity context;

    private NavigationDialogListener dialogListener;

    public interface NavigationDialogListener {
        void onNavigationDialogURLChosen(String url);

        void onNavigationDialogExit();

        void onNavigationDialogItemDelete(int position);

        void onToggleFullScreen(boolean fullScreen);

        void onToggleHideNavigation(boolean hide);

        void onToggleLayoutNoLimits(boolean layoutNoLimits);

        void onNavigationDialogDismiss();
    }

    public void setSettings(
            String currentURLAddress,
            ArrayList<String> recentURLs,
            boolean fullScreen,
            boolean hideNavigation,
            boolean layoutNoLimits,
            NavigationDialogListener dialogListener) {
        this.dialogListener = dialogListener;
        this.currentUrlAddress = currentURLAddress;
        this.recentURLs = recentURLs;
        this.fullScreen = fullScreen;
        this.hideNavigation = hideNavigation;
        this.layoutNoLimits = layoutNoLimits;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.navigation_dialog, null);

        builder.setView(view).setPositiveButton(R.string.exit_button, onExitClick);
        initComponents(view);

        alertDialog = builder.create();
        alertDialog.setTitle(getResources().getString(R.string.navigation_dialog_name));

        return alertDialog;
    }

    private void initComponents(View view) {
        openURLButton = view.findViewById(R.id.btnOpenUrl);
        urlTextInput = view.findViewById(R.id.inputTextUrlAddress);
        listViewRecentURLs = view.findViewById(R.id.listViewRecentURLs);

        openURLButton.setOnClickListener(this);
        urlTextInput.setText(currentUrlAddress);

        URLListItemAdapter urlListItemAdapter = new URLListItemAdapter(context, recentURLs, this);

        listViewRecentURLs.setAdapter(urlListItemAdapter);

        fullscreenCheckbox = view.findViewById(R.id.checkboxFullscreen);
        fullscreenCheckbox.setChecked(fullScreen);
        fullscreenCheckbox.setOnClickListener(this);

        hideNavigationCheckbox = view.findViewById(R.id.checkboxHideNavigation);
        hideNavigationCheckbox.setChecked(hideNavigation);
        hideNavigationCheckbox.setOnClickListener(this);

        layoutNoLimitsCheckbox = view.findViewById(R.id.checkboxLayoutNoLimits);
        layoutNoLimitsCheckbox.setChecked(layoutNoLimits);
        layoutNoLimitsCheckbox.setOnClickListener(this);
    }

    public void onItemClick(int itemPosition) {
        urlTextInput.setText(recentURLs.get(itemPosition));
    }

    public void onItemDelete(int itemPosition) {
        dialogListener.onNavigationDialogItemDelete(itemPosition);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btnOpenUrl) {
            openURL();
        } else if (view.getId() == R.id.checkboxFullscreen) {
            dialogListener.onToggleFullScreen(fullscreenCheckbox.isChecked());
        } else if (view.getId() == R.id.checkboxHideNavigation) {
            dialogListener.onToggleHideNavigation(hideNavigationCheckbox.isChecked());
        } else if (view.getId() == R.id.checkboxLayoutNoLimits) {
            dialogListener.onToggleLayoutNoLimits(layoutNoLimitsCheckbox.isChecked());
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        dialogListener.onNavigationDialogDismiss();
        dialogListener = null;
    }

    private final DialogInterface.OnClickListener onExitClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            exitApp();
        }
    };

    private void free() {
        recentURLs = null;
    }

    private void openURL() {
        dialogListener.onNavigationDialogURLChosen(urlTextInput.getText().toString());
        free();
        dismiss();
    }

    private void exitApp() {
        dialogListener.onNavigationDialogExit();
        free();
        dismiss();
    }
}
