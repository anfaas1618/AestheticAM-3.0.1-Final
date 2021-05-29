package com.mibtech.aesthetic_am.model;

import java.io.Serializable;

public class Address implements Serializable {

    String id, user_id, type, name, country_code, mobile, alternate_mobile, address, landmark, area_id, city_id, pincode, state, country, date_created, city_name, area_name, is_default, latitude, longitude, minimum_free_delivery_order_amount, delivery_charges;
    boolean isSelected;

    public Address(boolean isSelected, String id, String user_id, String type, String name, String country_code, String mobile, String alternate_mobile, String address, String landmark,
                   String area_id, String city_id, String pincode, String state, String country, String date_created, String city_name, String area_name, String is_default,
                   String latitude, String longitude, String minimum_free_delivery_order_amount, String delivery_charges) {
        this.isSelected = isSelected;
        this.id = id;
        this.user_id = user_id;
        this.type = type;
        this.name = name;
        this.country_code = country_code;
        this.mobile = mobile;
        this.alternate_mobile = alternate_mobile;
        this.address = address;
        this.landmark = landmark;
        this.area_id = area_id;
        this.city_id = city_id;
        this.pincode = pincode;
        this.state = state;
        this.country = country;
        this.date_created = date_created;
        this.city_name = city_name;
        this.area_name = area_name;
        this.is_default = is_default;
        this.latitude = latitude;
        this.longitude = longitude;
        this.minimum_free_delivery_order_amount = minimum_free_delivery_order_amount;
        this.delivery_charges = delivery_charges;
    }

    public String getMinimum_free_delivery_order_amount() {
        return minimum_free_delivery_order_amount;
    }

    public void setMinimum_free_delivery_order_amount(String minimum_free_delivery_order_amount) {
        this.minimum_free_delivery_order_amount = minimum_free_delivery_order_amount;
    }

    public String getDelivery_charges() {
        return delivery_charges;
    }

    public void setDelivery_charges(String delivery_charges) {
        this.delivery_charges = delivery_charges;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean selected) {
        isSelected = selected;
    }

    public String getIs_default() {
        return is_default;
    }

    public void setIs_default(String is_default) {
        this.is_default = is_default;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAlternate_mobile() {
        return alternate_mobile;
    }

    public void setAlternate_mobile(String alternate_mobile) {
        this.alternate_mobile = alternate_mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }
}
