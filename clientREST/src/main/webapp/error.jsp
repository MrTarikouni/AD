<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Error :(</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi" crossorigin="anonymous">
    </head>
    <body>
        <h1 class = "fw-bold" style = "text-align:center;">Error.</h1>
         
        <%  
            if (session.getAttribute("user") == null ) {
                %>
        
                <br><h3 class = "fw-bold">Usuario o contraseña incorrectos</h3>
                <br><a href="login.jsp" class = "btn btn-primary">Volver a login </a>
        <%
            }
            
            else {
                String codeError = request.getParameter("error");
                if (codeError.equals("2")) {
               %>
               
               <br><h3 class = "fw-bold">Ha ocurrido un problema al subir la imagen</h3>
                <br><a href="menu.jsp" class = "btn btn-primary">Volver a menú </a>
              
              
        <%     }
                if (codeError.equals("3")) {
               %>
               
               <br><h3 class = "fw-bold">No puede eliminar una imagen que no le pertenece</h3>
                <br><a href="menu.jsp" class = "btn btn-primary">Volver a menú </a>
              
        <%     }
               if (codeError.equals("4")) { 
               %>
               
               <br><h3 class = "fw-bold">Ha ocurrido un problema al eliminar la imagen</h3>
                <br><a href="menu.jsp" class = "btn btn-primary">Volver a menú </a>
               
        <%     }
               if (codeError.equals("5")) { 
               %>
               
               <br><h3 class = "fw-bold">No puede modificar una imagen que no sea de tu propiedad</h3>
                <br><a href="menu.jsp" class = "btn btn-primary">Volver a menú </a>
               
        <%     }
               if (codeError.equals("6")) { 
               %>
               
               <br><h3 class = "fw-bold">Ha ocurrido un problema al modificar la imagen</h3>
                <br><a href="menu.jsp" class = "btn btn-primary">Volver a menú </a>
               
        <%     }
            }
            %>
    </body>
</html>
