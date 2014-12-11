CKEditor for Java 4
===================

**Important Note**

The CKEditor for Java code is still **work in progress**. Its official release will be announced on http://ckeditor.com. 

CKEditor for Java is a server-side integration for CKEditor. It allows you to use this popular JavaScript editor 
in Java Server Pages (JSP) as a custom JSP tag or as a Java Object integrated with the code of your web application.

## Documentation

CKEditor for Java comes with documented code. If you are using it inside your IDE, through Maven, you can also 
import the `ckeditor-java-core-4.0-javadoc.jar` file in order to get descriptions of all 
CKEditor for Java classes, properties and methods you can use in your web application. 

The CKEditor for Java API is also available online at: http://link-to-api.com

The full developer documentation for the CKEditor for Java integration is available online at: http://link-to-docs

## Integration

Integrating CKEditor for Java with your web application can be done in three different ways. Just follow these steps:

 * In all three cases you need to get the standard JavaScript CKEditor installation package from http://ckeditor.com/download
 	and put it in a folder of your choice inside your web application. 
 
### Maven
 
 1. If you use Maven,you can get CKEditor for Java by placing the code below inside your `pom.xml` file:
 	
 		<dependency>
			<groupId>com.ckeditor</groupId>
			<artifactId>ckeditor-java-core</artifactId>
			<version>4.0</version>		
		</dependency> 
 	
### Sample Application
 
  1. **Download** the sample application from http://link-to-sample-app.com.
  2. **Extract** (decompress) the `war` file and go to the `WEB-INF/lib` folder.
  3. **Copy** the `ckeditor-java-core-4.0.jar` file and paste it into the `WEB-INF/lib`
 	 directory of your web application.
 	 
### Direct Download of Jar Files

  1. **Download** the `zip` file that contains three `jar` files from http://link-to-3-jars.com.  
  2. **Extract** (decompress) the `zip` file to the `WEB-INF/lib` folder
  	of your web application.

## CKEditor for Java Samples

A sample application with examples showing how to use CKEditor for Java in a web 
application is available at: http://link-to-sample-app.com.

To install the sample application, put the downloaded `war` file into your server.

To test your installation, call the following page of your website:

	http://<your site>/<CKEditor installation path>/samples/index.jsp

For example:

	http://www.example.com/ckeditor-java/samples/index.jsp

## License

Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.  
For licensing, see LICENSE.md or http://ckeditor.com/license