/*
 * UnifiedDataObjectImlp.java
 *
 * Created on 3. June 2007, 23:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.ujorm.implementation.xml.t001_simple;

import java.util.Date;
import org.ujorm.Key;
import org.ujorm.implementation.quick.QuickUjo;

/**
 * An UnifiedDataObject Implementation
 * @author Pavel Ponec
 */
public class UPerson extends QuickUjo {

    public static final Key<UPerson,String>  NAME = newKey("Name");
    public static final Key<UPerson,Boolean> MALE = newKey("Male");
    public static final Key<UPerson,Date>   BIRTH = newKey("Birth");

}
