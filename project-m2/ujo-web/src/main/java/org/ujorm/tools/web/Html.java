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

/** A proxy for a HTML element */
public interface Html {

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
    /** Script element */
    String SCRIPT = "script";
    /** Division in an HTML document. */
    String DIV = "div";
    /** Preformatted text. */
    String PRE = "pre";
    /** Span element */
    String SPAN = "span";
    /** Paragraph element */
    String P = "p";
    /** Form element */
    String FORM = "form";
    /** Heading prefix */
    String HEADING_PREFIX = "h";
    /** Heading element level 1 */
    String H1 = HEADING_PREFIX + 1;
    /** Heading element level 2 */
    String H2 = HEADING_PREFIX + 2;
    /** Heading element level 3 */
    String H3 = HEADING_PREFIX + 3;
    /** Table element */
    String TABLE = "table";
    /** Table header line */
    String THEAD = "thead";
    /** Table header cell */
    String TH = "th";
    /** Table row element */
    String TR = "tr";
    /** Table detail element */
    String TD = "td";
    /** Label element */
    String LABEL = "label";
    /** Input element */
    String INPUT = "input";
    /** Input element */
    String TEXT_AREA = "textarea";
    /** Select element */
    String SELECT = "select";
    /** Option element */
    String OPTION = "option";
    /** Button */
    String BUTTON = "button";
    /** Anchor element */
    String A = "a";
    /** Unordered list elements (root) */
    String UL = "ul";
    /** Ordered list elements (root) */
    String OL = "ol";
    /** Some item element*/
    String LI = "li";
    /** A line break */
    String BR = "br";

    // --- Attribute names ---

    String A_ACTION = "action";
    String A_CELLPADDING = "cellpadding";
    String A_CELLSPACING = "cellspacing";
    String A_CHARSET = "charset";
    String A_CHECKED = "checked";
    String A_CLASS = "class";
    String A_CONTENT = "content";
    String A_FOR = "for";
    String A_HREF = "href";
    String A_ID = "id";
    /** Language attribute of the HTML pagee (e.g. "en")  */
    String A_LANG = "lang";
    /** Reference of the programing language (e.g. "javascript") */
    String A_LANGUAGE = "language";
    String A_MAXLENGTH = "maxlength";
    String A_MEDIA = "media";
    String A_METHOD = "method";
    String A_NAME = "name";
    String A_READONLY = "readonly";
    String A_REL = "rel";
    String A_SELECTED = "selected";
    String A_SRC = "src";
    String A_TARGET = "target";
    String A_TYPE = "type";
    String A_VALUE = "value";
    String A_ROWS = "rows";
    String A_COLS = "cols";
    String A_ROWSPAN = "cols";
    String A_COLSPAN = "colspan";

    // --- Some attribute values ---

    String V_BLANK = "_blank";
    String V_CHECKBOX = "checkbox";
    String V_GET = "get";
    String V_HIDDEN = "hidden";
    String V_POST = "post";
    String V_RESET = "reset";
    String V_SUBMIT = "submit";
    String V_STYLESHEET = "stylesheet";
    String V_TEXT = "text";
    String V_TEXT_CSS = "text/css";

}
