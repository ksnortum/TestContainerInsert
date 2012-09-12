package com.catalystitservices.vaadin;

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
 * 
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
	 * 
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
					Property property = event.getProperty();
					Object value = property.getValue();
					System.out.println( "Value: " + value );
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
	}
}