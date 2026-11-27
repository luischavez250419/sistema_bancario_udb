<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Panel de Cajero</title>
    </head>
    
    <body>
        <h1>Panel de Cajero</h1>

        <p>Bienvenido, ${sessionScope.nombreUsuario}</p>

        <p>Has iniciado sesión como Cajero.</p>
        
         <a href="${pageContext.request.contextPath}/cerrar-sesion">
            Cerrar sesión
        </a>
            
    </body>
    
</html>
