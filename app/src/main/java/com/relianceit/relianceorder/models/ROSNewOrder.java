package com.relianceit.relianceorder.models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by sura on 4/28/15.
 */
public class ROSNewOrder {

    @Expose private String SalesOrdNum = null;
    private int orderStatus = 0;
    @Expose private String CustCode = null;
    @Expose private double GrossValue = 0.0;
    @Expose private double OVDiscount = 0.0;
    @Expose private double DiscountValue = 0.0;
    @Expose private double OrderValue = 0.0;
    @Expose private String AddedDate = null;
    @Expose private ArrayList<ROSNewOrderItem> Products = null;

    /*
    OrderNo  = SalesOrdNum
            OrderDate = OrderValue(dd/mm/yyyy)
	"CustCode" : "00001",
	"GrossValue" : 100,
	"OVDiscount" : ,
	"DiscountValue" : ,
	"OrderValue" : 1000.20,
	"Products" : [{
			"ProductCode" : "001",
                                   ProductName = ProductDescription
			"ProductBatchCode" : "0001",
                                   UnitPrice = UnitPrice
			"QtyOrdered" : 1,
			"ProdDiscount" : 0,
			"QtyBonus" : 0,
			"EffPrice" : 100.00
		}
	]
     */

    public ROSNewOrder() {
        this.SalesOrdNum = null;
        this.orderStatus = 0;
        this.CustCode = null;
        this.GrossValue = 0.0;
        this.OVDiscount = 0.0;
        this.DiscountValue = 0.0;
        this.OrderValue = 0.0;
        this.AddedDate = null;
        this.Products = null;
    }

    public void fillDbFields() {
        this.DiscountValue = GrossValue*OVDiscount/100;
        this.OrderValue = GrossValue - DiscountValue;
    }

    public String getSalesOrdNum() {
        return SalesOrdNum;
    }

    public void setSalesOrdNum(String salesOrdNum) {
        this.SalesOrdNum = salesOrdNum;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCustCode() {
        return CustCode;
    }

    public void setCustCode(String custCode) {
        this.CustCode = custCode;
    }

    public double getGrossValue() {
        return GrossValue;
    }

    public void setGrossValue(double grossValue) {
        this.GrossValue = grossValue;
        this.DiscountValue = grossValue*OVDiscount/100;
        this.OrderValue = grossValue - DiscountValue;
    }

    public double getOVDiscount() {
        return OVDiscount;
    }

    public void setOVDiscount(double OVDiscount) {
        this.OVDiscount = OVDiscount;
        this.DiscountValue = GrossValue*OVDiscount/100;
        this.OrderValue = GrossValue - DiscountValue;
    }

    public double getDiscountValue() {
        return DiscountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.DiscountValue = discountValue;
    }

    public double getOrderValue() {
        return OrderValue;
    }

    public void setOrderValue(double orderValue) {
        this.OrderValue = orderValue;
    }

    public String getAddedDate() {
        return AddedDate;
    }

    public void setAddedDate(String addedDate) {
        this.AddedDate = addedDate;
    }

    public ArrayList<ROSNewOrderItem> getProducts() {
        return Products;
    }

    public void setProducts(ArrayList<ROSNewOrderItem> products) {
        this.Products = products;
    }
}
