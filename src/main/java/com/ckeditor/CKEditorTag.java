/*
 * Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */
package com.ckeditor;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The {@code CKEditorTag} class is the base class for all CKEditor tags.
 */
public abstract class CKEditorTag extends TagSupport {

    /**
     * Serial identifier.
     */
    private static final long serialVersionUID = -5642419066547779817L;
    /**
     * Full or relative path to the CKEditor installation directory. If a full
     * path is used, the {@code CKEDITOR_BASEPATH} global variable will be
     * inserted into JSP.
     */
    private String basePath;
    /**
     * Timestamp value used to build the URL for all resources loaded by the
     * editor code, guaranteeing clean cache results when upgrading.
     */
    private String timestamp;
    /**
     * The Boolean flag informing whether CKEditor was initialized with base
     * parameters.
     */
    private boolean initialized;
    /**
     * CKEditor configuration object. It stores the entire editor instance
     * configuration.
     */
    private CKEditorConfig config;
    /**
     * JavaScript events that will be assigned to this particular editor
     * instance.
     */
    private EventHandler events;
    /**
     * JavaScript events that will be assigned to all editor instances.
     * Depending on scope, these events might be assigned to all editors present
     * on JSP (page or request scope), all editors available for the user during
     * the session (session scope) or all editor instances in an application
     * (application scope).
     */
    private GlobalEventHandler globalEvents;

    /**
     * Creates the {@code CKEditorTag} objects and sets all properties to their
     * default values.
     */
    public CKEditorTag() {
        timestamp = null;
        basePath = "";
        initialized = false;
        config = null;
        events = null;
    }

    /**
     * Creates the HTML representation of the CKEditor tag.
     *
     * @return {@code EVAL_PAGE} integer flag from the
     * {@code javax.servlet.jsp.tagext.Tag} class. This flag means that JSP can
     * be further evaluated.
     */
    @Override
    public int doEndTag() throws JspException {
        JspWriter out = pageContext.getOut();
        configureContextParams();
        try {
            String output = "";
            if (!initialized && !isInitializedParam()) {
                out.write(init());
            }

            if (globalEvents != null) {
                output += globalEvents.returnGlobalEvents();
            }

            CKEditorConfig globalConfig = getGlobalConfig();

            if (events != null) {
                if (config == null) {
                    config = new CKEditorConfig();
                }
                config = config.configSettings(this.events);
            }
            if (globalConfig != null) {
                config = globalConfig.mergeConfigValues(config);
            }
            output += getTagOutput(config);

            out.write(Utils.script(output));
        } catch (Exception e) {
            try {
                HttpServletResponse resp = (HttpServletResponse) pageContext
                        .getResponse();
                resp.reset();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Problem with tag creation.");
            } catch (IOException e1) {
                throw new JspException(e1);
            }
        }
        return EVAL_PAGE;
    }

    /**
     * Returns the global configuration object if it is found in the page,
     * request, session or application scope. The object is searched under the
     * {@code ckeditor_global_config} name.
     *
     * @return the {@code CKEditorConfig} object if it is found in one of the
     * scopes, {@code null} otherwise.
     *
     */
    private CKEditorConfig getGlobalConfig() {
        CKEditorConfig globalConfig = null;
        ServletRequest request = pageContext.getRequest();
        HttpSession session = pageContext.getSession();
        ServletContext context = pageContext.getServletContext();

        if (pageContext.getAttribute("ckeditor_global_config") != null) {
            globalConfig = (CKEditorConfig) pageContext
                    .getAttribute("ckeditor_global_config");
        } else if (request.getAttribute("ckeditor_global_config") != null) {
            globalConfig = (CKEditorConfig) request
                    .getAttribute("ckeditor_global_config");
        } else if (session.getAttribute("ckeditor_global_config") != null) {
            globalConfig = (CKEditorConfig) session
                    .getAttribute("ckeditor_global_config");
        } else if (context.getAttribute("ckeditor_global_config") != null) {
            globalConfig = (CKEditorConfig) context
                    .getAttribute("ckeditor_global_config");
        }
        return globalConfig;
    }

    /**
     * Gets the file browser-specific configuration parameters from
     * {@code pageContext} and puts them into the tag's {@code CKEditorConfig}
     * object. This method is used for example in the CKFinder integration.
     * <p>
     * <strong>How it works:</strong><br>
     * CKFinder or any other file browser puts the
     * {@code filebrowserBrowseXYZUrl} and {@code filebrowserUploadXYZUrl}
     * properties into a {@code Map} which is then put into another {@code Map}.
     * The second {@code Map} is put in the {@code pageContext} scope under the
     * attribute named {@code ckeditor-params}.
     *
     * <pre>
     * Map&lt;String, Map&lt;String, String&gt;&gt; attr = new HashMap&lt;String, Map&lt;String, String&gt;&gt;();
     * Map&lt;String, String&gt; params = new HashMap&lt;String, String&gt;();
     * params.put("filebrowserBrowseUrl", buildBrowseUrl(null));
     * params.put("filebrowserUploadUrl", buildUploadUrl("Files"));
     * ...
     * if (editor == null || editor.equals("")) {
     * 	attr.put("*", params);
     * } else {
     * 	attr.put(editor, params);
     * }
     * pageContext.setAttribute("ckeditor-params", attr);
     * </pre>
     *
     * All CKEditor tag does next is getting these parameters from
     * {@code pageContext} and putting them into tag's {@code CKEditorConfig}
     * object.
     */
    private void configureContextParams() {
        if (pageContext.getAttribute("ckeditor-params") != null) {
            @SuppressWarnings("unchecked")
            Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) pageContext
                    .getAttribute("ckeditor-params");
            if (map.get(getCKEditorName()) != null) {
                parseParamsFromContext(map.get(getCKEditorName()));
            } else if (map.get("*") != null) {
                parseParamsFromContext(map.get("*"));
            }
        }
    }

    /**
     * Rewrites configuration options from the {@code Map} provided as a
     * parameter into the tag's {@code CKEditorConfig} object.
     *
     * @param map a {@code Map} with attributes to be written into the tag's
     * {@code CKEditorConfig} object.
     */
    private void parseParamsFromContext(final Map<String, String> map) {
        if (!map.isEmpty() && config == null) {
            config = new CKEditorConfig();
        }
        for (Map.Entry<String, String> configEntry : map.entrySet()) {
            config.addConfigValue(configEntry.getKey(),
                    map.get(configEntry.getValue()));
        }
    }

    /**
     * Checks if the {@code ckeditor.js} script as well as other base parameters
     * were inserted into JSP. This check is performed with the
     * {@code ckeditor_initialized} attribute. If this attribute is present in
     * the page or request scope, it means that the editor was initialized, so
     * {@code true} is returned. If the attribute is not present, this method
     * sets it in the page scope and returns {@code false}.
     *
     * @return {@code true} if {@code ckeditor_initialized} is present in the
     * page or request scope, {@code false} otherwise.
     * @see #init()
     */
    private boolean isInitializedParam() {
        if (pageContext.getAttribute("ckeditor_initialized") != null
                || pageContext.getRequest().getAttribute(
                        "ckeditor_initialized") != null) {
            return true;
        } else {
            pageContext.setAttribute("ckeditor_initialized",
                    new Boolean(true));
            return false;
        }
    }

    /**
     * Returns the standard tag output which is an appropriate CKEditor
     * JavaScript creation method (based on the class implementing it) and in
     * some cases HTML.
     *
     * @param config the {@code CKEditorConfig} object storing configuration for
     * the editor instance.
     * @return a string representing the standard output of the tag.
     */
    protected abstract String getTagOutput(final CKEditorConfig config);

    /**
     * Creates a {@code String} representing the set of CKEditor JavaScript base
     * parameters.<br>
     * These parameters include:
     * <ul>
     * <li>The {@code script} tag pointing to an external {@code ckeditor.js}
     * file.</li>
     * <li>{@code window.CKEDITOR_BASEPATH} if an absolute path to the CKEditor
     * directory was provided.</li>
     * <li>{@code window.CKEDITOR.timestamp} if a timestamp string was
     * provided.</li>
     * </ul>
     * <p>
     *
     * The CKEditor tag inserts base parameters by default so there is no need
     * to do it manually. What is more, the CKEditor tag makes sure that only
     * one set of these parameters is inserted into JSP.<br>
     * If, however, the developer wishes to insert base parameters manually or
     * with a script loading library, the CKEditor tag should be informed about
     * it. There are two <strong>independent</strong> ways to achieve that:
     * <ol>
     * <li>
     * <strong>With page scope attribute.</strong><br>
     * The CKEditor tag inserts base parameters during its initialization if the
     * {@code ckeditor_initialized} attribute is not present in the
     * {@code pageContext} or {@code request} scope. After inserting parameters
     * CKEditor sets {@code ckeditor_initialized} in the {@code pageContext}
     * scope to inform other editor instances that initialization is done.<br>
     * Developer can set this attribute in page or request scope by putting
     * appropriate code into JSP (for example with the {@code c:set} tag from
     * the JSTL library) before the first CKEditor tag.<br>
     * Please note that setting this attribute will affect all editor instances
     * present in JSP.</li>
     * <li>
     * <strong>With the CKEditor tag attribute.</strong><br>
     * CKEditor has the {@code initialized} attribute which, when set to
     * {@code true}, will prevent the editor from inserting base parameters.<br>
     * Please note that this attribute is instance-specific and will only affect
     * the editor instance for which it was set.</li>
     * </ol>
     *
     * @return a string representing the set of CKEditor JavaScript base
     * parameters.
     *
     */
    protected String init() {
        StringBuilder out = new StringBuilder();
        String args = "";
        String ckeditorPath = getBasePath();
        if (timestamp != null) {
            args += "?t=" + timestamp;
        }
        if (!ckeditorPath.startsWith("..")
                && !ckeditorPath.startsWith("./")) {
            out.append(Utils.script(new StringBuilder(
                    "window.CKEDITOR_BASEPATH='").append(basePath)
                    .append("';\n").toString()));
        }
        out.append(Utils.createCKEditorIncJS(ckeditorPath, args));

        StringBuilder extraCode = new StringBuilder();
        if (timestamp != null) {
            extraCode.append((extraCode.length() > 0) ? "\n" : "")
                    .append("CKEDITOR.timestamp='").append(timestamp)
                    .append("';\n");
        }
        if (extraCode.length() > 0) {
            out.append(Utils.script(extraCode.toString()));
        }
        return out.toString();
    }

    /**
     * Returns the full or relative path to the CKEditor installation directory.
     * If a full path is used, the JavaScript {@code CKEDITOR_BASEPATH} global
     * variable will be inserted into JSP.
     *
     * @return a string representing the {@code basePath} (with the slash
     * character at the end).
     */
    private String getBasePath() {
        return basePath;
    }

    /**
     * Sets the full or relative path to the CKEditor installation directory. If
     * a full path is used, the JavaScript {@code CKEDITOR_BASEPATH} global
     * variable will be inserted into JSP.
     * <p>
     * If slash ("/") is not the last character of the base path, this method
     * will add it.
     *
     * @param basePath a string representing the {@code basePath}.
     */
    public final void setBasePath(final String basePath) {
        this.basePath = Utils.appendSlash(basePath);
    }

    /**
     * Returns the timestamp value which is used to build the URL for all
     * resources loaded by the editor code, guaranteeing clean cache results
     * when upgrading.
     *
     * @return a string of characters representing the timestamp.
     */
    public final String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp value which is used to build the URL for all resources
     * loaded by the editor code, guaranteeing clean cache results when
     * upgrading.
     *
     * @param timestamp a string of characters representing the timestamp.
     */
    public final void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns a flag informing the CKEditor tag whether base parameters like
     * the {@code ckeditor.js} script have already been inserted into JSP.
     *
     * @return the Boolean flag informing whether CKEditor was initialized with
     * base parameters.
     * @see #init()
     */
    public final boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets a flag informing the CKEditor tag whether base parameters like the
     * {@code ckeditor.js} script were already inserted into JSP.
     *
     * @param initialized the Boolean flag informing whether CKEditor was
     * initialized with base parameters.
     * @see #init()
     */
    public final void setInitialized(final boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Returns JavaScript events that will be assigned to all editor instances.
     * Depending on scope, these events might be assigned to all editors present
     * on JSP (page or request scope), all editors available for the user during
     * the session (session scope) or all editor instances in the application
     * (application scope).
     *
     * @return globalEvents the {@code GlobalEventHandler}.
     */
    public final GlobalEventHandler getGlobalEvents() {
        return globalEvents;
    }

    /**
     * Sets JavaScript events that will be assigned to all editor instances.
     * Depending on scope, these events might be assigned to all editors present
     * on JSP (page or request scope), all editors available for the user during
     * the session (session scope) or all editor instances in the application
     * (application scope).
     *
     * @param globalEvents the {@code GlobalEventHandler}.
     */
    public final void setGlobalEvents(final GlobalEventHandler globalEvents) {
        this.globalEvents = globalEvents;
    }

    /**
     * Returns the CKEditor configuration object which stores the entire editor
     * instance configuration.
     *
     * @return the {@code CKEditorConfig} configuration object.
     */
    public final CKEditorConfig getConfig() {
        return config;
    }

    /**
     * Sets the configuration of this editor instance.
     *
     * @param config the {@code CKEditorConfig} configuration object storing the
     * entire editor instance configuration.
     */
    public final void setConfig(final CKEditorConfig config) {
        this.config = config;
    }

    /**
     * Returns JavaScript events that will be assigned to this particular editor
     * instance.
     *
     * @return events the {@code EventHandler}.
     */
    public final EventHandler getEvents() {
        return events;
    }

    /**
     * Sets JavaScript events that will be assigned to this particular editor
     * instance.
     *
     * @param events the {@code EventHandler}.
     */
    public final void setEvents(final EventHandler events) {
        this.events = events;
    }

    /**
     * Returns the name of this CKEditor instance.
     *
     * @return a string representing the name of this CKEditor instance.
     */
    protected abstract String getCKEditorName();
}
