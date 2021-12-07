module ro.usv.lab09 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens ro.usv.lab09 to javafx.fxml;
    exports ro.usv.lab09;
}