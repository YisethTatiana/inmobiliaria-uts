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
    private static final String PUERTO;
    private static final String SSL;

    static {
        String host = System.getenv("MYSQL_ADDON_HOST");
        String base = System.getenv("MYSQL_ADDON_DB");
        String usuario = System.getenv("MYSQL_ADDON_USER");
        String clave = System.getenv("MYSQL_ADDON_PASSWORD");
        String puerto = System.getenv("MYSQL_ADDON_PORT");
        String ssl = System.getenv("DB_SSL");

        if (host == null || base == null || usuario == null) {
            String h = System.getenv("DB_HOST");
            String b = System.getenv("DB_NAME");
            String u = System.getenv("DB_USER");
            String p = System.getenv("DB_PASSWORD");
            if (h != null) host = h;
            if (b != null) base = b;
            if (u != null) usuario = u;
            if (p != null) clave = p;
            if (puerto == null) puerto = System.getenv("DB_PORT");
        }

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
        if (puerto == null) {
            puerto = "3306";
        }
        if (ssl == null) {
            ssl = "false";
        }

        HOST = host;
        BASE = base;
        USUARIO = usuario;
        CLAVE = clave;
        PUERTO = puerto;
        SSL = ssl;
    }

    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE
                    + "?useSSL=" + SSL + "&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
            return DriverManager.getConnection(url, USUARIO, CLAVE);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver de MySQL no encontrado.", e);
        }
    }
}