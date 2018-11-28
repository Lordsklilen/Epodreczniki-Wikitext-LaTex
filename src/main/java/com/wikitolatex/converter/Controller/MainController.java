package com.wikitolatex.converter.Controller;

import com.wikitolatex.converter.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/converter")
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private FileStorageService fileStorageService;

    //private static final String EXTERNAL_FILE_PATH = ".\\ResourceFiles\\";

    @RequestMapping("/index")
    String index()
    {
        return "index page";
    }


    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/converter/downloadFile/")
                .path(fileName)
                .toUriString();

        System.out.println("\nConversion successful!\nFile converted: " + fileName);

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /*
    Outdated code from last iteration

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
    */
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