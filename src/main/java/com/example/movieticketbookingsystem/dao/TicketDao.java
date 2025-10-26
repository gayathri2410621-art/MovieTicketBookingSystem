package com.example.movieticketbookingsystem.dao;

import com.example.movieticketbookingsystem.model.Ticket;

public interface TicketDao extends GenericDao<Ticket, Long> {
    // Add specific ticket queries if needed, e.g.:
    // List<Ticket> findTicketsByBooking(Long bookingId);
    // List<Ticket> findTicketsByShow(Long showId);
}