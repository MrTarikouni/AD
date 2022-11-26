/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.Gson;
import Database.Imagequery;
import data.CjtImages;
import data.image;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import data.user;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import static java.lang.System.out;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 *
 * @author alumne
 */
@WebServlet(name = "list", urlPatterns = {"/list"})
public class list extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);
        user u = new user();
        u = (user) session.getAttribute("user");
        if (u == null) {
        response.sendRedirect("login.jsp");
        }
        response.getWriter();
        URL url = new URL("http://localhost:8080/ServicioREST/resources/javaee8/list/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);               
         
            /* Enviar los datos del formulario dentro de la petición */            
            //String data = "";
            //OutputStream os = conn.getOutputStream();
            //os.write(data.getBytes("utf-8"));
            
            /* Recoger el resultado */
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String result = (br.readLine()); 
            
            Gson gson = new Gson();
            CjtImages cjt = new CjtImages();
            cjt = gson.fromJson(result, CjtImages.class);
            
            conn.disconnect();
            
        
        String dir; 
        int counter = 0;
        
        try (PrintWriter out = response.getWriter()) {
            out.println("   <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi\" crossorigin=\"anonymous\">\n" +
"");
            out.println("<br><h1 class = \"fw-bold\" style = \"text-align: center;\">Lista de imágenes registradas en el sistema</h1><br>");
            for (Map.Entry<Integer,image> set : cjt.getImages().entrySet()){
                image i = set.getValue();
                ++counter;
                //dir = "files/"+i.getFilename();
                int id = i.getId();
                dir = "http://localhost:8080/ServicioREST/resources/javaee8/getImage/" + id;
                
                
                out.println("<div style = \"text-align: center;\">");
                out.println("<img src="+ dir +" width= \"400px\" height= \"auto\" style = \"border: 5px solid;\"  /><br>");
                out.println("<br><strong>Titulo</strong> = " + i.getTitle());
                out.println("<br><strong>Descripción</strong> = " + i.getDescription());
                out.println("<br><strong>Palabras clave</strong> = " + i.getKeywords());
                out.println("<br><strong>Autor</strong> = " + i.getAuthor());
                out.println("<br><strong>Creador</strong> = " + i.getCreator());
                out.println("<br><strong>Fecha captura</strong> = " + i.getCapture_date());
                out.println("<br><strong>Fecha de guardado</strong> = " + i.getStorage_date());
                out.println("<br><strong>Nombre del fichero</strong> = " + i.getFilename());
                
                
                out.println("<br><a href="+dir+">Descargar imagen</a><br> ");
                
                
                if (u.getUsername() == null ? i.getCreator() == null : u.getUsername().equals(i.getCreator())){
                    out.println("<br><a href=\"modificarImagen.jsp?id="+id+"\" class = \"btn btn-primary\" style = \"background-color: crimson;\">Modificar Imagen</a><br>");
                    out.println("<br><a href=\"eliminarImagen.jsp?id="+id+"\" class = \"btn btn-primary\" style = \"background-color: crimson;\">Eliminar Imagen</a><br>");
                }
                out.println("</div><hr>");
        }
            if (counter == 0){
                out.println("<br><h3 class = \"fw-bold\">No hay ninguna imagen registrada en el sistema</h3>");
                out.println("<br><a href=\"menu.jsp\" class = \"btn btn-primary\">Volver al menú </a> o <a href=\"registrarImagen.jsp\" class = \"btn btn-primary\" > Introducir nueva imagen.</a>");
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
    }

    // <editor-fold defaultstate="" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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