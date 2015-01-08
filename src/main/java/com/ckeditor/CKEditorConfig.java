/*
 * Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */
package com.ckeditor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The {@code CKEditorConfig} class is used to create a set of configuration
 * options for a single editor instance. The "configuration options" or
 * "configuration parameters" terms refer to the {@code Map} of key-value pairs
 * where each pair represents a single configuration entry.
 */
public class CKEditorConfig implements Cloneable {

    /**
     * {@code Map} storing the set of key-value pairs representing CKEditor
     * configuration options.
     */
    private Map<String, Object> config;

    /**
     * Creates the {@code CKEditorConfig} object and initializes the {@code Map}
     * storing editor instance configuration options.
     */
    public CKEditorConfig() {
        config = new HashMap<String, Object>();
    }

    /**
     * Adds a {@code Number} parameter to the editor configuration.<br>
     * <strong>Usage:</strong>
     *
     * <pre>
     * CKEditorConfig config = new CKEditorConfig( );
     * config.addConfigValue( &quot;width&quot;, 100 );
     * config.addConfigValue( &quot;dialog_backgroundCoverOpacity&quot;, 0.7 );
     * </pre>
     *
     * @param key a string representing the configuration parameter name.
     * @param value a string representing the configuration parameter value.
     */
    public void addConfigValue(final String key, final Number value) {
        config.put(key, value);
    }

    /**
     * Adds a {@code String} parameter to the editor configuration.<br>
     * <strong>Usage:</strong>
     *
     * <pre>
     * CKEditorConfig config = new CKEditorConfig( );
     * config.addConfigValue( &quot;baseHref&quot;, &quot;http://www.example.com/path/&quot; );
     * config.addConfigValue( &quot;toolbar&quot;, &quot;[[ 'Source', '-', 'Bold', 'Italic' ]]&quot; );
     * </pre>
     *
     * @param key a string representing the configuration parameter name.
     * @param value a string representing the configuration parameter value.
     */
    public void addConfigValue(final String key, final String value) {
        config.put(key, value);
    }

    /**
     * Adds a {@code Map} parameter to the editor configuration.<br>
     * <strong>Usage:</strong>
     *
     * <pre>
     * CKEditorConfig config = new CKEditorConfig( );
     * Map&lt;String, Object&gt; map = new HashMap&lt;String, Object&gt;( );
     * map.put( &quot;element&quot;, &quot;span&quot; );
     * map.put( &quot;styles&quot;, &quot;{'background-color' : '#(color)'}&quot; );
     *
     * config.addConfigValue( &quot;colorButton_backStyle&quot;, map );
     * </pre>
     *
     * @param key a string representing the configuration parameter name.
     * @param value a string representing the configuration parameter value.
     */
    public void addConfigValue(final String key,
            final Map<String, ? extends Object> value) {
        config.put(key, value);
    }

    /**
     * Adds a {@code List} parameter to the editor configuration.<br>
     * <strong>Usage:</strong>
     *
     * <pre>
     * CKEditorConfig config = new CKEditorConfig( );
     * List&lt;List&lt;String&gt;&gt; list = new ArrayList&lt;List&lt;String&gt;&gt;( );
     * List&lt;String&gt; subList = new ArrayList&lt;String&gt;( );
     * subList.add( &quot;Source&quot; );
     * subList.add( &quot;-&quot; );
     * subList.add( &quot;Bold&quot; );
     * subList.add( &quot;Italic&quot; );
     * list.add( subList );
     * config.addConfigValue( &quot;toolbar&quot;, list );
     * </pre>
     *
     * @param key a string representing the configuration parameter name.
     * @param value a string representing the configuration parameter value.
     */
    public void addConfigValue(final String key,
            final List<? extends Object> value) {
        config.put(key, value);
    }

    /**
     * Adds a {@code Boolean} parameter to the editor configuration.<br>
     * <strong>Usage:</strong>
     *
     * <pre>
     * CKEditorConfig config = new CKEditorConfig( );
     * config.addConfigValue( &quot;autoUpdateElement&quot;, true );
     * </pre>
     *
     * @param key a string representing the configuration parameter name.
     * @param value a string representing the configuration parameter value.
     */
    public void addConfigValue(final String key, final Boolean value) {
        config.put(key, value);
    }

    /**
     * Gets a configuration parameter value based on a configuration parameter
     * name provided as the key.
     *
     * @param key a string representing the configuration parameter name.
     * @return an object representing the configuration parameter value.
     */
    public Object getConfigValue(final String key) {
        return config.get(key);
    }

    /**
     * Returns all editor instance configuration options.
     *
     * @return a {@code Map} storing all editor instance configuration options.
     */
    public Map<String, Object> getConfigValues() {
        return config;
    }

    /**
     * Removes a configuration parameter value based on a configuration
     * parameter name provided as the key.<br>
     * <strong>Usage:</strong>
     *
     * <pre>
     * config.removeConfigValue( &quot;toolbar&quot; );
     * </pre>
     *
     * @param key a string representing the configuration parameter name.
     */
    public void removeConfigValue(final String key) {
        config.remove(key);
    }

    /**
     * Clones this {@code CKEditorConfig} object and merges the cloned object
     * with events provided in the {@code EventHandler} object.
     *
     * @param eventHandler the {@code EventHandler} object whose events will be
     * merged with the cloned {@code CKEditorConfig} object.
     * @return cloned {@code CKEditorConfig} object with merged events.
     */
    CKEditorConfig configSettings(final EventHandler eventHandler) {
        try {
            CKEditorConfig cfg = (CKEditorConfig) this.clone();
            if (eventHandler != null) {
                for (Map.Entry<String, Set<String>> eventEntry : eventHandler.events
                        .entrySet()) {
                    String eventName = eventEntry.getKey();
                    Set<String> set = eventEntry.getValue();
                    if (set.isEmpty()) {
                        continue;
                    } else if (set.size() == 1) {
                        Map<String, String> hm = new HashMap<String, String>();
                        for (String code : set) {
                            hm.put(eventName, "@@" + code);
                        }
                        cfg.addConfigValue("on", hm);
                    } else {
                        Map<String, String> hm = new HashMap<String, String>();
                        StringBuilder sb = new StringBuilder(
                                "@@function (ev){");
                        for (String code : set) {
                            sb.append("(");
                            sb.append(code);
                            sb.append(")(ev);");
                        }
                        sb.append("}");
                        hm.put(eventName, sb.toString());
                        cfg.addConfigValue("on", hm);
                    }
                }
            }
            return cfg;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Merges this {@code CKEditorConfig} object's configuration options
     * {@code Map} with the configuration options {@code Map} of the
     * {@code CKEditorConfig} object provided as a parameter.<br>
     * Configuration options from the {@code CKEditorConfig} parameter either
     * overwrite existing values (if a given configuration parameter exists in
     * this {@code CKEditorConfig} object) or are added to this
     * {@code CKEditorConfig} object's {@code Map} (if a given configuration
     * parameter does not exist in this {@code CKEditorConfig} object).
     *
     * @param cfg the {@code CKEditorConfig} object whose configuration options
     * {@code Map} will be merged with this {@code CKEditorConfig} object's
     * configuration options {@code Map}.
     *
     * @return the {@code CKEditorConfig} object with configuration options
     * merged.
     */
    public CKEditorConfig mergeConfigValues(final CKEditorConfig cfg) {
        if (cfg != null) {
            Map<String, Object> configValues = cfg.getConfigValues();
            for (Map.Entry<String, Object> configEntry : configValues
                    .entrySet()) {
                config.put(configEntry.getKey(), configEntry.getValue());
            }
        }
        return this;
    }

    /**
     * Checks if the configuration object is empty.
     *
     * @return if the configuration is empty, this method returns {@code true}.
     * Otherwise it returns {@code false}.
     */
    public boolean isEmpty() {
        return config.isEmpty();
    }

    /**
     * Clones this {@code CKEditorConfig} object together with its configuration
     * options {@code Map}.
     *
     * @return new cloned {@code CKEditorConfig} object.
     * @throws CloneNotSupportedException if cloning cannot be performed.
     */
    protected Object clone() throws CloneNotSupportedException {
        CKEditorConfig cfg = (CKEditorConfig) super.clone();
        cfg.config = new HashMap<String, Object>(this.config);
        return cfg;
    }

}
