/*
 * Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */
package com.ckeditor;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * The {@code EventHandler} class is used to create a set of events which are
 * applicable to a CKEditor instance.<br>
 * 
 * <strong>Usage:</strong>
 * 
 * <pre>
 *   	EventHandler eventHandler = new EventHandler();
 * 		eventHandler.addEventHandler( "instanceReady", "function ( event ) {
 * 			alert( \"Loaded: \" + event.editor.name ); }");
 * </pre>
 */
public class EventHandler {

	/**
	 * {@code Map} storing all editor instance events.
	 */
	protected Map<String, Set<String>> events;

	/**
	 * Creates the {@code EventHandler} object and initializes events
	 * {@code Map}.
	 */
	public EventHandler ( ) {
		events = new HashMap<String, Set<String>>( );
	}

	/**
	 * Adds an event.
	 * 
	 * @param event
	 *            a string representing an event name.
	 * @param jsCode
	 *            a string representing code for an anonymous JavaScript
	 *            function or a JavaScript function name.
	 */
	public void addEventHandler( final String event, final String jsCode ) {
		if ( events.get( event ) == null ) {
			events.put( event, new LinkedHashSet<String>( ) );
		}
		events.get( event ).add( jsCode );
	}

	/**
	 * Removes registered event handlers based on an event name provided as a
	 * parameter. If an event name is {@code null}, all event handlers will be
	 * removed.
	 * 
	 * @param event
	 *            the name of the event for which event handlers should be
	 *            removed or {@code null} if all event handlers for all events
	 *            are to be deleted.
	 */
	public void clearEventHandlers( final String event ) {
		if ( event == null ) {
			events = new HashMap<String, Set<String>>( );
		} else {
			if ( events.get( event ) != null ) {
				events.get( event ).clear( );
			}
		}
	}

	/**
	 * Returns a {@code Map} with all events assigned to the editor instance.
	 * 
	 * @return all events registered to the editor instance.
	 */
	public final Map<String, Set<String>> getEvents( ) {
		return events;
	}

}
