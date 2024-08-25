package com.pherom.easysaleassignment.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import androidx.test.core.app.ApplicationProvider;

import com.pherom.easysaleassignment.model.User;
import com.pherom.easysaleassignment.util.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserViewModelTest {

    private UserViewModel userViewModel;

    @Before
    public void setUp() {
        userViewModel = new UserViewModel(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void testInsertAndGetAllUsers() throws InterruptedException, ExecutionException {
        User user1 = new User("johndoe@emailprovider.com", "John", "Doe", "https://avatarprovider.com/johndoe_avatar.jpg");
        User user2 = new User("janesmith@emailprovider.com", "Jane", "Smith", "https://avatarprovider.com/janesmith_avatar.jpg");
        userViewModel.insert(user1).get();
        userViewModel.insert(user2).get();

        List<User> allUsers = LiveDataTestUtil.getOrAwaitValue(userViewModel.getAllUsers());
        int allUsersSize = allUsers.size();

        assertNotNull(allUsers);
        assertEquals("johndoe@emailprovider.com", allUsers.get(allUsersSize - 2).getEmail());
        assertEquals("John", allUsers.get(allUsersSize - 2).getFirstName());
        assertEquals("Doe", allUsers.get(allUsersSize - 2).getLastName());
        assertEquals("https://avatarprovider.com/johndoe_avatar.jpg", allUsers.get(allUsersSize - 2).getAvatar());
        assertEquals("janesmith@emailprovider.com", allUsers.get(allUsersSize - 1).getEmail());
        assertEquals("Jane", allUsers.get(allUsersSize - 1).getFirstName());
        assertEquals("Smith", allUsers.get(allUsersSize - 1).getLastName());
        assertEquals("https://avatarprovider.com/janesmith_avatar.jpg", allUsers.get(allUsersSize - 1).getAvatar());
    }

    @Test
    public void testInsertAndGetUser() throws InterruptedException, ExecutionException {
        User user = new User("johndoe@emailprovider.com", "John", "Doe", "https://avatarprovider.com/johndoe_avatar.jpg");
        userViewModel.insert(user).get();

        List<User> allUsers = LiveDataTestUtil.getOrAwaitValue(userViewModel.getAllUsers());
        int allUsersSize = allUsers.size();

        User insertedUser = LiveDataTestUtil.getOrAwaitValue(userViewModel.getUserById(allUsersSize));

        assertNotNull(insertedUser);
        assertEquals("johndoe@emailprovider.com", insertedUser.getEmail());
        assertEquals("John", insertedUser.getFirstName());
        assertEquals("Doe", insertedUser.getLastName());
        assertEquals("https://avatarprovider.com/johndoe_avatar.jpg", insertedUser.getAvatar());
    }

    @Test
    public void testUpdateUser() throws InterruptedException, ExecutionException {
        User user = new User("janedoe@emailprovider.com", "Jane", "Doe", "https://avatarprovider.com/janedoe_avatar.jpg");
        userViewModel.insert(user).get();

        List<User> allUsers = LiveDataTestUtil.getOrAwaitValue(userViewModel.getAllUsers());
        int allUsersSize = allUsers.size();

        user.setId(allUsersSize);
        user.setLastName("Smith");
        userViewModel.update(user).get();
        User updatedUser = LiveDataTestUtil.getOrAwaitValue(userViewModel.getUserById(user.getId()));
        assertEquals("Smith", updatedUser.getLastName());
    }

    @Test
    public void testDeleteUser() throws InterruptedException, ExecutionException {
        User user = new User("janedoe@emailprovider.com", "Jane", "Doe", "https://avatarprovider.com/janedoe_avatar.jpg");
        userViewModel.insert(user).get();

        List<User> allUsers = LiveDataTestUtil.getOrAwaitValue(userViewModel.getAllUsers());
        int allUsersSize = allUsers.size();

        user.setId(allUsersSize);

        userViewModel.delete(user).get();
        assertNull(LiveDataTestUtil.getOrAwaitValue(userViewModel.getUserById(user.getId())));
    }

}
