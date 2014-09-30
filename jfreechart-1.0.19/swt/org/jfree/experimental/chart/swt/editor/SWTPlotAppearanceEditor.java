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
 * ----------------------------
 * SWTPlotAppearanceEditor.java
 * ----------------------------
 * (C) Copyright 2006-2008, by Henry Proudhon and Contributors.
 *
 * Original Author:  Henry Proudhon (henry.proudhon AT ensmp.fr);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * Changes
 * -------
 * 01-Aug-2006 : New class (HP);
 * 18-Dec-2008 : Use ResourceBundleWrapper - see patch 1607918 by
 *               Jess Thrysoee (DG);
 *
 */

package org.jfree.experimental.chart.swt.editor;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.experimental.swt.SWTPaintCanvas;
import org.jfree.experimental.swt.SWTUtils;

/**
 * An editor for plot properties.
 */
class SWTPlotAppearanceEditor extends Composite {

    private Spinner selectStroke;

    /** The stroke (pen) used to draw the outline of the plot. */
    private SWTStrokeCanvas strokeCanvas;

    /** The paint (color) used to fill the background of the plot. */
    private SWTPaintCanvas backgroundPaintCanvas;

    /** The paint (color) used to draw the outline of the plot. */
    private SWTPaintCanvas outlinePaintCanvas;

    /** The orientation for the plot. */
    private PlotOrientation plotOrientation;

    private Combo orientation;

    /** Orientation constants. */
    private final static String[] orientationNames = {"Vertical", "Horizontal"};
    private final static int ORIENTATION_VERTICAL = 0;
    private final static int ORIENTATION_HORIZONTAL = 1;

    /** The resourceBundle for the localization. */
    protected static ResourceBundle localizationResources
            = ResourceBundleWrapper.getBundle(
                    "org.jfree.chart.editor.LocalizationBundle");

    SWTPlotAppearanceEditor(Composite parent, int style, Plot plot) {
        super(parent, style);
        FillLayout layout = new FillLayout();
        layout.marginHeight = layout.marginWidth = 4;
        setLayout(layout);

        Group general = new Group(this, SWT.NONE);
        GridLayout groupLayout = new GridLayout(3, false);
        groupLayout.marginHeight = groupLayout.marginWidth = 4;
        general.setLayout(groupLayout);
        general.setText(localizationResources.getString("General"));

        // row 1: stroke
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Outline_stroke"));
        this.strokeCanvas = new SWTStrokeCanvas(general, SWT.NONE);
        this.strokeCanvas.setStroke(plot.getOutlineStroke());
        GridData strokeGridData = new GridData(SWT.FILL, SWT.CENTER, true,
                false);
        strokeGridData.heightHint = 20;
        this.strokeCanvas.setLayoutData(strokeGridData);
        this.selectStroke = new Spinner(general, SWT.BORDER);
        this.selectStroke.setMinimum(1);
        this.selectStroke.setMaximum(3);
        this.selectStroke.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                false, false));
        this.selectStroke.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        int w = SWTPlotAppearanceEditor.this.selectStroke
                                .getSelection();
                        if (w > 0) {
                            SWTPlotAppearanceEditor.this.strokeCanvas.setStroke(
                                    new BasicStroke(w));
                            SWTPlotAppearanceEditor.this.strokeCanvas.redraw();
                        }
                    }
                }
        );
        // row 2: outline color
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Outline_Paint"));
        this.outlinePaintCanvas = new SWTPaintCanvas(general, SWT.NONE,
                SWTUtils.toSwtColor(getDisplay(), plot.getOutlinePaint()));
        GridData outlineGridData = new GridData(SWT.FILL, SWT.CENTER, true,
                false);
        outlineGridData.heightHint = 20;
        this.outlinePaintCanvas.setLayoutData(outlineGridData);
        Button selectOutlineColor = new Button(general, SWT.PUSH);
        selectOutlineColor.setText(localizationResources.getString(
                "Select..."));
        selectOutlineColor.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
                false, false));
        selectOutlineColor.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        ColorDialog dlg = new ColorDialog(getShell());
                        dlg.setText(localizationResources.getString(
                                "Outline_Paint"));
                        dlg.setRGB(SWTPlotAppearanceEditor.this
                                .outlinePaintCanvas.getColor().getRGB());
                        RGB rgb = dlg.open();
                        if (rgb != null) {
                            SWTPlotAppearanceEditor.this.outlinePaintCanvas
                                    .setColor(new Color(getDisplay(), rgb));
                        }
                    }
                }
        );
        // row 3: background paint
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Background_paint"));
        this.backgroundPaintCanvas = new SWTPaintCanvas(general, SWT.NONE,
                SWTUtils.toSwtColor(getDisplay(), plot.getBackgroundPaint()));
        GridData bgGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        bgGridData.heightHint = 20;
        this.backgroundPaintCanvas.setLayoutData(bgGridData);
        Button selectBgPaint = new Button(general, SWT.PUSH);
        selectBgPaint.setText(localizationResources.getString("Select..."));
        selectBgPaint.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
                false));
        selectBgPaint.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        ColorDialog dlg = new ColorDialog(getShell());
                        dlg.setText(localizationResources.getString(
                                "Background_paint"));
                        dlg.setRGB(SWTPlotAppearanceEditor.this
                                .backgroundPaintCanvas.getColor().getRGB());
                        RGB rgb = dlg.open();
                        if (rgb != null) {
                            SWTPlotAppearanceEditor.this.backgroundPaintCanvas
                                    .setColor(new Color(getDisplay(), rgb));
                        }
                    }
                }
        );
        // row 4: orientation
        if (plot instanceof CategoryPlot) {
            this.plotOrientation = ((CategoryPlot) plot).getOrientation();
        }
        else if (plot instanceof XYPlot) {
            this.plotOrientation = ((XYPlot) plot).getOrientation();
        }
        if (this.plotOrientation != null) {
            boolean isVertical
                    = this.plotOrientation.equals(PlotOrientation.VERTICAL);
            int index = isVertical ? ORIENTATION_VERTICAL
                    : ORIENTATION_HORIZONTAL;
            new Label(general, SWT.NONE).setText(
                    localizationResources.getString("Orientation"));
            this.orientation = new Combo(general, SWT.DROP_DOWN);
            this.orientation.setItems(orientationNames);
            this.orientation.select(index);
            this.orientation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
                    true, false, 2, 1));
            this.orientation.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            switch (SWTPlotAppearanceEditor.this.orientation
                                    .getSelectionIndex()) {
                                case ORIENTATION_VERTICAL:
                                    SWTPlotAppearanceEditor.this.plotOrientation
                                            = PlotOrientation.VERTICAL;
                                    break;
                                case ORIENTATION_HORIZONTAL:
                                    SWTPlotAppearanceEditor.this.plotOrientation
                                            = PlotOrientation.HORIZONTAL;
                                    break;
                                default:
                                    SWTPlotAppearanceEditor.this.plotOrientation
                                            = PlotOrientation.VERTICAL;
                            }
                        }
                    }
            );
        }
    }

    /**
     * Returns the plot orientation.
     *
     * @return The plot orientation.
     */
    public PlotOrientation getPlotOrientation() {
        return this.plotOrientation;
    }

    /**
     * Returns the background paint.
     *
     * @return The background paint.
     */
    public Color getBackGroundPaint() {
        return this.backgroundPaintCanvas.getColor();
    }

    /**
     * Returns the outline paint.
     *
     * @return The outline paint.
     */
    public Color getOutlinePaint() {
        return this.outlinePaintCanvas.getColor();
    }

    /**
     * Returns the stroke.
     *
     * @return The stroke.
     */
    public Stroke getStroke() {
        return this.strokeCanvas.getStroke();
    }
}
