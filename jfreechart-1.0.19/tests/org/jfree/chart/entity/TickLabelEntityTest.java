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
 * TickLabelEntityTest.java
 * -------------------------
 * (C) Copyright 2004-2013, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 20-May-2004 : Version 1 (DG);
 *
 */

package org.jfree.chart.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Rectangle2D;

import org.jfree.chart.TestUtilities;
import org.junit.Test;

/**
 * Tests for the {@link TickLabelEntity} class.
 */
public class TickLabelEntityTest {

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    @Test
    public void testEquals() {
        TickLabelEntity e1 = new TickLabelEntity(new Rectangle2D.Double(1.0,
                2.0, 3.0, 4.0), "ToolTip", "URL");
        TickLabelEntity e2 = new TickLabelEntity(new Rectangle2D.Double(1.0,
                2.0, 3.0, 4.0), "ToolTip", "URL");
        assertTrue(e1.equals(e2));

        e1.setArea(new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0));
        assertFalse(e1.equals(e2));
        e2.setArea(new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0));
        assertTrue(e1.equals(e2));

        e1.setToolTipText("New ToolTip");
        assertFalse(e1.equals(e2));
        e2.setToolTipText("New ToolTip");
        assertTrue(e1.equals(e2));

        e1.setURLText("New URL");
        assertFalse(e1.equals(e2));
        e2.setURLText("New URL");
        assertTrue(e1.equals(e2));
    }

    /**
     * Confirm that cloning works.
     */
    @Test
    public void testCloning() throws CloneNotSupportedException {
        TickLabelEntity e1 = new TickLabelEntity(new Rectangle2D.Double(1.0,
                2.0, 3.0, 4.0), "ToolTip", "URL");
        TickLabelEntity e2 = (TickLabelEntity) e1.clone();
        assertTrue(e1 != e2);
        assertTrue(e1.getClass() == e2.getClass());
        assertTrue(e1.equals(e2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    @Test
    public void testSerialization() {
        TickLabelEntity e1 = new TickLabelEntity(new Rectangle2D.Double(1.0,
                2.0, 3.0, 4.0), "ToolTip", "URL");
        TickLabelEntity e2 = (TickLabelEntity) TestUtilities.serialised(e1);
        assertEquals(e1, e2);
    }

}
