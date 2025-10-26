package com.example.movieticketbookingsystem.dao;

import com.example.movieticketbookingsystem.model.Seat;
import com.example.movieticketbookingsystem.model.Screen;
import com.example.movieticketbookingsystem.model.Show;
import com.example.movieticketbookingsystem.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class SeatDaoImpl extends GenericDaoImpl<Seat, Long> implements SeatDao {

    public SeatDaoImpl() {
        super(Seat.class);
    }

    @Override
    public List<Seat> findSeatsByScreen(Screen screen) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        List<Seat> seats = entityManager.createQuery("SELECT s FROM Seat s WHERE s.screen = :screen ORDER BY s.seatRow, s.seatNumber", Seat.class)
                .setParameter("screen", screen)
                .getResultList();
        entityManager.close();
        return seats;
    }

    @Override
    public List<Seat> findAvailableSeatsForShow(Show show) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        // Get all seats for the show's screen
        // Then exclude seats that have a ticket booked for this specific show
        List<Seat> availableSeats = entityManager.createQuery(
                        "SELECT s FROM Seat s " +
                                "WHERE s.screen = :screen " +
                                "AND s NOT IN (SELECT t.seat FROM Ticket t WHERE t.show = :show)", Seat.class)
                .setParameter("screen", show.getScreen())
                .setParameter("show", show)
                .getResultList();
        entityManager.close();
        return availableSeats;
    }

    @Override
    public List<Seat> findBookedSeatsForShow(Show show) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        List<Seat> bookedSeats = entityManager.createQuery(
                        "SELECT t.seat FROM Ticket t WHERE t.show = :show", Seat.class)
                .setParameter("show", show)
                .getResultList();
        entityManager.close();
        return bookedSeats;
    }
}
