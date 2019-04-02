package com.unindra.restoserver.controllers;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.models.Item;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import static com.unindra.restoserver.models.DaftarMenu.menu;
import static com.unindra.restoserver.models.ItemService.getItems;

public class Controller implements Initializable {

    public JFXTreeTableView<Item> treeTableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        TreeTableColumn<Item, String> namaCol = new TreeTableColumn<>("Nama");
        TreeTableColumn<Item, Integer> jumlahCol = new TreeTableColumn<>("Jumlah");

        namaCol.setCellValueFactory(param -> menu(param.getValue().getValue()).namaProperty());
        jumlahCol.setCellValueFactory(param -> param.getValue().getValue().jumlahProperty());

        Predicate<Item> predicate = item -> item.getStatus_item().equals("dipesan");
        FilteredList<Item> filteredList = new FilteredList<>(getItems(), predicate);
        getItems().addListener((ListChangeListener<Item>) c -> filteredList.setPredicate(predicate));

        TreeItem<Item> root = new RecursiveTreeItem<>(filteredList, RecursiveTreeObject::getChildren);

        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
        treeTableView.getColumns().add(namaCol);
        treeTableView.getColumns().add(jumlahCol);
        treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
    }
}
