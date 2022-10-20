package com.oneun.jobsservice.model.elastic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oneun.jobsservice.model.JobpostingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.Date;

@Data
@Document(indexName = "jobopening")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobOpening {
    @Id
    private String id;

    private String unEntity;
    private String jobOpeningId;
//    @Column(columnDefinition = "CHARACTER LARGE OBJECT")
@Column(columnDefinition = "TEXT")
    private String jobTitle;
    private String dutyStation;
    private String jobFamily;
    private String jobNetwork;
    private String departmentOffice;
    private String level;
    private String postedDate;
    private String deadlineDate;
//    CHARACTER LARGE OBJECT
//@Column(columnDefinition = "CHARACTER LARGE OBJECT")
@Column(columnDefinition = "TEXT")
    private String postingUrl;

    private String wfpTypeOfContract;

//    @Column(columnDefinition = "CHARACTER LARGE OBJECT")
//    @JsonIgnore
@Column(columnDefinition = "TEXT")
private String unicefJobDescrBasic;

//    @Column(columnDefinition = "CHARACTER LARGE OBJECT")
//    @JsonIgnore
@Column(columnDefinition = "TEXT")
    private String postingDescrRaw;

    private Date addedDate;

    private JobpostingStatus jobpostingStatus;
}
