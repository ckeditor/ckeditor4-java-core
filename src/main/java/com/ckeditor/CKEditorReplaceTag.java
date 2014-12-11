/*
 * Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */
package com.ckeditor;

/**
 * The {@code CKEditorReplaceTag} class is used to create the JavaScript
 * {@code replace} method which replaces the specified {@code <textarea>}
 * element on JSP. This {@code <textarea>} is identifiable by the ID or name
 * which is passed as one of the attributes of this tag.
 * <p>
 * <strong>Usage on JSP:</strong>
 *
 * <pre>
 * &lt;textarea cols="80" id="editor1" name="editor1" rows="10"&gt;Sample text &lt;/textarea&gt;
 * &lt;ckeditor:replace replace="editor1" basePath="${pageContext.servletContext.contextPath}/ckeditor/" config="${cke_config}" globalEvents="${cke_global_events}"/&gt;
 * </pre>
 */
public class CKEditorReplaceTag extends CKEditorTag {

    /**
     * Serial identifier.
     */
    private static final long serialVersionUID = 1316780332328233835L;

    /**
     * The name of this editor instance which matches the ID or name of the
     * {@code <textarea>} element which will be replaced by the editor.
     */
    private String replace;

    /**
     * Creates the {@code CKEditorReplaceTag} object.
     */
    public CKEditorReplaceTag() {
        replace = "";
    }

    /**
     * Creates the JavaScript {@code replace} creation method. This method also
     * creates the custom configuration of the editor instance provided that the
     * {@code config} parameter is not {@code null}.
     *
     * @param config the {@code CKEditorConfig} object used to extend the
     * configuration of this editor instance.
     *
     * @return the JavaScript representation of this tag.
     */
    @Override
    protected String getTagOutput(final CKEditorConfig config) {
        StringBuilder sb = new StringBuilder();
        if (config != null && !config.isEmpty()) {
            sb.append("CKEDITOR.replace( '").append(replace).append("', ")
                    .append(Utils.jsEncode(config)).append(");\n");
            return sb.toString();
        } else {
            sb.append("CKEDITOR.replace( '").append(replace)
                    .append("' );\n");
            return sb.toString();
        }
    }

    /**
     * Sets the name of this editor instance. The name matches the ID or name of
     * the {@code <textarea>} which will be replaced by an editor instance.
     *
     * @param replace the name of the editor instance which matches the ID or
     * name of the {@code <textarea>} element.
     */
    public final void setReplace(final String replace) {
        this.replace = replace;
    }

    /**
     * Returns the name of this editor instance. The name matches the ID or name
     * of the {@code <textarea>} element which will be replaced by an editor
     * instance.
     *
     * @return the name of this editor instance.
     */
    @Override
    protected String getCKEditorName() {
        return this.replace;
    }

}
