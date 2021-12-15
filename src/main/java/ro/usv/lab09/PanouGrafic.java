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
    private final Button btnInsert = new Button("Inserare");
    private final Button btnFind = new Button("Cautare");
    private final Button btnDelete = new Button("Eliminare");
    private final Button btnOpen = new Button("Citire din fisier");
    private final Button btnSave = new Button("Salvare in fisier");
    private final Button btnClear = new Button("Goleste consola");
    private final Button btnClearTree = new Button("Goleste arborele");
    ArboreBinarDeCautare<String> arb = new ArboreBinarDeCautare<>();
    TextArea zonaTextArea = new TextArea("Operatii:");

    private HBox operatiiArbore() {
        HBox panou = new HBox(10, nodCrt, btnInsert, btnFind, btnDelete,
                btnOpen, btnSave, btnClear, btnClearTree);

        btnInsert.setOnAction(a -> {
                    sirIntrodus = nodCrt.getText().trim();
                    if (sirIntrodus.length() != 0) {
                        zonaTextArea.appendText(arb.add(sirIntrodus) ? "\nNodul " + sirIntrodus + " a fost inserat in arbore"
                                : "\nNodul " + sirIntrodus + " se afla deja in arbore");
                        nodCrt.setText("");
                    }
                }
        );
        btnFind.setOnAction(a -> {
            sirIntrodus = nodCrt.getText().trim();
            if (sirIntrodus.length() != 0) {
                zonaTextArea.appendText("\nCautare nod: " + (arb.contains(sirIntrodus) ? sirIntrodus : "Nodul " + sirIntrodus + " nu este in arbore"));
                nodCrt.setText("");
            }
        });
        btnDelete.setOnAction(a -> {
            sirIntrodus = nodCrt.getText().trim();
            if (sirIntrodus.length() != 0) {
                zonaTextArea.appendText("\nElimina date: " + (arb.contains(sirIntrodus) ? (arb.removeRec(sirIntrodus) ? "s-a eliminat din arbore" : "") : "nu este in arbore"));
                nodCrt.setText("");
            }
        });

        btnClear.setOnAction(a -> zonaTextArea.setText("Operatii:"));
        btnClearTree.setOnAction(a -> {
            arb = new ArboreBinarDeCautare<>();
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            zonaTextArea.setText(arb.isEmpty() ? "Arborele a fost golit." : "Nu s-a putut goli arborele.");
            pause.setOnFinished(e -> zonaTextArea.setText("Operatii: "));
            pause.play();
        });
        btnOpen.setOnAction(e -> {
            File fin = openFile(true);
            if (fin == null)
                return;
            try {
                int nr = 0;
                Scanner scanner = new Scanner(fin);
                while (scanner.hasNext()) {
                    arb.add(scanner.next().trim());
                    nr++;
                }
                if(nr == 1)
                    zonaTextArea.setText("A fost adaugat un cuvant in arbore.");
                else if(nr > 1)
                    zonaTextArea.setText("Au fost adaugate " + nr + " cuvinte din fisierul " + fin.getName());
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
        btnSave.setOnAction(e -> {
            File fin = openFile(false);
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
                if(nr == 1)
                    zonaTextArea.setText("A fost scris in fisierul " + fin.getName() + " un cuvant.");
                else if(nr > 1)
                    zonaTextArea.appendText("\nAu fost scrise in fisierul " + fin.getName() + " " + nr + " cuvinte.");
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
        return panou;
    }

    private File openFile(boolean citire) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Deschideti un fisier care contine cuvinte");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = citire ? fileChooser.showOpenDialog(mainStage) : fileChooser.showSaveDialog(mainStage);
        if (selectedFile != null) {
            zonaTextArea.appendText("\n" + selectedFile);
        }
        return selectedFile;
    }

    private static TextArea getTextArea() {
        TextArea ta = new TextArea();
        ta.appendText("Operatii:");
        ta.setPrefWidth(700);
        return ta;
    }

    private ListView<Object> getSelectionList() {
        ListView<Object> list = new ListView<>();
        list.setPrefWidth(130);
        list.setPrefHeight(130);
        ObservableList<Object> data = FXCollections.observableArrayList(
                "RSD (preordine)", "SRD (inordine)", "SDR (postordine)", "Numar nivele", new Separator(), "Info");
        list.setItems(data);
        list.getSelectionModel().selectedItemProperty()
                .addListener((ov, oldvalue, newvalue) -> {
                            switch (newvalue.toString()) {
                                case "RSD (preordine)" -> zonaTextArea.appendText("\n- " + newvalue + ": " + arb.RSD().stream().map(ArboreBinarDeCautare.Nod::toString).collect(Collectors.joining(" ")));
                                case "SRD (inordine)" -> zonaTextArea.appendText("\n- " + newvalue + ": " + arb.SRD().stream().map(ArboreBinarDeCautare.Nod::toString).collect(Collectors.joining(" ")));
                                case "SDR (postordine)" -> zonaTextArea.appendText("\n- " + newvalue + ": " + arb.SDR().stream().map(ArboreBinarDeCautare.Nod::toString).collect(Collectors.joining(" ")));
                                case "Numar nivele" -> zonaTextArea.appendText("\n- " + newvalue + ": " + arb.h_arbore());
                                case "Info" -> zonaTextArea.appendText("\nProgram: Arbore binar de cautare. Autor: Doroftei Andrei 3131B");
                            }
                        }
                );
        return list;
    }

    private HBox traversari() {
        zonaTextArea = getTextArea();
        return new HBox(10, getSelectionList(), zonaTextArea);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox panouGrafic = new VBox(20, operatiiArbore(), traversari());
        panouGrafic.setPadding(new Insets(10));
        Scene scene = new Scene(panouGrafic, 1000, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Arbore binar de cautare");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
