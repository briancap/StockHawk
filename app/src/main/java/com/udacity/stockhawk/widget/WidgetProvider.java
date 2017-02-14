package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.ui.StockDetailActivity;

public class WidgetProvider extends AppWidgetProvider{

    private String LOG_TAG = getClass().getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appIds){

        //can we pass in more than one ID at a time? answer must be yes.
        //widgets that refresh on the same schedule?
        for(int i = 0; i < appIds.length; i++) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            appWidgetManager.updateAppWidget(appIds[i], remoteViews);

            //go to MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(context, 0, intent, 0);
            //only sets this on the header
            remoteViews.setOnClickPendingIntent(R.id.widget_title_frame, pendingIntent);


            //set on the list in the widget
            setAdapter(context, remoteViews);
            Intent template = new Intent(context, StockDetailActivity.class);
            PendingIntent pendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(template)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_list, pendingIntentTemplate);

            appWidgetManager.updateAppWidget(appIds[i], remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);

        if(QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass())
            );
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
    }

    public void setAdapter(Context context, RemoteViews remoteViews){
        remoteViews.setRemoteAdapter(0, R.id.widget_list, new Intent(context, WidgetRemoteViewService.class));
    }
}
