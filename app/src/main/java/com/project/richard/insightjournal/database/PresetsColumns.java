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
public interface PresetsColumns {

    public static final String WALKING_MEDITAION = "walking meditation";
    public static final String SITTING_MEDITAION = "sitting meditation";

    @DataType(INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(TEXT) @NotNull String TYPE = "type";
    @DataType(INTEGER) @NotNull String PREPARATION_TIME = "preparation_time";
    @DataType(INTEGER) @NotNull String DURATION = "duration";
    @DataType(INTEGER) @NotNull String RECORD_TOGGLE_ON = "record_toggle_on";
}
