package com.workspace.shopping.code;

/*
 * Author :Vipin Jain
 * Since 1.1
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * ShoppingInvoiceGenerator parses an xml files (categories ,discount slabs and
 * shopping items or json object for (categories ,discount slabs and shopping
 * items) and generate final Invoice Details (Itemized Bill and final bill)
 */
public class ShoppingInvoiceGeneratorUsingXml {

	// tags for Categories xml

	private static final String TAG_CATEGORIES = "Categories";
	private static final String TAG_CATEGORY = "Category";
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";

	// tags for discount slabs
	private static final String TAG_FLAT_DISCOUNT_SLABS = "FlatDiscountSlabs";
	private static final String TAG_SLAB = "Slab";
	private static final String TAG_RANGE_MIN = "RangeMin";
	private static final String TAG_RANGE_MAX = "RangeMax";

	// tags for Shopping Cart Items

	private static final String TAG_SHOPPING_CARD = "ShoppingCart";
	private static final String TAG_ITEM_ID = "itemID";
	private static final String TAG_ITEM_CATEGORY_ID = "itemCategoryID";
	private static final String TAG_ITEM_NAME = "itemName";
	private static final String TAG_UNIT_PRICE = "unitPrice";
	private static final String TAG_QUANTITY = "quantity";

	// common tag
	private static final String TAG_DISCPERC = "discPerc";

	private static String READ_CATOGORY_XML = "read_category_xml";
	private static String READ_DISCOUNT_SLAB_XML = "read_discount_slab_xml";
	private static String READ_SHOPING_CARD_XML = "read_shoping_card_xml";

	// This is core function which is responsible to generate Invoice Details based on all three xmls files
	//First Step this function create map with xml file name(like read_category_xml etc) as key and  and File   FileInputStream object as value
	//This Map is Iterate in parseXml function and read All three xmls files and Create 3 Different List like List<Category>,List<DiscountSlab>,List<ShoppingItem>
	//Finally these all 3 list is passing to  generateInvoice function and get InvoiceDetails
	
	public InvoiceDetails parseShoppingReletedXmlFiles(String categoryFileName, String discountSlabFileName,
			String shoppingItemsFileName) throws FileNotFoundException, XMLStreamException {
		FileInputStream fileInputStream = null;
		URL resource = null;
		File file = null;
		Map<String, FileInputStream> fileMap = new HashMap<String, FileInputStream>();
		try {
			resource = getClass().getClassLoader().getResource("xmlToParse/" + categoryFileName);
			if (resource == null) {
				throw new FileNotFoundException("ERROR: file not found:" + categoryFileName);
			}

			file = new File(resource.getFile());
			fileInputStream = new FileInputStream(file);

			fileMap.put("read_category_xml", fileInputStream);
			resource = null;
			file = null;
			fileInputStream = null;
			resource = getClass().getClassLoader().getResource("xmlToParse/" + discountSlabFileName);
			if (resource == null) {
				throw new FileNotFoundException("ERROR: file not found:" + discountSlabFileName);
			}

			file = new File(resource.getFile());
			fileInputStream = new FileInputStream(file);

			fileMap.put("read_discount_slab_xml", fileInputStream);
			resource = null;
			file = null;
			fileInputStream = null;
			resource = getClass().getClassLoader().getResource("xmlToParse/" + shoppingItemsFileName);
			if (resource == null) {
				throw new FileNotFoundException("ERROR: file not found:" + shoppingItemsFileName);
			}

			file = new File(resource.getFile());
			fileInputStream = new FileInputStream(file);

			fileMap.put("read_shoping_card_xml", fileInputStream);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("ERROR: file not found");

		}
		return parseXml(fileMap);

	}

	/**
	 * Given an InputStream, reads and parses xml file .This
	 * method is read all xmls file based on type {READ_CATOGORY_XML,
	 * READ_DISCOUNT_SLAB_XML, READ_SHOPING_CARD_XML and Create
	 * List<Category>,List<DiscountSlab> and List<ShopingItems> .These all generated list's are used for invoice generation
	 * 
	 * 
	 * @param in
	 * @return InvoiceDetails
	 * @throws XMLStreamException
	 */
	public InvoiceDetails parseXml(Map<String, FileInputStream> fileMap) throws XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		String type = null;

		List<Category> categoryList = null;
		List<DiscountSlab> flatDiscountSlabList = null;
		List<ShoppingItem> shoppingItemsList = null;

		for (Map.Entry<String, FileInputStream> entry : fileMap.entrySet()) {
			//System.out.println(entry.getKey() + "/" + entry.getValue());
			XMLEventReader reader = factory.createXMLEventReader(entry.getValue());
			type = entry.getKey();
			while (reader.hasNext()) {
				XMLEvent event = reader.nextEvent();

				if (event.getEventType() == XMLEvent.START_ELEMENT) {
					StartElement element = event.asStartElement();
					if (type.equalsIgnoreCase(READ_CATOGORY_XML)) {
						if (TAG_CATEGORIES.equalsIgnoreCase(element.getName().getLocalPart())) {
							// Read category xml and create List<Category> which
							// will use in generate final Invoice details
							categoryList = parseCategory(element, reader);
						}
					} else if (type.equalsIgnoreCase(READ_DISCOUNT_SLAB_XML)) {
						if (TAG_FLAT_DISCOUNT_SLABS.equalsIgnoreCase(element.getName().getLocalPart())) {
							// Read flatDiscountSlab xml and create
							// List<DiscountSlab> which will use in generate
							// final Invoice details
							flatDiscountSlabList = parseDiscountSlab(element, reader);
						}
					} else if (type.equalsIgnoreCase(READ_SHOPING_CARD_XML)) {
						if (TAG_SHOPPING_CARD.equalsIgnoreCase(element.getName().getLocalPart())) {
							// Read shoppingCard xml and create
							// List<ShoppingItem> which will use in generate
							// final Invoice details
							shoppingItemsList = parseShoppingItems(element, reader);
						}
					}
				}
			}
		}

		// Using all three list Generate invoice Details

		return GenerateInvoice.generateInvoice(categoryList, flatDiscountSlabList, shoppingItemsList);

	}

	/**
	 * This function is called when the Category start-element is encountered.
	 * This function reads subsequent elements for category and create category
	 * object . This function read all Start and end Category tag and create all
	 * Categories object and make list for all categories.
	 * 
	 * 
	 * @param startElement
	 *            Category start-element passed to allow function to read
	 *            attributes
	 * @param reader
	 *            xml reader passed to read all xml elements until "Categories"
	 *            end-element is encountered
	 * @return List<Category>
	 * @throws XMLStreamException
	 */

	private List<Category> parseCategory(StartElement startElement, XMLEventReader reader) throws XMLStreamException {

		boolean isIdTag = false;
		boolean isNameTag = false;
		boolean isDiscountTag = false;
		Category category = null;
		List<Category> categoryList = new ArrayList<Category>();
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();

			if (event.getEventType() == XMLEvent.START_ELEMENT) {
				StartElement element = event.asStartElement();
				if (TAG_CATEGORY.equalsIgnoreCase(element.getName().getLocalPart())) {
					// Create new Category object whenever start tag is Category
					category = new Category();

				}
				if (TAG_ID.equalsIgnoreCase(element.getName().getLocalPart())) {
					isIdTag = true;

				} else if (TAG_NAME.equalsIgnoreCase(element.getName().getLocalPart())) {
					isNameTag = true;
				} else if (TAG_DISCPERC.equalsIgnoreCase(element.getName().getLocalPart())) {
					isDiscountTag = true;
				}
			} else if (event.getEventType() == XMLEvent.END_ELEMENT) {
				EndElement element = event.asEndElement();
				if (TAG_CATEGORY.equalsIgnoreCase(element.getName().getLocalPart())) {
					// Add Category objct into the list when Category end tag is
					// encountered
					categoryList.add(category);

				} else if (TAG_ID.equalsIgnoreCase(element.getName().getLocalPart())) {
					isIdTag = false;
				} else if (TAG_NAME.equalsIgnoreCase(element.getName().getLocalPart())) {
					isNameTag = false;
				} else if (TAG_DISCPERC.equalsIgnoreCase(element.getName().getLocalPart())) {
					isDiscountTag = false;
				}
				if (TAG_CATEGORIES.equalsIgnoreCase(element.getName().getLocalPart())) {
					// break the loop when "Categories " tag is encountered
					// ,This moment all category.xml reading is completed and
					// List<Category is
					// ready
					break;
				}

			} else if (isIdTag && event.getEventType() == XMLEvent.CHARACTERS) {

				category.setCategoryId(Long.parseLong(event.asCharacters().getData()));
			} else if (isNameTag && event.getEventType() == XMLEvent.CHARACTERS) {

				category.setCategoryName(event.asCharacters().getData());
			} else if (isDiscountTag && event.getEventType() == XMLEvent.CHARACTERS) {

				category.setDiscPerc(Integer.parseInt(event.asCharacters().getData()));
			}

		}

		/*for (Category c : categoryList) {
			System.out.println(c.getCategoryId() + "   " + c.getCategoryName());
		}*/

		return categoryList;
	}

	/**
	 * This function is called when the Slab start-element is encountered. This
	 * function reads subsequent elements for slab and create DiscountSlab
	 * object . This function read all Start and end Slab tag and create all
	 * DiscountSlab object and make list for all DiscountSlabes.
	 * 
	 * 
	 * @param startElement
	 *            Category start-element passed to allow function to read
	 *            attributes
	 * @param reader
	 *            xml reader passed to read all xml elements until
	 *            "FlatDiscountSlabs" end-element is encountered
	 * @return List<DiscountSlab>
	 * @throws XMLStreamException
	 */

	private List<DiscountSlab> parseDiscountSlab(StartElement startElement, XMLEventReader reader)
			throws XMLStreamException {

		boolean isMinTag = false;
		boolean isMaxTag = false;
		boolean isDiscountTag = false;
		DiscountSlab discountSlab = null;
		List<DiscountSlab> flatDiscountSlabList = new ArrayList<DiscountSlab>();
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();

			if (event.getEventType() == XMLEvent.START_ELEMENT) {
				StartElement element = event.asStartElement();
				if (TAG_SLAB.equalsIgnoreCase(element.getName().getLocalPart())) {
					// Create new discountSlab object whenever start tag is slab
					discountSlab = new DiscountSlab();

				}
				if (TAG_RANGE_MIN.equalsIgnoreCase(element.getName().getLocalPart())) {
					isMinTag = true;

				} else if (TAG_RANGE_MAX.equalsIgnoreCase(element.getName().getLocalPart())) {
					isMaxTag = true;
				} else if (TAG_DISCPERC.equalsIgnoreCase(element.getName().getLocalPart())) {
					isDiscountTag = true;
				}
			} else if (event.getEventType() == XMLEvent.END_ELEMENT) {
				EndElement element = event.asEndElement();
				if (TAG_SLAB.equalsIgnoreCase(element.getName().getLocalPart())) {
					// Add slab objct into the list when slab end tag is
					// encountered
					flatDiscountSlabList.add(discountSlab);

				} else if (TAG_RANGE_MIN.equalsIgnoreCase(element.getName().getLocalPart())) {
					isMinTag = false;
				} else if (TAG_RANGE_MAX.equalsIgnoreCase(element.getName().getLocalPart())) {
					isMaxTag = false;
				} else if (TAG_DISCPERC.equalsIgnoreCase(element.getName().getLocalPart())) {
					isDiscountTag = false;
				}
				if (TAG_FLAT_DISCOUNT_SLABS.equalsIgnoreCase(element.getName().getLocalPart())) {
					// break the loop when "FlatDiscountSlabs " tag is
					// encountered
					// ,This moment all flatDiscountSlabs.xml reading is
					// completed and List<DiscountSlab> is
					// ready
					break;
				}

			} else if (isMinTag && event.getEventType() == XMLEvent.CHARACTERS) {

				discountSlab.setRangMin(Double.parseDouble(event.asCharacters().getData()));
			} else if (isMaxTag && event.getEventType() == XMLEvent.CHARACTERS) {

				discountSlab.setRangMax(Double.parseDouble(event.asCharacters().getData()));
			} else if (isDiscountTag && event.getEventType() == XMLEvent.CHARACTERS) {

				discountSlab.setDiscPerc(Integer.parseInt(event.asCharacters().getData()));
			}

		}

		/*for (DiscountSlab c : flatDiscountSlabList) {
			System.out.println(c.getRangMin() + "   " + c.getRangMax() + " " + c.getDiscPerc());
		}*/

		return flatDiscountSlabList;
	}

	/**
	 * This function is called when the item start-element is encountered. This
	 * function reads subsequent elements for item and create ShoppingItem
	 * object . This function read all Start and end quantity tag and create all
	 * ShoppingItem object and make list for all ShoppingItem.
	 * 
	 * 
	 * @param startElement
	 *            Category start-element passed to allow function to read
	 *            attributes
	 * @param reader
	 *            xml reader passed to read all xml elements until
	 *            "ShoppingCart" end tag end-element is encountered
	 * @return List<ShoppingItem>
	 * @throws XMLStreamException
	 */

	private List<ShoppingItem> parseShoppingItems(StartElement startElement, XMLEventReader reader)
			throws XMLStreamException {

		boolean isItemIdTag = false;
		boolean isItemCategoryTag = false;
		boolean isItemNameTag = false;
		boolean isUnitPriceTag = false;
		boolean isQuantityTag = false;
		ShoppingItem shoppingItem = null;

		List<ShoppingItem> shoppingItemList = new ArrayList<ShoppingItem>();
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();

			if (event.getEventType() == XMLEvent.START_ELEMENT) {
				StartElement element = event.asStartElement();
				if (TAG_ITEM_ID.equalsIgnoreCase(element.getName().getLocalPart())) {
					// Create new ShoppingItem object whenever start tag is
					// itemId
					isItemIdTag = true;
					shoppingItem = new ShoppingItem();

				}
				if (TAG_ITEM_CATEGORY_ID.equalsIgnoreCase(element.getName().getLocalPart())) {
					isItemCategoryTag = true;

				} else if (TAG_ITEM_NAME.equalsIgnoreCase(element.getName().getLocalPart())) {
					isItemNameTag = true;
				} else if (TAG_UNIT_PRICE.equalsIgnoreCase(element.getName().getLocalPart())) {
					isUnitPriceTag = true;
				} else if (TAG_QUANTITY.equalsIgnoreCase(element.getName().getLocalPart())) {
					isQuantityTag = true;
				}
			} else if (event.getEventType() == XMLEvent.END_ELEMENT) {
				EndElement element = event.asEndElement();
				if (TAG_ITEM_ID.equalsIgnoreCase(element.getName().getLocalPart())) {
					isItemIdTag = false;

				} else if (TAG_ITEM_CATEGORY_ID.equalsIgnoreCase(element.getName().getLocalPart())) {
					isItemCategoryTag = false;
				} else if (TAG_ITEM_NAME.equalsIgnoreCase(element.getName().getLocalPart())) {
					isItemNameTag = false;
				} else if (TAG_UNIT_PRICE.equalsIgnoreCase(element.getName().getLocalPart())) {
					isUnitPriceTag = false;
				} else if (TAG_QUANTITY.equalsIgnoreCase(element.getName().getLocalPart())) {
					isQuantityTag = false;
					// Add shoping Item object into the list when TAG_QUANTITY
					// end tag is
					// encountered
					shoppingItemList.add(shoppingItem);
				}
				if (TAG_SHOPPING_CARD.equalsIgnoreCase(element.getName().getLocalPart())) {
					// break the loop when "ShoppingCart " tag is encountered
					// ,This moment all shoppingCart.xml reading is completed
					// and List<shoppingItem> is
					// ready
					break;
				}

			} else if (isItemIdTag && event.getEventType() == XMLEvent.CHARACTERS) {

				shoppingItem.setItemId(Long.parseLong(event.asCharacters().getData()));
			} else if (isItemCategoryTag && event.getEventType() == XMLEvent.CHARACTERS) {

				shoppingItem.setCategoryId(Long.parseLong(event.asCharacters().getData()));
			} else if (isItemNameTag && event.getEventType() == XMLEvent.CHARACTERS) {

				shoppingItem.setItemName(event.asCharacters().getData());
			} else if (isUnitPriceTag && event.getEventType() == XMLEvent.CHARACTERS) {

				shoppingItem.setUnitPrice(Double.parseDouble(event.asCharacters().getData()));
			} else if (isQuantityTag && event.getEventType() == XMLEvent.CHARACTERS) {

				shoppingItem.setItemQty(Integer.parseInt(event.asCharacters().getData()));

			}

		}

		/*for (ShoppingItem s : shoppingItemList) {
			System.out.println(s.getItemId() + " " + s.getCategoryId() + " " + s.getItemName() + " " + s.getUnitPrice()
					+ " " + s.getItemQty());
		}*/

		return shoppingItemList;
	}

	

	// ****************Main class
	// **********************************************
	

	public static void main(String[] args) {
		String categoryFileName = "categories.xml";
		String discountSlabFileName = "flatDiscountSlabs.xml";
		String shoppingItemsFileName = "shoppingCart.xml";
		InvoiceDetails invoiceDetails=new InvoiceDetails();
		if (args.length > 0) {
			categoryFileName = args[0];
			discountSlabFileName = args[1];
			shoppingItemsFileName = args[2];
		}
		System.out.println("Processing  xml files  : " + categoryFileName + "" + discountSlabFileName + "  "
				+ shoppingItemsFileName);

		ShoppingInvoiceGeneratorUsingXml shoppingInvoiceGenerator = new ShoppingInvoiceGeneratorUsingXml();
		
		try {
			invoiceDetails=shoppingInvoiceGenerator.parseShoppingReletedXmlFiles(categoryFileName, discountSlabFileName,
					shoppingItemsFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

		System.out.println("Finished processing xml");
		GenerateInvoice.printInvoiceDetails(invoiceDetails);
	}
	
	
}
