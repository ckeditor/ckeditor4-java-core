/*
 * Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */
package com.ckeditor;

/**
 * The {@code CKEditorInlineAllTag} class is used to create the JavaScript
 * {@code inlineAll} method which creates inline editor instances inside all
 * editable elements on JSP.
 * <p>
 * <strong>Usage on JSP:</strong>
 *
 * <pre>
 * &lt;ckeditor:inlineAll basePath="${pageContext.servletContext.contextPath}/ckeditor/"/&gt;
 * </pre>
 */
public class CKEditorInlineAllTag extends CKEditorTag {

    /**
     * Serial identifier.
     */
    private static final long serialVersionUID = -3921707324940967085L;

    /**
     * Creates a {@code CKEditorInlineAllTag} object.
     */
    public CKEditorInlineAllTag() {

    }

    /**
     * Creates the JavaScript {@code inlineAll} creation method. Provided that
     * the configuration object, passed as the parameter, is not {@code null},
     * this method also creates JavaScript code that extends the global CKEditor
     * configuration which is applicable to all editor instances available on
     * JSP.
     *
     * @param config the {@code CKEditorConfig} object used to extend the global
     * CKEditor configuration which is applicable to all instances available on
     * JSP.
     *
     * @return the JavaScript representation of this tag.
     */
    @Override
    protected String getTagOutput(final CKEditorConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append("CKEDITOR.inlineAll();\n ");

        if (config != null && !config.isEmpty()) {
            sb.append("CKEDITOR.tools.extend( CKEDITOR.config,")
                    .append(Utils.jsEncode(config)).append(", true);\n");
        }
        return sb.toString();
    }

    /**
     * This method should return the name of the editor instance but since this
     * tag represents the {@code inlineAll} JavaScript method which builds
     * editors inside all {@code contenteditable} elements found on JSP, it is
     * not possible to return a particular CKEditor instance name. As a result
     * {@code null} is returned.
     *
     * @return {@code null} because it is not possible to return the name for a
     * single editor instance.
     */
    @Override
    protected String getCKEditorName() {
        return null;
    }
}
