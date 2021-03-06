package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.HashMap;


public class WidgetRemoteViewService extends RemoteViewsService {
    public String LOG_TAG = getClass().getSimpleName();


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        //I like returning a local var for personal readability, probably not best practice
        @SuppressWarnings("UnnecessaryLocalVariable")
        RemoteViewsFactory remoteViewsFactory = new RemoteViewsFactory() {
            Cursor cursor;

            @Override
            public void onCreate() {
                //done
            }

            @Override
            public void onDataSetChanged() {
                if(cursor != null){
                    cursor.close();
                }

                /*blatantly stole the identityToken logic from Udacity's github,
                    would not have known this otherwise
                    link: https://github.com/udacity/Advanced_Android_Development/blob/1086d9b1c447017b86806f7cf0a401cfa241ea73/app/src/main/java/com/example/android/sunshine/app/widget/DetailWidgetRemoteViewsService.java
                Udacity's Explanation:
                    // This method is called by the app hosting the widget (e.g., the launcher)
                    // However, our ContentProvider is not exported so it doesn't have access to the
                    // data. Therefore we need to clear (and finally restore) the calling identity so
                    // that calls use our process and permission
                 */
                final long identityToken = Binder.clearCallingIdentity();

                cursor = getContentResolver().query(
                        Contract.Quote.URI
                        , Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{})
                        , null
                        , null
                        , Contract.Quote.COLUMN_SYMBOL);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if(cursor != null){
                    cursor.close();
                }
            }

            @Override
            public int getCount() {
                if(cursor != null){
                    return cursor.getCount();
                } else {
                    return 0;
                }
            }

            @Override
            public RemoteViews getViewAt(int position) {
                /*
                stole the INVALID_POSITION and moveToPosition check from Udacity
                would have only checked for null if it were up to me.
                But after looking into the INVALID_POSITION constant and the
                moveToPosition function I understand why those checks are made
                and decided to incorporate them
                */
                if(cursor == null || position == AdapterView.INVALID_POSITION
                        || !cursor.moveToPosition(position)){
                    return null;
                }

                cursor.moveToPosition(position);

                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.list_item_widget);

                String stockSymbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
                float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
                float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

                if(rawAbsoluteChange > 0){
                    remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                    StringBuilder builder = new StringBuilder()
                            .append("+")
                            .append(String.format("%.2f", percentageChange))
                            .append("%");
                    remoteViews.setTextViewText(R.id.change, builder.toString());
                } else {
                    remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                    StringBuilder builder = new StringBuilder()
                            .append(String.format("%.2f", percentageChange))
                            .append("%");
                    remoteViews.setTextViewText(R.id.change, builder.toString());
                }

                remoteViews.setTextViewText(R.id.symbol, stockSymbol);

                //set the intent
                HashMap<Object, Object> singleStock = new HashMap<>();
                for(int i = 0; i < cursor.getColumnCount(); i++){
                    singleStock.put(cursor.getColumnName(i), cursor.getString(i));
                }

                Intent intent = new Intent();

                intent.setData(Contract.Quote.makeUriForStock(stockSymbol));//how am i going to use this to filter down to a single stock?
                intent.putExtra(getString(R.string.single_stock_map), singleStock);

                remoteViews.setOnClickFillInIntent(R.id.list_item_widget, intent);

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_widget);
            }

            @Override
            public int getViewTypeCount() {
                return 1; //only supporting one view
            }

            @Override
            public long getItemId(int position) {
                return position; // this will be the same if we always order stocks based on stock symbol
            }

            @Override
            public boolean hasStableIds() {
                return true; //not changing to true because of choice in getItemId
            }
        };

        return remoteViewsFactory;
    }
}

