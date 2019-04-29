package com.unindra.restoserver;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.models.*;
import javafx.collections.FXCollections;

import static com.unindra.restoserver.models.ItemService.getItems;
import static com.unindra.restoserver.models.Menu.getMenus;
import static com.unindra.restoserver.models.Level.getLevelList;
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
            ItemService.add(item);

            return gson.toJson(new StandardResponse(StatusResponse.SUCCESS));
        });

        get("/items/:no_meja", (request, response) -> {
            response.type("application/json");

            if (ItemService.getStatus() == ItemService.StatusItem.CHANGED) {
                ItemService.setStatus(ItemService.StatusItem.STILL);
                return gson.toJson(new StandardResponse(
                        StatusResponse.SUCCESS,
                        gson.toJsonTree(getItems(request.params(":no_meja"))))
                );
            }
            return gson.toJson(new StandardResponse(StatusResponse.SUCCESS, "NOTHING UPDATE"));
        });

        put("/items", (request, response) -> {
            response.type("application/json");

            Item item = gson.fromJson(request.body(), Item.class);
            item.setChildren(FXCollections.observableArrayList());
            if (ItemService.update(item)) {
                return gson.toJson(new StandardResponse(StatusResponse.SUCCESS, "Item diedit"));
            } else {
                return gson.toJson(new StandardResponse(StatusResponse.ERROR, "Item gagal dihapus"));
            }
        });

        delete("/items", (request, response) -> {
            response.type("application/json");

            Item item = gson.fromJson(request.body(), Item.class);
            if (ItemService.delete(item)) {
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
                    gson.toJsonTree(getLevelList()))
            );
        });

        get("/bayar/:no_meja", (request, response) -> {
            response.type("application/json");

            Transaksi transaksi = new Transaksi(request.params(":no_meja"));
            TransaksiService.add(transaksi);

            return gson.toJson(new StandardResponse(StatusResponse.SUCCESS));
        });
    }
}
