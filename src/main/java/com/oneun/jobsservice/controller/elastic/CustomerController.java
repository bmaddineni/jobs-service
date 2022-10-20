package com.oneun.jobsservice.controller.elastic;

import com.oneun.jobsservice.model.elastic.Customer;
import com.oneun.jobsservice.repository.CustomerElasticSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/es")
public class CustomerController {

    @Autowired
    private CustomerElasticSearchRepository customerElasticSearchRepository;

    @PostMapping("/saveCustomers")
    public int saveCustomer(@RequestBody List<Customer> customers){

        customerElasticSearchRepository.saveAll(customers);

        return customers.size();
    }

    @PostMapping("/findAll")
    public Iterable<Customer> findAllCustomers() {

        return customerElasticSearchRepository.findAll();


    }

    @PostMapping("/findByName/{firstName}")
    public List<Customer> findByFirstName(@PathVariable String firstName){

        return customerElasticSearchRepository.findByFirstName(firstName);
    }




}
