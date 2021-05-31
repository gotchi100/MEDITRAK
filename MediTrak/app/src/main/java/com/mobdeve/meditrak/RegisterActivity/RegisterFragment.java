package com.mobdeve.meditrak.RegisterActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mobdeve.meditrak.R;

public class RegisterFragment extends Fragment {
    private int slide;
    private ViewGroup viewGroup;
    private Register parent;

    private SharedPreferences sharedPreferences;

    public RegisterFragment(int slide, Register registerActivity) {
        super();
        this.slide = slide;
        this.parent = registerActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_register, container, false);

        ImageView imgRegister = this.viewGroup.findViewById(R.id.imgRegister);

        TextView txtRegisterHeader = this.viewGroup.findViewById(R.id.txtRegisterHeader),
                txtRegisterContent = this.viewGroup.findViewById(R.id.txtRegisterContent);

        Button btnNext = this.viewGroup.findViewById(R.id.btnRegisterNext),
                btnBack = this.viewGroup.findViewById(R.id.btnRegisterBack);

        EditText etRegisterField = this.viewGroup.findViewById(R.id.etRegisterField);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE);

        etRegisterField.setOnFocusChangeListener((listener, hasFocus) -> {
            if (!hasFocus) this.dismissKeyboard(etRegisterField);
            Log.d("regfragment", hasFocus ? "has" : "nohas");
        });

        if (this.slide == 0) {
            imgRegister.setImageResource(R.drawable.ic_self);
            txtRegisterHeader.setText(R.string.register_header1);
            txtRegisterContent.setText(R.string.register_content1);
            etRegisterField.setHint(R.string.register_hint1);
            etRegisterField.setVisibility(View.VISIBLE);
            etRegisterField.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

            btnBack.setOnClickListener(listener -> {
                this.parent.finish();
            });

            btnNext.setOnClickListener(listener -> {
                // saved to shared pref
                String value = etRegisterField.getText().toString().trim().replaceAll("/\\s+/", " ");
                this.dismissKeyboard(etRegisterField);

                if (value.length() > 0) {
                    this.parent.name = value;
                    this.parent.vp2Register.setCurrentItem(1);
                } else {
                    etRegisterField.setError("Hey! I think you forgot something...");
                }
            });
        } else if (this.slide == 1) {
            etRegisterField.setRawInputType(InputType.TYPE_CLASS_PHONE);
            etRegisterField.setInputType(InputType.TYPE_CLASS_PHONE);
            imgRegister.setImageResource(R.drawable.ic_companion);
            txtRegisterHeader.setText(R.string.register_header2);
            txtRegisterContent.setText(R.string.register_content2);
            etRegisterField.setHint(R.string.register_hint2);
            etRegisterField.setVisibility(View.VISIBLE);
            etRegisterField.setInputType(InputType.TYPE_CLASS_PHONE);

            btnBack.setOnClickListener(listener -> {
                this.dismissKeyboard(etRegisterField);
                this.parent.vp2Register.setCurrentItem(0);
            });

            btnNext.setOnClickListener(listener -> {
                String value = etRegisterField.getText().toString().trim().replaceAll("/\\s+/", " ");
                this.dismissKeyboard(etRegisterField);

                if (value.length() > 0) {
                    this.parent.phone = value;
                    this.parent.vp2Register.setCurrentItem(2);
                } else {
                    etRegisterField.setError("Oops! You left something blank...");
                }
            });
        } else if (this.slide == 2) {
            imgRegister.setImageResource(R.drawable.ic_travel);
            txtRegisterHeader.setText(R.string.register_header3);
            txtRegisterContent.setText(R.string.register_content3);
            etRegisterField.setVisibility(View.GONE);
            btnNext.setText(R.string.btn_go);
            etRegisterField.setVisibility(View.GONE);

            btnBack.setOnClickListener(listener -> {
                // saved to shared pref
                this.dismissKeyboard(etRegisterField);
                this.parent.vp2Register.setCurrentItem(1);
            });

            btnNext.setOnClickListener(listener -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("name", this.parent.name);
                editor.putString("phone", this.parent.phone);
                editor.putBoolean("registered", true);
                editor.commit();

                this.dismissKeyboard(etRegisterField);
                this.parent.finish();
            });
        }

        return this.viewGroup;
    }

    public void dismissKeyboard(EditText editText) {
        ((InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(this.parent.vp2Register.getWindowToken(), 0);
    }
}
