package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private User mUser;

    @Before()
    public void startUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

        when(encoder.encode("somepassword")).thenReturn("encodedpassword");
    }

    @Test
    public void testFindByName() {
        setUser();

        when(userRepository.findByUsername(mUser.getUsername())).thenReturn(mUser);

        final ResponseEntity<User> response = userController.findByUserName(mUser.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(mUser.getId(), responseBody.getId());
        assertEquals(mUser.getUsername(), responseBody.getUsername());
        assertEquals(mUser.getPassword(), responseBody.getPassword());
    }



    @Test
    public void testFindByUserId() {
        setUser();

        when(userRepository.findById(mUser.getId())).thenReturn(Optional.of(mUser));

        final ResponseEntity<User> response = userController.findById(mUser.getId());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(mUser.getId(), responseBody.getId());
        assertEquals(mUser.getUsername(), responseBody.getUsername());
        assertEquals(mUser.getPassword(), responseBody.getPassword());
    }

    @Test
    public void testCreateUserHappyPath() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("somepassword");
        createUserRequest.setConfirmPassword("somepassword");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("encodedpassword", user.getPassword());
    }

    @Test
    public void testCreateUserNotConfirmPassword() {
        when(encoder.encode("somepassword")).thenReturn("encodedpassword");

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("somepassword");
        createUserRequest.setConfirmPassword("otherpassword");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    private void setUser() {
        mUser = new User();
        mUser.setId(0);
        mUser.setUsername("test");
        mUser.setPassword("somepassword");
    }
}