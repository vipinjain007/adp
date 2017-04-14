package com.workspace.shopping.code;


/**
 * Class use to create shopping item category  object 
 */
public class Category {
	
    private long categoryId;
    private String categoryName;
    private int discPerc;
    
	public Category(long categoryId, String categoryName, int discPerc) {
		
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.discPerc = discPerc;
	}
	public Category() {
		
	}
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public int getDiscPerc() {
		return discPerc;
	}
	public void setDiscPerc(int discPerc) {
		this.discPerc = discPerc;
	}
	@Override
	public String toString() {
		return "Category [categoryId=" + categoryId + ", categoryName=" + categoryName + ", discPerc=" + discPerc + "]";
	}
    
    
    
	
}
