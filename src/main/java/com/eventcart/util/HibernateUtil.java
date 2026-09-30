package com.eventcart.util;

import com.eventcart.config.AppConfig;
import com.eventcart.entity.Booking;
import com.eventcart.entity.BookingItem;
import com.eventcart.entity.Category;
import com.eventcart.entity.Event;
import com.eventcart.entity.Payment;
import com.eventcart.entity.TicketType;
import com.eventcart.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Thread-safe Singleton Hibernate SessionFactory utility.
 * Manages Session lifecycle, resource cleanup, and transaction execution.
 */
public final class HibernateUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {
        // Prevent direct instantiation
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) {
                    sessionFactory = buildSessionFactory();
                }
            }
        }
        return sessionFactory;
    }

    private static SessionFactory buildSessionFactory() {
        StandardServiceRegistry registry = null;
        try {
            logger.info("Initializing Hibernate SessionFactory for EventCart...");

            // Service registry settings configured via AppConfig
            Map<String, Object> settings = new HashMap<>();
            settings.put(Environment.JAKARTA_JDBC_DRIVER, AppConfig.getDbDriver());
            settings.put(Environment.JAKARTA_JDBC_URL, AppConfig.getDbUrl());
            settings.put(Environment.JAKARTA_JDBC_USER, AppConfig.getDbUsername());
            settings.put(Environment.JAKARTA_JDBC_PASSWORD, AppConfig.getDbPassword());
            settings.put(Environment.DIALECT, AppConfig.getHibernateDialect());
            settings.put(Environment.SHOW_SQL, String.valueOf(AppConfig.isHibernateShowSql()));
            settings.put(Environment.FORMAT_SQL, "true");
            settings.put(Environment.HBM2DDL_AUTO, AppConfig.get("hibernate.hbm2ddl.auto", "update"));
            settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

            StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
            registryBuilder.applySettings(settings);
            registry = registryBuilder.build();

            MetadataSources sources = new MetadataSources(registry);
            
            // Register mapped entities
            sources.addAnnotatedClass(User.class);
            sources.addAnnotatedClass(Category.class);
            sources.addAnnotatedClass(Event.class);
            sources.addAnnotatedClass(TicketType.class);
            sources.addAnnotatedClass(Booking.class);
            sources.addAnnotatedClass(BookingItem.class);
            sources.addAnnotatedClass(Payment.class);

            Metadata metadata = sources.getMetadataBuilder().build();
            SessionFactory factory = metadata.getSessionFactoryBuilder().build();

            logger.info("Hibernate SessionFactory initialized successfully.");
            return factory;

        } catch (Exception ex) {
            logger.error("Initial SessionFactory creation failed: {}", ex.getMessage(), ex);
            if (registry != null) {
                StandardServiceRegistryBuilder.destroy(registry);
            }
            throw new ExceptionInInitializerError("Hibernate initialization failed: " + ex.getMessage());
        }
    }

    /**
     * Obtains a new Session from the factory.
     */
    public static Session openSession() {
        return getSessionFactory().openSession();
    }

    /**
     * Executes a unit of work within a managed transaction.
     * Automatically commits on success and rolls back on exception.
     */
    public static void executeInTransaction(Consumer<Session> action) {
        Session session = null;
        Transaction tx = null;
        try {
            session = openSession();
            tx = session.beginTransaction();
            action.accept(session);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception rbEx) {
                    logger.error("Transaction rollback failed: {}", rbEx.getMessage(), rbEx);
                }
            }
            logger.error("Transaction failed: {}", e.getMessage(), e);
            throw new RuntimeException("Transaction error: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Executes a unit of work within a managed transaction and returns a result.
     * Automatically commits on success and rolls back on exception.
     */
    public static <R> R executeInTransactionWithResult(Function<Session, R> action) {
        Session session = null;
        Transaction tx = null;
        try {
            session = openSession();
            tx = session.beginTransaction();
            R result = action.apply(session);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception rbEx) {
                    logger.error("Transaction rollback failed: {}", rbEx.getMessage(), rbEx);
                }
            }
            logger.error("Transaction failed with result: {}", e.getMessage(), e);
            throw new RuntimeException("Transaction error: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Safely closes the SessionFactory and releases all resources.
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            logger.info("Closing Hibernate SessionFactory...");
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
