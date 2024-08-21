package com.pherom.easysaleassignment.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.pherom.easysaleassignment.util.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class UserRepositoryTest {

    private UserDatabase db;
    private UserRepository userRepository;

    @Before
    public void setUp() {
        db = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                UserDatabase.class
        ).build();
        userRepository = new UserRepository(db.userDao());
    }

    @Test
    public void testInsertAndGetAllUsers() throws InterruptedException {
        User user1 = new User("johndoe@emailprovider.com", "John", "Doe", "https://avatarprovider.com/johndoe_avatar.jpg");
        User user2 = new User("janesmith@emailprovider.com", "Jane", "Smith", "https://avatarprovider.com/janesmith_avatar.jpg");
        userRepository.insert(user1);
        user1.setId(1);
        userRepository.insert(user2);
        user2.setId(2);

        List<User> allUsers = LiveDataTestUtil.getOrAwaitValue(userRepository.getAllUsers());
        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
        assertEquals("johndoe@emailprovider.com", allUsers.get(0).getEmail());
        assertEquals("John", allUsers.get(0).getFirstName());
        assertEquals("Doe", allUsers.get(0).getLastName());
        assertEquals("https://avatarprovider.com/johndoe_avatar.jpg", allUsers.get(0).getAvatar());
        assertEquals("janesmith@emailprovider.com", allUsers.get(1).getEmail());
        assertEquals("Jane", allUsers.get(1).getFirstName());
        assertEquals("Smith", allUsers.get(1).getLastName());
        assertEquals("https://avatarprovider.com/janesmith_avatar.jpg", allUsers.get(1).getAvatar());
    }

    @Test
    public void testInsertAndGetUser() throws InterruptedException {
        User user = new User("johndoe@emailprovider.com", "John", "Doe", "https://avatarprovider.com/johndoe_avatar.jpg");
        userRepository.insert(user);
        user.setId(1);

        User insertedUser = LiveDataTestUtil.getOrAwaitValue(userRepository.getUserById(user.getId()));
        assertNotNull(insertedUser);
        assertEquals("johndoe@emailprovider.com", insertedUser.getEmail());
        assertEquals("John", insertedUser.getFirstName());
        assertEquals("Doe", insertedUser.getLastName());
        assertEquals("https://avatarprovider.com/johndoe_avatar.jpg", insertedUser.getAvatar());
    }

    @Test
    public void testUpdateUser() throws InterruptedException {
        User user = new User("janedoe@emailprovider.com", "Jane", "Doe", "https://avatarprovider.com/janedoe_avatar.jpg");
        userRepository.insert(user);
        user.setId(1);
        user.setLastName("Smith");
        userRepository.update(user);
        User updatedUser = LiveDataTestUtil.getOrAwaitValue(userRepository.getUserById(user.getId()));
        assertEquals("Smith", updatedUser.getLastName());
    }

    @Test
    public void testDeleteUser() throws InterruptedException {
        User user = new User("janedoe@emailprovider.com", "Jane", "Doe", "https://avatarprovider.com/janedoe_avatar.jpg");
        userRepository.insert(user);
        user.setId(1);
        userRepository.delete(user);
        assertNull(LiveDataTestUtil.getOrAwaitValue(userRepository.getUserById(user.getId())));
    }

    @After
    public void tearDown() {
        db.close();
    }
}
