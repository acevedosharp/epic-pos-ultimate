package com.acevedosharp;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocalSpringBootApplication {
    public static void main(String[] args) {
        Application.launch(ClientApplication.class, args);
    }
}
