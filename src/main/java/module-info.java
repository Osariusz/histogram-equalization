module com.histogramequalization.histogramequalization {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;
    requires javafx.swing;

    requires org.controlsfx.controls;

    opens com.histogramequalization.histogramequalization to javafx.fxml;
    exports com.histogramequalization.histogramequalization;
}