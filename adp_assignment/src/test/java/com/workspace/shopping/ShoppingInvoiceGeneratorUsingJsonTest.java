package com.workspace.shopping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Test;

import com.workspace.shopping.code.InvoiceDetails;
import com.workspace.shopping.code.ShoppingInvoiceGeneratorUsingJson;
import com.workspace.shopping.code.ShoppingItem;


public class ShoppingInvoiceGeneratorUsingJsonTest {
private ShoppingInvoiceGeneratorUsingJson app;
	
	@Before
	public void setup() {
		this.app = new ShoppingInvoiceGeneratorUsingJson();
	}
	
	@Test
	public void readAllThreeXmlFilesandGenerateInvoice() {
		String categoryFileName = "categoriesJson.txt";
		String discountSlabFileName = "flatDiscountSlabsJson.txt";
		String shoppingItemsFileName = "shoppingCartJson.txt";
		InvoiceDetails invoiceDetails=new InvoiceDetails();
		NumberFormat formatter = new DecimalFormat("#0.00");
		try {
			invoiceDetails=app.parseShoppingReletedJsonFiles(categoryFileName, discountSlabFileName,
					shoppingItemsFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		List<ShoppingItem> shopppingItemsDetails=new ArrayList<ShoppingItem>();
		
		for(ShoppingItem item:shopppingItemsDetails){
			  
			//UseCase 1 :Item base Discount and calculate netAmount(AfterDiscount)
			//ItemId 1 ,Category Id :3      :Discount % :2  [From categories.xml for CategoryId:3] (<name>Grocery</name><discPerc>2</discPerc>) Discount % :2  
			//Item Name : Muesli   Quty  :2         Unit price :100.00     originalAmount(qty*unitprice):200
			//Discount Amount[2% of 200] :(200/100) * 2 = 4
			//netAmount(AfterDiscount) =200-Discount Amount= (200-4) =196
			  if(item.getItemId()==1){
				  assertEquals(196,item.getNetAmount());
			  }
			//UseCase 2 : Calculate discount amount for item
				//ItemId 2 ,Category Id :5      :Discount % :15  [From categories.xml for CategoryId:5] 
			    //(<id>005</id><name>Apparrel</name><discPerc>15</discPerc> 
				//Item Name :Mens Tshirt Arrow 3463     Quty  :1        Unit price :1536.00    originalAmount(qty*unitprice):1536.00
				//Discount Amount[15% of 1536.00] :(1536.00/100) * 15 = 230.40
			  if(item.getItemId()==2){
				  assertEquals(230.40,formatter.format(item.getDiscount()));
			  }	
			
		}
		
		//Use Case Flat Discount :Grand Total :1634.20   Flat Discount : 2%    Form FlatDiscountSlabs.xml (Range 0-3000)
		//applicable discount[Flat Discount] :32.68 Net Amount:(1634.20-32.68): 1601.52
		String netAmount=formatter.format(invoiceDetails.getNetBillAmount());
		double expected=1601.52;
		double result=Double.parseDouble(netAmount);
		System.out.println("expected: "+expected  +"  "+result);
		
		 assertTrue((expected -  result) == 0);
		
		

	}	
}
