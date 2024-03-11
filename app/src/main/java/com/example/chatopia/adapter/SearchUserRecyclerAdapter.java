package com.example.chatopia.adapter;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatopia.ChatActivity;
import com.example.chatopia.R;
import com.example.chatopia.model.UserModel;
import com.example.chatopia.utils.FirebaseUtil;
import com.example.chatopia.utils.androidutil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Objects;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel,SearchUserRecyclerAdapter.UserModelViewHolder> {

   Context context;
    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options,Context context) {
        super(options);
        this.context =context;
    }

    @SuppressLint("SetTextI18n")

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.username.setText(model.getUsername());
        holder.phone.setText(model.getPhone());


        if (model.getUserId() != null && model.getUserId().equals(FirebaseUtil.currentUserId())) {
            holder.username.setText(model.getUsername() + " (Me)");
        }

        if (model.getUsername() != null) {
            FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId())
                    .getDownloadUrl()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri uri = task.getResult();
                            if (uri != null) {
                                androidutil.setProfilePic(getContext(), uri, holder.profile_pic);
                            }
                        }
                    });
        }




        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, ChatActivity.class);
            androidutil.passUserModel(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }


    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row,parent,false);

        return new UserModelViewHolder(view);


    }

    static class UserModelViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView phone;
        ImageView profile_pic;
        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name_text);
            phone = itemView.findViewById(R.id.phone_text);
            profile_pic = itemView.findViewById(R.id.profile_pic_image_view);

        }
    }
}
