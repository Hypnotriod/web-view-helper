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
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class NavigationDialog extends DialogFragment implements View.OnClickListener, URLListItemAdapter.OnItemClickListener {
    String currentUrlAddress = "";
    ArrayList<String> recentURLs;

    Button openURLButton;
    Button exitButton;
    EditText urlTextInput;
    ListView listViewRecentURLs;

    Activity context;

    private NavigationDialogListener _listener;

    public interface NavigationDialogListener {
        void onNavigationDialogURLChosen(String url);

        void onNavigationDialogExit();

        void onNavigationDialogItemDelete(int position);
    }

    public void setSettings(String currentURLAddress, ArrayList<String> recentURLs, NavigationDialogListener listener) {
        _listener = listener;
        currentUrlAddress = currentURLAddress;
        this.recentURLs = recentURLs;
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
        openURLButton = (Button) view.findViewById(R.id.btnOpenUrl);
        urlTextInput = (EditText) view.findViewById(R.id.inputTextUrlAddress);
        listViewRecentURLs = (ListView) view.findViewById(R.id.listViewRecentURLs);

        openURLButton.setOnClickListener(this);
        urlTextInput.setText(currentUrlAddress);

        URLListItemAdapter urlListItemAdapter = new URLListItemAdapter(context, recentURLs, this);

        listViewRecentURLs.setAdapter(urlListItemAdapter);
    }

    public void onItemClick(int itemPosition) {
        urlTextInput.setText(recentURLs.get(itemPosition));
    }

    public void onItemDelete(int itemPosition) {
        _listener.onNavigationDialogItemDelete(itemPosition);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btnOpenUrl) {
            openURL();
        }
    }

    private final DialogInterface.OnClickListener onExitClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            exitApp();
        }
    };

    private void free() {
        _listener = null;
        recentURLs = null;
    }

    private void openURL() {
        _listener.onNavigationDialogURLChosen(urlTextInput.getText().toString());
        free();
        dismiss();
    }

    private void exitApp() {
        _listener.onNavigationDialogExit();
        free();
        dismiss();
    }
}
