package com.pherom.easysaleassignment.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.pherom.easysaleassignment.network.UserApiService;
import com.pherom.easysaleassignment.util.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRepositoryTest {

    private final MockWebServer mockWebServer = new MockWebServer();
    private UserDatabase db;
    private UserRepository userRepository;

    @Before
    public void setUp() {
        db = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                UserDatabase.class
        ).build();

        userRepository = new UserRepository(db.userDao(), new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(UserApiService.class));
    }

    @Test
    public void testFetchUsersAndSave() throws InterruptedException, ExecutionException {
        String mockResponse = "{\"page\":1,\"per_page\":1,\"total\":1,\"total_pages\":1,\"data\":[{\"id\":1,\"email\":\"george.bluth@reqres.in\",\"first_name\":\"George\",\"last_name\":\"Bluth\",\"avatar\":\"https://reqres.in/img/faces/1-image.jpg\"},{\"id\":2,\"email\":\"janet.weaver@reqres.in\",\"first_name\":\"Janet\",\"last_name\":\"Weaver\",\"avatar\":\"https://reqres.in/img/faces/2-image.jpg\"},{\"id\":3,\"email\":\"emma.wong@reqres.in\",\"first_name\":\"Emma\",\"last_name\":\"Wong\",\"avatar\":\"https://reqres.in/img/faces/3-image.jpg\"},{\"id\":4,\"email\":\"eve.holt@reqres.in\",\"first_name\":\"Eve\",\"last_name\":\"Holt\",\"avatar\":\"https://reqres.in/img/faces/4-image.jpg\"},{\"id\":5,\"email\":\"charles.morris@reqres.in\",\"first_name\":\"Charles\",\"last_name\":\"Morris\",\"avatar\":\"https://reqres.in/img/faces/5-image.jpg\"},{\"id\":6,\"email\":\"tracey.ramos@reqres.in\",\"first_name\":\"Tracey\",\"last_name\":\"Ramos\",\"avatar\":\"https://reqres.in/img/faces/6-image.jpg\"}],\"support\":{\"url\":\"https://reqres.in/#support-heading\",\"text\":\"To keep ReqRes free, contributions towards server costs are appreciated!\"}}";
        mockWebServer.enqueue(new MockResponse().setBody(mockResponse).setResponseCode(200).setHeader("Content-Type", "application/json"));

        userRepository.fetchUsersAndSave();
        TimeUnit.SECONDS.sleep(5);

        User user1 = LiveDataTestUtil.getOrAwaitValue(userRepository.getUserById(1));

        assertNotNull(user1);
        assertEquals(1, user1.getId());
        assertEquals("george.bluth@reqres.in", user1.getEmail());
        assertEquals("George", user1.getFirstName());
        assertEquals("Bluth", user1.getLastName());
        assertEquals("https://reqres.in/img/faces/1-image.jpg", user1.getAvatar());
    }

    @Test
    public void testInsertAndGetAllUsers() throws InterruptedException, ExecutionException {
        User user1 = new User("johndoe@emailprovider.com", "John", "Doe", "https://avatarprovider.com/johndoe_avatar.jpg");
        User user2 = new User("janesmith@emailprovider.com", "Jane", "Smith", "https://avatarprovider.com/janesmith_avatar.jpg");
        Future<?> future1 = userRepository.insert(user1);
        user1.setId(1);
        Future<?> future2 = userRepository.insert(user2);
        user2.setId(2);

        future1.get();
        future2.get();

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
    public void testInsertAndGetUser() throws InterruptedException, ExecutionException {
        User user = new User("johndoe@emailprovider.com", "John", "Doe", "https://avatarprovider.com/johndoe_avatar.jpg");
        Future<?> future = userRepository.insert(user);
        user.setId(1);

        future.get();

        User insertedUser = LiveDataTestUtil.getOrAwaitValue(userRepository.getUserById(user.getId()));
        assertNotNull(insertedUser);
        assertEquals("johndoe@emailprovider.com", insertedUser.getEmail());
        assertEquals("John", insertedUser.getFirstName());
        assertEquals("Doe", insertedUser.getLastName());
        assertEquals("https://avatarprovider.com/johndoe_avatar.jpg", insertedUser.getAvatar());
    }

    @Test
    public void testUpdateUser() throws InterruptedException, ExecutionException {
        User user = new User("janedoe@emailprovider.com", "Jane", "Doe", "https://avatarprovider.com/janedoe_avatar.jpg");
        Future<?> insertFuture = userRepository.insert(user);
        user.setId(1);
        user.setLastName("Smith");

        insertFuture.get();

        Future<?> updateFuture = userRepository.update(user);

        updateFuture.get();

        User updatedUser = LiveDataTestUtil.getOrAwaitValue(userRepository.getUserById(user.getId()));
        assertEquals("Smith", updatedUser.getLastName());
    }

    @Test
    public void testDeleteUser() throws InterruptedException, ExecutionException {
        User user = new User("janedoe@emailprovider.com", "Jane", "Doe", "https://avatarprovider.com/janedoe_avatar.jpg");
        Future<?> insertFuture = userRepository.insert(user);
        user.setId(1);

        insertFuture.get();

        Future<?> deleteFuture = userRepository.delete(user);

        deleteFuture.get();

        assertNull(LiveDataTestUtil.getOrAwaitValue(userRepository.getUserById(user.getId())));
    }

    @Test
    public void testGetUserCount() throws ExecutionException, InterruptedException {
        User user = new User("janedoe@emailprovider.com", "Jane", "Doe", "https://avatarprovider.com/janedoe_avatar.jpg");
        userRepository.insert(user).get();

        List<User> allUsers = LiveDataTestUtil.getOrAwaitValue(userRepository.getAllUsers());
        int userCount = userRepository.getUserCount().get();

        assertEquals(allUsers.size(), userCount);
    }

    @After
    public void tearDown() throws IOException, InterruptedException {
        db.close();
        mockWebServer.shutdown();
    }
}
