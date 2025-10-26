package com.example.movieticketbookingsystem.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class HibernateUtil {

    private static EntityManagerFactory entityManagerFactory;

    static {
        try {
            System.out.println("===========================================");
            System.out.println("Initializing EntityManagerFactory...");
            System.out.println("Persistence Unit: MovieBookingPU");
            System.out.println("===========================================");

            // "MovieBookingPU" refers to the name of the persistence unit in persistence.xml
            entityManagerFactory = Persistence.createEntityManagerFactory("MovieBookingPU");

            System.out.println("✓ EntityManagerFactory created successfully!");
            System.out.println("✓ Database connection established!");
            System.out.println("===========================================\n");

        } catch (Throwable ex) {
            System.err.println("===========================================");
            System.err.println("❌ FATAL ERROR: EntityManagerFactory creation failed!");
            System.err.println("===========================================");
            System.err.println("Error details:");
            ex.printStackTrace();
            System.err.println("===========================================");
            System.err.println("\nPossible causes:");
            System.err.println("1. Check if Oracle database is running");
            System.err.println("2. Verify persistence.xml exists in META-INF");
            System.err.println("3. Check database credentials (user: moviedb, password: moviedb123)");
            System.err.println("4. Ensure JDBC URL is correct: jdbc:oracle:thin:@localhost:1521/XEPDB1");
            System.err.println("5. Verify all entity classes are properly annotated");
            System.err.println("===========================================\n");

            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Get a new EntityManager instance
     * @return EntityManager
     */
    public static EntityManager getEntityManager() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            throw new IllegalStateException("EntityManagerFactory is not initialized or has been closed!");
        }
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Get the EntityManagerFactory instance
     * @return EntityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    /**
     * Check if EntityManagerFactory is initialized and open
     * @return true if initialized and open, false otherwise
     */
    public static boolean isInitialized() {
        return entityManagerFactory != null && entityManagerFactory.isOpen();
    }

    /**
     * Shutdown the EntityManagerFactory
     * Should be called when the application is closing
     */
    public static void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            System.out.println("\n===========================================");
            System.out.println("Shutting down EntityManagerFactory...");
            entityManagerFactory.close();
            System.out.println("✓ EntityManagerFactory closed successfully!");
            System.out.println("===========================================");
        }
    }
}
