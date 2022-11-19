<%-- 
    Document   : modificarImagen
    Created on : 01-oct-2022, 19:41:44
    Author     : alumne
--%>

<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.net.URL"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="data.*"%>
<%@page import = "com.google.gson.Gson" %>

<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
    }
    %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Modificar Imagen</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi" crossorigin="anonymous">
        <%
            user u = new user();
            u = (user) session.getAttribute("user");
            String username = u.getUsername();
            
            if (request.getParameter("id") == null) {
                response.sendRedirect("error.jsp?error=6");
            }
            
            String imageID = request.getParameter("id");
            int id = Integer.parseInt(imageID);
            
            URL url = new URL("http://localhost:8080/ServicioREST/resources/javaee8/getImageFromID/"+id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);               

            /* Recoger el resultado */
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String result = (br.readLine());  
            conn.disconnect();
            
            Gson gson = new Gson();
            image i = gson.fromJson(result, image.class);
            
            if (!username.equals(i.getCreator())){
                response.sendRedirect("error.jsp?error=5");
            }
            
            else if (i.getCreator().length() == 0) {
                response.sendRedirect("error.jsp?error=5");
            }
            else{
            
            session.setAttribute("imageID", id);
        %>
    </head>
    
    <body>
            <h1 class = "fw-bold" style = "text-align: center;">Modificar imagen</h1>
        <h3 class = "fw-bold" style = "margin:25px 50px 25px 75px; ">Modifique los campos que desee sobre esta imagen.</h3>
        <form class="row g-2" style = "margin:25px 50px 25px 75px; max-width: 800px;" action="modificarImagen" enctype="multipart/form-data" method="POST">
  <div class="col-md-6">
    <label for="tit" class="form-label">Título</label>
    <input type="text" class="form-control"  name="title"  value = <%out.println(i.getTitle()); %> id="tit">
  </div>
   <div class="col-md-6">
    <label for="aut" class="form-label">Autor</label>
    <input type="text" class="form-control" id="aut" name="author" value = <%out.println(i.getAuthor()); %>>
  </div>
  <div class="col-12">
    <label for="des" class="form-label">Descripción</label>
    <input type="text" class="form-control"  name="description" value = <%out.println(i.getDescription()); %> id="des">
  </div>
  <div class="col-12">
    <label for="key" class="form-label">Palabras Clave</label>
    <input type="text" class="form-control" id="key"  name="keywords" value = <%out.println(i.getKeywords()); %>>
  </div>
    <% DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDate date1 = LocalDate.parse(i.getCapture_date(),formatter);
    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String data = date1.format(formatter1);
   %>
  <div class="col-md-6">
    <label for="cap" class="form-label">Fecha Captura</label>
    <input type="date" class="form-control" name="cdate" id="cap" value = <%out.println(data); %>>
  </div>
            
  <div class="input-group mb-3">
     
  <input type="file" class="form-control" id="arch"  name="file">
  <label class="input-group-text" for="arch">Subir Imagen</label>
</div>
  <div class="col-12">
    <button type="submit" class="btn btn-primary">Modificar Imagen</button>
  </div>
</form>
    </body>
    <%} %>

