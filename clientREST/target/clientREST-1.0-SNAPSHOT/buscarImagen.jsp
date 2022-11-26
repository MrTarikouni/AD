<%-- 
    Document   : buscarImagen
    Created on : 01-oct-2022, 19:49:36
    Author     : alumne
--%>

<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
    }
    %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Buscar Imagen</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi" crossorigin="anonymous">
    </head>
    <body>
         <h1 class = "fw-bold" style = "text-align: center;">Buscador de Imágenes</h1>
        <h3 class = "fw-bold" style = "margin:25px 50px 25px 75px; ">Busque imágenes utilizando distintos criterios de búsqueda. Rellene los campos por los que quiere filtrar. Si no introduce ningún dato se mostraran todas las imágenes registradas en la plataforma.</h3>
        <form class="row g-2" style = "margin:25px 50px 25px 75px; max-width: 800px;" action="buscarImagen" method="POST">

                               
  <div class="col-md-6">
    <label for="tit" class="form-label">Título</label>
    <input type="text" class="form-control"  name="title" id="tit">
  </div>
   <div class="col-md-6">
    <label for="aut" class="form-label">Autor</label>
    <input type="text" class="form-control" id="aut"  name="author">
  </div>
  <div class="col-12">
    <label for="des" class="form-label">Descripción</label>
    <input type="text" class="form-control" name="description" id="des">
  </div>
  <div class="col-12">
    <label for="key" class="form-label">Palabras Clave</label>
    <input type="text" class="form-control" id="key" name="keywords" placeholder="Palabra1, Palabra2...">
  </div>
  
  <div class="col-md-4">
    <label for="cap" class="form-label">Fecha Captura</label>
    <input type="date" class="form-control" name="cdate" id="cap">
  </div>
   <div class="col-md-4">
    <label for="aut" class="form-label">Creador</label>
    <input type="text" class="form-control" id="aut"  name="creator">
  </div>
            
  <div class="col-md-4">
     <label for="arch" class="form-label">Nombre del fichero</label>
  <input type="text" class="form-control" id="arch"  name="fname">
</div>
  <div class="col-12">
    <button type="submit" class="btn btn-primary">Realizar búsqueda</button>
  </div>
</form>
    </body>
</html>