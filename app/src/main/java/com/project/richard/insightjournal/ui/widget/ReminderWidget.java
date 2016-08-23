package com.project.richard.insightjournal.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.utils.SharedPrefUtils;
import com.project.richard.insightjournal.utils.TimerUtils;

/**
 * Implementation of App Widget functionality.
 */
public class ReminderWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        long millis = System.currentTimeMillis() - SharedPrefUtils.getLastSession(context);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.reminder_widget);
        views.setTextViewText(R.id.appwidget_text, "Time since last session: " +
                TimerUtils.millisToHours(millis));

        // Instruct the widget manager to update the widget
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

