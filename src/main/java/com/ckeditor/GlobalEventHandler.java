/* Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved. For licensing, see LICENSE.md or http://ckeditor.com/license */
package com.ckeditor;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * The {@code GlobalEventHandler} is used to create a set of events which are applicable to all editors available on JSP.<br>
 * <strong>Usage:</strong>
 * 
 * <pre>
 * 	GlobalEventHandler globalEventHandler = new GlobalEventHandler();
 * 	globalEventHandler.addEventHandler("dialogDefinition","function ( event ) {
 * 							alert( \"Loading dialog window: \" + event.data.name ); }");
 * </pre>
 */
public class GlobalEventHandler extends EventHandler {

	private static Map< String, Set< String >> globalEvents;

	/**
	 * Returns a {@code String} representing the JavaScript code for global CKEditor events.
	 * 
	 * @return a string representing JavaScript code for global CKEditor events.
	 */
	public String returnGlobalEvents() {
		StringBuilder out = new StringBuilder();
		if ( globalEvents == null ) {
			globalEvents = new HashMap< String, Set< String >>();
		}

		for ( Map.Entry< String, Set< String >> eventEntry : events.entrySet() ) {
			for ( String code : eventEntry.getValue() ) {
				String event = eventEntry.getKey();
				if ( globalEvents.get( event ) == null ) {
					globalEvents.put( event, new LinkedHashSet< String >() );
				}
				out.append( ( !code.equals( "" ) ? "\n" : "" ) + "CKEDITOR.on('" + event + "', " + code + ");" );
				globalEvents.get( event ).add( code );

			}
		}
		return out.toString();
	}

}
