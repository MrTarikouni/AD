<%-- 
    Document   : login
    Created on : 18-sep-2022, 13:20:41
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Log in</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi" crossorigin="anonymous">

    </head>
    <body>
        <h1 class = "fw-bold" style = "text-align: center;">Log in</h1>
        <form action="login" method="POST">
            <div class = "mx-auto" style = "width: 400px;">
                
                <label for="user" class ="form-label">Usuario</label>
                <input type="text" class="form-control" required id ="user" name="username"> </br>
                <label for="pass" class ="form-label">Contraseña</label>
                <input type="password" class ="form-control" required id ="pass"  name="password"> </br>
                
                <div class ="mx-auto" style = "text-align: match-parent;">
                    <button type = "submit" class = "btn btn-primary"> Log In</button> </br>
                </div>
            </div>
            
                
        </form>
        <div class = "mx-auto" style = "width: 400px;">
            <br><p class= "fw-bold">Aún no está registrado? <a href="register.jsp">Registrese aquí</a></p>
            </div>
        
        
        <%
            //Si haces un logout desde el menu y vueleve para alante en la pagina de log in volvias al menu
            //Esto lo evita
            if (request.getParameter("cerrar") != null) {
                session.invalidate();
            }
            %>
    </body>
</html>
