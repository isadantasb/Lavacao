package dao;

import io.hypersistence.utils.hibernate.query.SQLExtractor;
import java.sql.Connection;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.query.Query;

/**
 *
 * @author alison
 */
public class DAO {

    private static final boolean SHOW_SQL_TO_DEBUG = false;

    private static final SessionFactory sessionFactory;

    static {
        try {
            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
            Metadata metaData = new MetadataSources(standardRegistry).getMetadataBuilder().build();
            sessionFactory = metaData.getSessionFactoryBuilder().build();
        } catch (Exception e) {
            System.out.println("Falha ao criar a fábrica de conexões com o banco de dados: " + ExceptionUtils.getStackTrace(e));
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Session openSession() {
        return sessionFactory.openSession();
    }

    public static void closeSession(Session session) {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    public static void beginTransaction(Session session) {
        session.beginTransaction();

    }

    public static void commit(Session session) {
        if (session.getTransaction().isActive()) {
            session.getTransaction().commit();
        }
    }

    public static void rollback(Session session) {
        if (session.getTransaction().isActive()) {
            session.getTransaction().rollback();
        }
    }

    public static String getConnectionName() {
        String dbURL = null;
        Session session = null;
        try {
            session = openSession();
            ReturningWork<String> getConName = (Connection cnctn) -> cnctn.getMetaData().getURL();
            dbURL = session.doReturningWork(getConName);
        } catch (Exception e) {
            System.out.println("Não foi possível obter o nome da conexão com o banco de dados: " + ExceptionUtils.getStackTrace(e));
        } finally {
            closeSession(session);
        }
        return dbURL;
    }

    public static void printQuery(Query query) {
        if (SHOW_SQL_TO_DEBUG) {
            String sql = SQLExtractor.from(query);
            System.out.println("============");
            System.out.println(sql);
            System.out.println("============");
        }
    }

    public static void printException(Exception e) {
        System.err.println(ExceptionUtils.getStackTrace(e));
    }

    public static void shutdown() {
        sessionFactory.close();
    }

}