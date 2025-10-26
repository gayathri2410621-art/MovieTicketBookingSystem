package com.example.movieticketbookingsystem.dao;

import com.example.movieticketbookingsystem.model.Screen;

public class ScreenDaoImpl extends GenericDaoImpl<Screen, Long> implements ScreenDao {
    public ScreenDaoImpl() {
        super(Screen.class); // Calls the constructor of GenericDaoImpl
    }
    // Implement custom methods from ScreenDao if any.
}