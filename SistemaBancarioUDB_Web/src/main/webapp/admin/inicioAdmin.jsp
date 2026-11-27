<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Panel de Administrador</title>
    </head>
    
    <body>
     
        <h1>Panel de Administrador</h1>

        <p>Bienvenido, ${sessionScope.nombreUsuario}</p>

        <p>Has iniciado sesión como Administrador.</p>
        
        <a href="${pageContext.request.contextPath}/cerrar-sesion">
            Cerrar sesión
        </a>
            
    </body>
</html>
