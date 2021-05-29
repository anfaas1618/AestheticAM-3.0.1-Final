package com.mibtech.aesthetic_am.model;

import java.util.ArrayList;

public class Cart {

    String id, user_id, product_id, product_variant_id, qty, date_created, ready_to_cart;
    ArrayList<CartItems> item;

    public String getReady_to_cart() {
        return ready_to_cart;
    }

    public void setReady_to_cart(String ready_to_cart) {
        this.ready_to_cart = ready_to_cart;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_variant_id() {
        return product_variant_id;
    }

    public void setProduct_variant_id(String product_variant_id) {
        this.product_variant_id = product_variant_id;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public ArrayList<CartItems> getItems() {
        return item;
    }

    public void setItems(ArrayList<CartItems> items) {
        this.item = items;
    }
}
