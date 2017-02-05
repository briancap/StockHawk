package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Brian on 2/1/2017.
 */

public class StockDetailActivity extends AppCompatActivity {

    @BindView(R.id.graph_view)
    GraphView graphView;

    HashMap<Object, Object> singleStock;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        Intent intent = getIntent();
        if(intent != null){
            singleStock = (HashMap) intent.getSerializableExtra("data");
        }

        ButterKnife.bind(this);



    }

}
