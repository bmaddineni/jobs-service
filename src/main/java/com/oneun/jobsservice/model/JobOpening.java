package com.oneun.jobsservice.model;

import lombok.AllArgsConstructor;
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
public class JobOpening {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String unEntity;
    private String jobOpeningId;
    private String jobTitle;
    private String moreInfo;
}
