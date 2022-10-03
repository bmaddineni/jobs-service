package com.oneun.jobsservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

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
    @Column(columnDefinition = "CHARACTER LARGE OBJECT")
    private String jobTitle;
    private String dutyStation;
    private String jobFamily;
    private String jobNetwork;
    private String departmentOffice;
    private String level;
    private String postedDate;
    private String deadlineDate;
//    CHARACTER LARGE OBJECT
@Column(columnDefinition = "CHARACTER LARGE OBJECT")
    private String postingUrl;
    private String wfpTypeOfContract;

    @Column(columnDefinition = "CHARACTER LARGE OBJECT")
private String unicefJobDescrBasic;

    @Column(columnDefinition = "CHARACTER LARGE OBJECT")
    private String postingDescrRaw;

    private Date addedDate;
}
