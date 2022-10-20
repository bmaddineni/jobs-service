package com.oneun.jobsservice.helper;

import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

@Service
public class TwitterApi {


    public String createTweet(String tweet) throws TwitterException {
//        TwitterFactory twitterFactory = new TwitterFactory();
        Twitter twitter = TwitterFactory.getSingleton();
        Status status = twitter.updateStatus("creating baeldung API");
        return status.getText();
    }

}
