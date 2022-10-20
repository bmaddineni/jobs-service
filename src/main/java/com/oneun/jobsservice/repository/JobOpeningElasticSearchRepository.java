package com.oneun.jobsservice.repository;

import com.oneun.jobsservice.dto.JobOpeningsByEntity;
import com.oneun.jobsservice.model.elastic.Customer;
import com.oneun.jobsservice.model.elastic.JobOpening;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobOpeningElasticSearchRepository extends ElasticsearchRepository<JobOpening,String> {

    List<JobOpening> findByUnEntity(String entity);

    List<JobOpening> findByJobOpeningId(String iloJobId);


}
