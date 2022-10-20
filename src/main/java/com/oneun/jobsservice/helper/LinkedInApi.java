package com.oneun.jobsservice.helper;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LinkedInApi {

//    @Value("${LINKEDIN_API_TOKEN}")
    private String token = "Bearer "+System.getProperty("bearerToken");

    @Value("${LINKEDIN_PROFILE_END_POINT}")
    private String endPointForLinkedinUserProfile;

    @Value("${LINKEDIN_POST_END_POINT}")
    private String endPointToPublishPost;

    public JSONObject submitLinkedInPost(String text) throws JSONException {

        RestTemplate shareTextRestTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", token);

        JSONObject textShare = null;
        String id = getLinkedInProfile().getString("id");

        textShare = createLinkedInHttpHeaderForPost(id, text);

        HttpEntity<?> textShareHttpEntity = new HttpEntity<Object>(textShare.toString(), httpHeaders);

        System.out.println(textShareHttpEntity);

//        String postUrlEndPoint = "https://api.linkedin.com/v2/ugcPosts";
        ResponseEntity<String> responseEntity = shareTextRestTemplate.exchange(endPointToPublishPost, HttpMethod.POST, textShareHttpEntity, String.class);

        return new JSONObject(responseEntity.getBody().toString());

    }

    private JSONObject createLinkedInHttpHeaderForPost(String id, String text) throws JSONException {

        JSONObject shareCommentary= new JSONObject();
        shareCommentary.put("text",text);

        JSONObject shareContent = new JSONObject();
        shareContent.put("shareCommentary",shareCommentary);
        shareContent.put("shareMediaCategory","NONE");


        JSONObject specificContent = new JSONObject();
        specificContent.put("com.linkedin.ugc.ShareContent",shareContent);

        JSONObject visibility = new JSONObject();
        visibility.put("com.linkedin.ugc.MemberNetworkVisibility", "PUBLIC");

        JSONObject textShare = new JSONObject();
        textShare.put("author","urn:li:person:"+id);
        textShare.put("lifecycleState","PUBLISHED");
        textShare.put("specificContent",specificContent);
        textShare.put("visibility",visibility);

        return  textShare;
    }

    public JSONObject getLinkedInProfile() throws JSONException {


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization",token);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        System.out.println(endPointForLinkedinUserProfile);

        ResponseEntity<String> response = restTemplate.exchange(endPointForLinkedinUserProfile, HttpMethod.GET,entity,String.class);

        JSONObject jsonObjectGetProfile = new JSONObject(response.getBody().toString());

        return jsonObjectGetProfile;
    }


}
