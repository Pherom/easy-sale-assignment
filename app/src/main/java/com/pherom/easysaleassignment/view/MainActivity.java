package com.pherom.easysaleassignment.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pherom.easysaleassignment.R;
import com.pherom.easysaleassignment.viewmodel.UserViewModel;

public class MainActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private UserRecyclerViewAdapter userRecyclerViewAdapter;
    private UserViewModel userViewModel;
    private AlertDialog removeUserConfirmationDialog;
    private AlertDialog editUserConfirmationDialog;
    private int swipedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerViewAdapter = new UserRecyclerViewAdapter(getApplicationContext());
        userRecyclerView.setAdapter(userRecyclerViewAdapter);

        setUpRemoveUserConfirmationDialog();
        setUpEditUserConfirmationDialog();
        setUpRecyclerViewSwipeAction();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getAllUsers().observe(this, users -> userRecyclerViewAdapter.setUsers(users));
    }

    public void launchAddUserActivity(View v) {
        startActivity(new Intent(getApplicationContext(), EditUserActivity.class));
    }

    private void setUpRecyclerViewSwipeAction() {
        ItemTouchHelper.Callback itemTouchHelperLeftCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                swipedPosition = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    removeUserConfirmationDialog.show();
                }
                else if (direction == ItemTouchHelper.RIGHT) {
                    editUserConfirmationDialog.show();
                }
            }
        };

        new ItemTouchHelper(itemTouchHelperLeftCallback).attachToRecyclerView(userRecyclerView);
    }

    private void setUpRemoveUserConfirmationDialog() {
        removeUserConfirmationDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.remove_user_confirmation_dialog_message)
                .setPositiveButton(R.string.confirmation_dialog_positive_response, ((dialog, which) -> {
                    userViewModel.delete(userRecyclerViewAdapter.users.get(swipedPosition));
                    userRecyclerViewAdapter.notifyItemRemoved(swipedPosition);
                }))
                .setNegativeButton(R.string.confirmation_dialog_negative_response, ((dialog, which) ->
                {
                    userRecyclerViewAdapter.notifyItemChanged(swipedPosition);
                    dialog.dismiss();
                })).create();
    }

    private void setUpEditUserConfirmationDialog() {
        editUserConfirmationDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.edit_user_confirmation_dialog_message)
                .setPositiveButton(R.string.confirmation_dialog_positive_response, ((dialog, which) -> {
                    Intent intent = new Intent(getApplicationContext(), EditUserActivity.class);
                    intent.putExtra("firstName", userRecyclerViewAdapter.users.get(swipedPosition).getFirstName());
                    intent.putExtra("lastName", userRecyclerViewAdapter.users.get(swipedPosition).getLastName());
                    intent.putExtra("email", userRecyclerViewAdapter.users.get(swipedPosition).getEmail());
                    intent.putExtra("avatar", userRecyclerViewAdapter.users.get(swipedPosition).getAvatar());
                    intent.putExtra("id", userRecyclerViewAdapter.users.get(swipedPosition).getId());
                    startActivity(intent);
                    userRecyclerViewAdapter.notifyItemChanged(swipedPosition);
                }))
                .setNegativeButton(R.string.confirmation_dialog_negative_response, ((dialog, which) ->
                {
                    userRecyclerViewAdapter.notifyItemChanged(swipedPosition);
                    dialog.dismiss();
                })).create();
    }
}