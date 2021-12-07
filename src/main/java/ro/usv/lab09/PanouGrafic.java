package ro.usv.lab09;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class PanouGrafic extends Application {
    private final Stage mainStage = new Stage();
    private String sirIntrodus = "";
    private final TextField nodCrt = new TextField();
    private final Button btnInserare = new Button("Inserare");
    private final Button btnCauta = new Button("Cauta");
    private final Button btnEliminare = new Button("Elimina");
    private final Button btnOpen = new Button("Citire fisier");
    private final Button btnSave = new Button("Salvare in fisier");
    private final Button btnClear = new Button("Clear");
    private final Button btnClearTree = new Button("Clear Tree");
    ArboreBinarDeCautare<String> arb = new ArboreBinarDeCautare<>();
    TextArea zonaTextArea = new TextArea("Operatii:");

    private HBox operatiiArbore() {
        HBox panou = new HBox(10, nodCrt, btnInserare, btnCauta, btnEliminare,
                btnOpen, btnSave, btnClear, btnClearTree);

        btnInserare.setOnAction(a -> {
                    sirIntrodus = nodCrt.getText().trim();
                    zonaTextArea.appendText("\n" + sirIntrodus);
                    if (sirIntrodus.length() != 0) {
                        zonaTextArea.appendText(arb.add(sirIntrodus) ? " s-a inserat"
                                : " este deja in arbore");
                        nodCrt.setText("");
                    }
                }
        );
        btnCauta.setOnAction(a -> {
            sirIntrodus = nodCrt.getText().trim();
            if (sirIntrodus.length() != 0) {
                zonaTextArea.appendText("\nCauta date: " + (arb.contains(sirIntrodus) ? sirIntrodus : "nu este in arbore"));
                nodCrt.setText("");
            }
        });
        btnEliminare.setOnAction(a -> {
            sirIntrodus = nodCrt.getText().trim();
            if (sirIntrodus.length() != 0) {
                zonaTextArea.appendText("\nElimina date: " + (arb.contains(sirIntrodus) ? (arb.removeRec(sirIntrodus) ? "s-a eliminat din arbore" : "") : "nu este in arbore"));
                nodCrt.setText("");
            }
        });

        btnClear.setOnAction(a -> zonaTextArea.setText("Operatii:"));
        btnClearTree.setOnAction(a -> {
            arb = new ArboreBinarDeCautare<>();
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            zonaTextArea.setText(arb.isEmpty() ? "Arborele a fost golit." : "Nu s-a putut goli arborele.");
            pause.setOnFinished(e -> zonaTextArea.setText("Operatii: "));
            pause.play();
        });
        btnOpen.setOnAction(e -> {
            File fin = deschideFisier(true);
            if (fin == null)
                return;
            try {
                int nr = 0;
                Scanner scanner = new Scanner(fin);
                while (scanner.hasNext()) {
                    arb.add(scanner.next().trim());
                    nr++;
                }
                zonaTextArea.setText("S-au citit " + nr + " cuvinte.");
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
        btnSave.setOnAction(e -> {
            File fin = deschideFisier(false);
            if (fin == null)
                return;
            try {
                int nr;
                PrintWriter printWriter = new PrintWriter(fin);
                ArrayList<ArboreBinarDeCautare.Nod<String>> arr = arb.SRD();
                nr = arr.size();
                for (Object str : arr)
                    printWriter.println(str);
                printWriter.close();

                zonaTextArea.appendText("\nS-au scris " + nr + " cuvinte.");
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
        return panou;
    }

    private File deschideFisier(boolean citire) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Deschide fisier cu cuvinte");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = citire ? fileChooser.showOpenDialog(mainStage)
                : fileChooser.showSaveDialog(mainStage);
        if (selectedFile != null) {
            zonaTextArea.appendText("\n" + selectedFile);
        }
        return selectedFile;
    }

    private static TextArea getTextArea() {
        TextArea ta = new TextArea();
        ta.appendText("Operatii:");
        ta.setPrefWidth(450);
        return ta;
    }

    private ListView<Object> getListaSelectie() {
        ListView<Object> list = new ListView<>();
        list.setPrefWidth(240);
        list.setPrefHeight(250);
        ObservableList<Object> data = FXCollections.observableArrayList(
                "RSD", "SRD", "SDR", "Nivele", new Separator(), "info");
        list.setItems(data);
        list.getSelectionModel().selectedItemProperty()
                .addListener((ov, oldvalue, newvalue) -> {
                            switch (newvalue.toString()) {
                                case "RSD" -> zonaTextArea.appendText("\n- " + newvalue + ": " + arb.RSD().stream().map(ArboreBinarDeCautare.Nod::toString).collect(Collectors.joining(" ")));

                                case "SRD" -> zonaTextArea.appendText("\n- " + newvalue + ": " + arb.SRD().stream().map(ArboreBinarDeCautare.Nod::toString).collect(Collectors.joining(" ")));

                                case "SDR" -> zonaTextArea.appendText("\n- " + newvalue + ": " + arb.SDR().stream().map(ArboreBinarDeCautare.Nod::toString).collect(Collectors.joining(" ")));

                                case "Nivele" -> zonaTextArea.appendText("\n- " + newvalue + ": " + arb.h_arbore());

                                case "info" -> zonaTextArea.appendText("\nProgram Arbore de cautare binare autor Gherasim Daniel Adrian");
                            }
                        }
                );
        return list;
    }

    private HBox traversari() {
        zonaTextArea = getTextArea();
        return new HBox(10, getListaSelectie(), zonaTextArea);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox panouGrafic = new VBox(20, operatiiArbore(), traversari());
        panouGrafic.setPadding(new Insets(10));
        Scene scena = new Scene(panouGrafic, 720, 300);
        primaryStage.setScene(scena);
        primaryStage.setTitle("Arbore binar de cautare");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}