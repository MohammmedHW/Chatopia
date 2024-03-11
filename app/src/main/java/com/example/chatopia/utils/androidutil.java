package com.example.chatopia.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chatopia.model.UserModel;

import java.security.AccessControlContext;

public class androidutil {
    public static void ShowToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }
    public static void passUserModel(Intent intent, UserModel model){
        intent.putExtra("username",model.getUsername());
        intent.putExtra("phone",model.getPhone());
        intent.putExtra("userId",model.getUserId());

    }
    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel  =new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        return userModel;
    }
    public  static  void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);

    }

    public static void setProfilePic(AccessControlContext context, Uri uri, ImageView profilePic) {

    }
}
