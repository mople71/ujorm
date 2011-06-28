/*
 *  Copyright 2007-2010 Pavel Ponec
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */   
   
package org.ujorm.extensions;

import java.util.ArrayList;
import java.util.List;
import org.ujorm.CompositeProperty;
import org.ujorm.Ujo;
import org.ujorm.UjoProperty;

/**
 * A <strong>PathProperty</strong> class is an composite of a UjoProperty objects.
 * The PathProperty class can be used wherever is used UjoProperty - with a one important <strong>exception</strong>: 
 * do not send the PathProperty object to methods Ujo.readValue(...) and Ujo.writeValue(...) !!!
 * <p/>Note that method isDirect() returns a false in this class. For this reason, the property is not included 
 * in the list returned by Ujo.readProperties().
 * 
 * @author Pavel Ponec
 * @since 0.81
 */
final public class PathProperty<UJO extends Ujo, VALUE> implements CompositeProperty<UJO, VALUE> {

    /** Array of <strong>direct</strong> properties */
    private final UjoProperty[] properties;
    /** Is property ascending / descending */
    private final boolean ascending;
    private String name;

    public PathProperty(List<UjoProperty> properties) {
        this(properties.toArray(new UjoProperty[properties.size()]));
    }

    /** The main constructor. It is recommended to use the factory method 
     * {@link #newInstance(org.ujorm.UjoProperty, org.ujorm.UjoProperty) newInstance(..)}
     * for better performance in some cases.
     * @see #newInstance(org.ujorm.UjoProperty, org.ujorm.UjoProperty) newInstance(..)
     */
    public PathProperty(UjoProperty... properties) {
        this(null, properties);
    }

    /** Main constructor */
    @SuppressWarnings("unchecked")
    public PathProperty(Boolean ascending, UjoProperty... properties) {
        final ArrayList<UjoProperty> list = new ArrayList<UjoProperty>(properties.length + 3);
        for (UjoProperty property : properties) {
            if (property.isDirect()) {
                list.add(property);
            } else {
                ((CompositeProperty)property).exportProperties(list);
            }
        }
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Argument must not be empty");
        }
        this.ascending = ascending!=null ? ascending : properties[properties.length-1].isAscending();
        this.properties = list.toArray(new UjoProperty[list.size()]);
    }

    /** Private constructor for a better performance. For internal use only. */
    private PathProperty(final UjoProperty[] properties, final boolean ascending) {
        this.properties = properties;
        this.ascending = ascending;
    }

    /** Get the last property of the current object. The result may not be the direct property. */
    @SuppressWarnings("unchecked")
    public final <UJO_IMPL extends Ujo> UjoProperty<UJO_IMPL, VALUE> getLastPartialProperty() {
        return properties[properties.length - 1];
    }

    /** Get the first property of the current object. The result is direct property always. */
    @SuppressWarnings("unchecked")
    @Override
    final public <UJO_IMPL extends Ujo> UjoProperty<UJO_IMPL, VALUE> getLastProperty() {
        UjoProperty result = properties[properties.length - 1];
        return result.isDirect()
            ? result
            : ((PathProperty)result).getLastProperty()
            ;
    }

    /** Get the first property of the current object. The result is direct property always. */
    @SuppressWarnings("unchecked")
    @Override
    final public <UJO_IMPL extends Ujo> UjoProperty<UJO_IMPL, VALUE> getFirstProperty() {
        UjoProperty result = properties[0];
        return result.isDirect()
            ? result
            : ((PathProperty)result).getFirstProperty()
            ;
    }

    /** Full property name */
    final public String getName() {
        if (name==null) {
            StringBuilder result = new StringBuilder(32);
            for (UjoProperty p : properties) {
                if (result.length() > 0) {
                    result.append('.');
                }
                result.append(p.getName());
            }
            name = result.toString();
        }
        return name;
    }

    /** Property type */
    @Override
    public Class<VALUE> getType() {
        return getLastPartialProperty().getType();
    }

    /** Get a semifinal value from an Ujo object by a chain of properties.
     * If any value (not getLastPartialProperty) is null, then the result is null.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Ujo getSemifinalValue(UJO ujo) {

        Ujo result = ujo;
        for (int i=0; i<properties.length-1; i++) {
            if (result==null) { return result; }
            result = (Ujo) properties[i].getValue(result);
        }
        return result;
    }

    /** Get a value from an Ujo object by a chain of properties.
     * If a value  (not getLastPartialProperty) is null, then the result is null.
     */
    @SuppressWarnings("unchecked")
    @Override
    public VALUE getValue(final UJO ujo) {
        final Ujo u = getSemifinalValue(ujo);
        return  u!=null ? getLastPartialProperty().of(u) : null ;
    }

    @Override
    public void setValue(final UJO ujo, final VALUE value) {
        final Ujo u = getSemifinalValue(ujo);
        getLastPartialProperty().setValue(u, value);
    }

    @Override
    final public int getIndex() {
        return -1;
    }

    /** Returns a default value */
    @Override
    public VALUE getDefault() {
        return getLastPartialProperty().getDefault();
    }

    /** Indicates whether a parameter value of the ujo "equal to" this default value. */
    @Override
    public boolean isDefault(UJO ujo) {
        VALUE value = getValue(ujo);
        VALUE defaultValue = getDefault();
        final boolean result
        =  value==defaultValue
        || (defaultValue!=null && defaultValue.equals(value))
        ;
        return result;
    }

    /** Copy a value from the first UJO object to second one. A null value is not replaced by the default. */
    @Override
    public void copy(final UJO from, final UJO to) {
        final Ujo from2 = getSemifinalValue(from);
        final Ujo to2 = getSemifinalValue(to);
        getLastPartialProperty().copy(from2, to2);
    }

    /** Returns true if the property type is a type or subtype of the parameter class. */
    @SuppressWarnings("unchecked")
    @Override
    public boolean isTypeOf(final Class type) {
        return getLastProperty().isTypeOf(type);
    }
    
    /**
     * Returns true, if the property value equals to a parameter value. The property value can be null.
     * 
     * @param ujo A basic Ujo.
     * @param value Null value is supported.
     * @return Accordance
     */
    @Override
    public boolean equals(final UJO ujo, final VALUE value) {
        Object myValue = getValue(ujo);
        if (myValue==value) { return true; }
        
        final boolean result
        =  myValue!=null
        && value  !=null
        && myValue.equals(value)
        ;
        return result;
    }

    /**
     * Returns true, if the property value equals to a parameter value. The property value can be null.
     *
     * @param property A basic CujoProperty.
     * @param value Null value is supported.
     */
    @Override
    public boolean equals(Object property) {
        final String t1 = this.getName();
        final String t2 = property!=null ? property.toString() : null;
        return t1.equals(t2) && getType().equals(((UjoProperty)property).getType());
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    /**
     * A shortcut for the method getValue(Ujo) .
     * @see #getValue(Ujo)
     */
    @Override
    final public VALUE of(final UJO ujo) {
        return getValue(ujo);
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    /** Length of the Name */
    @Override
    public int length() {
        return getName().length();
    }

    /** A char from Name */
    @Override
    public char charAt(int index) {
        return getName().charAt(index);
    }

    /** Sub sequence from the Name */
    @Override
    public CharSequence subSequence(int start, int end) {
        return getName().subSequence(start, end);
    }
    
    /**
     * Method returns a false because this is a property of the another UJO class.
     * The composite property is excluded from from function Ujo.readProperties() by default.
     */
    @Override
    public final boolean isDirect() {
        return false;
    }

    /** A flag for an ascending direction of order. For the result is significant only the last property.
     * @see org.ujorm.core.UjoComparator
     */
    @Override
    public boolean isAscending() {
        return ascending;
    }

    /** Create a new instance of the property with a descending direction of order.
     * @see org.ujorm.core.UjoComparator
     */
    @Override
    @SuppressWarnings("unchecked")
    public UjoProperty<UJO,VALUE> descending() {
        return descending(true);
    }

    /** Create a new instance of the property with a descending direction of order.
     * @see org.ujorm.core.UjoComparator
     */
    @Override
    @SuppressWarnings("unchecked")
    public UjoProperty<UJO,VALUE> descending(boolean descending) {
        return isAscending()==descending
                ? new PathProperty(properties, !descending)
                : this
                ;
    }

    /** Export all <string>direct</strong> properties to the list from parameter. */
    @SuppressWarnings("unchecked")
    @Override
    public void exportProperties(List<UjoProperty> result) {
        for (UjoProperty p : properties) {
            if (p.isDirect()) {
                result.add(p);
            } else {
                ((PathProperty)p).exportProperties(result);
            }
        }
    }

    /** Create new composite (indirect) instance.
     * @since 0.92
     */
    @SuppressWarnings("unchecked")
    @Override
    public <VALUE_PAR> CompositeProperty<UJO, VALUE_PAR> add(final UjoProperty<? extends VALUE, VALUE_PAR> property) {

        UjoProperty[] props = new UjoProperty[properties.length+1];
        System.arraycopy(properties, 0, props, 0, properties.length);
        props[properties.length] = property;

        return new PathProperty(props);
    }

    /** Compare to another UjoProperty object by the index and name of the property.
     * @since 1.20
     */
    public int compareTo(final UjoProperty p) {
        return getIndex()<p.getIndex() ? -1
             : getIndex()>p.getIndex() ?  1
             : getName().compareTo(p.getName())
             ;
    }

    // ================ STATIC ================
    
    /** Create a new instance of property with a new sort attribute value.
     * @hidden
     */
    public static <UJO extends Ujo, VALUE> PathProperty<UJO, VALUE> sort(final UjoProperty<UJO, VALUE> property, final boolean ascending) {
        return property.isDirect()
            ? new PathProperty<UJO, VALUE>(new UjoProperty[]{property}, ascending)
            : new PathProperty<UJO, VALUE>(ascending, property)
            ;
    }

    /** Create a new instance of property with a new sort attribute value.
     * This is an alias for the static method {@link #sort(org.ujorm.UjoProperty, boolean) sort()}.
     * @hidden
     * @see #sort(org.ujorm.UjoProperty, boolean) sort(..)
     */
    public static <UJO extends Ujo, VALUE> PathProperty<UJO, VALUE> newInstance(final UjoProperty<UJO, VALUE> property, final boolean ascending) {
        return sort(property, ascending);
    }

    /** Quick instance for the direct property.
     * @hidden
     */
    public static <UJO extends Ujo, VALUE> PathProperty<UJO, VALUE> newInstance(final UjoProperty<UJO, VALUE> property) {
        return property.isDirect()
            ? new PathProperty<UJO, VALUE>(new UjoProperty[]{property}, property.isAscending())
            : new PathProperty<UJO, VALUE>(property.isAscending(), property)
            ;
    }

    /** Quick instance for the direct properrites
     * @hidden
     */
    public static <UJO1 extends Ujo, UJO2 extends Ujo, VALUE> PathProperty<UJO1, VALUE> newInstance
        ( final UjoProperty<UJO1, UJO2> property1
        , final UjoProperty<UJO2, VALUE> property2
        ) {
        return property1.isDirect() && property2.isDirect()
            ? new PathProperty<UJO1, VALUE>(new UjoProperty[]{property1,property2}, property2.isAscending())
            : new PathProperty<UJO1, VALUE>(property2.isAscending(), property1, property2)
            ;
    }

    /** Create new instance
     * @hidden 
     */
    public static <UJO1 extends Ujo, UJO2 extends Ujo, UJO3 extends Ujo, VALUE> PathProperty<UJO1, VALUE> newInstance
        ( final UjoProperty<UJO1, UJO2> property1
        , final UjoProperty<UJO2, UJO3> property2
        , final UjoProperty<UJO3, VALUE> property3
        ) {
        return new PathProperty<UJO1, VALUE>(property1, property2, property3);
    }

    /** Create new instance
     * @hidden 
     */
    public static <UJO1 extends Ujo, UJO2 extends Ujo, UJO3 extends Ujo, UJO4 extends Ujo, VALUE> PathProperty<UJO1, VALUE> newInstance
        ( final UjoProperty<UJO1, UJO2> property1
        , final UjoProperty<UJO2, UJO3> property2
        , final UjoProperty<UJO3, UJO4> property3
        , final UjoProperty<UJO4, VALUE> property4
        ) {
        return new PathProperty<UJO1, VALUE>(property1, property2, property3, property4);
    }

    /** Create new instance
     * @hidden 
     */
    @SuppressWarnings("unchecked")
    public static <UJO extends Ujo, VALUE> PathProperty<UJO, VALUE> create(UjoProperty<UJO, ? extends Object>... properties) {
        return new PathProperty(properties);
    }

}