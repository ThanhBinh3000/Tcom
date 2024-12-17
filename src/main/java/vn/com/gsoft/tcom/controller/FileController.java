package vn.com.gsoft.tcom.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.gsoft.tcom.model.FileResponse;
import vn.com.gsoft.tcom.service.FileService;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService service;

    @PostMapping("/uploadFiles")
    public ResponseEntity<String> uploadFiles(@RequestParam("files[]") List<MultipartFile> files, @RequestParam("folderId") Long folderId) {
        try {

            String response = service.uploadFiles(files, folderId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IOException e) {
            log.error("Error when saving file to the database", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while saving the file.");
        }
    }

    @GetMapping("/downloadFile/{idFile}")
    public ResponseEntity<FileResponse> downloadFile(@PathVariable Long idFile) {
        try {
            FileResponse fileResponse = service.downloadFile(idFile);
            if (fileResponse.getFileData() == null || fileResponse.getFileData().length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            String encodedFileName = URLEncoder.encode(fileResponse.getName(), StandardCharsets.UTF_8.toString());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                    .body(fileResponse);
        } catch (UnsupportedEncodingException e) {
            log.error("Error when encoding file name", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            log.error("Error when downloading file with id " + idFile, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/delete/{idFile}")
    public ResponseEntity<String> deleteFile(@PathVariable Long idFile) {
        try {
            boolean isDeleted = service.delete(idFile);
            if (isDeleted) {
                return new ResponseEntity<>("File deleted successfully!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("File not found for deletion", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error when deleting file with id " + idFile, e);
            return new ResponseEntity<>("An error occurred while deleting the file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
