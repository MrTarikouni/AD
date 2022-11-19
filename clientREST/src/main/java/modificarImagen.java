/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import data.image;
import data.user;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import static java.lang.System.out;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 *
 * @author alumne
 */
@WebServlet(name = "modificarImagen", urlPatterns = {"/modificarImagen"})
@MultipartConfig
public class modificarImagen extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    
        HttpSession session = request.getSession(false);
        user u = new user();
        u = (user) session.getAttribute("user");
        if (u == null) {
        response.sendRedirect("login.jsp");
        }
        
        PrintWriter writer = response.getWriter();
        
        int id = (int) session.getAttribute("imageID");
        
        //Obtene imagen con su id
        URL url1 = new URL("http://localhost:8080/ServicioREST/resources/javaee8/getImageFromID/"+id);
        HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);               

        /* Recoger el resultado */
        InputStreamReader in = new InputStreamReader(conn.getInputStream());
        BufferedReader br = new BufferedReader(in);
        String result = String.valueOf(br.readLine()); 

        Gson gson = new Gson();
        image i = gson.fromJson(result, image.class); //Imagen con sus metadatos antiguos
        
        if (i.getCreator() == null ? u.getUsername() != null : !i.getCreator().equals(u.getUsername())) response.sendRedirect("error.jsp?error=5");
        
        URL url = new URL("http://localhost:8080/ServicioREST/resources/javaee8/modify/");
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        
        if (request.getParameter("title").length() != 0) i.setTitle(request.getParameter("title"));
        if (request.getParameter("author").length() != 0) i.setAuthor(request.getParameter("author"));
        if (request.getParameter("description").length() != 0) i.setDescription(request.getParameter("description"));
        if (request.getParameter("keywords").length() != 0) i.setKeywords(request.getParameter("keywords"));
        if (request.getParameter("cdate").length() != 0){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date1 = LocalDate.parse(request.getParameter("cdate"),formatter);
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            i.setCapture_date((date1.format(formatter1)));
            
        }
        /*
        Part filePart = request.getPart("file");
        String fileName = getFileName(filePart);
        if (fileName.length() != 0) {
            int lastIndex = fileName.lastIndexOf('.');
            String fileNamewithoutextension = fileName;
            String extension = "";
            if (lastIndex != -1){
                fileNamewithoutextension = fileName.substring(0, lastIndex);
                extension = fileName.substring(lastIndex);
            }

            i.setFilename(fileNamewithoutextension + Integer.toString(id) + extension);            
        }
        */

        /* Enviar los datos del formulario dentro de la petición */            
        String data2 = "id="+i.getId()+"&title="+i.getTitle()+"&description="+i.getDescription()+"&keywords="+i.getKeywords()+"&author="+i.getAuthor()+"&creator="+i.getCreator()+"&capture="+i.getCapture_date();
        OutputStream os2 = conn.getOutputStream();
        os2.write(data2.getBytes("utf-8"));

        /* Recoger el resultado */
        InputStreamReader in2 = new InputStreamReader(conn.getInputStream());
        BufferedReader br2 = new BufferedReader(in2);
        String result2 = String.valueOf(br2.readLine()); 
        conn.disconnect();
        /*
        try {
    
            //Si hay que modificar el fichero subido
            if (fileName.length() != 0) {
                
                //String appPath = request.getServletContext().getRealPath("");
                //final String path = appPath + "/files/";
                final String path = "/home/alumne/Descargas/Practica2/src/main/webapp/files/";
                
                
                //Eliminamos el fichero anterior que habia en la carpeta
                File f = new File(path + File.separator + i.getFilename());
                f.delete();

                int lastIndex = fileName.lastIndexOf('.');
                String fileNamewithoutextension = fileName;
                String extension = "";
                if (lastIndex != -1){
                    fileNamewithoutextension = fileName.substring(0, lastIndex);
                    extension = fileName.substring(lastIndex);
                }
                i.setFilename(fileNamewithoutextension + Integer.toString(id) + extension);

                
                OutputStream out = null;
                InputStream filecontent = null;

                out = new FileOutputStream(new File(path + File.separator
                        + fileName));
                filecontent = filePart.getInputStream();

                int read = 0;
                final byte[] bytes = new byte[1024];

                while ((read = filecontent.read(bytes)) != -1) {
                    out.write(bytes, 0, read);

                }
                
                File file1 = new File(path+fileName);
                File file2 = new File(path+i.getFilename());
        
                file1.renameTo(file2);


            }
            */
           if (result2.equals("1")) {
                        writer.println("   <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi\" crossorigin=\"anonymous\">\n" +
             "");
                             writer.println(" <div style = margin: \"25px\";>");
                             writer.println("<h3 class = \"fw-bold\">Imagen modificada correctamente</h3> <br>");
                             writer.println("<br><a href=\"menu.jsp\" class = \"btn btn-primary\">Volver al menú </a>");
                             writer.println("</div>");
           }
           
           else {
               response.sendRedirect("error.jsp?error=6");
           }
        /*
        }catch(Exception e) {
            response.sendRedirect("error.jsp?error=6");
        } 
           */
        
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
    }
}