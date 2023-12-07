package com.example.gachaurant;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String userIDP;
    FirebaseAuth fAuthP;

    private  int checkInCount = 0;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
//        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
//        String name = currentuser.getEmail();
//        TextView strName = view.findViewById(R.id.userName);
//        strName.setText(name);
        Button btnCheck = (Button) view.findViewById(R.id.buttonCheckIn);
        Button btnReward = (Button) view.findViewById(R.id.buttonReward);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //where is update value of progress?
                checkIn();
            }
        });

        btnReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goReward();
            }
        });
    }

    private void checkIn(){
        checkInCount++;
        AlertDialog.Builder alterBuilder = new AlertDialog.Builder(getContext());
        alterBuilder.setMessage("Thank you for checking in! +10 Experience!");
        alterBuilder.setTitle("Check-in");
        alterBuilder.setPositiveButton("OK", null);
        AlertDialog dlg = alterBuilder.create();
        dlg.show();

    }
    private void goReward(){
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, checkInCount);
        Fragment rewardFragment = new RewardsFragment();
        rewardFragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, rewardFragment);
        fragmentTransaction.commit();
    }
}