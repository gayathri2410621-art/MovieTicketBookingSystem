package com.example.movieticketbookingsystem.dao;

import com.example.movieticketbookingsystem.model.Movie;

public class MovieDaoImpl extends GenericDaoImpl<Movie, Long> implements MovieDao {
    public MovieDaoImpl() {
        super(Movie.class);
    }
}
