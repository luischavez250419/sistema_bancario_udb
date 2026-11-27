package com.udb.web.servlets;

import com.udb.web.beans.Usuario;
import com.udb.web.dao.UsuariosDao;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UsuariosDao usuariosDao = new UsuariosDao();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String nombreUsuario = request.getParameter("nombreUsuario");
        String password = request.getParameter("password");

        if (nombreUsuario == null || password == null
                || nombreUsuario.isBlank() || password.isBlank()) {

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp?error=campos"
            );
            return;
        }

        try {

            Usuario usuario = usuariosDao.autenticar(
                    nombreUsuario.trim(), password
            );

            if (usuario == null) {

                response.sendRedirect(
                        request.getContextPath() + "/login.jsp?error=credenciales"
                );
                return;
            }

            HttpSession sesion = request.getSession(false);

            if (sesion != null) {
                sesion.invalidate();
            }

            sesion = request.getSession(true);
            request.changeSessionId();

            sesion.setAttribute("idUsuario", usuario.getIdUsuario());
            sesion.setAttribute("nombreUsuario", usuario.getNombreUsuario());
            sesion.setAttribute("rol", usuario.getRol());
            sesion.setAttribute("idCliente", usuario.getIdCliente());

            sesion.setMaxInactiveInterval(15 * 60);

            switch (usuario.getRol()) {

                case "Administrador":
                    response.sendRedirect(
                            request.getContextPath() + "/admin/inicioAdmin.jsp"
                    );
                    break;

                case "Cajero":
                    response.sendRedirect(
                            request.getContextPath() + "/cajero/inicioCajero.jsp"
                    );
                    break;

                case "Cliente":
                    response.sendRedirect(
                            request.getContextPath() + "/cliente/inicioCliente.jsp"
                    );
                    break;

                default:
                    sesion.invalidate();
                    response.sendError(
                            HttpServletResponse.SC_FORBIDDEN
                    );
            }

        } catch (Exception e) {

            throw new ServletException(
                    "Error al procesar la autenticación.", e
            );
        }
    }
}

