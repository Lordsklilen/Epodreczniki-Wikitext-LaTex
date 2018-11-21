package com.wikitolatex.converter.Controller;

import javassist.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/converter")
public class MainController {

    private static final String EXTERNAL_FILE_PATH = ".\\ResourceFiles\\";
    @RequestMapping("/index")
    String index()
    {
        return "index page";
    }

    //Displaying local pdf file in browser from "ResourceFiles" directory
    @RequestMapping("/file/{fileName:.+}")
    public void downloadPDFResource(HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("fileName") String fileName) throws IOException {
        File file = new File(EXTERNAL_FILE_PATH + fileName);
        InputStream inputStream = null;
        try {
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
            response.setContentLength((int) file.length());
            inputStream = new BufferedInputStream(new FileInputStream(file));
            FileCopyUtils.copy(inputStream, response.getOutputStream());
            System.out.println("\nConversion successful!\npath: " + file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            System.err.println("\n" + fileName + ": File does not exist");
            response.setContentType("text/html;charset=UTF-8");
            throw new FileNotFound(fileName + ": File does not exist");
        } catch (IOException e) {
            System.err.println("\n" + fileName + ": File exists, but there was IOException");
            response.setContentType("text/html;charset=UTF-8");
            throw new IOException(fileName + ": File exists, but there was IOException");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    response.setContentType("text/html;charset=UTF-8");
                    throw e;
                }
            }
        }
    }
}

/*
    @RequestMapping("/pdf/{fileName:.+}")
    public void downloadPDFResource( HttpServletRequest request,
                                     HttpServletResponse response,
                                     @PathVariable("fileName") String fileName)
    {

        String dataDirectory = request.getServletContext().getContextPath();
        Path file = Paths.get(dataDirectory, fileName);
        if (Files.exists(file))
        {
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename="+fileName);
            try
            {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

*/