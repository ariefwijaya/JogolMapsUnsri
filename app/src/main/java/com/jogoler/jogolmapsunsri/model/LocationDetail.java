package com.jogoler.jogolmapsunsri.model;

/**
 * Created by RazorX on 12/19/2016.
 */

public class LocationDetail {
    private int id;
    private int category_id;
    private String region_id;
    private String name;
    private String intro;
    private String description;
    private String thumb; //image
    private String link;
    private Double latitude;
    private Double longitude;
    private String address;
    private String phone;
    private String email;
    private boolean favorite;
    private String distance;


    public void setDistance(String distance){this.distance = distance;}

    public String getDistance(){return  distance;}

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setCategory_id(int category_id){
        this.category_id = category_id;
    }

    public int getCategory_id(){
        return category_id;
    }

    public void setRegion_id(String region_id){
        this.region_id = region_id;
    }

    public String getRegion_id(){
        return region_id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setIntro(String intro){
        this.intro = intro;
    }

    public String getIntro(){
        return intro;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public void setThumb(String thumb){
        this.thumb = thumb;
    }

    public String getThumb(){
        return  thumb;
    }

    public void setLink(String link){
        this.link = link;
    }

    public String getLink(){
        return link;
    }

    public void setLatitude(Double latitude){
        this.latitude = latitude;
    }

    public Double getLatitude(){
        return latitude;
    }

    public void setLongitude(Double longitude){
        this.longitude = longitude;
    }

    public Double getLongitude(){
        return longitude;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return address;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getPhone(){
        return phone;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    public void setFavorite(int id_favorite){
        if(id_favorite==0){
            this.favorite = false;
        }else {
            this.favorite = true;
        }

    }

    public boolean isFavorite(){
        return favorite;
    }


}
