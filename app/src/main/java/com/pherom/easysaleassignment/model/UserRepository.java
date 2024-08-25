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

    public List<Future<?>> fetchUsersAndSave() {
        return fetchUsersAndSaveRec(1);
    }

    private List<Future<?>> fetchUsersAndSaveRec(int page) {
        List<Future<?>> result = new LinkedList<>();
        userApiService.getUserPage(page).enqueue(new Callback<UserPage>() {
            @Override
            public void onResponse(@NonNull Call<UserPage> call, @NonNull Response<UserPage> response) {
                boolean moreUsersToFetch = true;
                List<Future<?>> futures = fetchUserPageOnResponse(call, response);
                if (futures.get(0) == null) {
                    moreUsersToFetch = false;
                    futures.remove(0);
                }
                result.addAll(futures);
                if (moreUsersToFetch) {
                    result.addAll(fetchUsersAndSaveRec(page + 1));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserPage> call, @NonNull Throwable throwable) {
                fetchUserPageOnFailure(call, throwable);
            }
        });

        return result;
    }

    private List<Future<?>> fetchUserPageOnResponse(Call<UserPage> call, Response<UserPage> response) {
        List<Future<?>> futures = new LinkedList<>();
        if (response.isSuccessful() && response.body() != null) {
            UserPage userPage = response.body();
            if (userPage.getPage() == userPage.getTotalPages()) {
                futures.add(null);
            }
            for (UserDto userDto : userPage.getData()) {
                User user = new User(userDto.getEmail(), userDto.getFirstName(), userDto.getLastName(), userDto.getAvatar());
                futures.add(insert(user));
            }
        }
        return futures;
    }

    private void fetchUserPageOnFailure(Call<UserPage> call, Throwable throwable) {
        Log.e(UserRepository.class.getName(), "Failed to fetch users from API");
    }
}
