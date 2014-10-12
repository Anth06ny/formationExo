package com.graphexemple.graphview;

import com.jjoe64.graphview.GraphViewDataInterface;

/**
 * Created by Anthony on 12/10/2014.
 */
public class GraphViewData implements GraphViewDataInterface {

    private double x, y;

    public GraphViewData(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }
}
