/*
 * Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */
package com.ckeditor;

/**
 * The {@code CKEditorInlineTag} class is used to create the JavaScript
 * {@code inline} method which creates an inline editor instance inside the
 * specified editable HTML element on JSP. This element is identifiable by an ID
 * which is passed as one of the attributes of this tag.
 * <p>
 * <strong>Usage on JSP:</strong>
 *
 * <pre>
 * &lt;div id="editable2" contenteditable="true"&gt;Sample text.&lt;/div&gt;
 * &lt;ckeditor:inline inline="editable2" basePath="${pageContext.servletContext.contextPath}/ckeditor/" config="${cke_config}"/&gt;
 * </pre>
 */
public class CKEditorInlineTag extends CKEditorTag {

    /**
     * Serial identifier.
     */
    private static final long serialVersionUID = 4116780332237893136L;

    /**
     * The name of this editor instance which matches the ID of the editable
     * HTML element.
     */
    private String inline;

    /**
     * Creates the {@code CKEditorInlineTag} object.
     */
    public CKEditorInlineTag() {
        inline = "";
    }

    /**
     * Creates the JavaScript {@code inline} creation method. Additionally, this
     * method also creates the {@code CKEDITOR.disableAutoInline = true;}
     * variable that tells the editor not to replace all editable HTML elements
     * on JSP except for the one whose ID was specified.
     *
     * @param config the {@code CKEditorConfig} object used to extend the
     * configuration of this editor instance.
     *
     * @return the JavaScript representation of this tag.
     */
    @Override
    protected String getTagOutput(final CKEditorConfig config) {
        StringBuilder sb = new StringBuilder();
        if (pageContext.getAttribute("ckeditor_disable_auto_inline_set") == null) {
            sb.append("CKEDITOR.disableAutoInline = true;\n");
            pageContext.setAttribute("ckeditor_disable_auto_inline_set",
                    new Boolean(true));
        }

        if (config != null && !config.isEmpty()) {
            return sb.append("CKEDITOR.inline( '").append(inline)
                    .append("', ").append(Utils.jsEncode(config))
                    .append(");").toString();
        } else {
            return sb.append("CKEDITOR.inline( '").append(inline)
                    .append("' );").toString();
        }
    }

    /**
     * Sets the name of this editor instance. The name matches the ID of the
     * editable HTML element inside which the editor will be created.
     *
     * @param inline the name of the editor instance which matches the ID of the
     * editable HTML element.
     */
    public final void setInline(final String inline) {
        this.inline = inline;
    }

    /**
     * Returns the name of this editor instance. The name matches the ID of the
     * editable HTML element inside which the editor will be created.
     *
     * @return the name of this editor instance.
     */
    @Override
    protected String getCKEditorName() {
        return this.inline;
    }
}
