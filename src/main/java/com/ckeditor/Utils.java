/*
 * Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */
package com.ckeditor;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code Utils} class contains a set of helper methods used by the CKEditor
 * object and CKEditor tags.
 */
public class Utils {

	/** Array of JavaScript string special characters. */
	private static final String [] CHARS_FROM = { "\\", "/", "\n", "\t", "\r",
			"\b", "\f", "\"" };

	/** Array of Java string special characters. */
	private static final String [] CHARS_TO = { "\\\\", "\\/", "\\\n", "\\\t",
			"\\\r", "\\\b", "\\\f", "\\\"" };

	/** Array of special characters which should be converted to HTML entities. */
	private static final String [] HTML_FROM = { "&", "\"", "<", ">" };

	/** Array of HTML entities into which special characters are converted. */
	private static final String [] HTML_TO = { "&amp;", "&quot;", "&lt;",
			"&gt;" };

	/** Default number of {@code &lt;textarea&gt;} rows. */
	private static final String DEFAULT_TEXTAREA_ROWS = "8";

	/** Default number of {@code &lt;textarea&gt;} columns. */
	private static final String DEFAULT_TEXTAREA_COLS = "60";

	/**
	 * Regular expression matching any character or set of characters in square
	 * or curly brackets. For example: {language : 'en'}.
	 */
	private static Matcher matcher = Pattern.compile( "[\\[{].*[\\]}]" )
			.matcher( "" );

	/**
	 * Returns a string wrapped into the HTML
	 * {@code &lt;script&gt;&lt;/script&gt;} tags.
	 * 
	 * @param input
	 *            the string to be wrapped.
	 * @return the string wrapped into the HTML
	 *         {@code &lt;script&gt;&lt;/script&gt;} tags.
	 */
	public static String script( final String input ) {
		StringBuilder out = new StringBuilder(
				"<script type=\"text/javascript\">\n" );
		out.append( "//<![CDATA[\n" );
		out.append( input );
		out.append( "\n//]]>" );
		out.append( "</script>\n" );
		return out.toString( );
	}

	/**
	 * Returns the HTML {@code &lt;script&gt;} tag which points to the {@code ckeditor.js}
	 * file. If the second parameter is not an empty {@code String} or {@code null}, a
	 * query string is appended to {@code ckeditor.js}.
	 * 
	 * @param basePath
	 *            the path to the CKEditor installation directory.
	 * @param args
	 *            a string formed as a query string appended to {@code ckeditor.js}.
	 * @return the HTML {@code &lt;script&gt;} tag pointing to {@code ckeditor.js}.
	 */
	public static String createCKEditorIncJS( final String basePath,
			final String args ) {
		return "<script type=\"text/javascript\" src=\""
				+ appendSlash( basePath ) + "ckeditor.js" + args
				+ "\"></script>\n";
	}

	/**
	 * General method used to convert Java objects into JavaScript-acceptable
	 * equivalents. The following Java objects are supported for conversion:
	 * {@code String}, {@code Number}, {@code Boolean}, {@code Map},
	 * {@code List}, {@code CKEditorConfig} and {@code null}. If another
	 * object is passed, an empty string is returned.
	 * 
	 * @param o
	 *            an object to encode.
	 * @return a string representing an appropriate JavaScript object or an empty string
	 *         if the object passed as a parameter is not supported.
	 */
	@SuppressWarnings("unchecked")
	public static String jsEncode( final Object o ) {
		if ( o == null ) {
			return "null";
		}
		if ( o instanceof String ) {
			return jsEncode( ( String ) o );
		}
		if ( o instanceof Number ) {
			return jsEncode( ( Number ) o );
		}
		if ( o instanceof Boolean ) {
			return jsEncode( ( Boolean ) o );
		}
		if ( o instanceof Map ) {
			return jsEncode( ( Map<String, Object> ) o );
		}
		if ( o instanceof List ) {
			return jsEncode( ( List<Object> ) o );
		}
		if ( o instanceof CKEditorConfig ) {
			return jsEncode( ( CKEditorConfig ) o );
		}
		return "";
	}

	/**
	 * Returns an acceptable form of a JavaScript string.
	 * 
	 * @param s
	 *            a {@code String} object to encode.
	 * @return a string representing an acceptable form of a JavaScript string.
	 */
	public static String jsEncode( final String s ) {
		if ( s.indexOf( "@@" ) == 0 ) {
			return s.substring( 2 );
		}
		if ( s.length( ) > 9
				&& s.substring( 0, 9 ).toUpperCase( ).equals( "CKEDITOR." ) ) {
			return s;
		}
		return clearString( s );
	}

	/**
	 * Converts the Java {@code Number} object into a JavaScript number.
	 * 
	 * @param n
	 *            a {@code Number} object to encode.
	 * @return a string representing the JavaScript number.
	 */
	public static String jsEncode( final Number n ) {
		return n.toString( ).replace( ",", "." );
	}

	/**
	 * Converts the Java {@code Boolean} object into a JavaScript Boolean.
	 * 
	 * @param b
	 *            a {@code Boolean} object to encode.
	 * @return a string representing the JavaScript Boolean.
	 */
	public static String jsEncode( final Boolean b ) {
		return b.toString( );
	}

	/**
	 * Converts the Java {@code Map} object into a JavaScript object.
	 * 
	 * @param map
	 *            a {@code Map} object to encode.
	 * @return a string representing the JavaScript object.
	 */
	public static String jsEncode( final Map<String, Object> map ) {
		StringBuilder sb = new StringBuilder( "{" );
		for ( Map.Entry<String, Object> entry : map.entrySet( ) ) {
			if ( sb.length( ) > 1 ) {
				sb.append( "," );
			}
			sb.append( jsEncode( entry.getKey( ) ) );
			sb.append( ":" );
			sb.append( jsEncode( entry.getValue( ) ) );
		}
		sb.append( "}" );
		return sb.toString( );
	}

	/**
	 * Converts the Java {@code List} object into a JavaScript array.
	 * 
	 * @param list
	 *            a {@code List} object to encode.
	 * @return a string representing the JavaScript array.
	 */
	public static String jsEncode( final List<Object> list ) {
		StringBuilder sb = new StringBuilder( "[" );
		for ( Object obj : list ) {
			if ( sb.length( ) > 1 ) {
				sb.append( "," );
			}
			sb.append( jsEncode( obj ) );
		}
		sb.append( "]" );
		return sb.toString( );
	}

	/**
	 * Converts the {@code CKEditorConfig} object into a JavaScript object.
	 * 
	 * 
	 * @param config
	 *            a {@code CKEditorConfig} object to encode.
	 * @return a string representing the JavaScript object.
	 */
	public static String jsEncode( final CKEditorConfig config ) {
		StringBuilder sb = new StringBuilder( "{" );

		for ( Map.Entry<String, Object> configEntry : config.getConfigValues( )
				.entrySet( ) ) {
			if ( sb.length( ) > 1 ) {
				sb.append( "," );
			}
			sb.append( jsEncode( configEntry.getKey( ) ) );
			sb.append( ":" );
			sb.append( jsEncode( ( configEntry.getValue( ) ) ) );
		}
		sb.append( "}" );
		return sb.toString( );
	}

	/**
	 * Changes JavaScript string special characters into Java {@code String}
	 * special characters and quotes this string if necessary.
	 * 
	 * @param s
	 *            a JavaScript string with special characters to change.
	 * @return a string with characters converted.
	 */
	private static String clearString( final String s ) {
		String string = s;
		for ( int i = 0; i < CHARS_FROM.length; i++ ) {
			string = string.replace( CHARS_FROM[i], CHARS_TO[i] );
		}
		if ( matcher.reset( string ).matches( ) ) {
			return string;
		} else {
			return "\"" + string + "\"";
		}
	}

	/**
	 * Creates a string representing the HTML {@code &lt;textarea&gt;} element which
	 * is to be inserted into JSP.
	 * 
	 * @param textAreaName
	 *            a {@code String} representing the name and ID of the {@code &lt;textarea&gt;}
	 *            element that will be inserted into JSP and used to replace it with an editor instance.
	 * 
	 * @param htmlValue
	 *            a {@code String} representing the HTML value for the {@code &lt;textarea&gt;} element.
	 *            This HTML will be later used as the initial value for the editor which
	 *            will be created on the {@code &lt;textarea&gt;} element.
	 * 
	 * @param textareaAttributes
	 *            The @{code Map} representing attributes for the {@code &lt;textarea&gt;} element
	 *            that will be inserted into JSP and replaced by an editor instance.
	 * 
	 * @return a string representing the HTML {@code &lt;textarea&gt;} element.
	 */
	public static String createTextareaTag( final String textAreaName,
			String htmlValue, Map<String, String> textareaAttributes ) {
		StringBuilder sb = new StringBuilder( );
		sb.append( "<textarea name=\"" );
		sb.append( textAreaName );
		sb.append( "\" " );
		sb.append( "id=\"" );
		sb.append( textAreaName );
		sb.append( "\"" );
		sb.append( createTextareaAttributesText( textareaAttributes ) );
		sb.append( ">" );
		sb.append( escapeHtml( htmlValue ) );
		sb.append( "</textarea>\n" );
		return sb.toString( );
	}

	/**
	 * Returns a {@code String} representing the {@code &lt;textarea&gt;} element attributes. If no attributes
	 * are provided as a parameter, a predefined set of attributes will be used.
	 * 
	 * @param textareaAttributes
	 *            A {@code Map} representing attributes for the {@code &lt;textarea&gt;} element
	 *            that will be inserted into JSP and replaced by an editor instance.
	 * 
	 * @return a string representing the {@code &lt;textarea&gt;} element attributes.
	 */
	private static String createTextareaAttributesText(
			Map<String, String> textareaAttributes ) {
		if ( textareaAttributes.isEmpty( ) ) {
			textareaAttributes.put( "rows", DEFAULT_TEXTAREA_ROWS );
			textareaAttributes.put( "cols", DEFAULT_TEXTAREA_COLS );
		}
		StringBuilder sb = new StringBuilder( );
		for ( Map.Entry<String, String> attrEntry : textareaAttributes
				.entrySet( ) ) {
			sb.append( " " );
			sb.append( attrEntry.getKey( ) ).append( "=\"" )
					.append( attrEntry.getValue( ) ).append( "\"" );
		}
		return sb.toString( );
	}

	/**
	 * Converts special characters found in a string into HTML entities.
	 * 
	 * @param text
	 *            a string with special characters to convert.
	 * @return a string with special characters converted to HTML entities.
	 */
	public static String escapeHtml( final String text ) {
		String result = text;
		if ( text == null || text.equals( "" ) )
			return "";
		for ( int i = 0; i < HTML_FROM.length; i++ ) {
			result = result.replaceAll( HTML_FROM[i], HTML_TO[i] );
		}
		return result;
	}

	/**
	 * Appends a slash character to the string provided that no slash is found at the
	 * end of this string. If {@code null} or empty is passed as a parameter,
	 * the slash character is returned.
	 * 
	 * @param s
	 *            the string to test.
	 * @return a string with a slash at the end.
	 */
	public static String appendSlash( String s ) {
		if ( isStringEmpty( s ) )
			s = "/";
		else if ( !s.endsWith( "/" ) )
			s = s + "/";
		return s;
	}

	/**
	 * Checks whether the {@code String} provided as a parameter is {@code null} or if it
	 * contains any characters.
	 * 
	 * @param s
	 *            the string to check.
	 * @return if the string provided as a parameter is {@code null} or has 0
	 *         characters, {@code true} is returned. Otherwise this method
	 *         returns {@code false}.
	 * 
	 */
	public static boolean isStringEmpty( final String s ) {
		if ( s == null || s.isEmpty( ) )
			return true;
		return false;
	}

}
