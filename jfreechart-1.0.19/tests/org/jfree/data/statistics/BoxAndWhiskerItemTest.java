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
 * --------------------------
 * BoxAndWhiskerItemTest.java
 * --------------------------
 * (C) Copyright 2004-2013, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 01-Mar-2004 : Version 1 (DG);
 *
 */

package org.jfree.data.statistics;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.jfree.chart.TestUtilities;
import org.junit.Test;

/**
 * Tests for the {@link BoxAndWhiskerItem} class.
 */
public class BoxAndWhiskerItemTest {

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    @Test
    public void testEquals() {

        BoxAndWhiskerItem i1 = new BoxAndWhiskerItem(new Double(1.0), 
                new Double(2.0), new Double(3.0), new Double(4.0),
                new Double(5.0), new Double(6.0), new Double(7.0), 
                new Double(8.0), new ArrayList());
        BoxAndWhiskerItem i2 = new BoxAndWhiskerItem(new Double(1.0), 
                new Double(2.0), new Double(3.0), new Double(4.0),
                new Double(5.0), new Double(6.0), new Double(7.0), 
                new Double(8.0), new ArrayList());
        assertTrue(i1.equals(i2));
        assertTrue(i2.equals(i1));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    @Test
    public void testSerialization() {
        BoxAndWhiskerItem i1 = new BoxAndWhiskerItem(new Double(1.0), 
                new Double(2.0), new Double(3.0), new Double(4.0),
                new Double(5.0), new Double(6.0), new Double(7.0), 
                new Double(8.0), new ArrayList());
        BoxAndWhiskerItem i2 = (BoxAndWhiskerItem) TestUtilities.serialised(i1);
        assertEquals(i1, i2);
    }

}
