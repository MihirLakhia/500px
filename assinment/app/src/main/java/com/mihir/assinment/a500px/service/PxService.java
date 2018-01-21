package com.mihir.assinment.a500px.service;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface PxService {
    String API_URL = "https://api.500px.com/";
    String CONSUMER_KEY = "AJHjroGYgXAl3OabA1SjMw180lakiWUOIyJBIodC";

    @GET("/v1/photos/search?&rpp=100&image_size=4&consumer_key=" + CONSUMER_KEY)
//    @GET("/v1/photos/search?image_size=4&consumer_key=" + CONSUMER_KEY)
    Observable<SearchResults> searchPhotos(@Query("term") String query);
}
