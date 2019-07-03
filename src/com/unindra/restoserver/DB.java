package com.unindra.restoserver;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.sql2o.Sql2o;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class DB {
    public static Sql2o sql2o;

    static {
        try {
            DBConfig config =  new Gson().fromJson(new JsonReader(new FileReader(new File("db.json"))), DBConfig.class);
            sql2o = new Sql2o(config.getHost(), config.getUser(), config.getPass());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static class DBConfig {
        private String host;
        private String user;
        private String pass;

        public DBConfig(String host, String user, String pass) {
            this.host = host;
            this.user = user;
            this.pass = pass;
        }

        String getHost() {
            return host;
        }

        String getUser() {
            return user;
        }

        String getPass() {
            return pass;
        }
    }
}
