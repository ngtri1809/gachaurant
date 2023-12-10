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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RewardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RewardsFragment extends Fragment {

    private static final String CHECK_IN_COUNT = "param1";

    private int checkCount;


    public RewardsFragment() {
        // Required empty public constructor
    }

    public static RewardsFragment newInstance(int checkInCount) {
        RewardsFragment fragment = new RewardsFragment();
        Bundle args = new Bundle();
        args.putInt(CHECK_IN_COUNT, checkInCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            checkCount = getArguments().getInt(CHECK_IN_COUNT);
            if (checkCount > 10) {
                checkCount -= 10;
            }
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
        int nprog = checkCount * 10;
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
        if(checkCount*10 > 99){
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