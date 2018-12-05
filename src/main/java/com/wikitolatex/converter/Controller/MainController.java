package com.wikitolatex.converter.Controller;

import com.wikitolatex.converter.Pandoc.DocumentConverter;
import com.wikitolatex.converter.Pandoc.InputFormat;
import com.wikitolatex.converter.Pandoc.OutputFormat;
import com.wikitolatex.converter.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
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

    @Autowired
    private DocumentConverter documentConverter;

    //private static final String EXTERNAL_FILE_PATH = ".\\ResourceFiles\\";

    @RequestMapping("/index")
    String index() {
        return "index page";
    }


    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/converter/downloadFile/")
                .path(fileName)
                .toUriString();

        //konwersja z wiki do latexa, trzeba podmnienic obiekt fileFrom na ten wlasciwy
        //documentConverter.fromFile(File fileFrom, InputFormat.TWIKI)
        //        .toFile(new File("converter/downloadFile/"+fileName), OutputFormat.LATEX)
        //        .convert();

        System.out.println("\nConversion successful!\nFile converted: " + fileName);

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {

        /*
            There should be a conversion.
         */

        for (MultipartFile file : files)
            System.out.println("\nConversion successful!\nFile converted: " + fileStorageService.storeFile(file));

        return Arrays.asList(files)
                .stream()
                .map(file -> {
                    try {
                        return uploadFile(file);
                    } catch (IOException e) {
                        return null;
                    }

                })
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
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                /*
                    in header: use attachment instead of inline for instant download in browser
                 */
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}