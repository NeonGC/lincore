package ru.lodes.lincore.data;

import lombok.NonNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class ServerDAO {

    private final SessionFactory factory = new Factory().getSessionFactory();

    public void save(@NonNull final Server server) {
        try (Session session = factory.openSession()) {
            Transaction trans = session.beginTransaction();
            session.save(server);
            trans.commit();
        }
    }

    public Server findById(int id) {
        try (Session session = factory.openSession()) {
            return session.get(Server.class, id);
        }
    }

    public Server findByName(String name) {
        try (Session session = factory.openSession()) {
            //return session.byNaturalId(Server.class).using("b_name", name).load();
        }
        return null;
    }

    public void update(@NonNull final Server server) {
        try (Session session = factory.openSession()) {
            Transaction tx1 = session.beginTransaction();
            session.update(server);
            tx1.commit();
        }
    }
}
