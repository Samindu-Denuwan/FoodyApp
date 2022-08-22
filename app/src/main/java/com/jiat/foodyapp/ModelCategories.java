package com.jiat.foodyapp;

public class ModelCategories {
    private String name;
    private  String details;
    private  String image;

    public ModelCategories() {
    }

    public ModelCategories(String name, String details, String image) {

        this.name = name;
        this.details = details;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
