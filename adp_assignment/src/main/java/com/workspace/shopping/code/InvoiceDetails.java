package com.workspace.shopping.code;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the invoice  of processing the  provided input xmls 
 */
public class InvoiceDetails {
     
	List<ShoppingItem> shopppingItemsDetails=new ArrayList<ShoppingItem>();
	private double grandTotal;
	private double applicableDiscount;
	private double netBillAmount;
	
	
	public List<ShoppingItem> getShopppingItemsDetails() {
		return shopppingItemsDetails;
	}
	public void setShopppingItemsDetails(List<ShoppingItem> shopppingItemsDetails) {
		this.shopppingItemsDetails = shopppingItemsDetails;
	}
	public double getGrandTotal() {
		return grandTotal;
	}
	public double getApplicableDiscount() {
		return applicableDiscount;
	}
	public void setApplicableDiscount(double applicableDiscount) {
		this.applicableDiscount = applicableDiscount;
	}
	public void setGrandTotal(double grandTotal) {
		this.grandTotal = grandTotal;
	}
	
	
	
	public double getNetBillAmount() {
		return netBillAmount;
	}
	public void setNetBillAmount(double netBillAmount) {
		this.netBillAmount = netBillAmount;
	}
	@Override
	public String toString() {
		return "InvoiceDetails [shopppingItemsDetails=" + shopppingItemsDetails + ", grandTotal=" + grandTotal
				+ ", applicableDocunt=" + applicableDiscount + ", netBillAmount=" + netBillAmount + "]";
	}
	
	
	
}
