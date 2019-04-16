package com.unindra.restoserver.models;

import java.util.Arrays;
import java.util.List;

public class Level {
    private int level;
    private int harga_level;

    private Level(int level, int harga_level) {
        this.level = level;
        this.harga_level = harga_level;
    }

    public static List<Level> levelList() {
        return Arrays.asList(
                new Level(0, 0),
                new Level(1, 0),
                new Level(2, 0),
                new Level(3, 0),
                new Level(4, 0),
                new Level(5, 1000),
                new Level(6, 1000),
                new Level(7, 1000),
                new Level(8, 2000),
                new Level(9, 2000),
                new Level(10, 2000)
        );
    }

    @Override
    public String toString() {
        return "Level{" +
                "level=" + level +
                ", harga_level=" + harga_level +
                '}';
    }
}
