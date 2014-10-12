package com.graphexemple;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.graphexemple.graphview.GraphViewData;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class MainActivity extends Activity implements View.OnClickListener {

    private LinearLayout ll_graph1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll_graph1 = (LinearLayout) findViewById(R.id.ll_graph1);

        createGraph1();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_1) {
            createGraph1();
        }
        else if (v.getId() == R.id.bt_2) {
            createGraph2();
        }
        else if (v.getId() == R.id.bt_3) {
            createGraph3();
        }
        else if (v.getId() == R.id.bt_4) {
            createGraph4();
        }

    }

    private void createGraph1() {
        // init example series data
        GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] { new GraphViewData(1, 2.0d), new GraphViewData(2, 1.5d),
                new GraphViewData(3, 2.5d), new GraphViewData(4, 1.0d) });

        GraphView graphView = new LineGraphView(this // context
                , "GraphViewDemo" // heading
        );
        graphView.addSeries(exampleSeries); // data

        ((LinearLayout) findViewById(R.id.ll_graph1)).addView(graphView);
    }

    private void createGraph2() {
        // init example series data
        GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] { new GraphViewData(1, 2.0d), new GraphViewData(2, 1.5d),
                new GraphViewData(3, 2.5d), new GraphViewData(4, 1.0d) });

        GraphView graphView = new BarGraphView(this // context
                , "GraphViewDemo" // heading
        );
        graphView.addSeries(exampleSeries); // data

        ll_graph1.removeAllViews();
        ll_graph1.addView(graphView);
    }

    private void createGraph3() {
        // init example series data
        GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] { new GraphViewData(1, 2.0d), new GraphViewData(2, 1.5d),
                new GraphViewData(3, 2.5d), new GraphViewData(4, 1.0d) });

        GraphView graphView = new LineGraphView(this // context
                , "GraphViewDemo" // heading
        );
        graphView.addSeries(exampleSeries); // data

        ll_graph1.addView(graphView);
    }

    private void createGraph4() {
        // init example series data
        GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] { new GraphViewData(1, 2.0d), new GraphViewData(2, 1.5d),
                new GraphViewData(3, 2.5d), new GraphViewData(4, 1.0d) });

        GraphView graphView = new LineGraphView(this // context
                , "GraphViewDemo" // heading
        );
        graphView.addSeries(exampleSeries); // data

        ll_graph1.addView(graphView);
    }

}
