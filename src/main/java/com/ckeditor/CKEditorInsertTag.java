/*
 * Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */
package com.ckeditor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

/**
 * The {@code CKEditorInsertTag} class is used to create the JavaScript
 * {@code replace} or {@code inline} methods which replace the specified
 * {@code <textarea>} element on JSP. What makes this tag different from the
 * {@code CKEditorReplaceTag} is that it does not need the HTML
 * {@code <textarea>} element to exist &mdash; the {@code <textarea>} is created
 * on the fly and inserted into JSP. The creation of this element can be based
 * on properties provided as the tag attribute, but this is not required. If the
 * user does not specify any {@code <textarea>} properties, the {@code insert}
 * tag will use a set of predefined properties.
 * <p>
 * <strong>Usage on JSP:</strong>
 * 
 * <pre>
 * &lt;ckeditor:editor basePath="${pageContext.servletContext.contextPath}/ckeditor/" editor="editor1" value="${cke_init_value}" inline="true"/&gt;
 * 
 * &lt;ckeditor:editor basePath="${pageContext.servletContext.contextPath}/ckeditor/" editor="editor1" textareaAttributes="${cke_textarea_attrs}"/&gt;
 * </pre>
 */
public class CKEditorInsertTag extends CKEditorTag {

	/**
	 * Private logger.
	 */
	private static final Logger ckeditorInsertTagLogger = Logger
			.getLogger( CKEditorInsertTag.class.getName( ) );
	/**
	 * Serial identifier.
	 */
	private static final long serialVersionUID = 1316780332328233835L;

	/**
	 * The name of this editor instance which matches the ID or name of the
	 * {@code <textarea>} element on which the editor will be created.
	 */
	private String editor;

	/**
	 * Initial value which will be inserted into the editor upon its creation.
	 */
	private String value;

	/**
	 * Attributes for the {@code <textarea>} element which will be inserted into
	 * JSP and replaced by the editor.
	 */
	private Map<String, String> textareaAttributes;

	/**
	 * Flag indicating whether classic or inline editor should be created.
	 */
	private boolean inline;

	/**
	 * Creates the {@code CKEditorInsertTag} object.
	 */
	public CKEditorInsertTag ( ) {
		textareaAttributes = new HashMap<String, String>( );
		editor = "";
		value = "";
	}

	/**
	 * Inserts the HTML {@code <textarea>} element into JSP. The
	 * {@code <textarea>} is created based on the {@code Map} of properties (
	 * {@code <textarea>} attribute names and values) provided as the attribute
	 * of this tag or based on the tag's predefined {@code Map} of properties.
	 * The {@code <textarea>} element is then replaced with an editor instance.
	 * 
	 * @return the {@code EVAL_PAGE} integer flag from the
	 *         {@code javax.servlet.jsp.tagext.Tag} class. This flag means that
	 *         JSP can be further evaluated.
	 */
	@Override
	public int doStartTag( ) {
		JspWriter out = pageContext.getOut( );
		try {
			out.write( Utils.createTextareaTag( editor, value,
					textareaAttributes ) );
		} catch ( IOException ie ) {
			ckeditorInsertTagLogger.log( Level.SEVERE,
					"Could not create CKEditorInsertTag.", ie );
			try {
				HttpServletResponse resp = ( HttpServletResponse ) pageContext
						.getResponse( );
				resp.reset( );
				resp.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Problem with tag creation." );
			} catch ( IOException ioe ) {
				ckeditorInsertTagLogger.log( Level.SEVERE,
						"Could not return response to the client.", ioe );
			}
		}
		return EVAL_PAGE;
	}

	/**
	 * Creates the JavaScript {@code replace} (default setting) or
	 * {@code inline} creation method based on the {@code inline} flag. This
	 * method also creates the custom configuration of the editor instance
	 * provided that the {@code config} parameter is not {@code null}.
	 * 
	 * @param config
	 *            the {@code CKEditorConfig} object used to extend the
	 *            configuration of this editor instance.
	 * 
	 * @return the JavaScript representation of this tag.
	 */
	@Override
	protected String getTagOutput( final CKEditorConfig config ) {
		StringBuilder sb = new StringBuilder( );

		if ( pageContext.getAttribute( "ckeditor_disable_auto_inline_set" ) == null
				&& inline ) {
			sb.append( "CKEDITOR.disableAutoInline = true;\n" );
			pageContext.setAttribute( "ckeditor_disable_auto_inline_set",
					new Boolean( true ) );
		}

		if ( config != null && !config.isEmpty( ) ) {
			sb.append( getEditorMethod( ) ).append( "'" ).append( editor )
					.append( "', " ).append( Utils.jsEncode( config ) )
					.append( ");\n" );
			return sb.toString( );
		} else {
			sb.append( getEditorMethod( ) ).append( "'" ).append( editor )
					.append( "');\n" );
			return sb.toString( );
		}
	}

	/**
	 * Based on the {@code inline} flag this method returns a {@code String}
	 * representing the JavaScript (@code inline} or {@code replace} method.
	 * 
	 * @return {@code String} representing an appropriate CKEditor JavaScript
	 *         creation method.
	 */
	private String getEditorMethod( ) {
		return inline ? "CKEDITOR.inline( " : "CKEDITOR.replace( ";
	}

	/**
	 * Sets the name of this editor instance which matches the ID or name of the
	 * {@code <textarea>} element which will be replaced by the editor.
	 * 
	 * @param editor
	 *            the name of the editor instance which matches the ID or name
	 *            of the {@code <textarea>} element.
	 */
	public final void setEditor( final String editor ) {
		this.editor = editor;
	}

	/**
	 * Sets the initial value which will be inserted into the editor upon its
	 * creation.
	 * 
	 * @param value
	 *            the initial HTML value for the editor.
	 */
	public final void setValue( final String value ) {
		this.value = value;
	}

	/**
	 * Sets the {@code Map} of key-value pairs representing attribute names and
	 * values for the {@code <textarea>} element which will be inserted into JSP
	 * and replaced with the editor.
	 * <p>
	 * The list of the attributes can be found on the <a
	 * href="http://www.w3schools.com/tags/tag_textarea.asp">W3Schools</a>
	 * website.<br>
	 * Please note, however, that attributes useful for CKEditor or that have
	 * any influence on it are just {@code rows}, {@code cols}, {@code disabled}
	 * and {@code form}.
	 * 
	 * @param textareaAttr
	 *            the {@code Map} of key-value pairs representing the
	 *            {@code <textarea>} attribute names and values.
	 */
	public final void setTextareaAttributes(
			final Map<String, String> textareaAttr ) {
		this.textareaAttributes = textareaAttr;
	}

	/**
	 * Sets a flag indicating whether classic or inline editor should be
	 * created.
	 * 
	 * @param inline
	 *            a {@code Boolean} flag indicating which type of editor should
	 *            be inserted into JSP.
	 */
	public final void setInline( final boolean inline ) {
		this.inline = inline;
	}

	/**
	 * Returns the name of this editor instance. The name matches the ID or name
	 * of the {@code <textarea>} element which will be replaced by the editor.
	 * 
	 * @return the name of this editor instance.
	 */
	@Override
	protected String getCKEditorName( ) {
		return this.editor;
	}
}
