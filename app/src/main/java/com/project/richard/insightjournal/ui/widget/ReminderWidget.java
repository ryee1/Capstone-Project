package com.project.richard.insightjournal.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.ui.mainpagerscreen.PagerActivity;
import com.project.richard.insightjournal.utils.SharedPrefUtils;
import com.project.richard.insightjournal.utils.TimerUtils;

/**
 * Implementation of App Widget functionality.
 */
public class ReminderWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.reminder_widget);
        views.setTextViewText(R.id.appwidget_text, context.getString(R.string.widget_time_since_session_text) +
                TimerUtils.millisToTime(SharedPrefUtils.getLastSession(context)));
        views.setContentDescription(R.id.appwidget_text, context.getString(R.string.widget_time_since_session_text)
                + TimerUtils.millisToTime(SharedPrefUtils.getLastSession(context)));
        // Instruct the widget manager to update the widget

        Intent intent = new Intent(context, PagerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

