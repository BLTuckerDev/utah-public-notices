package com.bltucker.utahpublicnotices;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public final class InitialSetup extends Fragment implements View.OnClickListener {

    private InitialSetupFragmentListener mListener;

    private EditText cityNameEditText;


    public static InitialSetup newInstance() {
        return new InitialSetup();
    }


    public InitialSetup() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_initial_setup, container, false);

        cityNameEditText = (EditText) rootView.findViewById(R.id.initial_setup_edit_text);
        Button okButton = (Button) rootView.findViewById(R.id.initial_setup_ok_button);

        okButton.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (InitialSetupFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View v) {
        String cityName = cityNameEditText.getText().toString();
        if(cityName.isEmpty()){
            Toast.makeText(getActivity(),getActivity().getString(R.string.please_enter_a_city_name), Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(getActivity().getString(R.string.city_setting_pref_key),cityName);
        editor.apply();

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(cityNameEditText.getWindowToken(), 0);
        this.mListener.onInitialSetupCompleted();
    }


    public interface InitialSetupFragmentListener {
        public void onInitialSetupCompleted();
    }
}
