package com.project.richard.insightjournal.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by a11 on 6/4/16.
 */
public interface LogsColumns {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(INTEGER) @NotNull String SESSION_DURATION = "session_duration";
    @DataType(INTEGER) @NotNull String SESSION_DATETIME = "session_datetime";
    @DataType(TEXT) @NotNull String TITLE = "title";
    @DataType(TEXT) String JOURNAL_ENTRY = "journal_entry";
    @DataType(TEXT) String GOALS = "goals";
    @DataType(TEXT) String LOCATION = "location";
}
