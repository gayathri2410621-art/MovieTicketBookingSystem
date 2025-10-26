package com.example.movieticketbookingsystem.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "SEATS")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "seat_row", nullable = false)
    private String seatRow;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    // ADD THIS NEW FIELD
    @Column(name = "is_available", nullable = false)
    private Boolean available = true;

    public Seat() {}

    public Seat(Screen screen, String seatRow, Integer seatNumber) {
        this.screen = screen;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.available = true; // Default to available
    }

    // Existing Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Screen getScreen() { return screen; }
    public void setScreen(Screen screen) { this.screen = screen; }
    public String getSeatRow() { return seatRow; }
    public void setSeatRow(String seatRow) { this.seatRow = seatRow; }
    public Integer getSeatNumber() { return seatNumber; }
    public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber; }

    // ADD THESE NEW METHODS
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    // Convenience methods
    public boolean isAvailable() { return available != null && available; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return Objects.equals(id, seat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", row=" + seatRow +
                ", num=" + seatNumber +
                ", available=" + available +
                '}';
    }
}
