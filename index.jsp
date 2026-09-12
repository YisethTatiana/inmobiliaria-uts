<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="com.inmobiliaria.modelo.Propiedad" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%@ page import="com.inmobiliaria.dao.PropiedadDAO" %>
<%@ page import="com.inmobiliaria.dao.CatalogoDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    request.setAttribute("titulo", "Inicio");
    List<Propiedad> destacadas = null;
    List<Map<String, Object>> ciudades = null;
    List<Map<String, Object>> tipos = null;
    try {
        destacadas = new PropiedadDAO().listarDestacadas(3);
        ciudades = new CatalogoDAO().listarCiudades();
        tipos = new CatalogoDAO().listarTipos();
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container-fluid hero-imagen py-5">
    <div class="container">
        <div class="row justify-content-center text-center text-white">
            <div class="col-md-9 col-lg-7">
                <h1 class="display-4 fw-bold titulo-hero">Encuentra tu hogar ideal</h1>
                <p class="lead">Casas, apartamentos, locales, oficinas y terrenos gestionados por inmobiliarias confiables.</p>
            </div>
        </div>
        <div class="row justify-content-center">
            <div class="col-lg-8 col-md-10">
                <form action="<%= request.getContextPath() %>/PropiedadServlet" method="get" class="buscador-landing rounded-4 shadow p-3">
                    <div class="row g-2">
                        <div class="col-md-5">
                            <input type="text" name="texto" class="form-control" placeholder="Buscar por nombre o direcci&#243;n...">
                        </div>
                        <div class="col-6 col-md-3">
                            <select name="ciudad" class="form-select">
                                <option value="">Todas las ciudades</option>
                                <% if (ciudades != null) for (Map<String, Object> c : ciudades) { %>
                                    <option value="<%= c.get("id") %>"><%= c.get("nombre") %></option>
                                <% } %>
                            </select>
                        </div>
                        <div class="col-6 col-md-2">
                            <select name="tipo" class="form-select">
                                <option value="">Todo tipo</option>
                                <% if (tipos != null) for (Map<String, Object> t : tipos) { %>
                                    <option value="<%= t.get("id") %>"><%= t.get("nombre") %></option>
                                <% } %>
                            </select>
                        </div>
                        <div class="col-md-2 d-grid">
                            <button type="submit" class="btn btn-primary">Buscar</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0">Publicaciones destacadas</h2>
        <a href="<%= request.getContextPath() %>/PropiedadServlet" class="btn btn-outline-primary btn-sm">Ver todo el cat&#225;logo</a>
    </div>
    <div class="row g-4">
        <% if (destacadas == null || destacadas.isEmpty()) { %>
            <div class="col-12">
                <div class="alert alert-info">A&#250;n no hay publicaciones destacadas.</div>
            </div>
        <% } else { %>
            <% for (Propiedad p : destacadas) { %>
                <div class="col-md-6 col-lg-4">
                    <div class="card card-propiedad h-100 shadow-sm">
                        <img src="<%= !p.getImagenPrincipal().isEmpty() ? p.getImagenPrincipal() : "https://picsum.photos/seed/default/" + p.getIdPropiedad() + "/800/500" %>"
                             class="card-img-top" alt="<%= p.getTitulo() %>">
                        <div class="card-body d-flex flex-column">
                            <h5 class="card-title"><%= p.getTitulo() %></h5>
                            <p class="card-text text-muted small mb-1">
                                <i class="bi bi-geo-alt"></i> <%= p.getNombreCiudad() %> &middot; <%= p.getNombreTipo() %>
                            </p>
                            <p class="text-primary fw-bold fs-5 mb-3">$ <%= String.format("%,.0f", p.getPrecio()) %></p>
                            <a href="<%= request.getContextPath() %>/DetallePropiedadServlet?id=<%= p.getIdPropiedad() %>"
                               class="btn btn-primary btn-sm mt-auto">Ver detalle</a>
                        </div>
                    </div>
                </div>
            <% } %>
        <% } %>
    </div>
</div>

<div class="container pb-5">
    <div class="row g-4 text-center">
        <div class="col-md-4">
            <div class="card h-100 shadow-sm"><div class="card-body">
                <h5><i class="bi bi-building"></i> Sobre nosotros</h5>
                <p class="text-muted small mb-0">Inmobiliaria UTS es una empresa ficticia que administra propiedades de diversos tipos en todo el pa&#237;s.</p>
            </div></div>
        </div>
        <div class="col-md-4">
            <div class="card h-100 shadow-sm"><div class="card-body">
                <h5><i class="bi bi-search"></i> B&#250;squeda inteligente</h5>
                <p class="text-muted small mb-0">Filtra por ciudad, tipo de inmueble y rango de precio con resultados al instante.</p>
            </div></div>
        </div>
        <div class="col-md-4">
            <div class="card h-100 shadow-sm"><div class="card-body">
                <h5><i class="bi bi-shield-check"></i> Tr&#225;mites seguros</h5>
                <p class="text-muted small mb-0">Agenda visitas, radica documentos y sigue el estado de tu solicitud de compra o arriendo.</p>
            </div></div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>