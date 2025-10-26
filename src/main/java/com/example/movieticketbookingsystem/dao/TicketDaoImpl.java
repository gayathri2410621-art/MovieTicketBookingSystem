package com.example.movieticketbookingsystem.dao;

import com.example.movieticketbookingsystem.model.Ticket;

public class TicketDaoImpl extends GenericDaoImpl<Ticket, Long> implements TicketDao {
    public TicketDaoImpl() {
        super(Ticket.class); // Calls the constructor of GenericDaoImpl
    }
    // Implement custom methods from TicketDao if any.
}
