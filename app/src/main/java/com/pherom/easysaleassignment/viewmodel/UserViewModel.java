package com.pherom.easysaleassignment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pherom.easysaleassignment.R;
import com.pherom.easysaleassignment.model.User;
import com.pherom.easysaleassignment.model.UserDatabase;
import com.pherom.easysaleassignment.model.UserRepository;
import com.pherom.easysaleassignment.network.ApiClient;
import com.pherom.easysaleassignment.network.UserApiService;

import java.util.List;
import java.util.concurrent.Future;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final LiveData<List<User>> allUsers;

    public UserViewModel(@NonNull Application application) {
        super(application);
        UserDatabase userDatabase = UserDatabase.getDatabase(application.getApplicationContext());
        ApiClient apiClient = ApiClient.getInstance(application.getString(R.string.reqres_api_url));
        userRepository = new UserRepository(userDatabase.userDao(), apiClient.getRetrofit().create(UserApiService.class));
        allUsers = userRepository.getAllUsers();
        userRepository.fetchUsersAndSave();
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public Future<?> insert(User user) {
        return userRepository.insert(user);
    }

    public Future<?> delete(User user) {
        return userRepository.delete(user);
    }

    public Future<?> update(User user) {
        return userRepository.update(user);
    }

    public LiveData<User> getUserById(int id) {
        return userRepository.getUserById(id);
    }
}
