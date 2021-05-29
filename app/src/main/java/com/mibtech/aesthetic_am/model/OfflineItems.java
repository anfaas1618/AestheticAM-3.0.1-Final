package com.mibtech.aesthetic_am.model;

import java.util.ArrayList;

public class OfflineItems {

    String id, product_id, type, measurement, measurement_unit_id, price, discounted_price, serve_for, stock, stock_unit_id, name, image, unit, cart_count;
    ArrayList other_images;

    public String getCart_count() {
        return cart_count;
    }

    public void setCart_count(String cart_count) {
        this.cart_count = cart_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getMeasurement_unit_id() {
        return measurement_unit_id;
    }

    public void setMeasurement_unit_id(String measurement_unit_id) {
        this.measurement_unit_id = measurement_unit_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscounted_price() {
        return discounted_price;
    }

    public void setDiscounted_price(String discounted_price) {
        this.discounted_price = discounted_price;
    }

    public String getServe_for() {
        return serve_for;
    }

    public void setServe_for(String serve_for) {
        this.serve_for = serve_for;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getStock_unit_id() {
        return stock_unit_id;
    }

    public void setStock_unit_id(String stock_unit_id) {
        this.stock_unit_id = stock_unit_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList getOther_images() {
        return other_images;
    }

    public void setOther_images(ArrayList other_images) {
        this.other_images = other_images;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
