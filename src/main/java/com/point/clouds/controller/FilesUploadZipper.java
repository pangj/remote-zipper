package com.point.clouds.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class FilesUploadZipper {

    @RequestMapping(value="/zipuploaded", produces="application/zip", method = RequestMethod.POST )
    public ResponseEntity<StreamingResponseBody> zipUploadsWithOutputStreamAsResp(@RequestParam("files") MultipartFile[] files)  {
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=\"uploadedCompressed.zip\"")
                .body(out -> {
                    try (var zipOutputStream = new ZipOutputStream(new BufferedOutputStream(out))) {
                        for (MultipartFile file : files) {
                            String name = file.getOriginalFilename();
                            zipOutputStream.putNextEntry(new ZipEntry(name));
                            InputStream fis = file.getInputStream();
                            copy(fis, zipOutputStream);
                            fis.close();
                            zipOutputStream.closeEntry();
                        }
                    }
                });
    }

    private void copy(InputStream fis, ZipOutputStream zipOut) throws IOException {
        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
