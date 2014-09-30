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
 * ----------------------------
 * TimeTableXYDatasetTests.java
 * ----------------------------
 * (C) Copyright 2004-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Rob Eden;
 *
 * Changes
 * -------
 * 15-Sep-2004 : Version 1 (DG);
 * 25-Jul-2007 : Added test for clear() method, by Rob Eden (DG);
 *
 */

package org.jfree.data.time;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.util.TimeZone;

import org.jfree.chart.TestUtilities;
import org.junit.Test;

/**
 * A collection of test cases for the {@link TimeTableXYDataset} class.
 */
public class TimeTableXYDatasetTest {

    private static final double DELTA = 0.0000000001;

    /**
     * Some checks for a simple dataset.
     */
    @Test
    public void testStandard() {
        TimeTableXYDataset d = new TimeTableXYDataset();
        d.add(new Year(1999), 1.0, "Series 1");
        assertEquals(d.getItemCount(), 1);
        assertEquals(d.getSeriesCount(), 1);
        d.add(new Year(2000), 2.0, "Series 2");
        assertEquals(d.getItemCount(), 2);
        assertEquals(d.getSeriesCount(), 2);
        assertEquals(d.getYValue(0, 0), 1.0, DELTA);
        assertTrue(Double.isNaN(d.getYValue(0, 1)));
        assertTrue(Double.isNaN(d.getYValue(1, 0)));
        assertEquals(d.getYValue(1, 1), 2.0, DELTA);
    }

    /**
     * Some checks for the getTimePeriod() method.
     */
    @Test
    public void testGetTimePeriod()  {
        TimeTableXYDataset d = new TimeTableXYDataset();
        d.add(new Year(1999), 1.0, "Series 1");
        d.add(new Year(1998), 2.0, "Series 1");
        d.add(new Year(1996), 3.0, "Series 1");
        assertEquals(d.getTimePeriod(0), new Year(1996));
        assertEquals(d.getTimePeriod(1), new Year(1998));
        assertEquals(d.getTimePeriod(2), new Year(1999));
    }

    /**
     * Some checks for the equals() method.
     */
    @Test
    public void testEquals() {
        TimeTableXYDataset d1 = new TimeTableXYDataset();
        TimeTableXYDataset d2 = new TimeTableXYDataset();
        assertTrue(d1.equals(d2));
        assertTrue(d2.equals(d1));

        d1.add(new Year(1999), 123.4, "S1");
        assertFalse(d1.equals(d2));
        d2.add(new Year(1999), 123.4, "S1");
        assertTrue(d1.equals(d2));

        d1.setDomainIsPointsInTime(!d1.getDomainIsPointsInTime());
        assertFalse(d1.equals(d2));
        d2.setDomainIsPointsInTime(!d2.getDomainIsPointsInTime());
        assertTrue(d1.equals(d2));

        d1 = new TimeTableXYDataset(TimeZone.getTimeZone("GMT"));
        d2 = new TimeTableXYDataset(TimeZone.getTimeZone(
                "America/Los_Angeles"));
        assertFalse(d1.equals(d2));
    }

    /**
     * Some checks for cloning.
     */
    @Test
    public void testClone() {

        TimeTableXYDataset d = new TimeTableXYDataset();
        d.add(new Year(1999), 25.0, "Series");

        TimeTableXYDataset clone = null;
        try {
            clone = (TimeTableXYDataset) d.clone();
        }
        catch (CloneNotSupportedException e) {
            assertTrue(false);
        }
        assertTrue(clone.equals(d));

        // now test that the clone is independent of the original
        clone.add(new Year(2004), 1.2, "SS");
        assertFalse(clone.equals(d));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    @Test
    public void testSerialization() {
        TimeTableXYDataset d1 = new TimeTableXYDataset();
        d1.add(new Year(1999), 123.4, "S1");
        TimeTableXYDataset d2 = (TimeTableXYDataset) 
                TestUtilities.serialised(d1);
        assertTrue(d1.equals(d2));
    }

    /**
     * Test clearing data.
     */
    @Test
    public void testClear() {
        TimeTableXYDataset d = new TimeTableXYDataset();
        d.add(new Year(1999), 1.0, "Series 1");
        assertEquals(d.getItemCount(), 1);
        assertEquals(d.getSeriesCount(), 1);
        d.add(new Year(2000), 2.0, "Series 2");

        d.clear();
        // Make sure there's nothing left
        assertEquals(0, d.getItemCount());
        assertEquals(0, d.getSeriesCount());
    }

}
