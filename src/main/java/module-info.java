module com.example.movieticketbookingsystem {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    // Database and ORM
    requires java.sql;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;

    // Hibernate dependencies
    requires net.bytebuddy;
    requires com.fasterxml.classmate;

    // JAXB for XML processing
    requires jakarta.xml.bind;

    // UI Component libraries
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // Open packages for JavaFX and Hibernate reflection
    opens com.example.movieticketbookingsystem to javafx.fxml, org.hibernate.orm.core;
    opens com.example.movieticketbookingsystem.model to org.hibernate.orm.core, javafx.base;
    opens com.example.movieticketbookingsystem.view to javafx.fxml;  // Your com.example.movieticketbookingsystem.view controllers
    opens com.example.movieticketbookingsystem.dao to org.hibernate.orm.core;
    opens com.example.movieticketbookingsystem.controller to javafx.fxml;

    // Export packages
    exports com.example.movieticketbookingsystem;
    exports com.example.movieticketbookingsystem.model;
    exports com.example.movieticketbookingsystem.controller;
    exports com.example.movieticketbookingsystem.view;  // Export com.example.movieticketbookingsystem.view package
    exports com.example.movieticketbookingsystem.dao;
}
