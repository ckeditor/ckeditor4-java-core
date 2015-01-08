/*
 * Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */
package com.ckeditor;

/**
 * The {@code CKEditorReplaceAllTag} class is used to create the JavaScript
 * {@code replaceAll} method which replaces either all {@code <textarea>}
 * elements on JSP or all {@code <textarea>} elements whose CSS class matches
 * the class name provided as one of the attributes of this tag.
 * <p>
 * <strong>Usage on JSP:</strong>
 *
 * <pre>
 * &lt;ckeditor:replaceAll basePath="${pageContext.servletContext.contextPath}/ckeditor/"/&gt;
 *
 * &lt;ckeditor:replaceAll basePath="${pageContext.servletContext.contextPath}/ckeditor/" className="cke-class" /&gt;
 *
 * &lt;ckeditor:replaceAll basePath="${pageContext.servletContext.contextPath}/ckeditor/" config="${cke_config}" /&gt;
 * </pre>
 */
public class CKEditorReplaceAllTag extends CKEditorTag {

    /**
     * Serial identifier.
     */
    private static final long serialVersionUID = -7331873466295495480L;

    /**
     * The CSS class used to identify {@code <textarea>} elements on JSP which
     * will be replaced by editor instances.
     */
    private String className;

    /**
     * Creates the {@code CKEditorReplaceAllTag} object.
     */
    public CKEditorReplaceAllTag() {
        this.className = "";
    }

    /**
     * Creates the JavaScript {@code replaceAll} creation method which replaces
     * either all {@code <textarea>} elements on JSP or all {@code <textarea>}
     * elements whose CSS class matches the class name provided as an attribute
     * of this tag.<br>
     * Additionally, provided that the configuration object passed as parameter
     * is not {@code null}, this method also creates the JavaScript code that
     * extends the CKEditor configuration which is applicable to all editor
     * instances available on JSP.
     *
     * @param config the {@code CKEditorConfig} object used to extend CKEditor
     * configuration which is applicable to all instances available on JSP.
     *
     * @return the JavaScript representation of this tag.
     */
    @Override
    protected String getTagOutput(final CKEditorConfig config) {
        StringBuilder sb = new StringBuilder();

        if (config == null || config.isEmpty()) {
            if (className == null || "".equals(className)) {
                sb.append("CKEDITOR.replaceAll();\n");
                return sb.toString();
            } else {
                sb.append("CKEDITOR.replaceAll( '").append(className)
                        .append("' );\n");
                return sb.toString();
            }
        } else {
            sb.append("CKEDITOR.replaceAll( function(textarea, config) {\n");
            if (className != null && !"".equals(className)) {
                sb.append("	var classRegex = new RegExp('(?:^| )' + '")
                        .append(className)
                        .append("' + '(?:$| )');\n")
                        .append("	if (!classRegex.test(textarea.className))\n")
                        .append("		return false;\n");
            }
            sb.append("CKEDITOR.tools.extend( config,")
                    .append(Utils.jsEncode(config))
                    .append(", true);} );\n");
            return sb.toString();
        }
    }

    /**
     * Sets the CSS class used to identify {@code <textarea>} elements that will
     * be replaced by CKEditor instances.
     *
     * @param className the CSS class name that identifies {@code <textarea>}
     * elements to be replaced on JSP.
     */
    public final void setClassName(final String className) {
        this.className = className;
    }

    /**
     * This method should return the name of the editor instance but since this
     * tag represents the {@code replaceAll} JavaScript method which replaces
     * all {@code <textarea>} elements (or all {@code <textarea>} elements with
     * the specified CSS class name) found on JSP with CKEditor instances, it is
     * not possible to return a particular CKEditor instance name. As a result
     * {@code null} is returned.
     *
     * @return {@code null}, because it is not possible to return the name of a
     * single editor instance.
     */
    @Override
    protected String getCKEditorName() {
        return null;
    }

}
