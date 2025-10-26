package com.example.movieticketbookingsystem.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "SCREENS")
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(name = "screen_number", nullable = false)
    private Integer screenNumber;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    public Screen() {}

    public Screen(Theater theater, Integer screenNumber, Integer capacity) {
        this.theater = theater;
        this.screenNumber = screenNumber;
        this.capacity = capacity;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Theater getTheater() { return theater; }
    public void setTheater(Theater theater) { this.theater = theater; }
    public Integer getScreenNumber() { return screenNumber; }
    public void setScreenNumber(Integer screenNumber) { this.screenNumber = screenNumber; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Screen screen = (Screen) o;
        return Objects.equals(id, screen.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Screen{" +
                "id=" + id +
                ", screenNumber=" + screenNumber +
                '}';
    }
}
