package com.workspace.shopping.code;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

/**
 * ShoppingInvoiceGenerator parses  json object for (categories ,discount slabs and shopping
 * items) and generate final Invoice Details (Itemized Bill and final bill)
 */
public class ShoppingInvoiceGeneratorUsingJson {
	
	
	private static String READ_CATOGORY_JSON = "read_category_json";
	private static String READ_DISCOUNT_SLAB_JSON = "read_discount_slab_json";
	private static String READ_SHOPING_CARD_JSON = "read_shoping_card_json";

	// This is core function which is responsible to generate Invoice Details based on all three json files
		//First Step this function create map with json file name(like read_category_json etc) as key and  and File   FileInputStream object as value
		//This Map is Iterate in readJson function and read All three json files and Create 3 Different List like List<Category>,List<DiscountSlab>,List<ShoppingItem>
		//Finally these all 3 list is passing to  generateInvoice function and get InvoiceDetails
	public InvoiceDetails parseShoppingReletedJsonFiles(String categoryFileName, String discountSlabFileName,
			String shoppingItemsFileName)throws FileNotFoundException, JsonGenerationException{
		
		FileInputStream fileInputStream = null;
		URL resource = null;
		File file = null;
		Map<String, FileInputStream> fileMap = new HashMap<String, FileInputStream>();
		try {
			resource = getClass().getClassLoader().getResource("Json/" + categoryFileName);
			if (resource == null) {
				throw new FileNotFoundException("ERROR: file not found:" + categoryFileName);
			}

			file = new File(resource.getFile());
			fileInputStream = new FileInputStream(file);

			fileMap.put("read_category_json", fileInputStream);
			resource = null;
			file = null;
			fileInputStream = null;
			resource = getClass().getClassLoader().getResource("Json/" + discountSlabFileName);
			if (resource == null) {
				throw new FileNotFoundException("ERROR: file not found:" + discountSlabFileName);
			}
			

			file = new File(resource.getFile());
			fileInputStream = new FileInputStream(file);

			fileMap.put("read_discount_slab_Json", fileInputStream);
			
			
			resource = null;
			file = null;
			fileInputStream = null;
			resource = getClass().getClassLoader().getResource("Json/" + shoppingItemsFileName);
			if (resource == null) {
				throw new FileNotFoundException("ERROR: file not found:" + shoppingItemsFileName);
			}
			

			file = new File(resource.getFile());
			fileInputStream = new FileInputStream(file);

			fileMap.put("read_shoping_card_Json", fileInputStream);
		} catch (FileNotFoundException e) {
			  e.printStackTrace();
			throw new FileNotFoundException("ERROR: file not found");

		}
		return readJson(fileMap);

		
	}
	
	/**
	 * Given an InputStream, reads and parses json file .This
	 * method is read all json files  based on type {READ_CATOGORY_JSON,
	 * READ_DISCOUNT_SLAB_JSON, READ_SHOPING_CARD_JSON and Create
	 * List<Category>,List<DiscountSlab> and List<ShopingItems> .These all generated list's are used for invoice generation
	 * 
	 * 
	 * @param in
	 * @return InvoiceDetails
	 * @throws JsonGenerationException
	 */
	public InvoiceDetails readJson(Map<String, FileInputStream> fileMap) throws JsonGenerationException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		String type = null;

		List<Category> categoryList = null;
		List<DiscountSlab> flatDiscountSlabList = null;
		List<ShoppingItem> shoppingItemsList = null;
		JsonReader jsonReader=null;
		JsonObject jsonObject=null;
		for (Map.Entry<String, FileInputStream> entry : fileMap.entrySet()) {
			//System.out.println(entry.getKey() + "/" + entry.getValue());
		     
			type = entry.getKey();
			 jsonReader=null;
			 jsonObject=null;
			//create JsonReader object
			 jsonReader = Json.createReader(entry.getValue());
			//get JsonObject from JsonReader
		      jsonObject = jsonReader.readObject();
			if (type.equalsIgnoreCase(READ_CATOGORY_JSON)) {
				// Read category json and create List<Category> which
				// will use in generate final Invoice details
					categoryList=readJaonCategory(jsonObject);	
			}else if (type.equalsIgnoreCase(READ_DISCOUNT_SLAB_JSON)) {
				// Read flatDiscountSlab json and create
				// List<DiscountSlab> which will use in generate
				// final Invoice details
				flatDiscountSlabList=readJaonDiscountSlab(jsonObject);	
			}if (type.equalsIgnoreCase(READ_SHOPING_CARD_JSON)) {
				// Read shoppingCard json and create
				// List<ShoppingItem> which will use in generate
				// final Invoice details
				shoppingItemsList=readJaonShoppintItems(jsonObject);
			} 
			
		}	
		return  GenerateInvoice.generateInvoice(categoryList, flatDiscountSlabList, shoppingItemsList);
	}
	
	/**
	 * This function is called to Create categoryList reading step by step Category json object
	 * Categories object and make list for all categories.
	 * 
	 * 
	 * @param JsonObject
	 *          
	 * @return List<Category>
	 * 
	 */
	private List<Category> readJaonCategory(JsonObject jsonObject) {
		List<Category> categoryList=new ArrayList<Category>();
		//reading arrays from json
				JsonArray jsonArray = jsonObject.getJsonArray("category");
				
				for(int i=0;i<jsonArray.size();i++){
					Category category=new Category();
					category.setCategoryId(Long.parseLong(jsonArray.getJsonObject(i).getString("id")));
					category.setCategoryName(jsonArray.getJsonObject(i).getString("name"));
					category.setDiscPerc(Integer.parseInt(jsonArray.getJsonObject(i).getString("discPerc")));
					
					categoryList.add(category);
				}
				
			/*for (Category c : categoryList) {
				System.out.println(c.getCategoryId() + "   " + c.getCategoryName());
			}*/
		return categoryList;
	  
	}
	
	/**
	 * This function is called to Create flatDiscountSlabList reading step by step DiscountSlab json object
	 * Categories object and make list for all categories.
	 * 
	 * 
	 * @param JsonObject
	 *          
	 * @return List<DiscountSlab>
	 * 
	 */
	private List<DiscountSlab> readJaonDiscountSlab(JsonObject jsonObject) {
		List<DiscountSlab> flatDiscountSlabList = new ArrayList<DiscountSlab>();
		//reading arrays from json
				JsonArray jsonArray = jsonObject.getJsonArray("Slab");
				
				for(int i=0;i<jsonArray.size();i++){
					DiscountSlab discountSlab=new DiscountSlab();
					discountSlab.setRangMin(Double.parseDouble(jsonArray.getJsonObject(i).getString("RangeMin")));
					if((jsonArray.getJsonObject(i).getString("RangeMax")!=null)&&(!jsonArray.getJsonObject(i).getString("RangeMax").isEmpty()))
					  discountSlab.setRangMax(Double.parseDouble(jsonArray.getJsonObject(i).getString("RangeMax")));
					else{
						discountSlab.setRangMax((Double.parseDouble(jsonArray.getJsonObject(i).getString("RangeMin"))+5000));	
					}
					discountSlab.setDiscPerc(Integer.parseInt(jsonArray.getJsonObject(i).getString("discPerc")));
					flatDiscountSlabList.add(discountSlab);
				}
				
			
				/*for (DiscountSlab c : flatDiscountSlabList) {
					System.out.println(c.getRangMin() + "   " + c.getRangMax() + " " + c.getDiscPerc());
				}*/	
				
		return flatDiscountSlabList;
	  
	}

	/**
	 * This function is called to Create shoppingItemList reading step by step ShoppingItem json object
	 * Categories object and make list for all categories.
	 * 
	 * 
	 * @param JsonObject
	 *          
	 * @return List<ShoppingItem>
	 * 
	 */
	private List<ShoppingItem> readJaonShoppintItems(JsonObject jsonObject) {
		ShoppingItem shoppingItem = null;

		List<ShoppingItem> shoppingItemList = new ArrayList<ShoppingItem>();
		//reading arrays from json
				JsonArray jsonArray = jsonObject.getJsonArray("ShoppingCart");
				
				for(int i=0;i<jsonArray.size();i++){
					shoppingItem=new ShoppingItem();
					shoppingItem.setItemId(Long.parseLong(jsonArray.getJsonObject(i).getString("itemID")));
					shoppingItem.setCategoryId(Long.parseLong(jsonArray.getJsonObject(i).getString("itemCategoryID")));
					shoppingItem.setItemName(jsonArray.getJsonObject(i).getString("itemName"));
					shoppingItem.setItemQty(Integer.parseInt(jsonArray.getJsonObject(i).getString("unitPrice")));
					shoppingItem.setUnitPrice(Double.parseDouble(jsonArray.getJsonObject(i).getString("quantity")));
					shoppingItemList.add(shoppingItem);
				}
				
			
			/*	for (ShoppingItem s : shoppingItemList) {
					System.out.println(s.getItemId() + " " + s.getCategoryId() + " " + s.getItemName() + " " + s.getUnitPrice()
							+ " " + s.getItemQty());
				}	*/	
				
		return shoppingItemList;
	  
	}
	
	// ****************      Main class
		// **********************************************
		

		public static void main(String[] args) {
			String categoryFileName = "categoriesJson.txt";
			String discountSlabFileName = "flatDiscountSlabsJson.txt";
			String shoppingItemsFileName = "shoppingCartJson.txt";
			InvoiceDetails invoiceDetails=new InvoiceDetails();
			if (args.length > 0) {
				categoryFileName = args[0];
				discountSlabFileName = args[1];
				shoppingItemsFileName = args[2];
			}
			System.out.println("Processing  json files  : " + categoryFileName + "  " + discountSlabFileName + "  "
					+ shoppingItemsFileName);

			ShoppingInvoiceGeneratorUsingJson shoppingInvoiceGenerator = new ShoppingInvoiceGeneratorUsingJson();
			// VehicleReport report = null;
			try {
				invoiceDetails=shoppingInvoiceGenerator.parseShoppingReletedJsonFiles(categoryFileName, discountSlabFileName,
						shoppingItemsFileName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("Finished processing json");
			GenerateInvoice.printInvoiceDetails(invoiceDetails);
		}
}
