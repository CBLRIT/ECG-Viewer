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
 * -------------------
 * SwtPaintCanvas.java
 * -------------------
 * (C) Copyright 2000-2007, by Object Refinery Limited.
 *
 * Original Author:  Henry Proudhon (henry.proudhon AT ensmp.fr);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * Changes
 * -------
 * 04-Aug-2006 : New class (HP);
 * 25-Oct-2007 : Fixed Eclipse warnings (DG);
 * 
 */

package org.jfree.experimental.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * A paint canvas.
 */
public class SWTPaintCanvas extends Canvas {
    private Color myColor;
    
    /**
     * Creates a new instance.
     * 
     * @param parent  the parent.
     * @param style  the style.
     * @param color  the color.
     */
    public SWTPaintCanvas(Composite parent, int style, Color color) {
        this(parent, style);
        setColor(color);
    }
    
    /**
     * Creates a new instance.
     * 
     * @param parent  the parent.
     * @param style  the style.
     */
    public SWTPaintCanvas(Composite parent, int style) {
        super(parent, style);
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                e.gc.setForeground(e.gc.getDevice().getSystemColor(
                        SWT.COLOR_BLACK));
                e.gc.setBackground(SWTPaintCanvas.this.myColor);
                e.gc.fillRectangle(getClientArea());
                e.gc.drawRectangle(getClientArea().x, getClientArea().y, 
                        getClientArea().width - 1, getClientArea().height - 1);
            }
        });
    }
    
    /**
     * Sets the color.
     * 
     * @param color  the color.
     */
    public void setColor(Color color) {
        if (this.myColor != null) {
            this.myColor.dispose();
        }
        //this.myColor = new Color(getDisplay(), color.getRGB());
        this.myColor = color;
    }

    /**
     * Returns the color.
     * 
     * @return The color.
     */
    public Color getColor() {
        return this.myColor;
    }
    
    /**
     * Overridden to do nothing.
     * 
     * @param c  the color.
     */
    public void setBackground(Color c) {
        return;
    }

    /**
     * Overridden to do nothing.
     * 
     * @param c  the color.
     */
    public void setForeground(Color c) {
        return;
    }
    
    /**
     * Frees resources.
     */
    public void dispose() {
        this.myColor.dispose();
    }
}
