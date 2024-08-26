package com.pherom.easysaleassignment.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pherom.easysaleassignment.R;
import com.pherom.easysaleassignment.viewmodel.UserViewModel;

public class MainActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private FloatingActionButton addUserButton;
    private UserRecyclerViewAdapter userRecyclerViewAdapter;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addUserButton = findViewById(R.id.addUserButton);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerViewAdapter = new UserRecyclerViewAdapter(getApplicationContext());
        userRecyclerView.setAdapter(userRecyclerViewAdapter);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getAllUsers().observe(this, users -> userRecyclerViewAdapter.setUsers(users));
    }

    public void launchAddUserActivity(View v) {
        startActivity(new Intent(getApplicationContext(), EditUserActivity.class));
    }
}