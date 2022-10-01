package com.oneun.jobsservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity(name="JOB_OPENING_TBL")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobOpening {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String unEntity;
    private String jobOpeningId;
    private String jobTitle;
    private String dutyStation;
    private String jobFamily;
    private String jobNetwork;
    private String departmentOffice;
    private String level;
    private String postedDate;
    private String deadlineDate;
    private String postingUrl;
}
