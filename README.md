Test Container Insert
=====================

_Note:_ The name is no longer applicable.  This project was originally used to test the insertion of an item into an *IndexedContainer*.  It is now being used to test the *ValueChangeListener*.

This project sets up a simple Table using [Vaadin](vaadin.com) and tries to listen to changes made in its cells.

Running
-------

1. download the project
2. download [Maven](http://maven.apache.org/download.html) if necessary
3. `cd` into the project home (the folder with the pom.xml in it)
4. execute `mvn clean install tomcat7:run`
5. open your favorite browser.  The URL will be in the messages, but it should be [http://localhost:9090/test-insert](http://localhost:9090/test-insert)

_Note:_ You can also run this in the Eclipse IDE by importing the project and running with the [m2e](http://www.eclipse.org/m2e/download/) plugin.

Things To Notice
----------------

* make sure you can see the command window and the browser
* the values that are pre-loaded appear in the command window
* change a name or number, nothing happens
* `tab`, nothing happens
* click in the fields, nothing happens
* click *outside* the fields or on the ID, now the change(s) register

Question
--------

How can I code a *ValueChangeListener* that will fire when the value changes or at least when you tab out of the field?

Answer
------

*ValueChangeListener* will fire when any change is made, even programmatic.  The event you need to listen to if you want only user input changes is *onBlur*.  Also, when you listen to the container, it only fires when the row updates.  This is why the clicking outside the field fires the VCL event.  You need to listen to each column, which for a grid is done in a *FieldFactory*.  This also does away with the need to do reflection in *getIdAndProperty()*.

Contact
-------

Knute Snortum <ksnortum@catalystitservices.com>