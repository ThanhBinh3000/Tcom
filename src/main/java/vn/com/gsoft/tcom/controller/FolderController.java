package vn.com.gsoft.tcom.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.gsoft.tcom.entity.Folder;
import vn.com.gsoft.tcom.service.FolderService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/folders")
public class FolderController {

    @Autowired
    private FolderService service;

    @GetMapping("/getFolder")
    public ResponseEntity<List<Folder>> getFolderTree() {
        try {
            List<Folder> folders = service.folderTree();
            return new ResponseEntity<>(folders, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error when fetching the folder tree", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<Folder> getFolderDetails(@PathVariable Long id) {
        try {
            Folder folder = service.details(id);
            return folder != null ?
                    new ResponseEntity<>(folder, HttpStatus.OK) :
                    new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error when fetching folder details with id " + id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createFolder")
    public ResponseEntity<String> createFolder(@RequestParam(required = false) Long idFolder, @RequestParam String name) {
        try {
            Folder folder = service.create(idFolder, name);
            return new ResponseEntity<>("Folder created successfully!", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error when creating folder", e);
            return new ResponseEntity<>("An error occurred while creating the folder", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/updateFolder")
    public ResponseEntity<String> updateFolder(@RequestParam Long idFolder, @RequestParam String name) {
        try {
            Folder updatedFolder = service.update(idFolder,name);
            if (updatedFolder != null) {
                return new ResponseEntity<>("Folder updated successfully!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Folder with this ID not found!", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error when updating folder", e);
            return new ResponseEntity<>("An error occurred while updating the folder", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/delete/{idFolder}")
    public ResponseEntity<String> deleteFolder(@PathVariable Long idFolder) {
        try {
            boolean isDeleted = service.delete(idFolder);
            if (isDeleted) {
                return new ResponseEntity<>("Folder deleted successfully!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Folder not found for deletion", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error when deleting folder with id " + idFolder, e);
            return new ResponseEntity<>("An error occurred while deleting the folder", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
