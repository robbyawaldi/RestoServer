package com.unindra.restoserver.models;

import java.util.Arrays;
import java.util.List;

public class Level {
    private int level_item;
    private int harga_level;

    // Constructor
    private Level(int level, int harga_level) {
        this.level_item = level;
        this.harga_level = harga_level;
    }

    // Getter
    public static List<Level> getLevelList() {
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

    static Level level(int level) {
        return getLevelList().stream().filter(l -> l.level_item == level).findFirst().orElse(null);
    }

    int getHarga_level() {
        return harga_level;
    }
}
