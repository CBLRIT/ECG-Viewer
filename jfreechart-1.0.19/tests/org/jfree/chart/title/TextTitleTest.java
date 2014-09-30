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
 * ------------------
 * TextTitleTest.java
 * ------------------
 * (C) Copyright 2004-2013, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 17-Feb-2004 : Version 1 (DG);
 * 06-Jun-2005 : Use GradientPaint in equals() test (DG);
 * 07-Oct-2005 : Updated testEquals() (DG);
 * 28-Apr-2008 : Extended testEquals() (DG);
 *
 */

package org.jfree.chart.title;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;

import org.jfree.chart.TestUtilities;

import org.jfree.ui.HorizontalAlignment;
import org.junit.Test;

/**
 * Tests for the {@link TextTitle} class.
 */
public class TextTitleTest {

    /**
     * Check that the equals() method distinguishes all fields.
     */
    @Test
    public void testEquals() {
        TextTitle t1 = new TextTitle();
        TextTitle t2 = new TextTitle();
        assertEquals(t1, t2);

        t1.setText("Test 1");
        assertFalse(t1.equals(t2));
        t2.setText("Test 1");
        assertTrue(t1.equals(t2));

        Font f = new Font("SansSerif", Font.PLAIN, 15);
        t1.setFont(f);
        assertFalse(t1.equals(t2));
        t2.setFont(f);
        assertTrue(t1.equals(t2));

        t1.setTextAlignment(HorizontalAlignment.RIGHT);
        assertFalse(t1.equals(t2));
        t2.setTextAlignment(HorizontalAlignment.RIGHT);
        assertTrue(t1.equals(t2));

        // paint
        t1.setPaint(new GradientPaint(1.0f, 2.0f, Color.red,
                3.0f, 4.0f, Color.blue));
        assertFalse(t1.equals(t2));
        t2.setPaint(new GradientPaint(1.0f, 2.0f, Color.red,
                3.0f, 4.0f, Color.blue));
        assertTrue(t1.equals(t2));

        // backgroundPaint
        t1.setBackgroundPaint(new GradientPaint(4.0f, 3.0f, Color.red,
                2.0f, 1.0f, Color.blue));
        assertFalse(t1.equals(t2));
        t2.setBackgroundPaint(new GradientPaint(4.0f, 3.0f, Color.red,
                2.0f, 1.0f, Color.blue));
        assertTrue(t1.equals(t2));

        // maximumLinesToDisplay
        t1.setMaximumLinesToDisplay(3);
        assertFalse(t1.equals(t2));
        t2.setMaximumLinesToDisplay(3);
        assertTrue(t1.equals(t2));

        // toolTipText
        t1.setToolTipText("TTT");
        assertFalse(t1.equals(t2));
        t2.setToolTipText("TTT");
        assertTrue(t1.equals(t2));

        // urlText
        t1.setURLText(("URL"));
        assertFalse(t1.equals(t2));
        t2.setURLText(("URL"));
        assertTrue(t1.equals(t2));

        // expandToFitSpace
        t1.setExpandToFitSpace(!t1.getExpandToFitSpace());
        assertFalse(t1.equals(t2));
        t2.setExpandToFitSpace(!t2.getExpandToFitSpace());
        assertTrue(t1.equals(t2));

    }

    /**
     * Two objects that are equal are required to return the same hashCode.
     */
    @Test
    public void testHashcode() {
        TextTitle t1 = new TextTitle();
        TextTitle t2 = new TextTitle();
        assertTrue(t1.equals(t2));
        int h1 = t1.hashCode();
        int h2 = t2.hashCode();
        assertEquals(h1, h2);
    }

    /**
     * Confirm that cloning works.
     */
    @Test
    public void testCloning() throws CloneNotSupportedException {
        TextTitle t1 = new TextTitle();
        TextTitle t2 = (TextTitle) t1.clone();
        assertTrue(t1 != t2);
        assertTrue(t1.getClass() == t2.getClass());
        assertTrue(t1.equals(t2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    @Test
    public void testSerialization() {
        TextTitle t1 = new TextTitle("Test");
        TextTitle t2 = (TextTitle) TestUtilities.serialised(t1);
        assertEquals(t1, t2);
    }

}
