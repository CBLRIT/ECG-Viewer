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
 * -------------------------
 * SimpleTimePeriodTest.java
 * -------------------------
 * (C) Copyright 2003-2013, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 13-Mar-2003 : Version 1 (DG);
 * 21-Oct-2003 : Added hashCode() test (DG);
 * 02-Jun-2008 : Added a test for immutability (DG);
 *
 */

package org.jfree.data.time;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.jfree.chart.TestUtilities;
import org.junit.Test;

/**
 * Tests for the {@link SimpleTimePeriod} class.
 */
public class SimpleTimePeriodTest {

    /**
     * Check that an instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    @Test
    public void testEqualsSelf() {
        SimpleTimePeriod p = new SimpleTimePeriod(new Date(1000L),
                new Date(1001L));
        assertTrue(p.equals(p));
    }

    /**
     * Test the equals() method.
     */
    @Test
    public void testEquals() {
        SimpleTimePeriod p1 = new SimpleTimePeriod(new Date(1000L),
                new Date(1004L));
        SimpleTimePeriod p2 = new SimpleTimePeriod(new Date(1000L),
                new Date(1004L));
        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));

        p1 = new SimpleTimePeriod(new Date(1002L), new Date(1004L));
        assertFalse(p1.equals(p2));
        p2 = new SimpleTimePeriod(new Date(1002L), new Date(1004L));
        assertTrue(p1.equals(p2));

        p1 = new SimpleTimePeriod(new Date(1002L), new Date(1003L));
        assertFalse(p1.equals(p2));
        p2 = new SimpleTimePeriod(new Date(1002L), new Date(1003L));
        assertTrue(p1.equals(p2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    @Test
    public void testSerialization() {
        SimpleTimePeriod p1 = new SimpleTimePeriod(new Date(1000L),
                new Date(1001L));
        SimpleTimePeriod p2 = (SimpleTimePeriod) TestUtilities.serialised(p1);
        assertEquals(p1, p2);
    }

    /**
     * Two objects that are equal are required to return the same hashCode.
     */
    @Test
    public void testHashcode() {
        SimpleTimePeriod s1 = new SimpleTimePeriod(new Date(10L),
                new Date(20L));
        SimpleTimePeriod s2 = new SimpleTimePeriod(new Date(10L),
                new Date(20L));
        assertTrue(s1.equals(s2));
        int h1 = s1.hashCode();
        int h2 = s2.hashCode();
        assertEquals(h1, h2);
    }

    /**
     * This class is immutable, so it should not implement Cloneable.
     */
    @Test
    public void testClone() {
        SimpleTimePeriod s1 = new SimpleTimePeriod(new Date(10L),
                new Date(20));
        assertFalse(s1 instanceof Cloneable);
    }

    /**
     * Some simple checks for immutability.
     */
    @Test
    public void testImmutable() {
        SimpleTimePeriod p1 = new SimpleTimePeriod(new Date(10L),
                new Date(20L));
        SimpleTimePeriod p2 = new SimpleTimePeriod(new Date(10L),
                new Date(20L));
        assertEquals(p1, p2);
        p1.getStart().setTime(11L);
        assertEquals(p1, p2);

        Date d1 = new Date(10L);
        Date d2 = new Date(20L);
        p1 = new SimpleTimePeriod(d1, d2);
        d1.setTime(11L);
        assertEquals(new Date(10L), p1.getStart());
    }

    /**
     * Some checks for the compareTo() method.
     */
    @Test
    public void testCompareTo() {
        SimpleTimePeriod s1 = new SimpleTimePeriod(new Date(10L),
                new Date(20L));
        SimpleTimePeriod s2 = new SimpleTimePeriod(new Date(10L),
                new Date(20L));
        assertEquals(0, s1.compareTo(s2));

        s1 = new SimpleTimePeriod(new Date(9L), new Date(21L));
        s2 = new SimpleTimePeriod(new Date(10L), new Date(20L));
        assertEquals(-1, s1.compareTo(s2));

        s1 = new SimpleTimePeriod(new Date(11L), new Date(19L));
        s2 = new SimpleTimePeriod(new Date(10L), new Date(20L));
        assertEquals(1, s1.compareTo(s2));

        s1 = new SimpleTimePeriod(new Date(9L), new Date(19L));
        s2 = new SimpleTimePeriod(new Date(10L), new Date(20L));
        assertEquals(-1, s1.compareTo(s2));

        s1 = new SimpleTimePeriod(new Date(11L), new Date(21));
        s2 = new SimpleTimePeriod(new Date(10L), new Date(20L));
        assertEquals(1, s1.compareTo(s2));

        s1 = new SimpleTimePeriod(new Date(10L), new Date(18));
        s2 = new SimpleTimePeriod(new Date(10L), new Date(20L));
        assertEquals(-1, s1.compareTo(s2));

        s1 = new SimpleTimePeriod(new Date(10L), new Date(22));
        s2 = new SimpleTimePeriod(new Date(10L), new Date(20L));
        assertEquals(1, s1.compareTo(s2));
    }

}
