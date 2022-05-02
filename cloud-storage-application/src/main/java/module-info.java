module com.buzas.cloud.application {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires io.netty.codec;
    requires lombok;


    opens com.buzas.cloud.application to javafx.fxml;
    exports com.buzas.cloud.application;
    exports com.buzas.cloud.application.controllers;
    opens com.buzas.cloud.application.controllers to javafx.fxml;
}