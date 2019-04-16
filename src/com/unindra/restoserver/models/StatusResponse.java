package com.unindra.restoserver.models;

public enum StatusResponse {
    SUCCESS("Success"), ERROR("Error");

    final private String status;

    StatusResponse(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StatusResponse{" +
                "status='" + status + '\'' +
                '}';
    }
}
