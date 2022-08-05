module com.xdavide9.calculatorfx {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.xdavide9.calculatorfx to javafx.fxml;
    exports com.xdavide9.calculatorfx;
}