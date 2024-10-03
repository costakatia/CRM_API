package org.crmApi.controller;


import org.crmApi.repository.CustomerRepository;
import org.crmApi.repository.UserRepository;
import org.crmApi.model.Customer;
import org.crmApi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListCustomers() {
        Customer customer1 = new Customer();
        Customer customer2 = new Customer();
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));

        var result = customerController.listCustomers();

        assertEquals(2, result.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetCustomer() {
        Customer customer = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        var result = customerController.getCustomer(1L);

        assertEquals(customer, result);
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            customerController.getCustomer(1L);
        });

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void testCreateCustomer() throws IOException {
        User user = new User();
        when(userRepository.findByUsername("John")).thenReturn(user);

        MockMultipartFile photo = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", "test image".getBytes());

        Customer customer = new Customer();
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        var result = customerController.createCustomer("John", "Doe", "123", photo);

        assertEquals(customer, result);
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(userRepository, times(1)).findByUsername("John");
    }

    @Test
    void testUpdateCustomer() throws IOException {
        User user = new User();
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        Customer customer = new Customer();
        customer.setName("OldName");
        customer.setSurname("OldSurname");
        customer.setPhoto("old photo".getBytes());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        MockMultipartFile photo = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", "test image".getBytes());

        when(authentication.getName()).thenReturn("John");

        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = customerController.updateCustomer(1L, "John", "Doe", photo, authentication);

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        assertArrayEquals(photo.getBytes(), result.getPhoto());
        assertEquals(user, result.getLastModifiedBy());
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(userRepository, times(1)).findByUsername("John");
    }

    @Test
    void testUpdateCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        MockMultipartFile photo = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", "test image".getBytes());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            customerController.updateCustomer(1L, "John", "Doe", photo, authentication);
        });

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void testDeleteCustomer() {
        doNothing().when(customerRepository).deleteById(1L);

        customerController.deleteCustomer(1L);

        verify(customerRepository, times(1)).deleteById(1L);
    }
}