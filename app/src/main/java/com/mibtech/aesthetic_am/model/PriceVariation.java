package com.mibtech.aesthetic_am.model;

import java.io.Serializable;

public class PriceVariation implements Serializable {
    final String id;
    final String product_id;
    final String type;
    final String measurement;
    final String measurement_unit_id;
    final String price;
    final String discounted_price;
    final String serve_for;
    final String stock;
    final String stock_unit_id;
    final String measurement_unit_name;
    final String stock_unit_name;
    final String discountpercent;
    String cart_count;
    int qty;
    double totalprice;

    public PriceVariation(String cart_count, String id, String product_id, String type, String measurement, String measurement_unit_id, String price, String discounted_price, String serve_for, String stock, String stock_unit_id, String measurement_unit_name, String stock_unit_name, String discountpercent) {
        this.cart_count = cart_count;
        this.id = id;
        this.product_id = product_id;
        this.type = type;
        this.measurement = measurement;
        this.measurement_unit_id = measurement_unit_id;
        this.price = price;
        this.discounted_price = discounted_price;
        this.serve_for = serve_for;
        this.stock = stock;
        this.stock_unit_id = stock_unit_id;
        this.measurement_unit_name = measurement_unit_name;
        this.stock_unit_name = stock_unit_name;
        this.discountpercent = discountpercent.replace("-", "").replace(".00", "");
    }

    public String getCart_count() {
        return cart_count;
    }

    public void setCart_count(String cart_count) {
        this.cart_count = cart_count;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getDiscountpercent() {
        return discountpercent;
    }

    public String getId() {
        return id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getType() {
        return type;
    }

    public String getMeasurement() {
        return measurement;
    }

    public String getMeasurement_unit_id() {
        return measurement_unit_id;
    }

    public String getPrice() {
        return price;
    }

    public String getDiscounted_price() {
        return discounted_price;
    }

    public String getServe_for() {
        return serve_for;
    }

    public String getStock() {
        return stock;
    }

    public String getStock_unit_id() {
        return stock_unit_id;
    }

    public String getMeasurement_unit_name() {
        return measurement_unit_name;
    }

    public String getStock_unit_name() {
        return stock_unit_name;
    }
}
