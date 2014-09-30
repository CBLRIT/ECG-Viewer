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
 * LookupPaintScaleTest.java
 * -------------------------
 * (C) Copyright 2006-2013, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 05-Jul-2006 : Version 1 (DG);
 * 31-Jan-2007 : Additional serialization tests (DG);
 * 07-Mar-2007 : Added new tests (DG);
 * 09-Mar-2007 : Check independence in testCloning() (DG);
 *
 */

package org.jfree.chart.renderer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.GradientPaint;

import org.jfree.chart.TestUtilities;
import org.junit.Test;

/**
 * Tests for the {@link LookupPaintScale} class.
 */
public class LookupPaintScaleTest {

    /**
     * A test for the equals() method.
     */
    @Test
    public void testEquals() {
        LookupPaintScale g1 = new LookupPaintScale();
        LookupPaintScale g2 = new LookupPaintScale();
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));

        g1 = new LookupPaintScale(1.0, 2.0, Color.red);
        assertFalse(g1.equals(g2));
        g2 = new LookupPaintScale(1.0, 2.0, Color.red);
        assertTrue(g1.equals(g2));

        g1.add(1.5, new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f,
                Color.blue));
        assertFalse(g1.equals(g2));
        g2.add(1.5, new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f,
                Color.blue));
        assertTrue(g1.equals(g2));
    }

    /**
     * Confirm that cloning works.
     */
    @Test
    public void testCloning() throws CloneNotSupportedException {
        LookupPaintScale g1 = new LookupPaintScale();
        LookupPaintScale g2 = (LookupPaintScale) g1.clone();
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));

        // check independence
        g1.add(0.5, Color.red);
        assertFalse(g1.equals(g2));
        g2.add(0.5, Color.red);
        assertTrue(g1.equals(g2));

        // try with gradient paint
        g1 = new LookupPaintScale(1.0, 2.0, new GradientPaint(1.0f, 2.0f,
                Color.red, 3.0f, 4.0f, Color.green));
        g1.add(1.5, new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f,
                Color.blue));
        g2 = (LookupPaintScale) g1.clone();
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    @Test
    public void testSerialization() {
        LookupPaintScale g1 = new LookupPaintScale();
        LookupPaintScale g2 = (LookupPaintScale) TestUtilities.serialised(g1);
        assertEquals(g1, g2);

        g1 = new LookupPaintScale(1.0, 2.0, new GradientPaint(1.0f, 2.0f,
                Color.red, 3.0f, 4.0f, Color.yellow));
        g1.add(1.5, new GradientPaint(1.1f, 2.2f, Color.red, 3.3f, 4.4f,
                Color.blue));
        g2 = (LookupPaintScale) TestUtilities.serialised(g1);
        assertEquals(g1, g2);
    }

    private static final double EPSILON = 0.0000000001;

    /**
     * Some checks for the default constructor.
     */
    @Test
    public void testConstructor1() {
        LookupPaintScale s = new LookupPaintScale();
        assertEquals(0.0, s.getLowerBound(), EPSILON);
        assertEquals(1.0, s.getUpperBound(), EPSILON);
    }

    /**
     * Some checks for the other constructor.
     */
    @Test
    public void testConstructor2() {
        LookupPaintScale s = new LookupPaintScale(1.0, 2.0, Color.red);
        assertEquals(1.0, s.getLowerBound(), EPSILON);
        assertEquals(2.0, s.getUpperBound(), EPSILON);
        assertEquals(Color.red, s.getDefaultPaint());
    }

    /**
     * Some general checks for the lookup table.
     */
    @Test
    public void testGeneral() {

        LookupPaintScale s = new LookupPaintScale(0.0, 100.0, Color.black);
        assertEquals(Color.black, s.getPaint(-1.0));
        assertEquals(Color.black, s.getPaint(0.0));
        assertEquals(Color.black, s.getPaint(50.0));
        assertEquals(Color.black, s.getPaint(100.0));
        assertEquals(Color.black, s.getPaint(101.0));

        s.add(50.0, Color.blue);
        assertEquals(Color.black, s.getPaint(-1.0));
        assertEquals(Color.black, s.getPaint(0.0));
        assertEquals(Color.blue, s.getPaint(50.0));
        assertEquals(Color.blue, s.getPaint(100.0));
        assertEquals(Color.black, s.getPaint(101.0));

        s.add(50.0, Color.red);
        assertEquals(Color.black, s.getPaint(-1.0));
        assertEquals(Color.black, s.getPaint(0.0));
        assertEquals(Color.red, s.getPaint(50.0));
        assertEquals(Color.red, s.getPaint(100.0));
        assertEquals(Color.black, s.getPaint(101.0));

        s.add(25.0, Color.green);
        assertEquals(Color.black, s.getPaint(-1.0));
        assertEquals(Color.black, s.getPaint(0.0));
        assertEquals(Color.green, s.getPaint(25.0));
        assertEquals(Color.red, s.getPaint(50.0));
        assertEquals(Color.red, s.getPaint(100.0));
        assertEquals(Color.black, s.getPaint(101.0));

        s.add(75.0, Color.yellow);
        assertEquals(Color.black, s.getPaint(-1.0));
        assertEquals(Color.black, s.getPaint(0.0));
        assertEquals(Color.green, s.getPaint(25.0));
        assertEquals(Color.red, s.getPaint(50.0));
        assertEquals(Color.yellow, s.getPaint(75.0));
        assertEquals(Color.yellow, s.getPaint(100.0));
        assertEquals(Color.black, s.getPaint(101.0));
    }

}
