package com.catalystitservices.vaadin;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * The main application class to test addItem versus insertItem
 */
@SuppressWarnings( "serial" )
public class TestApp extends Application {
	private IndexedContainer container;
	private Table table;
	private int itemId = 0;
	private int insertItemId = 0;

	/**
	 * This starts the webapp by setting the main window
	 */
	@Override
	public void init() {
		setMainWindow( new TestWindow() );
	}

	/**
	 * Setup the main window: add table and button
	 */
	public class TestWindow extends Window {
		public TestWindow() {
			VerticalLayout mainLayout = new VerticalLayout();
			mainLayout.addComponent( createTable() );
			Button borisButton = new Button( "Add Borris" );

			// Add a listener so that we can see the insertion of an item
			borisButton.addListener( new ClickListener() {

				@Override
				public void buttonClick( ClickEvent event ) {
					insertItem( "Boris", 12 );
				}
			} );

			mainLayout.addComponent( borisButton );
			setContent( mainLayout );
		}

		/**
		 * Create a table and container
		 * 
		 * @return the created table object
		 */
		private Table createTable() {
			createContainer();
			table = new Table();
			table.setContainerDataSource( container );
			table.setWidth( "50%" );
			table.setVisibleColumns( new Object[] { "id", "name", "number" } );
			table.setColumnExpandRatio( "id", 1 );
			table.setColumnExpandRatio( "name", 3 );
			table.setColumnExpandRatio( "number", 3 );
			table.setEditable( true );
			table.setImmediate( true );

			return table;
		}

		/**
		 * Setup the Indexed Container and add items and listener
		 */
		private void createContainer() {

			// Set the properties (columns)
			container = new IndexedContainer();
			container.addContainerProperty( "name", String.class, null );
			container.addContainerProperty( "number", Integer.class, null );
			container.addContainerProperty( "id", String.class, null );

			// Test value change listener
			container.addListener( new Property.ValueChangeListener() {

				@Override
				public void valueChange( ValueChangeEvent event ) {
					
					// This is all we can get w/o reflection
					Property property = event.getProperty();
					Object value = property.getValue();
					System.out.println( "\nValue: " + value );

					Map<String, Object> returnData;

					// Use reflection to get item and property ID
					try {
						returnData = getIdAndProperty( property );
					} catch ( NoSuchFieldException e ) {
						e.printStackTrace();
						return;
					}

					Object itemId = returnData.get( "itemId" );
					String propertyId = (String) returnData.get( "propertyId" );
					System.out.println( "Item ID: " + itemId
							+ ", Property ID: " + propertyId );

					// Get the item from the container
					if ( table != null ) {
						
						// Pretend that container isn't visible
						IndexedContainer container = (IndexedContainer) table
								.getContainerDataSource(); 
						Item item = container.getItem( itemId );
						System.out.println( "Item is "
								+ ( item == null ? "" : "not " ) + "null" );

						// Change things using the item and property ID
						if ( propertyId.equals( "number" )
								&& value != null
								&& value.toString().equals( "0" ) ) {
							item.getItemProperty( "name" ).setValue( "Zero" );
							
						} else if ( propertyId.equals( "name" ) 
								&& value != null
								&& value.toString().equals( "Boris" ) ) {
							item.getItemProperty( "number" ).setValue( 42 );
						}
					}
				}
			} );

			// Add items (rows)
			addItem( "Bob", 10 );
			insertItemId = itemId;
			addItem( "Harry", 1 );
			insertItem( "Margaret", 13 );
			insertItemId = itemId;
			addItem( "Glenda", 22 );
			addItem( "Jessica", 24 );
			addItem( null, 99 );
			addItem( "Knute", null );
		}

		/**
		 * Add an item
		 * 
		 * @param name
		 *            name to set
		 * @param number
		 *            number to set
		 */
		private void addItem( String name, Integer number ) {
			Item item = container.addItem( itemId );
			setValues( name, number, item );
		}

		/**
		 * Insert an item
		 * 
		 * @param name
		 *            name to set
		 * @param number
		 *            number to set
		 */
		private void insertItem( String name, Integer number ) {
			Item item = container.addItemAfter( insertItemId, itemId );
			setValues( name, number, item );
		}

		/**
		 * Set this item's properties
		 * 
		 * @param name
		 *            name to set
		 * @param number
		 *            number to set
		 * @param item
		 *            the added or inserted item
		 */
		private void setValues( String name, Integer number, Item item ) {
			item.getItemProperty( "name" ).setValue( name );
			item.getItemProperty( "number" ).setValue( number );
			item.getItemProperty( "id" ).setValue( itemId );
			item.getItemProperty( "id" ).setReadOnly( true );
			itemId++;
		}

		/**
		 * Get itemId and propertyId from the eventProperty via reflection
		 * 
		 * @param eventProperty
		 * @return map with "itemId" and "propertyId" keys
		 * @throws NoSuchFieldException
		 * @throws
		 */
		private Map<String, Object> getIdAndProperty( Property eventProperty )
				throws NoSuchFieldException {

			Map<String, Object> returnData = new HashMap<String, Object>();
			Class<? extends Property> clazz = eventProperty.getClass();
			final Field idField = clazz.getDeclaredField( "itemId" );
			final Field propertyField = clazz.getDeclaredField( "propertyId" );

			AccessController.doPrivileged( new PrivilegedAction<Object>() {
				@Override
				public Object run() {
					idField.setAccessible( true );
					propertyField.setAccessible( true );
					return null;
				}
			} );

			try {
				returnData.put( "itemId", idField.get( eventProperty ) );
				returnData
						.put( "propertyId", propertyField.get( eventProperty ) );
			} catch ( IllegalArgumentException e ) {
				e.printStackTrace();
			} catch ( IllegalAccessException e ) {
				e.printStackTrace();
			}

			return returnData;
		}
	}
}