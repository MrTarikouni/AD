
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import data.user;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author alumne
 */
@WebServlet(urlPatterns = {"/register"})
public class register extends HttpServlet {

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
        
        PrintWriter out = response.getWriter();
        
        String username, pass1, pass2;
        
        username = request.getParameter("username");
        pass1 = request.getParameter("pass1");
        pass2 = request.getParameter("pass2");
        
        
        user ur = new user();
        ur.setUsername(username);
        if (pass1.equals(pass2)) {
           
            ur.setPassword(pass1);
            
            URL url = new URL("http://localhost:8080/ServicioREST/resources/javaee8/signup/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);               
         
            /* Enviar los datos del formulario dentro de la petición */            
            String data = "username="+username+"&password="+pass1;
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes("utf-8"));
            
            /* Recoger el resultado */
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            int result = Integer.valueOf(br.readLine()); 
            conn.disconnect();
            System.out.println(result);
            if (result == 1) {
                //Si el usuario es correcto
                response.sendRedirect("login.jsp");
            }

            else {
                //si el usuario no es correcto
                out.println("<br><h3 class = \"fw-bold\">El Usuario introducido ya existe</h3>");
                out.println("<br><a href=\"register.jsp\" class = \"btn btn-primary\">Volver a registro </a>");
            }
        }
        
        else{
           
            out.println("<br><h3 class = \"fw-bold\">La contraseña no coincide</h3>");
                out.println("<br><a href=\"register.jsp\" class = \"btn btn-primary\">Volver a registro </a>");
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
