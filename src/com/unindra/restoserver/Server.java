package com.unindra.restoserver;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.models.Item;
import com.unindra.restoserver.models.StandardResponse;
import com.unindra.restoserver.models.StatusResponse;
import javafx.collections.FXCollections;

import static com.unindra.restoserver.models.Menu.getMenus;
import static com.unindra.restoserver.models.ItemService.delete;
import static com.unindra.restoserver.models.ItemService.*;
import static com.unindra.restoserver.models.Level.levelList;
import static spark.Spark.delete;
import static spark.Spark.*;

class Server {
    private static Gson gson;

    static {
        gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return fieldAttributes.getDeclaringClass().equals(RecursiveTreeObject.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).addDeserializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return fieldAttributes.getDeclaringClass().equals(RecursiveTreeObject.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).create();

        post("/items", (request, response) -> {
            response.type("application/json");

            Item item = gson.fromJson(request.body(), Item.class);
            add(item);

            return gson.toJson(new StandardResponse(StatusResponse.SUCCESS));
        });

        get("/items/:no_meja", (request, response) -> {
            response.type("application/json");

            return gson.toJson(new StandardResponse(
                    StatusResponse.SUCCESS,
                    gson.toJsonTree(getItems(request.params(":no_meja"))))
            );
        });

        put("/items", (request, response) -> {
            response.type("application/json");

            Item item = gson.fromJson(request.body(), Item.class);
            item.setChildren(FXCollections.observableArrayList());
            if (update(item)) {
                return gson.toJson(new StandardResponse(StatusResponse.SUCCESS, "Item diedit"));
            } else {
                return gson.toJson(new StandardResponse(StatusResponse.ERROR, "Item gagal dihapus"));
            }
        });

        delete("/items", (request, response) -> {
            response.type("application/json");

            Item item = gson.fromJson(request.body(), Item.class);
            if (delete(item)) {
                return gson.toJson(new StandardResponse(StatusResponse.SUCCESS, "Item dihapus"));
            } else {
                return gson.toJson(new StandardResponse(StatusResponse.ERROR, "Item gagal dihapus"));
            }
        });

        get("/menus", (request, response) -> {
            response.type("application/json");

            return gson.toJson(new StandardResponse(
                    StatusResponse.SUCCESS,
                    gson.toJsonTree(getMenus()))
            );
        });

        get("/levels", (request, response) -> {
            response.type("application/json");

            return gson.toJson(new StandardResponse(
                    StatusResponse.SUCCESS,
                    gson.toJsonTree(levelList()))
            );
        });

        get("/bayar/:no_meja", (request, response) -> {
            response.type("application/json");

            return gson.toJson(new StandardResponse(StatusResponse.SUCCESS));
        });
    }

    public static void main(String[] args) {
        new Server();
    }
}