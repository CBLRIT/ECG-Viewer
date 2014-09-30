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
 * ----------------------
 * SeriesChangeEvent.java
 * ----------------------
 * (C) Copyright 2001-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 15-Nov-2001 : Version 1 (DG);
 * 18-Aug-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.data.general;

import java.io.Serializable;
import java.util.EventObject;

/**
 * An event with details of a change to a series.
 */
public class SeriesChangeEvent extends EventObject implements Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 1593866085210089052L;

    /**
     * Constructs a new event.
     *
     * @param source  the source of the change event.
     */
    public SeriesChangeEvent(Object source) {
        super(source);
    }

}
