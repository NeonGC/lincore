package ru.lodes.lincore.data;

public class ServerRepository {

    private static final ServerDAO serverDao = new ServerDAO();

    public static Server find(int id) {
        return serverDao.findById(id);
    }

    public static Server find(String name) {
        return find(serverDao.findByName(name).getId());
    }

    public static void save(Server server) {
        serverDao.save(server);
    }

    public static void update(Server server) {
        serverDao.update(server);
    }
}
