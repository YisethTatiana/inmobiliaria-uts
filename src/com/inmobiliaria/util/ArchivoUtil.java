package com.inmobiliaria.util;

import com.inmobiliaria.dao.ImagenDAO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

public class ArchivoUtil {

    public static String guardarImagen(ServletContext contexto, Part part, String prefijo) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }
        String nombreOriginal = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        String extension = nombreOriginal.contains(".")
                ? nombreOriginal.substring(nombreOriginal.lastIndexOf('.')).toLowerCase() : "";
        if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)$")) {
            return null;
        }
        String nombreUnico = (prefijo != null ? prefijo : "img_") + System.currentTimeMillis() + "_" + ExtensionAleatoria() + extension;
        String rutaCarpeta = contexto.getRealPath("/uploads");
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        part.write(rutaCarpeta + File.separator + nombreUnico);
        return "uploads/" + nombreUnico;
    }

    public static void guardarImagenesPropiedad(ServletContext contexto, HttpServletRequest request, int idPropiedad)
            throws IOException, ServletException {
        ImagenDAO imagenDAO = new ImagenDAO();
        boolean primera = true;
        for (Part part : request.getParts()) {
            if (!"imagen".equals(part.getName())) {
                continue;
            }
            String ruta = ArchivoUtil.guardarImagen(contexto, part, "prop" + idPropiedad + "_");
            if (ruta != null) {
                try {
                    imagenDAO.insertar(idPropiedad, ruta, "Imagen de la propiedad", primera);
                    primera = false;
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String ExtensionAleatoria() {
        return String.valueOf((int) (Math.random() * 100000));
    }
}