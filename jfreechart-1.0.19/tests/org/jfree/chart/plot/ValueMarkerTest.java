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
 * --------------------
 * ValueMarkerTest.java
 * --------------------
 * (C) Copyright 2003-2013, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 18-Aug-2003 : Version 1 (DG);
 * 14-Jun-2004 : Renamed MarkerTests --> ValueMarkerTests (DG);
 * 01-Jun-2005 : Strengthened equals() test (DG);
 * 05-Sep-2006 : Added checks for MarkerChangeEvent generation (DG);
 * 26-Sep-2007 : Added test1802195() method (DG);
 * 08-Oct-2007 : Added test1808376() method (DG);
 *
 */

package org.jfree.chart.plot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Stroke;

import org.jfree.chart.TestUtilities;

import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.event.MarkerChangeListener;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.junit.Test;

/**
 * Tests for the {@link ValueMarker} class.
 */
public class ValueMarkerTest implements MarkerChangeListener {

    MarkerChangeEvent lastEvent;

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    @Test
    public void testEquals() {

        Marker m1 = new ValueMarker(45.0);
        Marker m2 = new ValueMarker(45.0);
        assertTrue(m1.equals(m2));
        assertTrue(m2.equals(m1));

        m1.setPaint(new GradientPaint(1.0f, 2.0f, Color.green,
                3.0f, 4.0f, Color.red));
        assertFalse(m1.equals(m2));
        m2.setPaint(new GradientPaint(1.0f, 2.0f, Color.green,
                3.0f, 4.0f, Color.red));
        assertTrue(m1.equals(m2));

        BasicStroke stroke = new BasicStroke(2.2f);
        m1.setStroke(stroke);
        assertFalse(m1.equals(m2));
        m2.setStroke(stroke);
        assertTrue(m1.equals(m2));

        m1.setOutlinePaint(new GradientPaint(4.0f, 3.0f, Color.yellow,
                2.0f, 1.0f, Color.white));
        assertFalse(m1.equals(m2));
        m2.setOutlinePaint(new GradientPaint(4.0f, 3.0f, Color.yellow,
                2.0f, 1.0f, Color.white));
        assertTrue(m1.equals(m2));

        m1.setOutlineStroke(stroke);
        assertFalse(m1.equals(m2));
        m2.setOutlineStroke(stroke);
        assertTrue(m1.equals(m2));

        m1.setAlpha(0.1f);
        assertFalse(m1.equals(m2));
        m2.setAlpha(0.1f);
        assertTrue(m1.equals(m2));

        m1.setLabel("New Label");
        assertFalse(m1.equals(m2));
        m2.setLabel("New Label");
        assertTrue(m1.equals(m2));

        m1.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        assertFalse(m1.equals(m2));
        m2.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        assertTrue(m1.equals(m2));

        m1.setLabelPaint(new GradientPaint(1.0f, 2.0f, Color.blue,
                3.0f, 4.0f, Color.yellow));
        assertFalse(m1.equals(m2));
        m2.setLabelPaint(new GradientPaint(1.0f, 2.0f, Color.blue,
                3.0f, 4.0f, Color.yellow));
        assertTrue(m1.equals(m2));

        m1.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        assertFalse(m1.equals(m2));
        m2.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        assertTrue(m1.equals(m2));

        m1.setLabelTextAnchor(TextAnchor.BASELINE_RIGHT);
        assertFalse(m1.equals(m2));
        m2.setLabelTextAnchor(TextAnchor.BASELINE_RIGHT);
        assertTrue(m1.equals(m2));

        m1.setLabelOffset(new RectangleInsets(10.0, 10.0, 10.0, 10.0));
        assertFalse(m1.equals(m2));
        m2.setLabelOffset(new RectangleInsets(10.0, 10.0, 10.0, 10.0));
        assertTrue(m1.equals(m2));

        m1.setLabelOffsetType(LengthAdjustmentType.EXPAND);
        assertFalse(m1.equals(m2));
        m2.setLabelOffsetType(LengthAdjustmentType.EXPAND);
        assertTrue(m1.equals(m2));

        m1 = new ValueMarker(12.3);
        m2 = new ValueMarker(45.6);
        assertFalse(m1.equals(m2));
        m2 = new ValueMarker(12.3);
        assertTrue(m1.equals(m2));

    }

    /**
     * Confirm that cloning works.
     */
    @Test
    public void testCloning() throws CloneNotSupportedException {
        ValueMarker m1 = new ValueMarker(25.0);
        ValueMarker m2 = (ValueMarker) m1.clone();
        assertTrue(m1 != m2);
        assertTrue(m1.getClass() == m2.getClass());
        assertTrue(m1.equals(m2));
    }

   /**
     * Serialize an instance, restore it, and check for equality.
     */
    @Test
    public void testSerialization() {
        ValueMarker m1 = new ValueMarker(25.0);
        ValueMarker m2 = (ValueMarker) TestUtilities.serialised(m1);
        assertEquals(m1, m2);
    }

    private static final double EPSILON = 0.000000001;

    /**
     * Some checks for the getValue() and setValue() methods.
     */
    @Test
    public void testGetSetValue() {
        ValueMarker m = new ValueMarker(1.1);
        m.addChangeListener(this);
        this.lastEvent = null;
        assertEquals(1.1, m.getValue(), EPSILON);
        m.setValue(33.3);
        assertEquals(33.3, m.getValue(), EPSILON);
        assertEquals(m, this.lastEvent.getMarker());
    }

    /**
     * Records the last event.
     *
     * @param event  the last event.
     */
    @Override
    public void markerChanged(MarkerChangeEvent event) {
        this.lastEvent = event;
    }

    /**
     * A test for bug 1802195.
     */
    @Test
    public void test1802195() {
        ValueMarker m1 = new ValueMarker(25.0);
        ValueMarker m2 = (ValueMarker) TestUtilities.serialised(m1);
        assertEquals(m1, m2);
        try {
            m2.setValue(-10.0);
        }
        catch (NullPointerException e) {
            fail("No exception should be thrown.");
        }
    }

    /**
     * A test for bug report 1808376.
     */
    @Test
    public void test1808376() {
        Stroke stroke = new BasicStroke(1.0f);
        Stroke outlineStroke = new BasicStroke(2.0f);
        ValueMarker m = new ValueMarker(1.0, Color.red, stroke, Color.blue,
                outlineStroke, 0.5f);
        assertEquals(1.0, m.getValue(), EPSILON);
        assertEquals(Color.red, m.getPaint());
        assertEquals(stroke, m.getStroke());
        assertEquals(Color.blue, m.getOutlinePaint());
        assertEquals(outlineStroke, m.getOutlineStroke());
        assertEquals(0.5f, m.getAlpha(), EPSILON);
    }

}
