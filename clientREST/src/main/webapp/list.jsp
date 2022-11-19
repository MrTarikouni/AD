<%-- 
    Document   : list
    Created on : 01-oct-2022, 19:47:13
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
        <title>Listar imágenes</title>
    </head>
    <body>
        <%
            response.sendRedirect("list");
            %>
    </body>
</html>
