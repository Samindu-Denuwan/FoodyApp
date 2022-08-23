package com.jiat.foodyapp.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.constants.Constants;

public class SettingsUserFragment extends Fragment {


    private SwitchCompat pushNoti_switch;
    private TextView noti_status;
    private ImageView muteNoti, unMuteNoti;

    private FirebaseAuth firebaseAuth;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private boolean isCheck = false;

    private static final  String enabledMessage = "Notification are enabled";
    private static final  String disabledMessage = "Notification are disabled";

    public SettingsUserFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_user, container, false);
        pushNoti_switch = view.findViewById(R.id.notificationSwitch);
        noti_status = view.findViewById(R.id.notificationStatus);
        muteNoti = view.findViewById(R.id.mute);
        unMuteNoti = view.findViewById(R.id.unMute);

        firebaseAuth = FirebaseAuth.getInstance();
        //init sharedPreference
        sp = getActivity().getSharedPreferences("SETTINGS_SP", Context.MODE_PRIVATE);

        //checking last selected
        isCheck = sp.getBoolean("FCM_ENABLED", false);
        pushNoti_switch.setChecked(isCheck);


        if (isCheck){
            //was enable
            noti_status.setText(enabledMessage);
            muteNoti.setVisibility(View.GONE);
            unMuteNoti.setVisibility(View.VISIBLE);

        }else{
            //was disable
            muteNoti.setVisibility(View.VISIBLE);
            unMuteNoti.setVisibility(View.GONE);
            noti_status.setText(disabledMessage);
        }


        pushNoti_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //checked, enable notification
                    subscribeToTopic();


                }else{
                    //unchecked, disable notification
                    unsubscribeToTopic();
                }
            }


        });
        return view;
    }

    private void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //subscribe success
                        //save
                        spEditor = sp.edit();
                        spEditor.putBoolean("FCM_ENABLED", true);
                        spEditor.apply();
                        unMuteNoti.setVisibility(View.VISIBLE);
                        muteNoti.setVisibility(View.GONE);
                        noti_status.setText(enabledMessage);
                        Toast.makeText(getActivity(), ""+enabledMessage, Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //subscribe failed
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unsubscribeToTopic(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //unsubscribe success
                        //save
                        spEditor = sp.edit();
                        spEditor.putBoolean("FCM_ENABLED", false);
                        spEditor.apply();
                        unMuteNoti.setVisibility(View.GONE);
                        muteNoti.setVisibility(View.VISIBLE);
                        noti_status.setText(disabledMessage);
                        Toast.makeText(getActivity(), ""+disabledMessage, Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //unsubscribe Failed
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}