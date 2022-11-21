
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import data.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Calendar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.servlet.http.Part;


/**
 *
 * @author alumne
 */
@WebServlet(urlPatterns = {"/registrarImagen"})
@MultipartConfig
public class registrarImagen extends HttpServlet {
    
    private static Logger logger = Logger.getLogger(registrarImagen.class.getCanonicalName());

      public registrarImagen() {
        super();
      }

protected void processRequest(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");

    // Create path components to save the file
    final String path = "/home/alumne/Escritorio/Practica3/clientREST/src/main/webapp/files/";
    final Part filePart = request.getPart("file");
    final String fileName = getFileName(filePart);
    HttpSession session = request.getSession(false);
    
    String title = request.getParameter("title");
    String author = request.getParameter("author");
    String description = request.getParameter("description");
    String keywords = request.getParameter("keywords");
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate date1 = LocalDate.parse(request.getParameter("cdate"),formatter);
    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    String capture_date = (date1.format(formatter1));
    
    
    user u = new user();
    u = (user) session.getAttribute("user");
    String creator = (u.getUsername());

    URL url = new URL("http://localhost:8080/ServicioREST/resources/javaee8/register/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);               

        /* Enviar los datos del formulario dentro de la petición */            
        String data = "title="+title+"&description="+description+"&keywords="+keywords+"&author="+author+"&creator="+creator+"&capture="+capture_date+"&filename="+fileName;
        OutputStream os = conn.getOutputStream();
        os.write(data.getBytes("utf-8"));

        /* Recoger el resultado */
        InputStreamReader in = new InputStreamReader(conn.getInputStream());
        BufferedReader br = new BufferedReader(in);
        int result = Integer.valueOf(br.readLine());  
        conn.disconnect();

    if (result == 1) {
        
        OutputStream out = null;
        InputStream filecontent = null;
        final PrintWriter writer = response.getWriter();

            out = new FileOutputStream(new File(path + File.separator
                    + fileName));
            filecontent = filePart.getInputStream();

            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);

            }

            writer.println("   <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi\" crossorigin=\"anonymous\">\n" +
    "");
            writer.println("<br><h3 class = \"fw-bold\">Imagen registrada correctamente.</h3>");
            writer.println("<br><a href=\"menu.jsp\" class = \"btn btn-primary\">Volver al menú </a> o <a href=\"buscarImagen.jsp\" class = \"btn btn-primary\" > Registrar otra imagen</a>");

            File file1 = new File(path+fileName);
            File file2 = new File(path+fileName);

            file1.renameTo(file2);
    }


        else {
          response.sendRedirect("error.jsp?error=2");
        }
    }

private String getFileName(final Part part) {
    final String partHeader = part.getHeader("content-disposition");
    
    for (String content : part.getHeader("content-disposition").split(";")) {
        if (content.trim().startsWith("filename")) {
            return content.substring(
                    content.indexOf('=') + 1).trim().replace("\"", "");
        }
    }
    return null;
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


