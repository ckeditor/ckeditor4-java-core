/*
 * Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */
package com.ckeditor;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.ckeditor.Utils;

/**
 * The {@code CKEditor} class is used to create CKEditor objects in Java. Such
 * objects can then be put in scope (like in a request or a session) and read
 * with Expression Lagunage (EL) on JSP. Another possibility is using the
 * {@code CKEditor} class directly on JSP with scriptlets. Please note, however,
 * that scriptlets, although not deprecated, are discouraged and it is best not
 * to use them.
 * <p>
 * The {@code CKEditor} object is created with constructors which represent
 * different creation methods. "Creation methods" here mean JavaScript methods
 * that are written to JSP and are used to create the editor on the client side.
 */

public class CKEditor {

	/**
	 * Enumeration of markers for CKEditor JavaScript creation methods.
	 * */
	public enum Methods {
		/** {@code Enum} marker for the JavaScript {@code replaceAll} method. */
		REPLACE_ALL,
		/** {@code Enum} marker for the JavaScript {@code inlineAll} method. */
		INLINE_ALL,
		/** {@code Enum} marker for the JavaScript {@code replace} method. */
		REPLACE,
		/** {@code Enum} marker for the JavaScript {@code inline} method. */
		INLINE,
		/**
		 * {@code Enum} marker for the JavaScript {@code replace} or
		 * {@code inline} method.
		 */
		INSERT;

		/**
		 * Enumeration of markers for the CKEditor JavaScript mulitple instances
		 * creation methods.
		 * */
		public enum Multi {
			/**
			 * {@code Enum} marker for the JavaScript {@code replaceAll} method.
			 */
			REPLACE_ALL,
			/** {@code Enum} marker for the JavaScript {@code inlineAll} method. */
			INLINE_ALL
		};

		/**
		 * Enumeration of markers for the CKEditor JavaScript single instance
		 * creation methods.
		 * */
		public enum Single {
			/** {@code Enum} marker for the JavaScript {@code inline} method. */
			REPLACE,
			/** {@code Enum} marker for the JavaScript {@code replace} method. */
			INLINE
		};
	}

	/**
	 * Attributes for the {@code <textarea>} element that will be inserted
	 * into JSP and replaced with CKEditor.
	 */
	private HashMap<String, String> textareaAttributes;

	/**
	 * Field indicating which JavaScript method should be inserted into JSP to
	 * create CKEditor.
	 */
	private Methods creationMethod = Methods.REPLACE_ALL;

	/**
	 * The Boolean flag informing whether CKEditor was initialized with base
	 * parameters.
	 */
	private boolean initialized;

	/**
	 * Name of a CSS marker class used to identify {@code <textarea>}
	 * elements which will be replaced by editor instances.
	 */
	private String className;

	/**
	 * Flag indicating whether CKEditor should be created as inline or classic
	 * editor.
	 */
	private boolean inline;

	/**
	 * CKEditor configuration object. It stores the entire editor instance
	 * configuration.
	 */
	private CKEditorConfig config;

	/** Current request object. */
	private HttpServletRequest request;

	/** Name or ID of the {@code <textarea>} element. */
	private String instanceName;

	/**
	 * Initial CKEditor value. It will be inserted into an editor instance upon
	 * its creation.
	 */
	private String value;

	/**
	 * Full or relative path to the CKEditor installation directory. If a full
	 * path is used, the {@code CKEDITOR_BASEPATH} global variable will be
	 * inserted into the JSP.
	 */
	private String basePath;

	/**
	 * Timestamp value used to build the URL for all resources loaded by the
	 * editor code, guaranteeing clean cache results when upgrading.
	 */
	private String timestamp;

	/**
	 * JavaScript event handlers that will be assigned to this particular editor
	 * instance.
	 */
	private EventHandler events;

	/**
	 * JavaScript event handlers that will be assigned to all editor instances.
	 * Depending on scope, these events might be assigned to all editors present
	 * on JSP (request scope), all editors available for the user during the
	 * session (session scope) or all editor instances in the application
	 * (application scope).
	 */
	private GlobalEventHandler globalEvents;

	/**
	 * Represents the JavaScript {@code replaceAll} method&#x2e; It replaces all
	 * {@code <textarea>} elements on JSP with CKEditor instances.
	 * 
	 * @param request
	 *            current {@code HttpServletRequest} instance.
	 * @param basePath
	 *            a string representing an absolute or a relative path to the
	 *            CKEditor installation directory.
	 */
	public CKEditor ( HttpServletRequest request, String basePath ) {
		this.request = request;
		setBasePath( basePath );
	}

	/**
	 * Represents the JavaScript {@code replaceAll} method&#x2e; On a JSP it
	 * replaces all {@code <textarea>} elements whose CSS class matches
	 * the class name provided as an argument to this constructor with CKEditor
	 * instances.
	 * 
	 * @param request
	 *            current {@code HttpServletRequest} instance.
	 * @param basePath
	 *            a string representing an absolute or a relative path to the
	 *            CKEditor installation directory.
	 * @param className
	 *            the name of the CSS marker class used to identify
	 *            {@code <textarea>} elements on which the editor should
	 *            be created.
	 */
	public CKEditor ( HttpServletRequest request, String basePath,
			String className ) {
		this( request, basePath );
		this.className = className;
	}

	/**
	 * Represents the JavaScript {@code replaceAll} method&#x2e; On a JSP it
	 * replaces all {@code <textarea>} elements whose CSS class matches
	 * the class name provided as an argument to this constructor with CKEditor
	 * instances.<br>
	 * Additionally this constructor allows for passing customized editor
	 * instance configuration.
	 * 
	 * @param request
	 *            current {@code HttpServletRequest} instance.
	 * @param basePath
	 *            a string representing absolute or relative path to CKEditor
	 *            installation directory.
	 * @param className
	 *            the name of the CSS marker class used to identify
	 *            {@code <textarea>} elements on which the editor should
	 *            be created.
	 * @param config
	 *            the CKEditor configuration object storing this editor instance
	 *            custom configuration.
	 */
	public CKEditor ( HttpServletRequest request, String basePath,
			String className, CKEditorConfig config ) {
		this( request, basePath, className );
		this.config = config;
	}

	/**
	 * Represents the JavaScript {@code replaceAll} or {@code inlineAll}
	 * method&#x2e; On a JSP it replaces all {@code <textarea>}
	 * elements with CKEditor instances or creates inline editors inside all
	 * {@code contenteditable} elements available on a JSP.
	 * 
	 * @param request
	 *            current {@code HttpServletRequest} instance.
	 * @param basePath
	 *            a string representing an absolute or a relative path to the
	 *            CKEditor installation directory.
	 * @param creationMethod
	 *            the {@code Enum} marker indicating which JavaScript method
	 *            should be used to create the editor instance. Only
	 *            {@code CKEditor.Methods.Multi} enumeration values are allowed.
	 */
	public CKEditor ( HttpServletRequest request, String basePath,
			Methods.Multi creationMethod ) {
		this( request, basePath );

		if ( creationMethod == Methods.Multi.INLINE_ALL )
			this.creationMethod = Methods.INLINE_ALL;
		else if ( creationMethod == Methods.Multi.REPLACE_ALL )
			this.creationMethod = Methods.REPLACE_ALL;
	}

	/**
	 * Represents the JavaScript {@code replace} method, however, it does not
	 * require the {@code <textarea>} tag to be available on the JSP.
	 * <p>
	 * This constructor inserts the HTML {@code <textarea>} element into
	 * the page and then replaces it with an editor instance.
	 * 
	 * @param request
	 *            current {@code HttpServletRequest} instance.
	 * @param basePath
	 *            a string representing an absolute or a relative path to the
	 *            CKEditor installation directory.
	 * @param instanceName
	 *            the name or ID of the {@code <textarea>} element that is
	 *            to be inserted into the JSP.
	 * @param value
	 *            the initial HTML code for a CKEditor instance.
	 * @param textareaAttributes
	 *            the {@code Map} of key-value pairs representing
	 *            {@code <textarea>} attributes and their values. The list
	 *            of attributes can be found on the <a
	 *            href="http://www.w3schools.com/tags/tag_textarea.asp">
	 *            W3Schools</a> website.<br>
	 *            Please note, however, that attributes useful for CKEditor or
	 *            that have any influence on it are {@code rows}, {@code cols},
	 *            {@code disabled} and {@code form}.
	 */
	public CKEditor ( HttpServletRequest request, String basePath,
			String instanceName, String value,
			HashMap<String, String> textareaAttributes ) {
		this( request, basePath );
		this.instanceName = instanceName;
		this.creationMethod = Methods.INSERT;
		this.value = value;
		this.textareaAttributes = textareaAttributes;
	}

	/**
	 * Represents the JavaScript {@code replace} method, however, it does not
	 * require the {@code <textarea>} tag to be available on the JSP.
	 * <p>
	 * This constructor inserts the HTML {@code <textarea>} element into
	 * the page and then replaces it with an editor instance.<br>
	 * Additionally, this constructor allows for passing a customized editor
	 * instance configuration.
	 * 
	 * @param request
	 *            current {@code HttpServletRequest} instance.
	 * @param basePath
	 *            a string representing an absolute or a relative path to the
	 *            CKEditor installation directory.
	 * @param instanceName
	 *            the name or ID of the {@code <textarea>} element that is
	 *            to be inserted into the JSP.
	 * @param value
	 *            the initial HTML code for a CKEditor instance.
	 * @param textareaAttributes
	 *            the {@code Map} of key-value pairs representing
	 *            {@code <textarea>} attributes and their values. The list
	 *            of attributes can be found on the <a
	 *            href="http://www.w3schools.com/tags/tag_textarea.asp">
	 *            W3Schools</a> website.<br>
	 *            Please note, however, that attributes useful for CKEditor or
	 *            that have any influence on it are {@code rows}, {@code cols},
	 *            {@code disabled} and {@code form}.
	 * @param config
	 *            the CKEditor configuration object storing this editor instance
	 *            custom configuration.
	 */
	public CKEditor ( HttpServletRequest request, String basePath,
			String instanceName, String value,
			HashMap<String, String> textareaAttributes, CKEditorConfig config ) {
		this( request, basePath, instanceName, value, textareaAttributes );
		this.config = config;
	}

	/**
	 * Represents the JavaScript {@code replace} or {@code inline} method&#x2e;
	 * On a JSP it replaces the specified {@code <textarea>} element with
	 * a CKEditor instance or creates inline editor inside the {@code contenteditable}
	 * element available on a JSP.
	 * 
	 * @param request
	 *            current {@code HttpServletRequest} instance.
	 * @param basePath
	 *            a string representing an absolute or a relative path to the
	 *            CKEditor installation directory.
	 * @param creationMethod
	 *            the {@code Enum} marker indicating which JavaScript method
	 *            should be used to create an editor instance. Only
	 *            {@code CKEditor.Methods.Single} enumeration values are
	 *            allowed.
	 * @param instanceName
	 *            the name or ID of the {@code <textarea>} or
	 *            {@code contenteditable} element available on a JSP.
	 */
	public CKEditor ( HttpServletRequest request, String basePath,
			Methods.Single creationMethod, String instanceName ) {
		this( request, basePath );
		this.instanceName = instanceName;

		if ( creationMethod == Methods.Single.INLINE )
			this.creationMethod = Methods.INLINE;
		else if ( creationMethod == Methods.Single.REPLACE )
			this.creationMethod = Methods.REPLACE;
	}

	/**
	 * Represents the JavaScript {@code replace} or {@code inline} method&#x2e;
	 * On a JSP it replaces the specified {@code <textarea>} element with
	 * a CKEditor instance or creates inline editor inside the {@code contenteditable}
	 * element available on a JSP.<br>
	 * Additionally this constructor allows for passing a customized editor
	 * instance configuration.
	 * 
	 * @param request
	 *            current {@code HttpServletRequest} instance.
	 * @param basePath
	 *            a string representing an absolute or a relative path to the
	 *            CKEditor installation directory.
	 * @param creationMethod
	 *            the {@code Enum} marker indicating which JavaScript method
	 *            should be used to create an editor instance. Only
	 *            {@code CKEditor.Methods.Single} enumeration values are
	 *            allowed.
	 * @param instanceName
	 *            the name or ID of the {@code <textarea>} or
	 *            {@code contenteditable} element available on a JSP.
	 * @param config
	 *            the CKEditor configuration object storing this editor instance
	 *            custom configuration.
	 */
	public CKEditor ( HttpServletRequest request, String basePath,
			Methods.Single creationMethod, String instanceName,
			CKEditorConfig config ) {
		this( request, basePath, creationMethod, instanceName );
		this.config = config;
	}

	/**
	 * Represents the JavaScript {@code replace} or {@code inline} method,
	 * however, it does not require the {@code <textarea>} tag to be
	 * available on the JSP.
	 * <p>
	 * This constructor inserts the {@code <textarea>} element into the
	 * page and then replaces it with an editor instance.<br>
	 * Additionally, this constructor allows for passing a customized editor
	 * instance configuration.
	 * 
	 * @param request
	 *            current {@code HttpServletRequest} instance.
	 * @param basePath
	 *            a string representing an absolute or a relative path to the
	 *            CKEditor installation directory.
	 * @param instanceName
	 *            the name or ID of the {@code <textarea>} element to be
	 *            inserted into the JSP.
	 * @param value
	 *            the initial HTML code for the CKEditor instance.
	 * @param textareaAttributes
	 *            the {@code Map} of key-value pairs representing
	 *            {@code <textarea>} attributes and their values. The list
	 *            of attributes can be found on the <a
	 *            href="http://www.w3schools.com/tags/tag_textarea.asp">
	 *            W3Schools</a> website.<br>
	 *            Please note, however, that attributes useful for CKEditor or
	 *            that have any influence on it are just {@code rows},
	 *            {@code cols}, {@code disabled} and {@code form}.
	 * @param inline
	 *            a Boolean flag indicating whether inline or classic editor
	 *            should be used to replace the {@code <textarea>} on a
	 *            JSP.
	 * @param config
	 *            the CKEditor configuration object storing this editor instance
	 *            custom configuration.
	 */
	public CKEditor ( HttpServletRequest request, String basePath,
			String instanceName, String value,
			HashMap<String, String> textareaAttributes, boolean inline,
			CKEditorConfig config ) {
		this( request, basePath, instanceName, value, textareaAttributes,
				inline );
		this.config = config;
	}

	/**
	 * Represents the JavaScript {@code replace} or {@code inline} method,
	 * however, it does not require the {@code <textarea>} tag to be
	 * available on the JSP.<br>
	 * This constructor inserts the HTML {@code <textarea>} element into
	 * the page and then replaces it with an editor instance.
	 * 
	 * @param request
	 *            current {@code HttpServletRequest} instance.
	 * @param basePath
	 *            a string representing an absolute or a relative path to the
	 *            CKEditor installation directory.
	 * @param instanceName
	 *            the name or ID of the {@code <textarea>} element to be
	 *            inserted into JSP.
	 * @param value
	 *            the initial HTML code for a CKEditor instance.
	 * @param textareaAttributes
	 *            the {@code Map} of key-value pairs representing
	 *            {@code <textarea>} attributes and their values. The list
	 *            of attributes can be found on the <a
	 *            href="http://www.w3schools.com/tags/tag_textarea.asp">
	 *            W3Schools</a> website.<br>
	 *            Please note, however, that attributes useful for CKEditor or
	 *            that have any influence on it are just {@code rows},
	 *            {@code cols}, {@code disabled} and {@code form}.
	 * @param inline
	 *            a Boolean flag indicating whether inline or classic editor
	 *            should be used to replace the {@code <textarea>} on a
	 *            JSP.
	 */
	public CKEditor ( HttpServletRequest request, String basePath,
			String instanceName, String value,
			HashMap<String, String> textareaAttributes, boolean inline ) {
		this( request, basePath, instanceName, value, textareaAttributes );
		this.inline = inline;
	}

	/**
	 * Creates the HTML representation of this editor instance.
	 * 
	 * @return the HTML representation of this editor instance.
	 */
	@Override
	public String toString( ) {
		StringBuilder sb = new StringBuilder( );
		StringBuilder sbInit = new StringBuilder( );

		sbInit.append( getInitParams( ) );

		if ( creationMethod == Methods.INSERT ) {
			sbInit.append( Utils.createTextareaTag( instanceName, value,
					textareaAttributes ) );
			// Change the creation method if the user requested inline editor
			// to be built on a textarea element.
			if ( isInline( ) )
				creationMethod = Methods.INLINE;
		}

		if ( globalEvents != null )
			sb.append( globalEvents.returnGlobalEvents( ) );

		if ( events != null ) {
			if ( config == null )
				config = new CKEditorConfig( );
			config = config.configSettings( this.events );
		}

		CKEditorConfig globalConfig = getGlobalConfig( );

		if ( globalConfig != null ) {
			if ( config == null )
				config = new CKEditorConfig( );
		}

		if ( request.getAttribute( "ckeditor_disable_auto_inline_set" ) == null
				&& creationMethod == Methods.INLINE ) {
			sb.append( "CKEDITOR.disableAutoInline = true;\n" );
			request.setAttribute( "ckeditor_disable_auto_inline_set",
					new Boolean( true ) );
		}

		sb.append( getStartMethodTemplate( creationMethod ) );
		if ( !Utils.isStringEmpty( className )
				&& ( ( config != null && !config.isEmpty( ) ) || ( globalConfig != null && !globalConfig
						.isEmpty( ) ) ) ) {
			sb.append( "function(textarea, config) {\n" )
					.append( "	var classRegex = new RegExp('(?:^| )' + '" )
					.append( className ).append( "' + '(?:$| )');\n" )
					.append( "	if (!classRegex.test(textarea.className))\n" )
					.append( "		return false;\n" )
					.append( "CKEDITOR.tools.extend( config," );

			if ( globalConfig != null ) {
				config = globalConfig.mergeConfigValues( config );
			}
			sb.append( Utils.jsEncode( config ) ).append( ", true);\n" )
					.append( "}\n" );
		} else {
			if ( !Utils.isStringEmpty( className )
					|| !Utils.isStringEmpty( instanceName ) ) {
				sb.append( "'" );
				if ( !Utils.isStringEmpty( className ) )
					sb.append( className );
				else
					sb.append( instanceName );
				sb.append( "'" );

				if ( config != null ) {
					if ( globalConfig != null )
						config = globalConfig.mergeConfigValues( config );

					if ( !config.isEmpty( ) ) {
						sb.append( "," );
						sb.append( Utils.jsEncode( config ) );
					}
				}
			}
		}
		sb.append( ");\n" );

		if ( ( creationMethod == Methods.INLINE_ALL || ( Utils
				.isStringEmpty( className ) && creationMethod == Methods.REPLACE_ALL ) )
				&& globalConfig != null && !globalConfig.isEmpty( ) ) {
			sb.append( "CKEDITOR.tools.extend( CKEDITOR.config," )
					.append( Utils.jsEncode( globalConfig ) )
					.append( ", true);" );
		}

		return ( sbInit.append( Utils.script( sb.toString( ) ) ) ).toString( );
	}

	/**
	 * Creates a string representing a set of CKEditor JavaScript base
	 * parameters.<br>
	 * These parameters include:
	 * <ul>
	 * <li>The {@code script} tag pointing to an external {@code ckeditor.js}
	 * file.</li>
	 * <li>The {@code window.CKEDITOR_BASEPATH} if an absolute path to the
	 * CKEditor folder was provided.</li>
	 * <li>The {@code window.CKEDITOR.timestamp} if a timestamp string was
	 * provided.</li>
	 * </ul>
	 * <p>
	 * The {@code CKEditor} object inserts base parameters by default, so there
	 * is no need to do it manually. What is more, the {@code CKEditor} object
	 * makes sure that only one set of these parameters is inserted into JSP.<br>
	 * If, however, the developer wishes to insert base parameters manually or
	 * with some script loading library, the {@code CKEditor} object should be
	 * informed about it. There are two <strong>independent</strong> ways to
	 * achieve that:
	 * <ol>
	 * <li>
	 * <strong>With the request scope attribute.</strong><br>
	 * The {@code CKEditor} object inserts base parameters during its
	 * initialization if the {@code ckeditor_initialized} attribute is not
	 * present in the request scope. After inserting parameters {@code CKEditor}
	 * sets {@code ckeditor_initialized} in the request scope to inform other
	 * editor instances that the initialization is done.<br>
	 * The developer can set this attribute in the request scope by putting
	 * appropriate code into the Servlet or JSP. In a JavaServer Page the
	 * programmer can use the {@code c:set} tag from the JSTL library or the
	 * scriptlet (unrecommended) before the first {@code CKEditor} object code.<br>
	 * Please note that setting this attribute will affect all editor instances
	 * present in JSP.</li>
	 * <li>
	 * <strong>With the {@code CKEditor} object property.</strong><br>
	 * {@code CKEditor} has a property {@code initialized} which, when set to
	 * {@code true}, will prevent the editor from inserting base parameters.<br>
	 * Please note that this property is instance-specific and will only affect
	 * the editor instance for which it was set.</li>
	 * </ol>
	 * 
	 * @return a string representing a set of CKEditor JavaScript base
	 *         parameters.
	 * @see #setInitialized(boolean)
	 */
	private String getInitParams( ) {
		StringBuilder sb = new StringBuilder( );
		if ( !initialized
				&& request.getAttribute( "ckeditor_initialized" ) == null ) {
			String args = "";
			if ( !Utils.isStringEmpty( timestamp ) ) {
				args += "?t=" + timestamp;
			}
			if ( !Utils.isStringEmpty( basePath ) ) {
				if ( !basePath.startsWith( ".." )
						&& !basePath.startsWith( "./" ) ) {
					sb.append( Utils.script( new StringBuilder(
							"window.CKEDITOR_BASEPATH='" ).append( basePath )
							.append( "';" ).toString( ) ) );
				}
				sb.append( Utils.createCKEditorIncJS( basePath, args ) );

			}

			if ( !Utils.isStringEmpty( timestamp ) ) {
				sb.append( ( sb.length( ) > 0 ) ? "\n" : "" )
						.append(
								Utils.script( new StringBuilder(
										"CKEDITOR.timestamp='" )
										.append( timestamp ).append( "';\n" )
										.toString( ) ) );
			}
			request.setAttribute( "ckeditor_initialized", new Boolean( true ) );
		}
		return sb.toString( );
	}

	/**
	 * Returns the global configuration object if it is found in the request,
	 * session or application scope. The object is searched under the
	 * {@code ckeditor_global_config} name.
	 * 
	 * @return the {@code CKEditorConfig} object if it is found in one of the
	 *         scopes, {@code null} otherwise.
	 * */
	private CKEditorConfig getGlobalConfig( ) {
		CKEditorConfig globalConfig = null;
		HttpSession session = request.getSession( );
		ServletContext context = session.getServletContext( );
		if ( request.getAttribute( "ckeditor_global_config" ) != null )
			globalConfig = ( CKEditorConfig ) request
					.getAttribute( "ckeditor_global_config" );
		else if ( session.getAttribute( "ckeditor_global_config" ) != null )
			globalConfig = ( CKEditorConfig ) session
					.getAttribute( "ckeditor_global_config" );
		else if ( context.getAttribute( "ckeditor_global_config" ) != null )
			globalConfig = ( CKEditorConfig ) context
					.getAttribute( "ckeditor_global_config" );
		return globalConfig;
	}

	/**
	 * Based on the enumeration marker provided, this method returns a string
	 * representing the start of the CKEditor JavaScript creation method. This
	 * string is later concatenated with the rest of the JavaScript code and
	 * inserted into the JSP.
	 * 
	 * @return a string representing the start of the CKEditor JavaScript
	 *         creation method. If none of the markers matches, {@code null} is
	 *         returned.
	 * @see Methods
	 * */
	private String getStartMethodTemplate( final CKEditor.Methods creationMethod ) {
		switch ( creationMethod ) {
		case REPLACE:
			return "CKEDITOR.replace( ";
		case REPLACE_ALL:
			return "CKEDITOR.replaceAll( ";
		case INSERT:
			return "CKEDITOR.replace( ";
		case INLINE:
			return "CKEDITOR.inline( ";
		case INLINE_ALL:
			return "CKEDITOR.inlineAll( ";
		default:
			return null;
		}
	}

	/**
	 * Creates the HTML representation of this editor instance.
	 * 
	 * @return HTML representation of this editor instance.
	 * @see #toString()
	 */
	public String createHtml( ) {
		return toString( );
	}

	/**
	 * Returns the {@code Enum} marker indicating which JavaScript method should
	 * be inserted into JSP to create CKEditor.
	 * 
	 * @return the {@code Enum} marker representing the CKEditor JavaScript
	 *         creation method.
	 */
	public Methods getCreationMethod( ) {
		return creationMethod;
	}

	/**
	 * Sets the {@code Enum} marker indicating which JavaScript method should be
	 * inserted into JSP to create CKEditor.
	 * 
	 * @param creationMethod
	 *            the {@code Enum} marker representing the CKEditor JavaScript
	 *            creation method.
	 */
	public void setCreationMethod( Methods creationMethod ) {
		this.creationMethod = creationMethod;
	}

	/**
	 * Returns the name of the CSS marker class used to identify
	 * {@code <textarea>} elements which will be replaced by editor
	 * instances.
	 * 
	 * @return a string representing the class name.
	 */
	public String getClassName( ) {
		return className;
	}

	/**
	 * Sets the name of the CSS marker class used to identify
	 * {@code <textarea>} elements which will be replaced by editor
	 * instances.
	 * 
	 * @param className
	 *            a string representing the name of the CSS marker class.
	 * 
	 */
	public void setClassName( String className ) {
		this.className = className;
	}

	/**
	 * Returns a Boolean flag indicating whether CKEditor should be created as
	 * inline or classic editor.
	 * 
	 * @return {@code true} if inline editor should be created, {@code false} if
	 *         classic editor should be created.
	 */
	public boolean isInline( ) {
		return inline;
	}

	/**
	 * Sets a Boolean flag indicating whether CKEditor should be created as
	 * inline or classic editor.
	 * 
	 * @param inline
	 *            a Boolean flag indicating whether inline or classic editor
	 *            should be created.
	 */
	public void setInline( boolean inline ) {
		this.inline = inline;
	}

	/**
	 * Returns the {@code Map} of key-value pairs representing attributes and
	 * their values for the {@code <textarea>} element that will be
	 * inserted into JSP and replaced by an editor instance.
	 * 
	 * @return the {code Map} of {@code <textarea>} attributes.
	 */
	public HashMap<String, String> getTextareaAttributes( ) {
		return textareaAttributes;
	}

	/**
	 * Sets the {@code Map} of key-value pairs representing attributes and their
	 * values for the {@code <textarea>} element that will be inserted
	 * into JSP and replaced by an editor instance.
	 * <p>
	 * The list of attributes can be found on the <a
	 * href="http://www.w3schools.com/tags/tag_textarea.asp">W3Schools</a>
	 * website.<br>
	 * Please note, however, that attributes useful for CKEditor or that have
	 * any influence on it are just {@code rows}, {@code cols}, {@code disabled}
	 * and {@code form}.
	 * 
	 * @param textareaAttributes
	 *            the {@code Map} of {@code <textarea>} attributes.
	 */
	public void setTextareaAttributes(
			HashMap<String, String> textareaAttributes ) {
		this.textareaAttributes = textareaAttributes;
	}

	/**
	 * Returns the CKEditor configuration object which stores the entire editor
	 * instance configuration.
	 * 
	 * @return the {@code CKEditorConfig} configuration object.
	 */
	public CKEditorConfig getConfig( ) {
		return config;
	}

	/**
	 * Sets the CKEditor configuration object which stores the entire editor
	 * instance configuration.
	 * 
	 * @param config
	 *            the {@code CKEditorConfig} object with the editor
	 *            configuration.
	 */
	public void setConfig( CKEditorConfig config ) {
		this.config = config;
	}

	/**
	 * Returns the name of this editor instance. The name matches the name or ID
	 * of the {@code <textarea>} element which will be replaced by an editor
	 * instance.
	 * 
	 * @return a string representing the editor instance name.
	 */
	public String getInstanceName( ) {
		return instanceName;
	}

	/**
	 * Sets the name of this editor instance. The name matches the name or ID of
	 * the {@code <textarea>} element which will be replaced by an editor
	 * instance.
	 * 
	 * @param instanceName
	 *            a string representing the editor instance name.
	 */
	public void setInstanceName( String instanceName ) {
		this.instanceName = instanceName;
	}

	/**
	 * Returns the initial CKEditor value that will be inserted into the editor
	 * instance upon its creation.
	 * 
	 * @return a string representing the initial HTML value for the editor.
	 */
	public String getValue( ) {
		return value;
	}

	/**
	 * Sets the initial CKEditor value that will be inserted into the editor
	 * instance upon its creation.
	 * 
	 * @param value
	 *            the initial HTML value for the editor.
	 */
	public void setValue( String value ) {
		this.value = value;
	}

	/**
	 * Returns the full or relative path to the CKEditor installation directory.
	 * If a full path is used, the JavaScript {@code CKEDITOR_BASEPATH} global
	 * variable will be inserted into JSP.
	 * 
	 * @return a string representing the base path.
	 */
	public String getBasePath( ) {
		return basePath;
	}

	/**
	 * Sets the full or relative path to the CKEditor installation directory. If
	 * a full path is used, the JavaScript {@code CKEDITOR_BASEPATH} global
	 * variable will be inserted into JSP.
	 * <p>
	 * If a slash ("/") is not the last character of the base path, this method
	 * will add it.
	 * 
	 * @param basePath
	 *            a string representing the base path.
	 */
	public void setBasePath( String basePath ) {
		this.basePath = Utils.appendSlash( basePath );
	}

	/**
	 * Returns the timestamp value which is used to build the URL for all
	 * resources loaded by the editor code, guaranteeing clean cache results
	 * when upgrading.
	 * 
	 * @return a string of characters representing a timestamp.
	 */
	public String getTimestamp( ) {
		return timestamp;
	}

	/**
	 * Sets the timestamp value which is used to build the URL for all resources
	 * loaded by the editor code, guaranteeing clean cache results when
	 * upgrading.
	 * 
	 * @param timestamp
	 *            a string of characters representing a timestamp.
	 */
	public void setTimestamp( final String timestamp ) {
		this.timestamp = timestamp;
	}

	/**
	 * Returns JavaScript events that will be assigned to this particular editor
	 * instance.
	 * 
	 * @return events the {@code EventHandler}.
	 */
	public EventHandler getEvents( ) {
		return events;
	}

	/**
	 * Sets JavaScript events that will be assigned to this particular editor
	 * instance.
	 * 
	 * @param events
	 *            the {@code EventHandler}.
	 */
	public void setEvents( final EventHandler events ) {
		this.events = events;
	}

	/**
	 * Returns JavaScript events that will be assigned to all editor instances.
	 * Depending on scope, these events might be assigned to all editors present
	 * on JSP (request scope), all editors available for the user during the
	 * session (session scope) or all editor instances in the application
	 * (application scope).
	 * 
	 * @return globalEvents the {@code GlobalEventHandler}.
	 */
	public GlobalEventHandler getGlobalEvents( ) {
		return globalEvents;
	}

	/**
	 * Sets JavaScript events that will be assigned to all editor instances.
	 * Depending on scope, these events might be assigned to all editors present
	 * on JSP (request scope), all editors available for the user during the
	 * session (session scope) or all editor instances in the application
	 * (application scope).
	 * 
	 * @param globalEvents
	 *            the {@code GlobalEventHandler}.
	 */
	public void setGlobalEvents( final GlobalEventHandler globalEvents ) {
		this.globalEvents = globalEvents;
	}

	/**
	 * Returns a flag informing the {@code CKEditor} object whether base
	 * parameters like the {@code ckeditor.js} script were already inserted into
	 * JSP.
	 * 
	 * @return a Boolean flag informing whether CKEditor was initialized with
	 *         base parameters.
	 */
	public final boolean isInitialized( ) {
		return initialized;
	}

	/**
	 * Sets a flag informing the {@code CKEditor} object whether base parameters
	 * like the {@code ckeditor.js} script were already inserted into the JSP.
	 * 
	 * @param initialized
	 *            a Boolean flag informing whether CKEditor was initialized with
	 *            base parameters.
	 */
	public final void setInitialized( final boolean initialized ) {
		this.initialized = initialized;
	}
}