package com.example.movieticketbookingsystem.dao;

import com.example.movieticketbookingsystem.model.Booking;

public interface BookingDao extends GenericDao<Booking, Long> {
    // Add specific booking queries if needed, e.g.:
    // List<Booking> findBookingsByUser(Long userId);
    // List<Booking> findBookingsByShow(Long showId);
}
