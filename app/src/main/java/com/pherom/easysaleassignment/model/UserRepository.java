package com.pherom.easysaleassignment.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService executorService;
    private final LiveData<List<User>> allUsers;

    public UserRepository(UserDao userDao) {
        executorService = Executors.newSingleThreadExecutor();
        this.userDao = userDao;
        allUsers = userDao.getAllUsers();
    }

    public void insert(User user) {
        executorService.execute(() -> userDao.insert(user));
    }

    public void update(User user) {
        executorService.execute(() -> userDao.update(user));
    }

    public void delete(User user) {
        executorService.execute(() -> userDao.delete(user));
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public LiveData<User> getUserById(int id) {
        return userDao.getUserById(id);
    }
}
