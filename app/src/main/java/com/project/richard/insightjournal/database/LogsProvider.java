package com.project.richard.insightjournal.database;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by a11 on 6/4/16.
 */

@ContentProvider(authority = LogsProvider.AUTHORITY, database = LogsDatabase.class)
public final class LogsProvider {

    public static final String AUTHORITY = "com.project.richard.insightjournal.database.LogsProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    @TableEndpoint(table = LogsDatabase.LOGS) public static class Logs {
        @ContentUri(
                path = "logs",
                type = "vnd.android.cursor.dir/log",
                defaultSort = LogsColumns.DATETIME + "DESC"
        )
        public static final Uri LOGS = Uri.parse(BASE_CONTENT_URI + "/logs");
    }
}
