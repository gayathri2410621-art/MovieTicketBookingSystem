package com.example.movieticketbookingsystem.dao;

import com.example.movieticketbookingsystem.model.Movie;
import com.example.movieticketbookingsystem.model.Show;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowDao extends GenericDao<Show, Long> {
    List<Show> findShowsByMovie(Movie movie);
    List<Show> findShowsByTimeRange(LocalDateTime start, LocalDateTime end);
    // Add more specific queries as needed, e.g., by theater, by date
}