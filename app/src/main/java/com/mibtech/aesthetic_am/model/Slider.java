package com.mibtech.aesthetic_am.model;


public class Slider {
    final String image;
    String type;
    String type_id;
    String name;

    public Slider(String type, String type_id, String name, String image) {
        this.type = type;
        this.type_id = type_id;
        this.name = name;
        this.image = image;
    }

    public Slider(String image) {
        this.image = image;
    }

    public String getType_id() {
        return type_id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getType() {
        return type;
    }
}
