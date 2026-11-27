<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Inicio de sesión-Sistema Bancario UDB</title>
    </head>
    
    <body>
        <h2>Sistema Bancario UDB</h2>
        <h3>Iniciar sesión</h3>

        <% if ("credenciales".equals(request.getParameter("error"))) { %>
            <p style="color: red;">
                Usuario o contraseña incorrectos.
            </p>
        <% } else if ("campos".equals(request.getParameter("error"))) { %>
            <p style="color: red;">
                Debes completar todos los campos.
            </p>
        <% } %>

        <form action="${pageContext.request.contextPath}/login" method="POST">

            <label for="nombreUsuario">Usuario:</label>
            <input type="text" id="nombreUsuario"
                   name="nombreUsuario" required>

            <br><br>

            <label for="password">Contraseña:</label>
            <input type="password" id="password"
                   name="password" required>

            <br><br>

            <button type="submit">Ingresar</button>

        </form>
    </body>
</html>
