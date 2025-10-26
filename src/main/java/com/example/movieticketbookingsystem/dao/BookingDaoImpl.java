package com.example.movieticketbookingsystem.dao;

import com.example.movieticketbookingsystem.model.Booking;

public class BookingDaoImpl extends GenericDaoImpl<Booking, Long> implements BookingDao {
    public BookingDaoImpl() {
        super(Booking.class); // Calls the constructor of GenericDaoImpl
    }
    // Implement custom methods from BookingDao if any.
}
