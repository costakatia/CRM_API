package org.crmApi.controller;

import org.crmApi.model.User;
import org.crmApi.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testListUsers() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setRole("ROLE_USER");

        User user2 = new User();
        user2.setUsername("user2");
        user2.setRole("ROLE_ADMIN");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Call the method
        List<User> users = userController.listUsers();

        // Verify the results
        assertEquals(2, users.size());
        assertEquals("user1", users.get(0).getUsername());
        assertEquals("user2", users.get(1).getUsername());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setUsername("newuser");
        user.setRole("ROLE_USER");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Call the method
        User createdUser = userController.createUser("newuser", "password", "ROLE_USER");

        // Verify the results
        assertNotNull(createdUser);
        assertEquals("newuser", createdUser.getUsername());
        assertEquals("ROLE_USER", createdUser.getRole());

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    public void testUpdateUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("existinguser");
        existingUser.setRole("ROLE_USER");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Call the method
        User updatedUser = userController.updateUser(1L, "updateduser", "newpassword", "ROLE_ADMIN");

        // Verify the results
        assertNotNull(updatedUser);
        assertEquals("updateduser", updatedUser.getUsername());
        assertEquals("ROLE_ADMIN", updatedUser.getRole());
        assertEquals("encodedPassword", updatedUser.getPassword());

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test(expected = RuntimeException.class)
    public void testUpdateUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Call the method and expect an exception
        userController.updateUser(1L, "updateduser", "newpassword", "ROLE_ADMIN");

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDeleteUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(userRepository).deleteById(anyLong());

        // Call the method
        userController.deleteUser(1L);

        // Verify the results
        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test(expected = RuntimeException.class)
    public void testDeleteUserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        // Call the method and expect an exception
        userController.deleteUser(1L);

        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, never()).deleteById(anyLong());
    }
}