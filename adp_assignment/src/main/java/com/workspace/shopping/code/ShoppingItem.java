package com.workspace.shopping.code;

/**
 * Class use to create Shopping item object 
 */
public class ShoppingItem {
      
	private long itemId;
	private long categoryId;
	
	private String itemName;
    private double unitPrice;
    private int ItemQty;
    
    
    //These below  parameter will use when we crate invoice details
    //Without discount
    private double originalAmount;
    
    //Discount for item specific
    private double discount;
    
    //Amount after discount
    private double netAmount;
    
    
	public ShoppingItem(long itemId, long categoryId, String itemName, double unitPrice, int itemQty) {
		super();
		this.itemId = itemId;
		this.categoryId = categoryId;
		this.itemName = itemName;
		this.unitPrice = unitPrice;
		ItemQty = itemQty;
	}


	public ShoppingItem() {
		
	}


	public long getItemId() {
		return itemId;
	}


	public void setItemId(long itemId) {
		this.itemId = itemId;
	}


	public long getCategoryId() {
		return categoryId;
	}


	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}


	public String getItemName() {
		return itemName;
	}


	public void setItemName(String itemName) {
		this.itemName = itemName;
	}


	public double getUnitPrice() {
		return unitPrice;
	}


	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}


	public int getItemQty() {
		return ItemQty;
	}


	public void setItemQty(int itemQty) {
		ItemQty = itemQty;
	}


	public double getOriginalAmount() {
		return originalAmount;
	}


	public void setOriginalAmount(double originalAmount) {
		this.originalAmount = originalAmount;
	}


	public double getDiscount() {
		return discount;
	}


	public void setDiscount(double discount) {
		this.discount = discount;
	}


	public double getNetAmount() {
		return netAmount;
	}


	public void setNetAmount(double netAmount) {
		this.netAmount = netAmount;
	}


	@Override
	public String toString() {
		return "ShoppingItem [itemId=" + itemId + ", categoryId=" + categoryId + ", itemName=" + itemName
				+ ", unitPrice=" + unitPrice + ", ItemQty=" + ItemQty + ", originalAmount=" + originalAmount
				+ ", discount=" + discount + ", netAmount=" + netAmount + "]";
	}


	
    
    
}
