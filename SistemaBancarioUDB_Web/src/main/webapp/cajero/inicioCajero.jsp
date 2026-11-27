<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if (session.getAttribute("csrfToken") == null) {
        session.setAttribute(
            "csrfToken",
            java.util.UUID.randomUUID().toString()
        );
    }
%>

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
        
          <form action="${pageContext.request.contextPath}/cerrar-sesion"
      method="POST">

        <input type="hidden"
               name="csrfToken"
               value="${sessionScope.csrfToken}">

        <button type="submit">Cerrar sesión</button>

        </form>
            
    </body>
    
</html>
