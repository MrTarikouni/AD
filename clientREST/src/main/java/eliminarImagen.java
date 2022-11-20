/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.Gson;
import data.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author alumne
 */
@WebServlet(name = "eliminarImagen", urlPatterns = {"/eliminarImagen"})
public class eliminarImagen extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
  
        
       HttpSession session = request.getSession(false);
       
        user u = (user) session.getAttribute("user");
        
        if (u == null) response.sendRedirect("login.jsp");
        
       PrintWriter out = response.getWriter();
        //String appPath = request.getServletContext().getRealPath("");
        //final String path = appPath + "/files/";
       final String path = "/home/alumne/Descargas/Practica3/clientREST/src/main/webapp/files/";
       
       u = (user) session.getAttribute("user");
       String username = u.getUsername();
       
       String imageID = (session.getAttribute("imageID")).toString();
       int id = Integer.parseInt(imageID); //parseInt converts a string to an int
       
        URL url = new URL("http://localhost:8080/ServicioREST/resources/javaee8/getImageFromID/"+id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);               

        /* Recoger el resultado */
        InputStreamReader in = new InputStreamReader(conn.getInputStream());
        BufferedReader br = new BufferedReader(in);
        String result = String.valueOf(br.readLine()); 
        
        
        

        Gson gson = new Gson();
        image i = gson.fromJson(result, image.class);
        image x = new image();
        
        if ((i.getCreator() == null ? u.getUsername() != null : !i.getCreator().equals(u.getUsername())) || i == null || i == x) response.sendRedirect("error.jsp?error=3");

        //Si parametro creator == username de la sesion del usuario
        else if (username.equals(i.getCreator())) {
            //File f = new File(path + File.separator + i.getFilename());
            
            //if (f.delete()) {

            URL url2 = new URL("http://localhost:8080/ServicioREST/resources/javaee8/delete/");
            conn = (HttpURLConnection) url2.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);               

            /* Enviar los datos del formulario dentro de la petición */            
            String data2 = "id="+id+"&creator="+i.getCreator();
            OutputStream os2 = conn.getOutputStream();
            os2.write(data2.getBytes("utf-8"));

            /* Recoger el resultado */
            InputStreamReader in2 = new InputStreamReader(conn.getInputStream());
            BufferedReader br2 = new BufferedReader(in2);
            String result2 = String.valueOf(br2.readLine()); 
            conn.disconnect();

            if (result2.equals("1")) {
                out.println("   <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi\" crossorigin=\"anonymous\">\n" +
"");
                out.println(" <div style = margin: \"25px\";>");
                out.println("<h3 class = \"fw-bold\">Imagen eliminada correctamente</h3> <br>");
                out.println("<br><a href=\"menu.jsp\" class = \"btn btn-primary\">Volver al menú </a>");
                out.println("</div>");                    
            }
            else {
                response.sendRedirect("error.jsp?error=4");
            }
            //}
            
           // else response.sendRedirect("error.jsp?error=4");
            
        }

        else {
            response.sendRedirect("error.jsp?error=3");
        }
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}