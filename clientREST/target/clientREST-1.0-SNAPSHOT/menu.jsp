<%-- 
    Document   : menu
    Created on : 26-sep-2022, 20:12:33
    Author     : alumne
--%>

<%
    //Si ponemos menu.jsp en la url esto evita que podamos entrar. Como no existe ningun
    //atributo para la session nos manda directamente al login otra vez.
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
    }
    %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Menu</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi" crossorigin="anonymous">
    </head>
    <body>
        <h1 class="fw-bold">¡Bienvenido!</h1><br>
            
        <ol class="list-group list-group-numbered">
  <li class="list-group-item d-flex justify-content-between align-items-start" style = "width: 800px;">
    <div class="ms-2 me-auto">
      <div class="fw-bold"><a href="registrarImagen.jsp">Registrar una nueva imagen</a></div>
      ¡Registre una nueva imagen en la plataforma!
    </div>
      <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" fill="currentColor" class="bi bi-file-arrow-up-fill" viewBox="0 0 16 16">
  <path d="M12 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2zM7.5 6.707 6.354 7.854a.5.5 0 1 1-.708-.708l2-2a.5.5 0 0 1 .708 0l2 2a.5.5 0 0 1-.708.708L8.5 6.707V10.5a.5.5 0 0 1-1 0V6.707z"/>
</svg>
  </li>
  <li class="list-group-item d-flex justify-content-between align-items-start" style = "width: 800px;">
    <div class="ms-2 me-auto">
      <div class="fw-bold"><a href="list.jsp">Listar todas las imágenes</a></div>
      ¡Visualize todas las imágenes de la plataforma y sus metadatos!
    </div>
    <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" fill="currentColor" class="bi bi-images" viewBox="0 0 16 16">
  <path d="M4.502 9a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3z"/>
  <path d="M14.002 13a2 2 0 0 1-2 2h-10a2 2 0 0 1-2-2V5A2 2 0 0 1 2 3a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v8a2 2 0 0 1-1.998 2zM14 2H4a1 1 0 0 0-1 1h9.002a2 2 0 0 1 2 2v7A1 1 0 0 0 15 11V3a1 1 0 0 0-1-1zM2.002 4a1 1 0 0 0-1 1v8l2.646-2.354a.5.5 0 0 1 .63-.062l2.66 1.773 3.71-3.71a.5.5 0 0 1 .577-.094l1.777 1.947V5a1 1 0 0 0-1-1h-10z"/>
</svg>
  </li>
  <li class="list-group-item d-flex justify-content-between align-items-start" style = "width: 800px;">
    <div class="ms-2 me-auto">
      <div class="fw-bold"><a href="buscarImagen.jsp">Buscar imágenes</a></div>
      ¡Busque imágenes utilizando distintos criterios de búsqueda!
    </div>
    <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" fill="currentColor" class="bi bi-search" viewBox="0 0 16 16">
  <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/>
</svg>
  </li>
        </ol> <br>
        <div class ="mx-auto" >
                    <a href="login.jsp?cerrar=true" class = "btn btn-primary" style = "background-color: crimson;">Cerrar Sesion</a> </br>
                </div>
    </body>
</html>
