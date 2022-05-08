module com.xdavide9.simplecaulculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires org.slf4j;

    opens com.xdavide9.simplecalculator to javafx.fxml;
    exports com.xdavide9.simplecalculator;
}