package com.mibtech.aesthetic_am.model;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {

    String tax_percentage, id, name, slug, subcategory_id, row_order, image, description, status, date_added, category_id, indicator, manufacturer, made_in, return_status, cancelable_status, till_status;
    ArrayList<PriceVariation> priceVariations;
    JSONArray other_images;
    boolean is_favorite;

    public Product() {
    }

    public Product(String tax_percentage, String row_order, String till_status, String cancelable_status, String manufacturer, String made_in, String return_status, String id, String name, String slug, String subcategory_id, String image, JSONArray other_images, String description, String status, String date_added, boolean is_favorite, String category_id, ArrayList<PriceVariation> priceVariations, String indicator) {
        this.tax_percentage = tax_percentage;
        this.row_order = row_order;
        this.till_status = till_status;
        this.cancelable_status = cancelable_status;
        this.manufacturer = manufacturer;
        this.made_in = made_in;
        this.return_status = return_status;
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.subcategory_id = subcategory_id;
        this.image = image;
        this.other_images = other_images;
        this.description = description;
        this.status = status;
        this.date_added = date_added;
        this.is_favorite = is_favorite;
        this.priceVariations = priceVariations;
        this.category_id = category_id;
        this.indicator = indicator;
    }

    public String getTax_percentage() {
        return tax_percentage;
    }

    public void setTax_percentage(String tax_percentage) {
        this.tax_percentage = tax_percentage;
    }

    public String getRow_order() {
        return row_order;
    }

    public void setRow_order(String row_order) {
        this.row_order = row_order;
    }

    public String getTill_status() {
        return till_status;
    }

    public void setTill_status(String till_status) {
        this.till_status = till_status;
    }

    public String getCancelable_status() {
        return cancelable_status;
    }

    public void setCancelable_status(String cancelable_status) {
        this.cancelable_status = cancelable_status;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getMade_in() {
        return made_in;
    }

    public void setMade_in(String made_in) {
        this.made_in = made_in;
    }

    public String getReturn_status() {
        return return_status;
    }

    public void setReturn_status(String return_status) {
        this.return_status = return_status;
    }

    public boolean isIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(boolean is_favorite) {
        this.is_favorite = is_favorite;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubcategory_id() {
        return subcategory_id;
    }

    public void setSubcategory_id(String subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public JSONArray getOther_images() {
        return other_images;
    }

    public void setOther_images(JSONArray other_images) {
        this.other_images = other_images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public ArrayList<PriceVariation> getPriceVariations() {
        return priceVariations;
    }

    public void setPriceVariations(ArrayList<PriceVariation> priceVariations) {
        this.priceVariations = priceVariations;
    }
}
