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
 * ------------------------
 * ItemLabelAnchorTest.java
 * ------------------------
 * (C) Copyright 2004-2013, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 19-Feb-2004 : Version 1 (DG);
 *
 */

package org.jfree.chart.labels;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jfree.chart.TestUtilities;
import org.junit.Test;

/**
 * Tests for the {@link ItemLabelAnchor} class.
 */
public class ItemLabelAnchorTest {

    /**
     * Test the equals() method.
     */
    @Test
    public void testEquals() {
        assertTrue(ItemLabelAnchor.INSIDE1.equals(ItemLabelAnchor.INSIDE1));
        assertFalse(ItemLabelAnchor.INSIDE1.equals(ItemLabelAnchor.INSIDE2));
    }

    /**
     * Serialize an instance, restore it, and check for identity.
     */
    @Test
    public void testSerialization() {
        ItemLabelAnchor a1 = ItemLabelAnchor.INSIDE1;
        ItemLabelAnchor a2 = (ItemLabelAnchor) TestUtilities.serialised(a1);
        assertTrue(a1 == a2);
    }

}
