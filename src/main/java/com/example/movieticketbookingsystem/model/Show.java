package com.example.movieticketbookingsystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal; // <-- ADD THIS IMPORT!
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "SHOWS")
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "show_time", nullable = false)
    private LocalDateTime showTime;

    // --- FIX 1: Change field type ---
    @Column(name = "ticket_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal ticketPrice; // CHANGED from Double to BigDecimal

    public Show() {}

    // --- FIX 2: Change constructor parameter type ---
    public Show(Movie movie, Screen screen, LocalDateTime showTime, BigDecimal ticketPrice) { // CHANGED from Double to BigDecimal
        this.movie = movie;
        this.screen = screen;
        this.showTime = showTime;
        this.ticketPrice = ticketPrice;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }
    public Screen getScreen() { return screen; }
    public void setScreen(Screen screen) { this.screen = screen; }
    public LocalDateTime getShowTime() { return showTime; }
    public void setShowTime(LocalDateTime showTime) { this.showTime = showTime; }

    // --- FIX 3 & 4: Change getter return type and setter parameter type ---
    public BigDecimal getTicketPrice() { return ticketPrice; } // CHANGED from Double to BigDecimal
    public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; } // CHANGED from Double to BigDecimal

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Show show = (Show) o;
        return Objects.equals(id, show.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Show{" +
                "id=" + id +
                ", showTime=" + showTime +
                '}';
    }
}