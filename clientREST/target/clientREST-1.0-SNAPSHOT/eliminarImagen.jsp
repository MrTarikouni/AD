<%-- 
    Document   : eliminarImagen
    Created on : 01-oct-2022, 19:45:00
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
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Eliminar Imagen</title>
        
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi" crossorigin="anonymous">
    </head>
    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.3/umd/popper.min.js" integrity="sha384-vFJXuSJphROIrBnz7yo7oB41mKfc8JzQZiCq4NCceLEaO4IHwicKwpJf9c9IpFgh" crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/js/bootstrap.min.js" integrity="sha384-alpBpkh1PFOepccYVYDB4do5UnbKysX5WZXm3XxPqe5iKTfUKjNkCk9SaVuEZflJ" crossorigin="anonymous"></script>

    <body>
            <%
                if (request.getParameter("id") != null) {
                    session.setAttribute("imageID", request.getParameter("id"));
                    //out.println("You will delete this image " + request.getParameter("id"));
                }
                
                else {
                    response.sendRedirect("error.jsp?error=4");
                }
                int id = Integer.valueOf(request.getParameter("id"));
                
                
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
                
                
                if (result.length() < 3) response.sendRedirect("error.jsp?error=3");

                else {
                Gson gson = new Gson();
                image i = gson.fromJson(result, image.class);
                
                String dir; 
        
                user u = (user) session.getAttribute("user");
                String username = u.getUsername();
                
                if (!username.equals(i.getCreator())) {
                    response.sendRedirect("error.jsp?error=3");
                    i = new image();
                }
                else if (i.getCreator().length() == 0 || i == null) response.sendRedirect("error.jsp?error=3");
                else{
                    try {
                        //dir = "files/" + i.getFilename();
                        dir = "http://localhost:8080/ServicioREST/resources/javaee8/getImage/" + id;
                        out.println("<div style = \"text-align: center;\">");
                        out.println("<br><h1 class = \"fw-bold\">Borrará la siguiente imagen:</h1>");
                        out.println("<img src="+ dir +" width= \"400px\" height= \"auto\" style = \"border: 5px solid;\"  /><br>");

                        out.println("<br>Titulo = " + i.getTitle());
                        out.println("<br>Descripción = " + i.getDescription());
                        out.println("<br>Palabras Clave = " + i.getKeywords());
                        out.println("<br>Autor = " + i.getAuthor());
                        out.println("<br>Creador = " + i.getCreator());
                        out.println("<br>Fecha Captura = " +i.getCapture_date());
                        out.println("<br>Fecha de guardado = " + i.getStorage_date());
                        out.println("<br>Nombre del fichero = " + i.getFilename());

                        //out.println("<br><a href="+dir+">Visualiza la imagen </a> ");
                    }
                    catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
                }


                    %>
            <br>
<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModal">
  Eliminar Imagen
</button>

<div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel">Eliminar Imagen</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        ¿Está seguro que quiere eliminar esta imagen? Esta acción es irreversible.
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
        <a href="eliminarImagen" class="btn btn-primary">Confirmar</a>
      </div>
    </div>
  </div>
</div>
            <br>


            <br>
            <a href="menu.jsp"  class="btn btn-primary">Volver al menú.</a> 
            </div>

    </body>
</html>