package com.mbrats01.epl498_group_project.ui.allAttractions;

import java.util.List;

public class Attraction {

    private String placeId;
    private String name;
    private double lat;
    private double lon;
    private String formatted;
    private String description;
    private String website;
    private String openingHours;
    private List<String> categories;
    private String email;
    private String phone;
    private String imageUrl;

    public Attraction(String placeId,
                      String name,
                      double lat,
                      double lon,
                      String formatted,
                      String description,
                      String website,
                      String openingHours,
                      List<String> categories,
                      String email,
                      String phone,
                      String imageUrl)
    {
        this.placeId = placeId;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.formatted = formatted;
        this.description = description;
        this.website = website;
        this.openingHours = openingHours;
        this.categories = categories;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }

    public Attraction() {
    }


    public String getPlaceId() {
        return placeId;
    }
    public void setPlaceId(String placeId){
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(double lat){
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }
    public void setLon(double lon){
        this.lon = lon;
    }

    public String getFormatted() {
        return formatted;
    }
    public void setFormatted(String formatted){
        this.formatted = formatted;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website){
        this.website = website;
    }

    public String getOpeningHours() {
        return openingHours;
    }
    public void setOpeningHours(String openingHours){
        this.openingHours = openingHours;
    }

    public List<String> getCategories()
    {
        return categories;
    }
    public void setCategories(List<String> categories){
        this.categories = categories;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }
}
