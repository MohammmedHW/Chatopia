package com.example.chatopia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.widget.EditText;
import android.widget.ImageButton;

import com.example.chatopia.adapter.SearchUserRecyclerAdapter;
import com.example.chatopia.model.UserModel;
import com.example.chatopia.utils.FirebaseUtil;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity {
EditText searchInput;
ImageButton searchButton, backButton;
RecyclerView recyclerView;
SearchUserRecyclerAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchButton = findViewById(R.id.search_user_btn);
        searchInput =findViewById(R.id.seach_username_input);
        backButton =findViewById(R.id.back_btn);
        recyclerView =findViewById(R.id.search_user_recyle_view);
        searchInput.requestFocus();
        backButton.setOnClickListener(v->{
            onBackPressed();
        });
        searchButton.setOnClickListener(v->{
            String searchTerms  = searchInput.getText().toString();
            if(searchTerms.isEmpty()||searchTerms.length()<=3){
                searchInput.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerms);
        });
    }
    public void setupSearchRecyclerView(String searchTerms){
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username",searchTerms);
        FirestoreRecyclerOptions<UserModel> options = new  FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class).build();


         adapter = new SearchUserRecyclerAdapter(options,getApplicationContext());
         recyclerView.setLayoutManager(new LinearLayoutManager(this));
         recyclerView.setAdapter(adapter);
         adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null){
            adapter.startListening();
        }
    }
}