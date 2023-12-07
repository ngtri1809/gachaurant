package com.example.gachaurant;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RewardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RewardsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int checkCount;


    public RewardsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RewardsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RewardsFragment newInstance(String param1, String param2) {
        RewardsFragment fragment = new RewardsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            checkCount = getArguments().getInt(ARG_PARAM1);
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rewards, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        ProgressBar bar = view.findViewById(R.id.progressBarReward);
        TextView strBar = view.findViewById(R.id.currentProgress);
        int nprog = checkCount%10 * 10;
        strBar.setText(String.valueOf(nprog) + "/100");
        bar.setProgress(nprog);
        Button btnRedeem = (Button) view.findViewById(R.id.buttonRedeem);
        btnRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //where is update value of progress?
                redeemMessage();
            }
        });
    }

    private void redeemMessage(){
        if(checkCount%10*10 > 99){
            AlertDialog.Builder alterBuilder = new AlertDialog.Builder(getContext());
            alterBuilder.setMessage("Congrats Enjoy Your 5$ Reward");
            alterBuilder.setTitle("Congratulations");
            alterBuilder.setPositiveButton("OK", null);
            AlertDialog dlg = alterBuilder.create();
            dlg.show();
        }
        else {
            AlertDialog.Builder alterBuilder = new AlertDialog.Builder(getContext());
            alterBuilder.setMessage("You Experience must be 100 to Redeem Reward");
            alterBuilder.setTitle("Experience Low");
            alterBuilder.setPositiveButton("OK", null);
            AlertDialog dlg = alterBuilder.create();
            dlg.show();
        }

    }
}