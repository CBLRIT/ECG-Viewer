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
 * ------------------------------------
 * StandardPieToolTipGeneratorTest.java
 * ------------------------------------
 * (C) Copyright 2003-2013, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 18-Mar-2003 : Version 1 (DG);
 * 13-Aug-2003 : Added clone tests (DG);
 * 04-Mar-2004 : Added test for equals() method (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 03-May-2006 : Extended test for clone() method (DG);
 * 03-May-2006 : Renamed StandardPieItemLabelGeneratorTests
 *               --> StandardPieToolTipGeneratorTests (DG);
 * 23-Apr-2008 : Added testPublicCloneable() (DG);
 *
 */

package org.jfree.chart.labels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jfree.chart.TestUtilities;

import org.jfree.util.PublicCloneable;
import org.junit.Test;

/**
 * Tests for the {@link StandardPieToolTipGenerator} class.
 */
public class StandardPieToolTipGeneratorTest {

    /**
     * Test that the equals() method distinguishes all fields.
     */
    @Test
    public void testEquals() {
        StandardPieToolTipGenerator g1 = new StandardPieToolTipGenerator();
        StandardPieToolTipGenerator g2 = new StandardPieToolTipGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));

        g1 = new StandardPieToolTipGenerator("{0}",
                new DecimalFormat("#,##0.00"),
                NumberFormat.getPercentInstance());
        assertFalse(g1.equals(g2));
        g2 = new StandardPieToolTipGenerator("{0}",
                new DecimalFormat("#,##0.00"),
                NumberFormat.getPercentInstance());
        assertTrue(g1.equals(g2));

        g1 = new StandardPieToolTipGenerator("{0} {1}",
                new DecimalFormat("#,##0.00"),
                NumberFormat.getPercentInstance());
        assertFalse(g1.equals(g2));
        g2 = new StandardPieToolTipGenerator("{0} {1}",
                new DecimalFormat("#,##0.00"),
                NumberFormat.getPercentInstance());
        assertTrue(g1.equals(g2));

        g1 = new StandardPieToolTipGenerator("{0} {1}",
                new DecimalFormat("#,##0"), NumberFormat.getPercentInstance());
        assertFalse(g1.equals(g2));
        g2 = new StandardPieToolTipGenerator("{0} {1}",
                new DecimalFormat("#,##0"), NumberFormat.getPercentInstance());
        assertTrue(g1.equals(g2));

        g1 = new StandardPieToolTipGenerator("{0} {1}",
                new DecimalFormat("#,##0"), new DecimalFormat("0.000%"));
        assertFalse(g1.equals(g2));
        g2 = new StandardPieToolTipGenerator("{0} {1}",
                new DecimalFormat("#,##0"), new DecimalFormat("0.000%"));
        assertTrue(g1.equals(g2));
    }

    /**
     * Simple check that hashCode is implemented.
     */
    @Test
    public void testHashCode() {
        StandardPieToolTipGenerator g1
                = new StandardPieToolTipGenerator();
        StandardPieToolTipGenerator g2
                = new StandardPieToolTipGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g1.hashCode() == g2.hashCode());
    }

    /**
     * Some checks for cloning.
     */
    @Test
    public void testCloning() throws CloneNotSupportedException {
        StandardPieToolTipGenerator g1 = new StandardPieToolTipGenerator();
        StandardPieToolTipGenerator g2 = (StandardPieToolTipGenerator) 
                g1.clone();
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
        assertTrue(g1.getNumberFormat() != g2.getNumberFormat());
        assertTrue(g1.getPercentFormat() != g2.getPercentFormat());
    }

    /**
     * Check to ensure that this class implements PublicCloneable.
     */
    @Test
    public void testPublicCloneable() {
        StandardPieToolTipGenerator g1 = new StandardPieToolTipGenerator();
        assertTrue(g1 instanceof PublicCloneable);
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    @Test
    public void testSerialization() {
        StandardPieToolTipGenerator g1 = new StandardPieToolTipGenerator();
        StandardPieToolTipGenerator g2 = (StandardPieToolTipGenerator)
                TestUtilities.serialised(g1);
        assertEquals(g1, g2);
    }

}
