package com.example.movieticketbookingsystem.dao;

import com.example.movieticketbookingsystem.model.Theater;

// Interface name starts with a capital 'T' and uses 'Dao' (lowercase 'd')
public interface TheaterDao extends GenericDao<Theater, Long> {
    // Add specific theater queries if needed, e.g.:
    // Theater findByName(String name);
    // List<Theater> findByCity(String city);
}
