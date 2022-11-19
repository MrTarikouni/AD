<%-- 
    Document   : register
    Created on : 18-sep-2022, 13:27:07
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Registro</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi" crossorigin="anonymous">
    </head>
    <body>
        <h1 class = "fw-bold" style = "text-align: center;">Registro</h1>
        <form action="register" method="POST">
            <div class = "mx-auto" style = "width: 400px;">
                
                <label for="user" class ="form-label">Usuario</label>
                <input type="text" class="form-control" required id ="user" name="username"> </br>
                <label for="pass" class ="form-label">Contraseña</label>
                <input type="password" class ="form-control" required id ="pass"  name="pass1"> </br>
                <label for="conpass" class ="form-label"> Confirmar Contraseña</label>
                <input type="password" class ="form-control" required id ="conpass"  name="pass2"> </br>
                
                <div class ="mx-auto" style = "text-align: match-parent;">
                    <button type = "submit" class = "btn btn-primary"> Registrarse</button> </br>
                </div>
            </div>
            
                
        </form>
    </body>
</html>
