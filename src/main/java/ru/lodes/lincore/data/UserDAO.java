package ru.lodes.lincore.data;

import lombok.NonNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class UserDAO {

    private final SessionFactory factory = new Factory().getSessionFactory();

    public void save(@NonNull final User user) {
        try (Session session = factory.openSession()) {
            Transaction trans = session.beginTransaction();
            session.save(user);
            trans.commit();
        }
    }

    public User findById(int id) {
        try (Session session = factory.openSession()) {
            return session.get(User.class, id);
        }
    }

    public User findByName(String name) {
        try (Session session = factory.openSession()) {
            //return session.byNaturalId(User.class).using("playerId", name).load();
        }
        return null;
    }

    public void update(@NonNull final User user) {
        try (Session session = factory.openSession()) {
            Transaction tx1 = session.beginTransaction();
            session.update(user);
            tx1.commit();
        }
    }
}
