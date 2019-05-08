package com.unindra.restoserver;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.models.*;
import javafx.collections.FXCollections;

import static com.unindra.restoserver.models.PesananService.getItems;
import static com.unindra.restoserver.models.Level.getLevels;
import static com.unindra.restoserver.models.Menu.getMenus;
import static spark.Spark.*;

class Server {
    private static Gson gson;

    public static void main(String[] args) {
        new Server();
    }
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

            if (PesananService.getStatus() == PesananService.StatusPesanan.CHANGED) {
                PesananService.setStatus(PesananService.StatusPesanan.STILL);
                return gson.toJson(new StandardResponse(
                        StatusResponse.SUCCESS,
                        gson.toJsonTree(getItems(request.params(":no_meja"))))
                );
            }
            return gson.toJson(new StandardResponse(StatusResponse.SUCCESS, "NOTHING UPDATE"));
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
    }
}
