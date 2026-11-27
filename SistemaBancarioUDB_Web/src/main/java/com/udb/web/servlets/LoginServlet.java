package com.udb.web.servlets;

import com.udb.web.beans.Usuario;
import com.udb.web.dao.UsuariosDao;
import com.udb.web.seguridad.IntentosLogin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final Logger logger =
            LogManager.getLogger(LoginServlet.class);

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

        nombreUsuario = nombreUsuario.trim();

        String claveIntentos = nombreUsuario.toLowerCase(Locale.ROOT)
                + "|" + request.getRemoteAddr();

        // Comprobar si se alcanzó el límite de intentos.
        if (IntentosLogin.estaBloqueado(claveIntentos)) {

            logger.warn(
                    "Inicio de sesión bloqueado temporalmente. Usuario: {}, IP: {}",
                    nombreUsuario, request.getRemoteAddr()
            );

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp?error=bloqueado"
            );
            return;
        }

        try {

            Usuario usuario = usuariosDao.autenticar(
                    nombreUsuario, password
            );

            if (usuario == null) {

                boolean bloqueado = IntentosLogin.registrarIntentoFallido(
                        claveIntentos
                );

                logger.warn(
                        "Intento de inicio de sesión fallido. Usuario: {}, IP: {}",
                        nombreUsuario, request.getRemoteAddr()
                );

                if (bloqueado) {
                    logger.warn(
                            "Usuario bloqueado durante 5 minutos. Usuario: {}, IP: {}",
                            nombreUsuario, request.getRemoteAddr()
                    );

                    response.sendRedirect(
                            request.getContextPath() + "/login.jsp?error=bloqueado"
                    );

                } else {
                    response.sendRedirect(
                            request.getContextPath() + "/login.jsp?error=credenciales"
                    );
                }

                return;
            }

            // Reiniciar intentos después de una autenticación exitosa.
            IntentosLogin.reiniciarIntentos(claveIntentos);

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

            logger.info(
                    "Inicio de sesión exitoso. Usuario: {}, Rol: {}",
                    usuario.getNombreUsuario(), usuario.getRol()
            );

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

            logger.error(
                    "Error al procesar el inicio de sesión para el usuario: {}",
                    nombreUsuario, e
            );

            throw new ServletException(
                    "Error al procesar la autenticación."
            );
        }
    }
}
