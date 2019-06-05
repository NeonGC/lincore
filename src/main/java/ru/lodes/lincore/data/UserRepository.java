package ru.lodes.lincore.data;

public class UserRepository {

    private static final UserDAO userDao = new UserDAO();

    public static User find(int id) {
        return userDao.findById(id);
    }

    public static User find(String name) {
        return find(userDao.findByName(name).getPlayerId());
    }

    public static void save(User user) {
        userDao.save(user);
    }

    public static void update(User user) {
        userDao.update(user);
    }
}
