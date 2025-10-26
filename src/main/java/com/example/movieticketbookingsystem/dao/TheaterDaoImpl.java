package com.example.movieticketbookingsystem.dao;

import com.example.movieticketbookingsystem.model.Theater;

public class TheaterDaoImpl extends GenericDaoImpl<Theater, Long> implements TheaterDao {
    public TheaterDaoImpl() {
        super(Theater.class); // Calls the constructor of GenericDaoImpl
    }
    // Implement custom methods from TheaterDao if any.
}