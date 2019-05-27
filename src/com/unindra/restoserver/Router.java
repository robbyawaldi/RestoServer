package com.unindra.restoserver;

import com.google.gson.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.models.*;
import javafx.collections.FXCollections;

import static com.unindra.restoserver.models.Level.getLevels;
import static com.unindra.restoserver.models.Menu.getMenus;
import static com.unindra.restoserver.models.PesananService.getItems;
import static spark.Spark.*;

class Router {
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

        post("/pesanan", (request, response) -> {
            response.type("application/json");

            Pesanan pesanan = gson.fromJson(request.body(), Pesanan.class);
            PesananService.add(pesanan);

            return gson.toJson(new StandardResponse(StatusResponse.SUCCESS));
        });


        get("/pesanan/:no_meja", (request, response) -> {
            response.type("application/json");

            JsonElement jsonElement = gson.toJsonTree(getItems(request.params(":no_meja")));
            return gson.toJson(new StandardResponse(StatusResponse.SUCCESS, jsonElement));
        });

        put("/pesanan", (request, response) -> {
            response.type("application/json");

            Pesanan pesanan = gson.fromJson(request.body(), Pesanan.class);
            pesanan.setChildren(FXCollections.observableArrayList());
            if (PesananService.update(pesanan))
                return gson.toJson(new StandardResponse(StatusResponse.SUCCESS, "Pesanan diedit"));

            return gson.toJson(new StandardResponse(StatusResponse.ERROR, "Pesanan gagal dihapus"));
        });

        delete("/pesanan", (request, response) -> {
            response.type("application/json");

            Pesanan pesanan = gson.fromJson(request.body(), Pesanan.class);
            if (PesananService.delete(pesanan))
                return gson.toJson(new StandardResponse(StatusResponse.SUCCESS, "Pesanan dihapus"));

            return gson.toJson(new StandardResponse(StatusResponse.ERROR, "Pesanan gagal dihapus"));
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
                    gson.toJsonTree(getLevels()))
            );
        });

        get("/bayar/:no_meja", (request, response) -> {
            response.type("application/json");

            Transaksi transaksi = new Transaksi(request.params(":no_meja"));
            TransaksiService.add(transaksi);

            return gson.toJson(new StandardResponse(StatusResponse.SUCCESS));
        });

        get("/detail_ramen/:nama_menu", (request, response) -> {
            response.type("application/json");

            return gson.toJson(new StandardResponse(
                    StatusResponse.SUCCESS,
                    gson.toJsonTree(DetailRamen.detailRamen(new DetailRamen(request.params(":nama_menu"))))));
        });
    }
}
