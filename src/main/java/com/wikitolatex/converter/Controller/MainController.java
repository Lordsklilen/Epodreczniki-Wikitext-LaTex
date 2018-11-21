package com.wikitolatex.converter.Controller;

import com.wikitolatex.converter.Model.POJOClass;
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

    @RequestMapping("/probny")
    POJOClass index()
    {
        return new POJOClass(10,"jazda");
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


///////uposledzona wersja wyswietlania, jesli mamy juz plik na naszym komputerze w miejscu okreslonym na sztywno jestesmy w stanie wyswietlic go w przegladarce
//////ogarnac pobieranie, zeby zapisywal sie plik w konkretnym filderze naszej aplikacji, i z tamtego miejsca wczytywać go.

    private static final String EXTERNAL_FILE_PATH = "C:/fileDownloadExample/";

    @RequestMapping("/file/{fileName:.+}")
    public void downloadPDFResource(HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("fileName") String fileName) throws IOException {

        File file = new File(EXTERNAL_FILE_PATH + fileName);
        if (file.exists()) {

            //get the mimetype
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                //unknown mimetype so set the mimetype to application/octet-stream
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);


            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

            //response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

            response.setContentLength((int) file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            FileCopyUtils.copy(inputStream, response.getOutputStream());

        }
    }

}
