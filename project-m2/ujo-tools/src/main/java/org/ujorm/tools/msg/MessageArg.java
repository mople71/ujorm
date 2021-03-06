/*
 * Copyright 2012-2017 Pavel Ponec, https://github.com/pponec
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
package org.ujorm.tools.msg;

import java.io.Serializable;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.ujorm.tools.Assert;
import org.ujorm.tools.Check;
import static org.ujorm.tools.msg.MessageService.PARAM_BEG;
import static org.ujorm.tools.msg.MessageService.PARAM_END;

/**
 * Message Argument
 * @author Pavel Ponec
 * @see MessageService
 * @since 1.54
 */
public final class MessageArg<T> implements Serializable {

    /** Name of the argument */
    @Nonnull
    private final String name;

    /** Optional format of the argument */
    @Nullable
    private final String format;

    /** Name constructor */
    public MessageArg(@Nonnull String name) {
        this(name, null);
    }

    /** Common constructor
     * @param name Argument name
     * @param format Format syntax is described on
     * <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html">java.util.Formatter</a>
     */
    public MessageArg(@Nonnull String name, @Nullable String format) {
        Assert.notNull(name, "Name is required", name);
        Assert.isTrue(name.indexOf(PARAM_END)<0  , "Forbidden character {} in argument {}", PARAM_END, name);
        Assert.isTrue(format == null
                   || format.indexOf(PARAM_END)<0, "Forbidden character {} in argument {}", PARAM_END, format);
        this.name = name;
        this.format = format;
    }

    /** Get Name of argument */
    @Nonnull
    public String getName() {
        return name;
    }

    /** Get optional format of the argument */
    @Nullable
    public String getFormat() {
        return format;
    }

    /** Returns the name */
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder(32);
        result.append(PARAM_BEG).append(name);
        if (Check.hasLength(format)) {
            result.append(',').append(format);
        }
        result.append(PARAM_END);
        return result.toString();
    }

    /** Get a value from a map */
    public T getValue(final Map<String, Object> map) {
        return (T) map.get(name);
    }
}
