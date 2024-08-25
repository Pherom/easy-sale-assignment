package com.pherom.easysaleassignment.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.pherom.easysaleassignment.network.UserApiService;
import com.pherom.easysaleassignment.network.UserDto;
import com.pherom.easysaleassignment.network.UserPage;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService executorService;
    private final LiveData<List<User>> allUsers;
    private final UserApiService userApiService;

    public UserRepository(UserDao userDao, UserApiService userApiService) {
        executorService = Executors.newSingleThreadExecutor();
        this.userDao = userDao;
        this.userApiService = userApiService;
        allUsers = userDao.getAllUsers();
    }

    public Future<?> insert(User user) {
        return executorService.submit(() -> userDao.insert(user));
    }

    public Future<?> update(User user) {
       return executorService.submit(() -> userDao.update(user));
    }

    public Future<?> delete(User user) {
        return executorService.submit(() -> userDao.delete(user));
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public LiveData<User> getUserById(int id) {
        return userDao.getUserById(id);
    }

    public void fetchUsersAndSave() {
        int userCount;
        try {
            userCount = getUserCount().get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (userCount == 0) {
            fetchUsersAndSaveRec(1);
        }
    }

    public Future<Integer> getUserCount() {
        return executorService.submit(userDao::getUserCount);
    }

    private void fetchUsersAndSaveRec(int page) {
            userApiService.getUserPage(page).enqueue(new Callback<UserPage>() {
            @Override
            public void onResponse(@NonNull Call<UserPage> call, @NonNull Response<UserPage> response) {
                boolean moreUsersToFetch = fetchUserPageOnResponse(call, response);
                if (moreUsersToFetch) {
                    fetchUsersAndSaveRec(page + 1);
                }
                else {
                    Log.e(UserRepository.class.getName(), "Failed to fetch users from API");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserPage> call, @NonNull Throwable throwable) {
                fetchUserPageOnFailure(call, throwable);
            }
        });
    }

    private boolean fetchUserPageOnResponse(Call<UserPage> call, Response<UserPage> response) {
        if (response.isSuccessful() && response.body() != null) {
            UserPage userPage = response.body();
            for (UserDto userDto : userPage.getData()) {
                User user = new User(userDto.getEmail(), userDto.getFirstName(), userDto.getLastName(), userDto.getAvatar());
                try {
                    insert(user).get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return userPage.getPage() < userPage.getTotalPages();
        }
        return false;
    }

    private void fetchUserPageOnFailure(Call<UserPage> call, Throwable throwable) {
        Log.e(UserRepository.class.getName(), "Failed to fetch users from API");
    }
}
