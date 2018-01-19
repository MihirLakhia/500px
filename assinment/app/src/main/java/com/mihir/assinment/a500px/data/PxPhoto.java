package com.mihir.assinment.a500px.data;

import com.google.gson.annotations.SerializedName;

public class PxPhoto {

    public long id;
    @SerializedName("name")
    public String name;
    @SerializedName("created_at")
    public String date;
    @SerializedName("votes_count")
    public String vote;
    @SerializedName("description")
    public String description;
    @SerializedName("image_url")
    public String imageUrl;
}
