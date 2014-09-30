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
 * ---------------
 * DomainInfo.java
 * ---------------
 * (C) Copyright 2000-2013, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 15-Nov-2001 : Moved to package com.jrefinery.data.* (DG);
 *               Updated Javadoc comments (DG);
 * 22-Apr-2002 : Added getValueRange() method (DG);
 * 12-Jul-2002 : Renamed getValueRange() --> getDomainRange() (DG);
 * 06-Oct-2004 : Renamed getDomainRange() --> getDomainBounds() (DG);
 * 17-Nov-2004 : Added 'includeInterval' argument to all methods (DG);
 * 11-Jan-2005 : Removed deprecated code in preparation for the 1.0.0
 *               release (DG);
 * 02-Aug-2013 : Updated Javadocs from David Tonhofer (bug 1117) (DG);
 *
 */

package org.jfree.data;

/**
 * An interface (optional) that can be implemented by a dataset to assist in
 * determining the minimum and maximum values.  If not present, 
 * {@link org.jfree.data.general.DatasetUtilities} will iterate over all the 
 * values in the dataset to get the bounds.
 */
public interface DomainInfo {

    /**
     * Returns the minimum x-value in the dataset.
     *
     * @param includeInterval  a flag that determines whether or not the
     *                         x-interval is taken into account.
     *
     * @return The minimum value or <code>Double.NaN</code> if there are no 
     *     values.
     */
    public double getDomainLowerBound(boolean includeInterval);

    /**
     * Returns the maximum x-value in the dataset.
     *
     * @param includeInterval  a flag that determines whether or not the
     *                         x-interval is taken into account.
     *
     * @return The maximum value or <code>Double.NaN</code> if there are no 
     *     values.
     */
    public double getDomainUpperBound(boolean includeInterval);

    /**
     * Returns the range of the values in this dataset's domain.
     *
     * @param includeInterval  a flag that determines whether or not the
     *                         x-interval is taken into account.
     *
     * @return The range (or <code>null</code> if the dataset contains no
     *     values).
     */
    public Range getDomainBounds(boolean includeInterval);

}
