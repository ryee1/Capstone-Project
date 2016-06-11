package com.project.richard.insightjournal.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by a11 on 6/10/16.
 */
public class PresetsColumns {

    @DataType(INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(TEXT) @NotNull String DATETIME = "datetime";
    @DataType(TEXT) @NotNull String TITLE = "title";
    @DataType(TEXT) String JOURNAL_ENTRY = "journal_entry";
}
