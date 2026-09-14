package com.inmobiliaria.util;

import java.io.InputStream;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CorreoUtil {

    public static final int SIN_CONFIG = 0;
    public static final int ENVIADO = 1;
    public static final int FALLO = 2;

    /**
     * Envía un correo real por SMTP usando la configuración de
     * WEB-INF/classes/smtp.properties.
     *
     * @return ENVIADO si el correo se envió por SMTP, SIN_CONFIG si el archivo
     *         todavía no está configurado, o FALLO si el SMTP rechazó el envío.
     */
    public static int enviar(String destinatario, String asunto, String cuerpo) {
        Properties config = cargarConfig();
        String host = config.getProperty("smtp.host", "").trim();
        String usuario = config.getProperty("smtp.usuario", "").trim();
        String password = config.getProperty("smtp.password", "").trim();
        String from = config.getProperty("smtp.from",
                usuario.isEmpty() ? "no-reply@inmobiliaria-uts.com" : usuario).trim();
        String puerto = config.getProperty("smtp.puerto", "587").trim();

        if (host.isEmpty() || usuario.isEmpty() || password.isEmpty() || usuario.startsWith("TU_")) {
            return SIN_CONFIG;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", puerto);
            props.put("mail.smtp.auth", "true");
            if ("465".equals(puerto)) {
                props.put("mail.smtp.ssl.enable", "true");
            } else {
                props.put("mail.smtp.starttls.enable", "true");
            }
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "15000");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(usuario, password);
                }
            });

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(destinatario, false));
            msg.setSubject(asunto, "UTF-8");
            msg.setText(cuerpo, "UTF-8");
            Transport.send(msg);
            return ENVIADO;
        } catch (Exception e) {
            e.printStackTrace();
            return FALLO;
        }
    }

    private static Properties cargarConfig() {
        Properties props = new Properties();
        try (InputStream in = CorreoUtil.class.getResourceAsStream("/smtp.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception ignored) {
        }
        return props;
    }
}