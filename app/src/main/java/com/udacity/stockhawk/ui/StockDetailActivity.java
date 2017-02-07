package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Brian on 2/1/2017.
 */

public class StockDetailActivity extends AppCompatActivity  {

    private final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.graph_view)
    GraphView graphView;
    @BindView(R.id.stock_symbol)
    TextView stockSymbolTV;

    HashMap<Object, Object> singleStock;
    HashMap<Date, Double> stockHistory = new HashMap<>();
    ArrayList<Date> historyDatesForSorting = new ArrayList<>();

    String stockSymbol;
    String currentStockPrice;
    String historyString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent != null) {
            singleStock = (HashMap) intent.getSerializableExtra("data");

            stockSymbol = singleStock.get(Contract.Quote.COLUMN_SYMBOL).toString();
            currentStockPrice = singleStock.get(Contract.Quote.COLUMN_PRICE).toString();
            historyString = singleStock.get(Contract.Quote.COLUMN_HISTORY).toString();

            stockSymbolTV.setText(stockSymbol);

            formatHistoryData(historyString);

            DataPoint[] dataPoints = new DataPoint[historyDatesForSorting.size()];

            for (int i = 0; i < historyDatesForSorting.size(); i++) {
                dataPoints[i] = new DataPoint(
                        historyDatesForSorting.get(i)
                        , stockHistory.get(historyDatesForSorting.get(i))
                );
            }

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
            graphView.addSeries(series);
            graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
            graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
        }
    }

    public void formatHistoryData(String historyString){
        stockHistory.clear();
        historyDatesForSorting.clear();

        String[] pricePerDay = historyString.split("\n"); //split off each individual day based on the line return

        for(int i = 0; i < pricePerDay.length; i++){
            String[] singleDay = pricePerDay[i].toString().split(","); //split the day from the price using the comma
            Long millis = Long.parseLong(singleDay[0].trim()); //0 = date, 1 = stock price
            Double stockPrice = Double.parseDouble(singleDay[1].trim());

            stockHistory.put(getDateFromMiliseconds(millis), stockPrice);
            historyDatesForSorting.add(getDateFromMiliseconds(millis));

        }

        Collections.reverse(historyDatesForSorting); //want the graph to read left to right an we feed the data in right to left

    }

    public Date getDateFromMiliseconds(Long milli){;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milli);

        return calendar.getTime();
    }



}
