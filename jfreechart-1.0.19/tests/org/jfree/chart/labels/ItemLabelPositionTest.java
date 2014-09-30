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
 * ItemLabelPositionTest.java
 * --------------------------
 * (C) Copyright 2003-2013, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 30-Oct-2003 : Version 1 (DG);
 * 19-Feb-2004 : Moved to org.jfree.chart.labels.junit (DG);
 *
 */

package org.jfree.chart.labels;

import static org.junit.Assert.assertEquals;

import org.jfree.chart.TestUtilities;
import org.junit.Test;

/**
 * Tests for the {@link ItemLabelPosition} class.
 */
public class ItemLabelPositionTest {

    /**
     * Check that the equals() method distinguishes all fields.
     */
    @Test
    public void testEquals() {
        ItemLabelPosition p1 = new ItemLabelPosition();
        ItemLabelPosition p2 = new ItemLabelPosition();
        assertEquals(p1, p2);
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    @Test
    public void testSerialization() {
        ItemLabelPosition p1 = new ItemLabelPosition();
        ItemLabelPosition p2 = (ItemLabelPosition) TestUtilities.serialised(p1);
        assertEquals(p1, p2);
    }

}
