package com.inmobiliaria.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static final String HOST;
    private static final String BASE;
    private static final String USUARIO;
    private static final String CLAVE;

    static {
        String host = System.getenv("DB_HOST");
        String base = System.getenv("DB_NAME");
        String usuario = System.getenv("DB_USER");
        String clave = System.getenv("DB_PASSWORD");

        if (host == null || base == null || usuario == null) {
            Properties props = new Properties();
            try (InputStream in = ConexionBD.class.getClassLoader()
                    .getResourceAsStream("database.properties")) {
                if (in != null) {
                    props.load(in);
                }
            } catch (IOException e) {
                throw new ExceptionInInitializerError(e);
            }
            if (host == null) {
                host = props.getProperty("db.host", "localhost");
            }
            if (base == null) {
                base = props.getProperty("db.name", "inmobiliaria_db");
            }
            if (usuario == null) {
                usuario = props.getProperty("db.user", "root");
            }
            if (clave == null) {
                clave = props.getProperty("db.password", "");
            }
        }

        HOST = host;
        BASE = base;
        USUARIO = usuario;
        CLAVE = clave;
    }

    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + HOST + ":3306/" + BASE
                    + "?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
            return DriverManager.getConnection(url, USUARIO, CLAVE);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver de MySQL no encontrado.", e);
        }
    }
}