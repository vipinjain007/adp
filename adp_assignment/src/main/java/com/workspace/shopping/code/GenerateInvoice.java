package com.workspace.shopping.code;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This class is having generateInvoice method which is use by Both Generater classes
 * -ShoppingInvoiceGeneratorUsingJson
 * -ShoppingInvoiceGeneratorUsingXml
 */
/**
 * @author vipin
 *
 */
public class GenerateInvoice {
	
// This Method is called by Both ShoppingInvoiceGeneratorUsingJson ,ShoppingInvoiceGeneratorUsingXml process and passed all require list created from xml and json
//This method is using provided list and Write business logic and finally create Invoice details for associated discount and final applicable discount based on flat category	
	public static InvoiceDetails generateInvoice(List<Category> categoryList, List<DiscountSlab> flatDiscountSlabList,
			List<ShoppingItem> shoppingItemsList) {
		InvoiceDetails invoiceDetails = new InvoiceDetails();

		// Create Map for <CategoryId ,CategoryObject> Which will make fast
		// calculation in next step (calculate the applicable
		// discount(associated discounts)
		Map<Long, Category> CategoryMap = new HashMap<Long, Category>();

		double grandTotal = 0;
		double applicalbeDis = 0;
		double netBillAmount = 0;

		for (Category category : categoryList) {

			CategoryMap.put(category.getCategoryId(), category);
		}

		// calculate the applicable discount(associated discounts) – for each
		// item based on category and update itemlist fields originalAmount,
		// discount and netAmount

		for (ShoppingItem shoppingItem : shoppingItemsList) {
			Category category = CategoryMap.get(shoppingItem.getCategoryId());
			// calculate Applicable discount for item

			// Without discount
			double originalAmount = shoppingItem.getUnitPrice() * shoppingItem.getItemQty();
			shoppingItem.setOriginalAmount(originalAmount);
			// Discount for item specific
			double discount = ((originalAmount / 100) * (category.getDiscPerc()));
			shoppingItem.setDiscount(discount);
			// Amount after discount
			double netAmount = originalAmount - discount;
			shoppingItem.setNetAmount(netAmount);
			grandTotal = grandTotal + netAmount;
		}

		// Apply a final slab based discount on the grand total, to generate the
		// net bill value.
		for (DiscountSlab discountSlab : flatDiscountSlabList) {
			if ((discountSlab.getRangMin() <= grandTotal) && (discountSlab.getRangMax() >= grandTotal)) {
				applicalbeDis = ((grandTotal / 100) * discountSlab.getDiscPerc());
			}

		}

		invoiceDetails.setApplicableDiscount(applicalbeDis);
		invoiceDetails.setGrandTotal(grandTotal);
		invoiceDetails.setNetBillAmount((grandTotal - applicalbeDis));
		invoiceDetails.setShopppingItemsDetails(shoppingItemsList);

		return invoiceDetails;
	}
	//This method will Display invoce details when call from java main method
	static void printInvoiceDetails(InvoiceDetails invoiceDetails) {
		List<ShoppingItem> shoppingItemsList =invoiceDetails.getShopppingItemsDetails();
		NumberFormat formatter = new DecimalFormat("#0.00");
		System.out.println("***********************Itemized bill**************************** ");
		System.out.println("ItemId    Category Id      Item Name  quantity    unit price    originalAmount(qty*unitprice)    discount(inr)      netAmount(AfterDiscount)   ");
		for(ShoppingItem shoppingItem:shoppingItemsList){
			System.out.println(shoppingItem.getItemId()+"                 "+shoppingItem.getCategoryId()+"        "+shoppingItem.getItemName() +"     "+shoppingItem.getItemQty()+"         "+formatter.format(shoppingItem.getUnitPrice())+"                      "+formatter.format(shoppingItem.getOriginalAmount())+"                    "+formatter.format(shoppingItem.getDiscount())+"              "+formatter.format(shoppingItem.getNetAmount())+"");	
		}
		System.out.println("Grand Total****************************"+formatter.format(invoiceDetails.getGrandTotal()));
		System.out.println("applicable discount[Flat Discount]*****"+formatter.format(invoiceDetails.getApplicableDiscount()));
		System.out.println("Net Amount**************************** "+formatter.format(invoiceDetails.getNetBillAmount()));
		
	}
}
