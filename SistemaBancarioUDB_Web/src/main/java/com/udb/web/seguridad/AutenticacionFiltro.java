package com.udb.web.seguridad;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter({
    "/admin/*",
    "/cajero/*",
    "/cliente/*"
})
public class AutenticacionFiltro implements Filter {

    @Override
    public void init(FilterConfig filterConfig)
            throws ServletException {
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        // Evitar que el navegador almacene páginas privadas.
        httpResponse.setHeader("Cache-Control", "no-store");
        httpResponse.setHeader("Pragma", "no-cache");

        HttpSession sesion = httpRequest.getSession(false);

        // Verificar si existe una sesión autenticada.
        if (sesion == null
                || sesion.getAttribute("idUsuario") == null
                || sesion.getAttribute("rol") == null) {

            httpResponse.sendRedirect(
                    httpRequest.getContextPath() + "/login.jsp"
            );
            return;
        }

        String rol = (String) sesion.getAttribute("rol");
        String ruta = httpRequest.getRequestURI()
                .substring(httpRequest.getContextPath().length());

        boolean autorizado = false;

        // Validar el acceso según el rol.
        if (ruta.startsWith("/admin/")) {
            autorizado = "Administrador".equals(rol);

        } else if (ruta.startsWith("/cajero/")) {
            autorizado = "Cajero".equals(rol);

        } else if (ruta.startsWith("/cliente/")) {
            autorizado = "Cliente".equals(rol);
        }

        if (!autorizado) {
            httpResponse.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "No tienes autorización para acceder a esta sección."
            );
            return;
        }

        // Permitir el acceso si cumple las condiciones.
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
