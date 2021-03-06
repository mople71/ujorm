/*
 * Copyright 2018-2018 Pavel Ponec,
 * https://github.com/pponec/ujorm/blob/master/project-m2/ujo-tools/src/main/java/org/ujorm/tools/HtmlElement.java
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

package org.ujorm.tools.xml.dom;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.ujorm.tools.Assert;
import org.ujorm.tools.xml.CommonXmlWriter;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.ujorm.tools.xml.CommonXmlWriter.CHAR_NEW_LINE;

/**
 * XML element model to rendering a HTML file
 * <h3>How to use the class:</h3>
 * <pre class="pre">
 * final HtmlElement html = new HtmlElement("Test");
 * html.getBody().addElement("div")
 *               .addText("Hello word!");
 * html.toResponse(new MockHttpServletResponse(), false);
 * </pre>
 * @since 1.86
 * @author Pavel Ponec
 */
public class HtmlElement extends XmlElement {

    /** Head element */
    @Nonnull
    private final XmlElement head;

    /** Body element */
    @Nonnull
    private final XmlElement body;

    /** Charset */
    @Nonnull
    protected final Charset charset;

    /** Create new instance with empty html headers */
    public HtmlElement(@Nonnull final Charset charset) {
        super(Html.HTML);

        Assert.notNull(charset, REQUIRED_MSG, "charset");
        this.charset = charset;
        this.head = addElement(Html.HEAD);
        this.body = addElement(Html.BODY);
    }

    /** Constructor buliding default html headers with codepage UTF-8 */
    public HtmlElement(@Nonnull final Object title, @Nullable final CharSequence... cssLinks) {
        this(title, UTF_8, cssLinks);
    }

    /** Generic constructor buliding default html headers
     * @param title Htmlpage title
     * @param charset A charset
     * @param cssLinks Nullable CSS link array
     */
    public HtmlElement(@Nonnull final Object title, @Nonnull final Charset charset, @Nullable final CharSequence... cssLinks) {
        this(charset);
        head.addElement(Html.META).setAttrib(Html.A_CHARSET, charset);
        head.addElement(Html.TITLE).addText(title);

        if (cssLinks != null) {
            for (CharSequence cssLink : cssLinks) {
                addCssLink(cssLink);
            }
        }
    }

    /** Returns body element
     * @deprecated Use the method {@link #addElementToHead(java.lang.String) } rather. */
    @Nonnull @Deprecated
    public <T extends XmlElement> T getHead() {
        return (T) head;
    }

    /** Returns body element */
    @Nonnull
    public <T extends XmlElement> T getBody() {
        return (T) body;
    }

    /** A shortcut for {@code HtmlList.getHead().addElement(CharSequence) }
     * @param name A name of the new XmlElement is requred.
     * @return The new XmlElement!
     */
    public <T extends XmlElement> T  addElementToHead(@Nonnull final String name) {
        return head.addElement(name);
    }

    /** A shortcut for {@code HtmlList.getBody().addElement(CharSequence) }
     * @param name A name of the new XmlElement is requred.
     * @return The new XmlElement!
     */
    public <T extends XmlElement> T  addElementToBody(@Nonnull final String name) {
        return body.addElement(name);
    }

    /** Create a new Javascript element and return it
     * @param javascriptLink URL to Javascript
     * @param defer A script that will not run until after the page has loaded
     * @return
     */
    public <T extends XmlElement> T addJavascriptLink(final boolean defer, @Nonnull final CharSequence ... javascriptLink) {
        Assert.notNull(javascriptLink, REQUIRED_MSG, "javascriptLink");
        return head.addElement(Html.SCRIPT)
                .setAttrib(Html.A_SRC, javascriptLink)
                .setAttrib(Html.A_TYPE, "text/javascript")
                .setAttrib("defer", defer ? "defer" : null)
                .addText("");
    }

    /** Create a new Javascript element and return it
     * @param javascript Add a javascriptLink link
     * @return New CSS element
     */
    public <T extends XmlElement> T addJavascriptContent(@Nonnull final CharSequence javascript) {
        Assert.notNull(javascript, REQUIRED_MSG, "javascript");
        return head.addElement(Html.SCRIPT)
                .setAttrib(Html.A_LANGUAGE, "javascript")
                .setAttrib(Html.A_TYPE, "text/javascript")
                .addText(javascript);
    }

    /** Create a new CSS element and return it
     * @param css Add a CSS link
     * @return New CSS element
     */
    public <T extends XmlElement> T addCssLink(@Nonnull final CharSequence css) {
        Assert.notNull(css, REQUIRED_MSG, "css");
        return head.addElement(Html.LINK)
                .setAttrib(Html.A_HREF, css)
                .setAttrib(Html.A_REL, "stylesheet")
                .setAttrib(Html.A_TYPE, "text/css");
    }

    /** Create a new CSS element and return it
     * @param css CSS content
     * @return New CSS element
     */
    public <T extends XmlElement> T addCssBody(@Nonnull final CharSequence css) {
        Assert.notNull(css, REQUIRED_MSG, "css");
        return head.addElement(Html.STYLE)
                .setAttrib(Html.A_TYPE, "text/css")
                .addRawText(css);
    }

    /** Render the HTML code including header */
    @Override @Nonnull
    public String toString() throws IllegalStateException {
        try {
            return toWriter(0, new XmlWriter(new StringBuilder(512)
                    .append(HTML_DOCTYPE)
                    .append(CHAR_NEW_LINE)))
                    .toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Render the component to a <a href="https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletResponse.html">HTML response</a>
     * @param httpServletResponse Argument type of {@code javax.servlet.http.HttpServletResponse} is required.
     * @param noCache Add a header attributes to switch off a cache.
     * @throws IOException An writting error.
     * @throws IllegalArgumentException Wrong argument type
     */
    public final void toResponse(@Nonnull final Object httpServletResponse, final boolean noCache) throws IOException, IllegalArgumentException {
        toResponse(httpServletResponse, noCache, false);
    }

    /** Render the result with an indentation */
    public final void toNiceResponse(@Nonnull final Object httpServletResponse) throws IOException, IllegalArgumentException {
        toResponse(httpServletResponse, true, true);
    }

    /**
     * Render the result
     * @param httpServletResponse
     * @param noCache
     * @param indentation
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public void toResponse(@Nonnull final Object httpServletResponse, final boolean noCache, final boolean indentation) throws IOException, IllegalArgumentException {
        try {
            final Writer writer = CommonXmlWriter.createWriter(httpServletResponse, charset, noCache);
            final String offset = indentation ? DEFAULT_INTENDATION : null;
            toWriter(new XmlWriter(writer.append(HtmlElement.HTML_DOCTYPE).append(CHAR_NEW_LINE), offset));
            writer.flush();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Response must be type of HttpServletResponse", e);
        }
    }

    /**
     * Render the component to a {@link XmlWriter}
     * @param xmlWriter An instance of the XmlWriter is required.
     * @throws IOException An writting error.
     * @throws IllegalArgumentException Wrong argument type
     */
    public XmlWriter toWriter(@Nonnull final XmlWriter xmlWriter) throws IOException, IllegalArgumentException {
            return toWriter(0, xmlWriter);
    }

    /** Some HTML constants */
    public static interface Html {

        // --- Element names ---

        /** Body element */
        String HTML = "html";
        /** Head element */
        String HEAD = "head";
        /** Meta element */
        String META = "meta";
        /** Body element */
        String BODY = "body";
        /** Title element */
        String TITLE = "title";
        /** Link element */
        String LINK = "link";
        /** Style element */
        String STYLE = "style";
        /** Javascript element */
        String SCRIPT = "script";

        // --- Attribute names ---

        String A_CHARSET = "charset";
        String A_HREF = "href";
        String A_REL = "rel";
        String A_TYPE = "type";
        String A_SRC = "src";
        String A_LANGUAGE = "language";
    }
}