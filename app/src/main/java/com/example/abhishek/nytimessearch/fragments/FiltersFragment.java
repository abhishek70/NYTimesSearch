package com.example.abhishek.nytimessearch.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.abhishek.nytimessearch.R;
import com.example.abhishek.nytimessearch.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FiltersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FiltersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FiltersFragment extends DialogFragment implements DatePickerDialogFragment.DatePickerDialogListener {

    /** the fragment initialization parameters, e.g. ARG_ITEM_NUMBER */
    private static final String ARG_FILTERS = "filters";

    // Member Vars
    private String mFilters;

    // Initializing the fragment action listener such as "Save"
    private OnFragmentInteractionListener mListener;


    // Binding Views using ButterKnife
    @BindView(R.id.etBeginDate) EditText etBeginDate;
    @BindView(R.id.spSortOrder) Spinner spSortOrder;
    @BindView(R.id.cbNewsDeskArts) CheckBox cbNewsDeskArts;
    @BindView(R.id.cbNewsDeskFashion) CheckBox cbNewsDeskFashion;
    @BindView(R.id.cbNewsDeskSports) CheckBox cbNewsDeskSports;

    private Unbinder unbinder;

    /**
     * Empty Constructor
     */
    public FiltersFragment() {}


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param
     * @return A new instance of fragment FiltersFragment.
     */
    public static FiltersFragment newInstance(String param) {
        FiltersFragment fragment = new FiltersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILTERS, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFilters = getArguments().getString(ARG_FILTERS);
        }
    }

    /**
     * Loading the fragment view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the fragemnt view
        View view = inflater.inflate(R.layout.fragment_filters, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", Constant.FILTER_DIALOG_TITLE);
        getDialog().setTitle(title);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        loadSettings();
    }

    /**
     * Save the values in the Shared Preferences
     */
    @OnClick(R.id.btnSave)
    public void saveSettings() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constant.BEGIN_DATE, etBeginDate.getText().toString());
        editor.putInt(Constant.SORT_ORDER, spSortOrder.getSelectedItemPosition());
        editor.putBoolean(Constant.NEWS_DESK_ARTS, cbNewsDeskArts.isChecked());
        editor.putBoolean(Constant.NEWS_DESK_FASHION, cbNewsDeskFashion.isChecked());
        editor.putBoolean(Constant.NEWS_DESK_SPORTS, cbNewsDeskSports.isChecked());
        editor.commit();

        if (mListener != null) {
            mListener.onFragmentInteraction();
        }

        dismiss();
    }


    /**
     * Loading the values stored in the shared preferences
     */
    public void loadSettings() {

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String beginDate = sharedPref.getString(Constant.BEGIN_DATE, "");
        int sortOrder = sharedPref.getInt(Constant.SORT_ORDER, 0);
        Boolean newsDeskArts = sharedPref.getBoolean(Constant.NEWS_DESK_ARTS, false);
        Boolean newsDeskFashion = sharedPref.getBoolean(Constant.NEWS_DESK_FASHION, false);
        Boolean newsDeskSports = sharedPref.getBoolean(Constant.NEWS_DESK_SPORTS, false);

        etBeginDate.setText(beginDate);
        spSortOrder.setSelection(sortOrder);
        cbNewsDeskArts.setChecked(newsDeskArts);
        cbNewsDeskFashion.setChecked(newsDeskFashion);
        cbNewsDeskSports.setChecked(newsDeskSports);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    /**
     * Load the Date Picker Dialog
     */
    @OnClick(R.id.etBeginDate)
    public void showDatePickerDialog() {
        DatePickerDialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.setTargetFragment(FiltersFragment.this, 300);
        newFragment.show(getFragmentManager(), "fragment_date_picker");
    }

    /**
     * Display the date selected from the date picker dialog on clicking "SET"
     * @param calendar
     */
    @Override
    public void onDatePicked(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = sdf.format(calendar.getTime());
        etBeginDate.setText(sDate);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
