package ru.lodes.lincore.data;

import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.lodes.lincore.LinCore;
import ru.lodes.lincore.api.config.file.FileConfiguration;

@Slf4j
public class Factory {

    private final String host;
    private final String user;
    private final int port;
    private final String db;
    private final String pass;

    private static SessionFactory session;

    public Factory() {
        FileConfiguration config = LinCore.getInstance().getPlugin().getConfig();
        this.host = config.getString("mysql.mg.host");
        this.user = config.getString("mysql.mg.user");
        this.port = config.getInt("mysql.mg.port");
        this.db = config.getString("mysql.mg.db");
        this.pass = config.getString("mysql.mg.pass");
    }

    public SessionFactory getSessionFactory() {
        if (session != null) {
            log.info("Создана новая сессия Hibernate");
            return session;
        }
        Configuration config = new Configuration()
                .addPackage("com.concretepage.persistence")
                .addProperties(setProperties())
                .addAnnotatedClass(Server.class)
                .addAnnotatedClass(User.class);
        return config.buildSessionFactory();
    }

    private Properties setProperties() {
        Properties property = new Properties();
        property.setProperty("hibernate.connection.url", "jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&failOverReadOnly=false&maxReconnects=10");
        property.setProperty("hibernate.connection.username", user);
        property.setProperty("hibernate.connection.password", pass);
        property.setProperty("dialect", "org.hibernate.dialect.MySQLDialect");
        property.setProperty("hibernate.ddl-auto", "none");
        return property;
    }
}
