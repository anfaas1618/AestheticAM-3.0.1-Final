package com.mibtech.aesthetic_am.model;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderTracker implements Serializable {

    public String otp, return_status, cancelable_status, till_status, username, id, user_id, order_id, product_variant_id, quantity, price, discount, dPercent, dAmount, sub_total, tax_amt, tax_percent, deliver_by, date_added, name, image, measurement, unit, status, statusdate, mobile, delivery_charge, payment_method, address, final_total, total, walletBalance, promoCode, promoDiscount, activeStatus, activeStatusDate, discounted_price;
    public ArrayList<OrderTracker> orderStatusArrayList;
    public ArrayList<OrderTracker> itemsList;


    public OrderTracker(String otp, String user_id, String order_id, String date_added, String status, String statusdate, ArrayList<OrderTracker> orderStatusArrayList, String mobile, String delivery_charge, String payment_method, String address, String total, String final_total, String tax_amt, String tax_percent, String walletBalance, String promoCode, String promoDiscount, String dPercent, String dAmount, String username, ArrayList<OrderTracker> itemsList, String activeStatus) {
        this.otp = otp;
        this.user_id = user_id;
        this.order_id = order_id;
        this.date_added = date_added;
        this.status = status;
        this.statusdate = statusdate;
        this.orderStatusArrayList = orderStatusArrayList;
        this.mobile = mobile;
        this.delivery_charge = delivery_charge;
        this.payment_method = payment_method;
        this.address = address;
        this.total = total;
        this.final_total = final_total;
        this.tax_amt = tax_amt;
        this.tax_percent = tax_percent;
        this.walletBalance = walletBalance;
        this.promoCode = promoCode;
        this.promoDiscount = promoDiscount;
        this.dAmount = dAmount;
        this.dPercent = dPercent;
        this.username = username;
        this.itemsList = itemsList;
        this.activeStatus = activeStatus;
    }

    public OrderTracker(String id, String order_id, String product_variant_id, String quantity, String price, String discount, String sub_total, String deliver_by, String name, String image, String measurement, String unit, String payment_method, String activeStatus, String activeStatusDate, ArrayList<OrderTracker> orderStatusArrayList, String return_status, String cancelable_status, String till_status, String discounted_price, String tax_percent) {
        this.id = id;
        this.order_id = order_id;
        this.product_variant_id = product_variant_id;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.sub_total = sub_total;
        this.deliver_by = deliver_by;
        this.name = name;
        this.image = image;
        this.measurement = measurement;
        this.unit = unit;
        this.payment_method = payment_method;
        this.activeStatus = activeStatus;
        this.activeStatusDate = activeStatusDate;
        this.orderStatusArrayList = orderStatusArrayList;
        this.return_status = return_status;
        this.cancelable_status = cancelable_status;
        this.till_status = till_status;
        this.discounted_price = discounted_price;
        this.tax_percent = tax_percent;
    }


    public OrderTracker(String status, String statusdate) {
        this.status = status;
        this.statusdate = statusdate;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getReturn_status() {
        return return_status;
    }

    public void setReturn_status(String return_status) {
        this.return_status = return_status;
    }

    public String getCancelable_status() {
        return cancelable_status;
    }

    public void setCancelable_status(String cancelable_status) {
        this.cancelable_status = cancelable_status;
    }

    public String getTill_status() {
        return till_status;
    }

    public void setTill_status(String till_status) {
        this.till_status = till_status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getProduct_variant_id() {
        return product_variant_id;
    }

    public void setProduct_variant_id(String product_variant_id) {
        this.product_variant_id = product_variant_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getdPercent() {
        return dPercent;
    }

    public void setdPercent(String dPercent) {
        this.dPercent = dPercent;
    }

    public String getdAmount() {
        return dAmount;
    }

    public void setdAmount(String dAmount) {
        this.dAmount = dAmount;
    }

    public String getSub_total() {
        return sub_total;
    }

    public void setSub_total(String sub_total) {
        this.sub_total = sub_total;
    }

    public String getTax_amt() {
        return tax_amt;
    }

    public void setTax_amt(String tax_amt) {
        this.tax_amt = tax_amt;
    }

    public String getTax_percent() {
        return tax_percent;
    }

    public void setTax_percent(String tax_percent) {
        this.tax_percent = tax_percent;
    }

    public String getDeliver_by() {
        return deliver_by;
    }

    public void setDeliver_by(String deliver_by) {
        this.deliver_by = deliver_by;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
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

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusdate() {
        return statusdate;
    }

    public void setStatusdate(String statusdate) {
        this.statusdate = statusdate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDelivery_charge() {
        return delivery_charge;
    }

    public void setDelivery_charge(String delivery_charge) {
        this.delivery_charge = delivery_charge;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFinal_total() {
        return final_total;
    }

    public void setFinal_total(String final_total) {
        this.final_total = final_total;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getPromoDiscount() {
        return promoDiscount;
    }

    public void setPromoDiscount(String promoDiscount) {
        this.promoDiscount = promoDiscount;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getActiveStatusDate() {
        return activeStatusDate;
    }

    public void setActiveStatusDate(String activeStatusDate) {
        this.activeStatusDate = activeStatusDate;
    }

    public String getDiscounted_price() {
        return discounted_price;
    }

    public void setDiscounted_price(String discounted_price) {
        this.discounted_price = discounted_price;
    }

    public ArrayList<OrderTracker> getOrderStatusArrayList() {
        return orderStatusArrayList;
    }

    public void setOrderStatusArrayList(ArrayList<OrderTracker> orderStatusArrayList) {
        this.orderStatusArrayList = orderStatusArrayList;
    }

    public ArrayList<OrderTracker> getItemsList() {
        return itemsList;
    }

    public void setItemsList(ArrayList<OrderTracker> itemsList) {
        this.itemsList = itemsList;
    }
}