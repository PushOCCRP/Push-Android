package com.push.occrpnews.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.push.occrpnews.R;
import com.push.occrpnews.interfaces.OnFragmentInteractionListener;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DonatePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DonatePage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    AQuery aq;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DonatePage.
     */
    // TODO: Rename and change types and number of parameters
    public static DonatePage newInstance(String param1, String param2) {
        DonatePage fragment = new DonatePage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DonatePage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donate_page, container, false);
        aq = new AQuery(view);

        aq.id(R.id.btn_donate).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String amount = aq.id(R.id.txt_amount).getText().toString().trim();
                if(amount.length() == 0){
                    Toast.makeText(getActivity(), getActivity().getString(R.string.enter_valid_amount), Toast.LENGTH_LONG).show();
                    return;
                }
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                DonateConfirmationDialog.newInstance(amount).show(getFragmentManager(), "confirm_donation");
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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

    public static class DonateConfirmationDialog extends DialogFragment{
        public static DonateConfirmationDialog newInstance(String a){
            DonateConfirmationDialog fragment = new DonateConfirmationDialog();
            Bundle b = new Bundle();
            b.putString("amount", a);
            fragment.setArguments(b);

            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String amount = getArguments().getString("amount");
            amount = new DecimalFormat("USD ###,###.##").format(Double.parseDouble(amount));
            //builder.setTitle("Confirm Donation");
            builder.setMessage(getActivity().getString(R.string.donate_confirmation_prompt) + " " + amount + "?");
            builder.setPositiveButton("Donate", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showDonationSummary();
                }
            });

            builder.setNegativeButton("Nope!", null);
            Dialog dialog = builder.create();

            return dialog;
        }

        private void showDonationSummary(){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container_body, new DonationSummary());
            transaction.commit();

            // set the toolbar title
            try {
                getActivity().getActionBar().setTitle("Thank you!");
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }
}
