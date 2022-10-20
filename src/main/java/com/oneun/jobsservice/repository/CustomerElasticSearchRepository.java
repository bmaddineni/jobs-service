package com.oneun.jobsservice.repository;

import com.oneun.jobsservice.model.elastic.Customer;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerElasticSearchRepository extends ElasticsearchRepository<Customer,String> {

    List<Customer> findByFirstName(String firstName);
}
