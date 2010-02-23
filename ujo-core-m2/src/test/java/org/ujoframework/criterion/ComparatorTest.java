/*
 * HUnifiedDataObjectTest.java
 * JUnit based test
 *
 * Created on 3. June 2007, 23:00
 */

package org.ujoframework.criterion;

import java.util.ArrayList;
import java.util.List;
import junit.framework.*;
import org.ujoframework.MyTestCase;
import org.ujoframework.core.UjoComparator;
import org.ujoframework.extensions.PathProperty;
import org.ujoframework.criterion.CriteriaTool;
import static org.ujoframework.criterion.Person.*;

/**
 * Criteria test
 * @author Pavel Ponec
 */
public class ComparatorTest extends MyTestCase {
    
    PathProperty<Person,Double> MOTHERS_CASH  = PathProperty.newInstance(MOTHER, CASH);
    PathProperty<Person,String> MOTHERS_NAME  = PathProperty.newInstance(MOTHER, NAME);
    PathProperty<Person,Double> GMOTHERS_CASH = PathProperty.newInstance(MOTHER, MOTHER, CASH);
    
    private List<Person> persons;
    
    public ComparatorTest(String testName) {
        super(testName);
    }
    
    public static TestSuite suite() {
        TestSuite suite = new TestSuite(ComparatorTest.class);
        return suite;
    }
    
    private Person newPerson(String name, Double cash) {
        Person result = new Person();
        result.set(NAME, name);
        result.set(CASH, cash);
        
        persons.add(result);
        return result;
    }
    
    @Override
    protected void setUp() throws Exception {
        persons = new ArrayList<Person>();
        
        Person p = newPerson("John" , 10.0);
        Person m = newPerson("Marry", 20.0);
        Person g = newPerson("Julia", 30.0);
        Person e = newPerson("Eva"  , 40.0);
        
        p.set(MOTHER, m);
        m.set(MOTHER, g);
        g.set(MOTHER, e);
        
    }
    
    @Override
    protected void tearDown() throws Exception {
        persons = null;
    }
    

    
    public void testInit_01() {
        CriteriaTool<Person> uc  = CriteriaTool.newInstance();
        UjoComparator comp = UjoComparator.newInstance(NAME);
        List<Person> result = uc.select(persons, comp);
        
        assertEquals("Eva", result.get(0).get(NAME) );
    }
    
    public void testInit_02() {
        CriteriaTool<Person> uc  = CriteriaTool.newInstance();
        UjoComparator comp = UjoComparator.newInstance(MOTHERS_NAME);
        List<Person> result = uc.select(persons, comp);
        
        assertEquals("Eva", result.get(0).get(MOTHERS_NAME) );
    }
    
    
    
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(suite());
    }
    
}
