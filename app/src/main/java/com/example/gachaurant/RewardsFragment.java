package com.example.gachaurant;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RewardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RewardsFragment extends Fragment {

    private static final String CHECK_IN_COUNT = "param1";

    private int checkCount;
    FirebaseFirestore fStore;

    FirebaseUser user;
    FirebaseAuth fAuth;
    private long level = 1;
    private long experience = 0;

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
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
//        if (getArguments() != null) {
//            checkCount = getArguments().getInt(CHECK_IN_COUNT);
//            if (checkCount > 10) {
//                checkCount -= 10;
//            }
//        }
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

        DocumentReference docRef = fStore.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot value = task.getResult();
                    if(value.get("level")!=null){
                        level = (long) value.get("level");
                    }
                    if(value.get("experience")!=null){
                        experience = (long) value.get("experience");
                    }
                    strBar.setText(experience + "/100");
                    bar.setProgress(Integer.valueOf(""+experience));
                }
            }
        });

        Button btnRedeem = (Button) view.findViewById(R.id.buttonRedeem);
        btnRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //where is update value of progress?
                redeemMessage(bar,strBar);
            }
        });
    }

    private void redeemMessage(ProgressBar bar, TextView strBar){
        if(experience > 99){
            AlertDialog.Builder alterBuilder = new AlertDialog.Builder(getContext());
            alterBuilder.setMessage("Congrats Enjoy Your $5 Reward");
            alterBuilder.setTitle("Congratulations");
            alterBuilder.setPositiveButton("OK", null);
            strBar.setText("0/100");
            bar.setProgress(0);
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