package org.crmApi.controller;

import org.crmApi.repository.CustomerRepository;
import org.crmApi.repository.UserRepository;
import org.crmApi.model.Customer;
import org.crmApi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
public class CustomerController {


    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;


    @Autowired
    public CustomerController( CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }


    @GetMapping
    public List<Customer> listCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable Long id) {
        return (Customer) customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @PostMapping("customers")
    public Customer createCustomer(@RequestParam String name, @RequestParam String surname, @RequestParam String customerId,
                                   @RequestParam("photo") MultipartFile photo) throws IOException {
        User user = userRepository.findByUsername(name);

        Customer customer = new Customer();
        customer.setName(name);
        customer.setSurname(surname);
        customer.setCustomerId(customerId);
        customer.setPhoto(photo.getBytes());
        customer.setCreatedBy(user);

        return customerRepository.save(customer);
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestParam String name, @RequestParam String surname,
                                   @RequestParam("photo") MultipartFile photo, Authentication authentication) throws IOException {
        User user = userRepository.findByUsername(authentication.getName());

        Customer customer = customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setName(name);
        customer.setSurname(surname);
        customer.setPhoto(photo.getBytes());
        customer.setLastModifiedBy(user);

        return customerRepository.save(customer);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerRepository.deleteById(id);
    }
}
