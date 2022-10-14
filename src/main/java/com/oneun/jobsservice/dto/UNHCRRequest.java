package com.oneun.jobsservice.dto;


public class UNHCRRequest {

    private String searchText;

    public UNHCRRequest(String searchText) {
        this.searchText = searchText;
    }

    public UNHCRRequest() {
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
