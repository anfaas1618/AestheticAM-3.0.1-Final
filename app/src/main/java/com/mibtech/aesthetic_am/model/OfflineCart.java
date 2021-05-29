package com.mibtech.aesthetic_am.model;

import java.util.ArrayList;

public class OfflineCart {

    String id, product_id, type, measurement, measurement_unit_id, price, discounted_price, serve_for, stock_unit_id, tax_title, tax_percentage;
    ArrayList<OfflineItems> item;

    public OfflineCart(String id, String product_id, String type, String measurement, String measurement_unit_id, String price, String discounted_price, String serve_for, String stock_unit_id, ArrayList<OfflineItems> item, String tax_title, String tax_percentage) {
        this.id = id;
        this.product_id = product_id;
        this.type = type;
        this.measurement = measurement;
        this.measurement_unit_id = measurement_unit_id;
        this.price = price;
        this.discounted_price = discounted_price;
        this.serve_for = serve_for;
        this.stock_unit_id = stock_unit_id;
        this.item = item;
        this.tax_title = tax_title;
        this.tax_percentage = tax_percentage;
    }

    public String getTax_title() {
        return tax_title;
    }

    public void setTax_title(String tax_title) {
        this.tax_title = tax_title;
    }

    public String getTax_percentage() {
        return tax_percentage;
    }

    public void setTax_percentage(String tax_percentage) {
        this.tax_percentage = tax_percentage;
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

    public String getStock_unit_id() {
        return stock_unit_id;
    }

    public void setStock_unit_id(String stock_unit_id) {
        this.stock_unit_id = stock_unit_id;
    }

    public ArrayList<OfflineItems> getItem() {
        return item;
    }

    public void setItem(ArrayList<OfflineItems> item) {
        this.item = item;
    }
}
