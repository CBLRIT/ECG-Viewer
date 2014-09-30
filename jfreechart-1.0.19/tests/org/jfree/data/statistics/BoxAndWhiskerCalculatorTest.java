/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2013, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * --------------------------------
 * BoxAndWhiskerCalculatorTest.java
 * --------------------------------
 * (C) Copyright 2003-2013, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 28-Aug-2003 : Version 1 (DG);
 *
 */

package org.jfree.data.statistics;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * Tests for the {@link BoxAndWhiskerCalculator} class.
 */
public class BoxAndWhiskerCalculatorTest {

    /**
     * Some checks for the calculateBoxAndWhiskerStatistics() method.
     */
    @Test
    public void testCalculateBoxAndWhiskerStatistics() {

        // try null list
        boolean pass = false;
        try {
            BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(null);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);

        // try a list containing a single value
        List values = new ArrayList();
        values.add(new Double(1.1));
        BoxAndWhiskerItem item
            = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(values);
        assertEquals(1.1, item.getMean().doubleValue(), EPSILON);
        assertEquals(1.1, item.getMedian().doubleValue(), EPSILON);
        assertEquals(1.1, item.getQ1().doubleValue(), EPSILON);
        assertEquals(1.1, item.getQ3().doubleValue(), EPSILON);
    }

    private static final double EPSILON = 0.000000001;

    /**
     * Tests the Q1 calculation.
     */
    @Test
    public void testCalculateQ1() {

        // try null argument
        boolean pass = false;
        try {
            BoxAndWhiskerCalculator.calculateQ1(null);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);

        List values = new ArrayList();
        double q1 = BoxAndWhiskerCalculator.calculateQ1(values);
        assertTrue(Double.isNaN(q1));
        values.add(new Double(1.0));
        q1 = BoxAndWhiskerCalculator.calculateQ1(values);
        assertEquals(q1, 1.0, EPSILON);
        values.add(new Double(2.0));
        q1 = BoxAndWhiskerCalculator.calculateQ1(values);
        assertEquals(q1, 1.0, EPSILON);
        values.add(new Double(3.0));
        q1 = BoxAndWhiskerCalculator.calculateQ1(values);
        assertEquals(q1, 1.5, EPSILON);
        values.add(new Double(4.0));
        q1 = BoxAndWhiskerCalculator.calculateQ1(values);
        assertEquals(q1, 1.5, EPSILON);
    }

    /**
     * Tests the Q3 calculation.
     */
    @Test
    public void testCalculateQ3() {
        // try null argument
        boolean pass = false;
        try {
            BoxAndWhiskerCalculator.calculateQ3(null);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);

        List values = new ArrayList();
        double q3 = BoxAndWhiskerCalculator.calculateQ3(values);
        assertTrue(Double.isNaN(q3));
        values.add(new Double(1.0));
        q3 = BoxAndWhiskerCalculator.calculateQ3(values);
        assertEquals(q3, 1.0, EPSILON);
        values.add(new Double(2.0));
        q3 = BoxAndWhiskerCalculator.calculateQ3(values);
        assertEquals(q3, 2.0, EPSILON);
        values.add(new Double(3.0));
        q3 = BoxAndWhiskerCalculator.calculateQ3(values);
        assertEquals(q3, 2.5, EPSILON);
        values.add(new Double(4.0));
        q3 = BoxAndWhiskerCalculator.calculateQ3(values);
        assertEquals(q3, 3.5, EPSILON);
    }

    /**
     * The test case included in bug report 1593149.
     */
    @Test
    public void test1593149() {
        ArrayList theList = new ArrayList(5);
        theList.add(0, new Double(1.0));
        theList.add(1, new Double(2.0));
        theList.add(2, new Double(Double.NaN));
        theList.add(3, new Double(3.0));
        theList.add(4, new Double(4.0));
        BoxAndWhiskerItem theItem =
            BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(theList);
        assertEquals(1.0, theItem.getMinRegularValue().doubleValue(), EPSILON);
        assertEquals(4.0, theItem.getMaxRegularValue().doubleValue(), EPSILON);
    }
}
