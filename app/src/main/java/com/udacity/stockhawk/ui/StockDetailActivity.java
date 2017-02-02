package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Brian on 2/1/2017.
 */

public class StockDetailActivity extends AppCompatActivity {

    @BindView(R.id.stock_detail_test)
    TextView tv;

    HashMap<Object, Object> singleRow;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        Intent intent = getIntent();
        if(intent != null){
            singleRow = (HashMap) intent.getSerializableExtra("data");
        }

        ButterKnife.bind(this);
        tv.setText(singleRow.get((String) Contract.Quote.COLUMN_SYMBOL).toString());

    }

}
