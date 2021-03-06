/*
 * Copyright 2018-2018 Pavel Ponec, https://github.com/pponec
 * https://github.com/pponec/ujorm/blob/master/samples/servlet/src/main/java/org/ujorm/ujoservlet/tools/Html.java
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

package org.ujorm.tools.web;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.ujorm.tools.Check;
import org.ujorm.tools.xml.AbstractElement;
import org.ujorm.tools.xml.dom.XmlElement;

/** A proxy for a HTML element */
public class Element extends AbstractElement<Element> implements Html {

    /** An original XML element */
    protected final AbstractElement origElement;

    /** New element with a parent */
    public Element(@Nonnull final AbstractElement original) {
        super(original.getName());
        this.origElement = original;
    }

    /**
     * Create new Element
     * @param name The element name
     * @return New instance of the Element
     * @throws IllegalStateException An envelope for IO exceptions
     */
    @Override @Nonnull
    public final <T extends Element> T addElement(@Nonnull final String name) throws IllegalStateException {
        try {
            return (T) new Element(origElement.addElement(name));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public final <T extends Element> T setAttrib(@Nonnull final String name, @Nullable final Object data) {
        try {
            origElement.setAttrib(name, data);
            return (T) this;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Add simple text
     * @param data Text item
     * @return A parent element.
     * @see #addAnchoredText(java.lang.String, java.lang.Object...)
     */
    @Override
    public <T extends Element> T addText(final Object data) throws IllegalStateException {
        try {
            origElement.addText(data);
            return (T) this;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Add many texts with no separator
     * @param data Text items
     * @return A parent element.
     * @see #addAnchoredText(java.lang.String, java.lang.Object...)
     */
    public <T extends Element> T addText(@Nonnull final Object... data) throws IllegalStateException {
        return addTextSeparted("", data);
    }

    /** Add many words separated by the separator */
    public <T extends Element> T addTextSeparted(@Nonnull final Object separator, @Nonnull final Object... data) throws IllegalStateException {
        try {
            for (int i = 0, max = data.length; i < max; i++) {
                if (i > 0) {
                    origElement.addText(separator);
                }
                origElement.addText(data[i]);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return (T) this;
    }

    @Override
    public <T extends Element> T addRawText(Object data) throws IllegalStateException {
        try {
            origElement.addRawText(data);
            return (T) this;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T extends Element> T addComment(CharSequence comment) throws IllegalStateException {
        try {
            origElement.addComment(comment);
            return (T) this;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T extends Element> T addCDATA(CharSequence charData) throws IllegalStateException {
        try {
            origElement.addCDATA(charData);
            return (T) this;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws IllegalStateException {
        try {
            origElement.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // -------------- Add TAG -----

    /**
     * Add a new Element with optional CSS classes
     * @param name A required name of the element
     * @param cssClasses Optional CSS classes.
     * @return New instance of the Element
     */
    public <T extends Element> T addElement(@Nonnull final String name, @Nonnull final CharSequence... cssClasses) {
        return addElement(name).setClass(cssClasses);
    }

    /** Add new Table with cellpadding a cellspacing values to zero. */
    public <T extends Element> T addTable(@Nonnull final CharSequence... cssClasses) {
        return addElement(TABLE, cssClasses)
                .setAttrib(Element.A_CELLPADDING, 0)
                .setAttrib(Element.A_CELLSPACING, 0);
    }

    /** Create a HTML table according to data */
    public <T extends Element> T addTable(final Object[][] data, final CharSequence... cssClass) {
        final T result = addTable(cssClass);

        if (Check.hasLength(cssClass)) {
            result.setAttrib(Html.A_CLASS, String.join(" ", cssClass));
        }

        for (Object[] rowValue : data) {
            final Element rowElement = result.addElement(Html.TR);
            for (Object value : rowValue) {
                rowElement.addElement(Html.TD).addText(value);
            }
        }
        return result;
    }

    /** Add new body element */
    public <T extends Element> T addBody(@Nonnull final CharSequence... cssClasses) {
        return addElement(BODY, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addTitle(@Nonnull final CharSequence... cssClasses) {
        return addElement(TITLE, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addLink(@Nonnull final CharSequence... cssClasses) {
        return addElement(LINK, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addStyle(@Nonnull final CharSequence... cssClasses) {
        return addElement(STYLE, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addScript(@Nonnull final CharSequence... cssClasses) {
        return addElement(SCRIPT, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addDiv(@Nonnull final CharSequence... cssClasses) {
        return addElement(DIV, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addPreformatted(@Nonnull final CharSequence... cssClasses) {
        return addElement(PRE, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addSpan(@Nonnull final CharSequence... cssClasses) {
        return addElement(SPAN, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addParagraph(@Nonnull final CharSequence... cssClasses) {
        return addElement(P, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addForm(@Nonnull final CharSequence... cssClasses) {
        return addElement(FORM, cssClasses);
    }

    /** Add new heading level one  */
    public <T extends Element> T addHeading(@Nonnull CharSequence title, @Nonnull final CharSequence... cssClasses) {
        return addHeading(1, title, cssClasses);
    }

    /** Add new heading with the required level where the first level is zero. */
    public <T extends Element> T addHeading(int level, @Nonnull CharSequence title, @Nonnull final CharSequence... cssClasses) {
        return addElement(HEADING_PREFIX + Math.max(1, level), cssClasses).addText(title);
    }

    /** Add new body element */
    public <T extends Element> T addTableHead(@Nonnull final CharSequence... cssClasses) {
        return addElement(THEAD, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addTableRow(@Nonnull final CharSequence... cssClasses) {
        return addElement(TR, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addTableDetail(@Nonnull final CharSequence... cssClasses) {
        return addElement(TD, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addLabel(@Nonnull final CharSequence... cssClasses) {
        return addElement(LABEL, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addInput(@Nonnull final CharSequence... cssClasses) {
        return addElement(INPUT, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addTextArea(@Nonnull final CharSequence... cssClasses) {
        return addElement(TEXT_AREA, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addSelect(@Nonnull final CharSequence... cssClasses) {
        return addElement(SELECT, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addOption(@Nonnull final CharSequence... cssClasses) {
        return addElement(OPTION, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addButton(@Nonnull final CharSequence... cssClasses) {
        return addElement(BUTTON, cssClasses);
    }

    /** Add an anchor element with URL and CSS classes */
    public <T extends Element> T addAnchor(@Nonnull final String url, @Nonnull final CharSequence... cssClasses) {
        final T result = addElement(A, cssClasses);
        result.setHref(url);
        return result;
    }

    /** Add an anchor element with texts */
    public <T extends Element> T addAnchoredText(@Nonnull final String url, @Nonnull final Object... text) {
        final T result = addElement(A)
               .setHref(url)
               .addText(text);
        return result;
    }

    /** Add new body element */
    public <T extends Element> T addUnorderedlist(@Nonnull final CharSequence... cssClasses) {
        return addElement(UL, cssClasses);
    }

    /** Add new body element */
    public <T extends Element> T addOrderedList(@Nonnull final CharSequence... cssClasses) {
        return addElement(OL, cssClasses);
    }

    public <T extends Element> T addListItem(@Nonnull final CharSequence... cssClasses) {
        return addElement(LI, cssClasses);
    }

    /** Set a CSS class attribute optionally, the empty attribute is ignored.
     * @param cssClasses Optional CSS classes. The css item is ignored when the value is empty or {@code null}.
     * @return The current instanlce
     */
    public <T extends Element> T setClass(@Nonnull final CharSequence... cssClasses) {
        if (Check.hasLength(cssClasses)) {
            final StringJoiner builder = new StringJoiner(" ");
            for (CharSequence cssClass : cssClasses) {
                if (Check.hasLength(cssClass)) {
                    builder.add(cssClass);
                }
            }
            final String result = builder.toString();
            if (Check.hasLength(result)) {
                setAttrib(A_CLASS, result);
            }
        }
        return (T) this;
    }

    /** Add a line break */
    public <T extends Element> T addBreak(@Nonnull final CharSequence... cssClasses) {
        return addElement(BR, cssClasses);
    }

    // ----- An attributes ----------

    /** Set a CSS class attribute */
    public <T extends Element> T setCellPadding(final int value) {
        setAttrib(A_CELLPADDING, value);
        return (T) this;
    }

    /** Set a CSS class attribute */
    public <T extends Element> T setCellSpacing(final int value) {
        setAttrib(A_CELLSPACING, value);
        return (T) this;
    }

    // ---- Static methods ----

    /** Crate a root element
     * @param cssLinks Nullable CSS link array
     */
    public static Element createHtmlRoot(@Nonnull final Object title, @Nullable final CharSequence... cssLinks) {
        return createHtmlRoot(title, null, cssLinks);
    }


    /** Crate a root element
     * @param charset A charset
     * @param cssLinks Nullable CSS link array
     */
    public static Element createHtmlRoot(@Nonnull final Object title, @Nullable final Charset charset, @Nullable final CharSequence... cssLinks) {
        XmlElement result = new XmlElement(HTML);
        XmlElement head = result.addElement(HEAD);
        head.addElement(META).setAttrib(A_CHARSET, charset);
        head.addElement(TITLE).addText(title);

        if (cssLinks != null) {
            for (CharSequence cssLink : cssLinks) {
                head.addElement(LINK)
                        .setAttrib(A_HREF, cssLink)
                        .setAttrib(A_REL, "stylesheet");
            }
        }

        return new Element(result);
    }

    /** Set an identifier of the element */
    public <T extends Element> T setId(@Nullable final CharSequence value) {
        setAttrib(A_ID, value);
        return (T) this;
    }

    /** Set a method of form */
    public <T extends Element> T setMethod(@Nullable final Object value) {
        setAttrib(A_METHOD, value);
        return (T) this;
    }

    /** Set an action type of from */
    public <T extends Element> T setAction(@Nullable final Object value) {
        setAttrib(A_ACTION, value);
        return (T) this;
    }

    /** Set a type of input element */
    public <T extends Element> T setType(@Nullable final Object value) {
        setAttrib(A_TYPE, value);
        return (T) this;
    }

    /** Set an name of input element */
    public <T extends Element> T setName(@Nullable final CharSequence value) {
        setAttrib(A_NAME, value);
        return (T) this;
    }

    /** Set an value of input element */
    public <T extends Element> T setValue(@Nullable final Object value) {
        setAttrib(A_VALUE, value);
        return (T) this;
    }

    /** Set an value of input element */
    public <T extends Element> T setFor(@Nullable final CharSequence value) {
        setAttrib(A_VALUE, value);
        return (T) this;
    }

    /** Row count of a text area */
    public <T extends Element> T setRows(@Nullable final int value) {
        setAttrib(A_ROWS, value);
        return (T) this;
    }

    /** Column count of a text area */
    public <T extends Element> T setCols(@Nullable final Object value) {
        setAttrib(A_COLS, value);
        return (T) this;
    }

    /** Column span inside the table */
    public <T extends Element> T setColSpan(@Nullable final int value) {
        setAttrib(A_COLSPAN, value);
        return (T) this;
    }

    /** Row span inside the table */
    public <T extends Element> T setRowSpan(@Nullable final int value) {
        setAttrib(A_ROWSPAN, value);
        return (T) this;
    }

    /** Set hyperlink reference */
    public <T extends Element> T setHref(@Nullable final CharSequence value) {
        setAttrib(A_HREF, value);
        return (T) this;
    }
}
