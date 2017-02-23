/*
 * Copyright 2015 Diogo Bernardino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.db.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint.Align;


/**
 * Class responsible to control horizontal measures, positions, yadda yadda.
 * If the drawing is requested it will also take care of it.
 */
public class XRenderer extends AxisRenderer {


    public XRenderer() {

        super();
    }


    /*
     * IMPORTANT: Method's order is crucial. Change it (or not) carefully.
     */
    @Override
    public void dispose() {

        super.dispose();

        defineMandatoryBorderSpacing(mInnerChartLeft, mInnerChartRight);
        defineLabelsPosition(mInnerChartLeft, mInnerChartRight);
    }

    @Override
    public void measure(int left, int top, int right, int bottom) {

        mInnerChartLeft = measureInnerChartLeft(left);
        mInnerChartTop = measureInnerChartTop(top);
        mInnerChartRight = measureInnerChartRight(right);
        mInnerChartBottom = measureInnerChartBottom(bottom);
    }

    @Override
    protected float defineAxisPosition() {

        float result = mInnerChartBottom;
        if (style.hasXAxis()) result += style.getAxisThickness() / 2;
        return result;
    }

    @Override
    protected float defineStaticLabelsPosition(float axisCoordinate, int distanceToAxis) {

        float result = axisCoordinate;

        if (style.getXLabelsPositioning() == LabelPosition.INSIDE) { // Labels sit inside of chart
            result -= distanceToAxis;
            result -= style.getLabelsPaint().descent();
            if (style.hasXAxis()) result -= style.getAxisThickness() / 2;

        } else if (style.getXLabelsPositioning() == LabelPosition.OUTSIDE) { // Labels sit outside of chart
            result += distanceToAxis;
            result += style.getFontMaxHeight() - style.getLabelsPaint().descent();
            if (style.hasXAxis()) result += style.getAxisThickness() / 2;
        }
        return result;
    }

    @Override
    public void draw(Canvas canvas) {

        // Draw axis
        if (style.hasXAxis())
            canvas.drawLine(mInnerChartLeft, axisPosition, mInnerChartRight, axisPosition,
                    style.getChartPaint());

        // Draw labels
        if (style.getXLabelsPositioning() != LabelPosition.NONE) {
            style.getLabelsPaint().setTextAlign(Align.CENTER);

            int nLabels = labels.size();
            for (int i = 0; i < nLabels; i++) {
                canvas.drawText(labels.get(i), labelsPos.get(i), labelsStaticPos,
                        style.getLabelsPaint());

            }
        }
    }

    @Override
    public float parsePos(int index, double value) {

        if (handleValues)
            return (float) (mInnerChartLeft
                    + (((value - minLabelValue) * screenStep) / (labelsValues.get(1) - minLabelValue)));
        else return labelsPos.get(index);
    }


    /**
     * Measure the necessary padding from the chart left border defining the
     * coordinate of the inner chart left border. Inner Chart refers only to the
     * area where chart data will be draw, excluding labels, axis, etc.
     *
     * @param left Left position of chart area
     * @return Coordinate of the inner left side of the chart
     */
    protected float measureInnerChartLeft(int left) {

        return (style.getXLabelsPositioning() != LabelPosition.NONE)
                ? style.getLabelsPaint().measureText(labels.get(0)) / 2
                : left;
    }


    /**
     * Measure the necessary padding from the chart left border defining the
     * coordinate of the inner chart top border. Inner Chart refers only to the
     * area where chart data will be draw, excluding labels, axis, etc.
     *
     * @param top Top position of chart area
     * @return Coordinate of the inner top side of the chart
     */
    protected float measureInnerChartTop(int top) {

        return top;
    }


    /**
     * Measure the necessary padding from the chart left border defining the
     * coordinate of the inner chart right border. Inner Chart refers only to the
     * area where chart data will be draw, excluding labels, axis, etc.
     *
     * @param right Right position of chart area
     * @return Coordinate of the inner right side of the chart
     */
    protected float measureInnerChartRight(int right) {

        // To manage horizontal width of the last axis label
        float lastLabelWidth = 0;
        // to fix possible crash on trying to access label by index -1.
        if (labels.size() > 0)
            lastLabelWidth = style.getLabelsPaint().measureText(labels.get(labels.size() - 1));

        float rightBorder = 0;
        if (style.getXLabelsPositioning() != LabelPosition.NONE
                && style.getAxisBorderSpacing() + mandatoryBorderSpacing < lastLabelWidth / 2)
            rightBorder = lastLabelWidth / 2 - (style.getAxisBorderSpacing() + mandatoryBorderSpacing);

        return right - rightBorder;
    }


    /**
     * Measure the necessary padding from the chart left border defining the
     * coordinate of the inner chart bottom border. Inner Chart refers only to the
     * area where chart data will be draw, excluding labels, axis, etc.
     *
     * @param bottom Bottom position of chart area
     * @return Coordinate of the inner bottom side of the chart
     */
    protected float measureInnerChartBottom(int bottom) {

        float result = bottom;

        if (style.hasXAxis()) result -= style.getAxisThickness();

        if (style.getXLabelsPositioning() == LabelPosition.OUTSIDE)
            result -= style.getFontMaxHeight() + style.getAxisLabelsSpacing();

        return result;
    }

}
