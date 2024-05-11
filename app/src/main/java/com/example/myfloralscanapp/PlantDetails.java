package com.example.myfloralscanapp;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;


public class PlantDetails {

    private String commonName; //test
    private String scientificName;
    private String imageUrl;

    private String description;

    public PlantDetails(String commonName, String scientificName, String imageUrl, String description) {
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    //Getters & Setters for these details

    public String getCommonName()
    {
        return commonName;
    }

    public String getScientificName()
    {
        return scientificName;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public String getDescription()
    {
        return description;
    }
}