package com.project.richard.insightjournal.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.*;

/**
 * Created by a11 on 6/4/16.
 */
public interface LogsColumns {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(TEXT) @NotNull String DATETIME_START = "datetime_start";
    @DataType(TEXT) @NotNull String DATETIME_END = "datetime_end";
    @DataType(TEXT) @NotNull String TITLE = "title";
    @DataType(TEXT) String JOURNAL_ENTRY = "journal_entry";
}
