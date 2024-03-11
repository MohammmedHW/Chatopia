package com.example.chatopia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatopia.model.UserModel;
import com.example.chatopia.utils.FirebaseUtil;
import com.example.chatopia.utils.androidutil;
import com.github.dhaval2404.imagepicker.ImagePicker;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class ProfileFragment extends Fragment {
ImageView profilPic;
EditText username,phone;
Button updateProfile;
ProgressBar progressBar;
TextView textView, logout_btn;
UserModel curreUserModel;
    Uri selectedImageUri;
    ActivityResultLauncher<Intent> imagePickLauncher;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            androidutil.setProfilePic(getContext(),selectedImageUri,profilPic);
                        }
                    }
                }
        );
    }




    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
          View view = inflater.inflate(R.layout.fragment_profile,container,false);
          profilPic = view.findViewById(R.id.profile_image_view);
          username = view.findViewById(R.id.profile_username);
        updateProfile = view.findViewById(R.id.profle_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        phone = view.findViewById(R.id.profile_phone);
        logout_btn = view.findViewById(R.id.logout_btn);

        getUserData();
        logout_btn.setOnClickListener(v->{
            FirebaseUtil.logout();
            Intent intent = new Intent(getContext(),SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        updateProfile.setOnClickListener(v->{
updateBtnClick();
        });

        profilPic.setOnClickListener((v)->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });
        return view;

    }


    void updateBtnClick(){
        String newUsername = username.getText().toString();
        if(newUsername.isEmpty() || newUsername.length()<3){
            username.setError("Username length should be at least 3 chars");
            return;
        }
        curreUserModel.setUsername(newUsername);
        setInProgress(true);
        if(selectedImageUri!=null){
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        updateToFirestore();
                    });
        }else{
            updateToFirestore();
        }

        updateToFirestore();
    }
    void updateToFirestore(){
    FirebaseUtil.currentUserDetails().set(curreUserModel)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    setInProgress(false);
                    androidutil.ShowToast(getContext(),"Updated Successfully");

                }else{
                    androidutil.ShowToast(getContext(),"Updated Failed");
                }
            });
    }
    void getUserData(){
        setInProgress(true);
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri  = task.getResult();
                        androidutil.setProfilePic(getContext(),uri,profilPic);
                    }
                });
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                curreUserModel = task.getResult().toObject(UserModel.class);
                if (curreUserModel != null) {
                    username.setText(curreUserModel.getUsername());
                    phone.setText(curreUserModel.getPhone());
                }
            }
        });
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateProfile.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            updateProfile.setVisibility(View.VISIBLE);
        }
    }
}