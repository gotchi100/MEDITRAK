package com.mobdeve.meditrak.HomeFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.mobdeve.meditrak.BackgroundService;
import com.mobdeve.meditrak.R;
import com.mobdeve.meditrak.RegisterActivity.Register;
import com.mobdeve.meditrak.data.DatabaseHandler;

public class HomeSettingsFragment extends Fragment {
    private View view;
    private Button btnClear;

    private TextView tvNameVal, tvContactVal;

    private LinearLayout llContact, llName;
    private SwitchMaterial remind5Min, remind10Min, remind15Min;
    private DatabaseHandler db;
    private SharedPreferences pref;

    public HomeSettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_home_settings, container, false);
        this.db = new DatabaseHandler(this.getContext());
        this.pref = this.getContext().getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE);

        this.btnClear = this.view.findViewById(R.id.btnClear);
        this.tvNameVal = this.view.findViewById(R.id.tvNameVal);
        this.tvContactVal = this.view.findViewById(R.id.tvContactVal);
        this.remind5Min = this.view.findViewById(R.id.rem5Min);
        this.remind10Min = this.view.findViewById(R.id.rem10Min);
        this.remind15Min = this.view.findViewById(R.id.rem15Min);
        this.llContact = this.view.findViewById(R.id.llContact);
        this.llName = this.view.findViewById(R.id.llName);

        this.llName.setOnClickListener(listener -> {
            LayoutInflater lf = requireActivity().getLayoutInflater();
            View v = lf.inflate(R.layout.text_dialog, null);
            EditText etDialogEntry = v.findViewById(R.id.etDialogEntry);
            TextView tvDialogTitle = v.findViewById(R.id.tvDialogTitle);
            TextView tvDialogHint = v.findViewById(R.id.tvDialogHint);

            tvDialogTitle.setText("Name Update");
            tvDialogHint.setText("Hey there! Looking to change your name? Just put the right one below!");
            etDialogEntry.setText("");
            etDialogEntry.setHint(this.pref.getString("name", "Name"));
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogTheme);
            builder.setView(v);

            builder.setPositiveButton("Done", (listener0, whichButton) -> {
                String text = etDialogEntry.getText().toString().trim().replaceAll("\\s+", " ");

                if (text.length() > 0) {
                    this.tvNameVal.setText(text);
                    this.pref.edit().putString("name", text).commit();
                }
            });
            builder.setNegativeButton("Cancel", null);

            builder.show();
        });

        this.llContact.setOnClickListener(listener -> {
            LayoutInflater lf = requireActivity().getLayoutInflater();
            View v = lf.inflate(R.layout.text_dialog, null);
            EditText etDialogEntry = v.findViewById(R.id.etDialogEntry);
            TextView tvDialogTitle = v.findViewById(R.id.tvDialogTitle);
            TextView tvDialogHint = v.findViewById(R.id.tvDialogHint);

            tvDialogTitle.setText("Companion Update");
            tvDialogHint.setText("Hey there! Looking to change your companion's phone number? Just put the new one below!");
            etDialogEntry.setText("");
            etDialogEntry.setHint(this.pref.getString("phone", "+1234567890"));
            etDialogEntry.setInputType(InputType.TYPE_CLASS_PHONE);

            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogTheme);
            builder.setView(v);

            builder.setPositiveButton("Done", (listener0, whichButton) -> {
                String text = etDialogEntry.getText().toString().trim().replaceAll("\\s+", " ");

                if (text.length() > 0) {
                    this.tvNameVal.setText(text);
                    this.pref.edit().putString("phone", text).commit();
                }
            });
            builder.setNegativeButton("Cancel", null);

            builder.show();
        });

        this.remind5Min.setOnCheckedChangeListener((view, checked) -> {
            this.pref.edit().putBoolean("remind5MinBefore", checked).commit();
        });

        this.remind10Min.setOnCheckedChangeListener((view, checked) -> {
            this.pref.edit().putBoolean("remind10MinBefore", checked).commit();
        });

        this.remind15Min.setOnCheckedChangeListener((view, checked) -> {
            this.pref.edit().putBoolean("remind15MinBefore", checked).commit();
        });

        this.btnClear.setOnClickListener(listener -> {
            new AlertDialog.Builder(this.getContext())
                .setTitle("Clear Everything")
                .setMessage("Do you really want to reset everything? This will clear your medicines and schedules and it's irreversible too!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    Intent intent0 = new Intent(this.getContext(), BackgroundService.class);
                    this.getActivity().stopService(intent0);

                    this.db.clear();
                    this.pref.edit().clear().commit();

                    Intent intent1 = new Intent(this.getContext(), Register.class);
                    this.getActivity().startActivity(intent1);
                })
                    .setNegativeButton(android.R.string.no, null).show();
        });

        return this.view;
    }

    @Override
    public void onResume() {
        this.tvNameVal.setText(this.pref.getString("name", ""));
        this.tvContactVal.setText(this.pref.getString("phone", ""));

        this.remind5Min.setChecked(this.pref.getBoolean("remind5MinBefore", false));
        this.remind10Min.setChecked(this.pref.getBoolean("remind10MinBefore", false));
        this.remind15Min.setChecked(this.pref.getBoolean("remind15MinBefore", false));

        super.onResume();
    }
}