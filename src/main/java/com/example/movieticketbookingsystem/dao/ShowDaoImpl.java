package com.example.movieticketbookingsystem.dao;

import com.example.movieticketbookingsystem.model.Movie;
import com.example.movieticketbookingsystem.model.Show;
import com.example.movieticketbookingsystem.util.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

public class ShowDaoImpl extends GenericDaoImpl<Show, Long> implements ShowDao {

    public ShowDaoImpl() {
        super(Show.class);
    }

    @Override
    public List<Show> findShowsByMovie(Movie movie) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        List<Show> shows = entityManager.createQuery("SELECT s FROM Show s WHERE s.movie = :movie ORDER BY s.showTime", Show.class)
                .setParameter("movie", movie)
                .getResultList();
        entityManager.close();
        return shows;
    }

    @Override
    public List<Show> findShowsByTimeRange(LocalDateTime start, LocalDateTime end) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        List<Show> shows = entityManager.createQuery("SELECT s FROM Show s WHERE s.showTime BETWEEN :start AND :end ORDER BY s.showTime", Show.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
        entityManager.close();
        return shows;
    }
}