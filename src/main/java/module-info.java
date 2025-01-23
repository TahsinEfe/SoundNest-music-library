module com.soundnest.soundnest {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.soundnest.soundnest to javafx.fxml;
    exports com.soundnest.soundnest;

    opens com.soundnest.soundnest.Controllers to javafx.fxml;
    exports com.soundnest.soundnest.Controllers;
}