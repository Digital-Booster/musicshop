package net.digitalbooster.musicshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.digitalbooster.musicshop.repository.CustomerRepository;
import net.digitalbooster.musicshop.model.Customer;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public boolean emailExists(String email) {
        return customerRepository.findByEmail(email) != null;
    }
}