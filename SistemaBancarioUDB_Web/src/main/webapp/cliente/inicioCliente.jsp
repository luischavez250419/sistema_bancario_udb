<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Portal de Cliente</title>
    </head>
    
    <body>
        <h1>Portal de Cliente</h1>

        <p>Bienvenido, ${sessionScope.nombreUsuario}</p>

        <p>Has iniciado sesión como Cliente.</p>
        
         <a href="${pageContext.request.contextPath}/cerrar-sesion">
            Cerrar sesión
        </a>
            
    </body>
    
</html>
