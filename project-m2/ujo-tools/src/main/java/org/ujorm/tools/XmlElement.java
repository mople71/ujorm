/*
 * Copyright 2018-2018 Pavel Ponec, https://github.com/pponec
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ujorm.tools;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * XML element model to rendering a XML file
 * @author Pavel Ponec
 */
public class XmlElement {

    /** XML header */
    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";

    /** A special XML character */
    protected static final char XML_GT = '>';
    /** A special XML character */
    protected static final char XML_LT = '<';
    /** A special XML character */
    protected static final char XML_AMP = '&';
    /** A special XML character */
    protected static final char XML_QUOT = '\'';
    /** A special XML character */
    protected static final char XML_2QUOT = '"';
    /** A special XML character */
    protected static final char CHAR_SPACE = ' ';
    /** A CDATA beg markup sequence */
    protected static final String CDATA_BEG = "<![CDATA[";
    /** A CDATA end markup sequence */
    protected static final String CDATA_END = "]]>";

    /** Element name */
    @Nonnull
    protected final CharSequence name;

    /** Attributes */
    @Nullable
    private Map<String, Object> attributes;

    /** Child elements */
    @Nullable
    private List<Object> children;

    /** New element */
    public XmlElement(@Nonnull final CharSequence name) {
        this.name = name;
    }

    /** New element with a parent */
    public XmlElement(@Nonnull final CharSequence name, @Nonnull final XmlElement parent) {
        this(name);
        parent.getChildren().add(this);
    }

    /** Return attributes */
    @Nonnull
    protected Map<String, Object> getAttribs() {
        if (attributes == null) {
            attributes = new LinkedHashMap<>();
        }
        return attributes;
    }

    /** Add an attribute
     * @return This instance */
    @Nonnull
    public <T extends XmlElement> T addAttrib(@Nonnull final CharSequence name, @Nonnull final Object value) {
        getAttribs().put(name.toString(), value);
        return (T) this;
    }

    /** Return child entities */
    @Nonnull
    protected List<Object> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    /**
     * Add a child element
     * @param element Add a child element. An undefined argument is ignored.
     * @return This instance */
    @Nonnull
    public <T extends XmlElement> T addElement(@Nullable final XmlElement element) {
        if (element != null) {
            getChildren().add(element);
        }
        return (T) this;
    }

    /** Create a new {@link XmlElement} for a required name and add it to children.
     * @param name A name of the new XmlElement is requred.
     * @return The new XmlElement!
     */
    @Nonnull
    public XmlElement addElement(@Nonnull final CharSequence name) {
        Assert.hasLength(name, "Undefined element name");
        return new XmlElement(name, this);
    }

    /**
     * Add a text and escape special character
     * @param text text An empty argument is ignored.
     * @return This instance */
    @Nonnull
    public <T extends XmlElement> T addText(@Nonnull final CharSequence text) {
        if (Check.hasLength(text)) {
            getChildren().add(text);
        }
        return (T) this;
    }

    /** Add an native text with no escaped characters, for example: XML code, JavaScript, CSS styles
     * @param rawText text An empty argument is ignored.
     * @return This instance */
    @Nonnull
    public <T extends XmlElement> T addRawText(@Nullable final CharSequence rawText) {
        if (Check.hasLength(rawText)) {
            getChildren().add(new RawEnvelope(rawText));
        }
        return (T) this;
    }

    /**
     * Add a <strong>character data</strong> in {@code CDATA} format to XML only.
     * The CDATA structure isn't really for HTML at all.
     * @param charData A text including the final DATA sequence. An empty argument is ignored.
     * @return This instance
     */
    @Nonnull
    public <T extends XmlElement> T addCDATA(@Nullable final CharSequence charData) {
        if (Check.hasLength(charData)) {
            addRawText(CDATA_BEG);
            final String text = charData.toString();
            int i = 0, j;
            while ((j = text.indexOf(CDATA_END, i)) >= 0) {
                j += CDATA_END.length();
                addRawText(text.subSequence(i, j));
                i = j;
                addText(CDATA_END);
                addRawText(CDATA_BEG);
            }
            addRawText(i == 0 ? text : text.substring(i));
            addRawText(CDATA_END);
        }
        return (T) this;
    }

    /**
     * Write escaped value to the output
     * @param value A value
     * @param attribute Render the value to an element attribute, or a text
     * @param out An output writer
     * @throws IOException
     */
    protected void writeValue(@Nonnull final Object value, final boolean attribute, @Nonnull final Writer out) throws IOException {
        final CharSequence text = value instanceof CharSequence ? (CharSequence) value : String.valueOf(value);
        for (int i = 0, max = text.length(); i < max; i++) {
            final char c = text.charAt(i);
            switch (c) {
                case XML_LT:
                    out.append(XML_AMP).append("lt;");
                    break;
                case XML_GT:
                    out.append(XML_AMP).append("gt;");
                    break;
                case XML_AMP:
                    out.append(XML_AMP).append("#38;");
                    break;
                case XML_QUOT:
                    out.append(XML_AMP).append("#39;");
                    break;
                case XML_2QUOT:
                    out.append(XML_AMP).append("#34;");
                    break;
                default: {
                    if (c > 32 || c == CHAR_SPACE) {
                        out.append(c);
                    } else {
                        out.append(XML_AMP).append("#");
                        out.append(Integer.toString(c));
                        out.append(";");
                    }
                }
            }
        }
    }

    /** Render the XML code including header */
    @Override @Nonnull
    public String toString() {
        return toWriter(new CharArrayWriter(512).append(HEADER).append('\n')).toString();
    }

    /** Render the XML code without header */
    @Nonnull
    public Writer toWriter(@Nonnull final Writer out) throws IllegalStateException {
        try {
            out.append(XML_LT);
            out.append(name);

            if (Check.hasLength(attributes)) {
                for (String key : attributes.keySet()) {
                    out.append(CHAR_SPACE);
                    out.append(key);
                    out.append('=');
                    out.append(XML_2QUOT);
                    writeValue(attributes.get(key), true, out);
                    out.append(XML_2QUOT);
                }
            }
            if (Check.hasLength(children)) {
                out.append(XML_GT);
                for (Object child : children) {
                    if (child instanceof XmlElement) {
                        out.append('\n');
                        ((XmlElement)child).toWriter(out);
                    } else if (child instanceof RawEnvelope) {
                        out.append(((RawEnvelope) child).get());
                    } else {
                        writeValue(child, false, out);
                    }
                }
                out.append(XML_LT);
                out.append('/');
                out.append(name);
            } else {
                out.append('/');
            }
            out.append(XML_GT);

            return out;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    // -------- Inner class --------

    /** Raw XML code envelope */
    protected static final class RawEnvelope {
        /** Xml content */
        private final CharSequence body;

        public RawEnvelope(@Nonnull final CharSequence body) {
            this.body = body;
        }

        /** Get the body */
        @Nonnull
        public CharSequence get() {
            return body;
        }
    }
}
