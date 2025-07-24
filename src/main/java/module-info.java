module com.miempresa.finance4dummies {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    // Permitimos a javafx.fxml reflejar los controladores en este paquete...
    opens com.miempresa.app.ui to javafx.fxml;

    // Si tienes más subpaquetes con controladores FXML, ábrelos también:
    // opens com.miempresa.app.ui.subpaquete to javafx.fxml;

    // Exportas tu API pública si la necesitas
    exports com.miempresa.app;
}

