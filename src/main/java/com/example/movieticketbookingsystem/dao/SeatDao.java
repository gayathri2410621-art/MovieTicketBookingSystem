package com.example.movieticketbookingsystem.dao;

import com.example.movieticketbookingsystem.model.Seat;
import com.example.movieticketbookingsystem.model.Screen;
import com.example.movieticketbookingsystem.model.Show;

import java.util.List;

public interface SeatDao extends GenericDao<Seat, Long> {
    List<Seat> findSeatsByScreen(Screen screen);
    List<Seat> findAvailableSeatsForShow(Show show);
    List<Seat> findBookedSeatsForShow(Show show);
}