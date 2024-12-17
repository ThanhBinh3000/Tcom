package vn.com.gsoft.tcom.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.gsoft.tcom.entity.File;
import vn.com.gsoft.tcom.entity.Folder;
import vn.com.gsoft.tcom.repository.FileRepository;
import vn.com.gsoft.tcom.repository.FolderRepository;
import vn.com.gsoft.tcom.service.FolderService;
import java.util.*;

@Service
public class FolderServiceImpl implements FolderService {
    @Autowired
    private FolderRepository hdrRepo;
    @Autowired
    private FileRepository dtlRepo;

    public List<Folder> folderTree() throws Exception {
        List<Folder> folders = hdrRepo.findAllByIdFolderIsNull();
        for (Folder folder : folders) {
            loadSubFoldersAndFiles(folder);
        }
        return folders;
    }

    private void loadSubFoldersAndFiles(Folder folder) {
        if (folder != null) {
            List<Folder> folders = hdrRepo.findAllByIdFolder(folder.getId());
            folder.setChildren(folders);
            List<File> files = dtlRepo.findAllByIdFolder(folder.getId());
            folder.setFiles(files);
            for (Folder subFolder : folders) {
                loadSubFoldersAndFiles(subFolder);
            }
        }
    }

    @Override
    public Folder details(Long idFolder) throws Exception {
        if (idFolder == null) {
            throw new Exception("Invalid folder!");
        }
        Folder parentFolder = hdrRepo.findById(idFolder).orElse(null);
        if (parentFolder == null) {
            throw new Exception("Folder not found with id: " + idFolder);
        }
        buildPath(parentFolder);
        List<Folder> subFolders = hdrRepo.findAllByIdFolder(idFolder);
        List<File> files = dtlRepo.findAllByIdFolder(idFolder);
        parentFolder.setChildren(subFolders);
        parentFolder.setFiles(files);
        return parentFolder;
    }

    private void buildPath(Folder folder) {
        if (folder.getIdFolder() != null) {
            Folder parentFolder = hdrRepo.findById(folder.getIdFolder()).orElse(null);
            if (parentFolder != null) {
                buildPath(parentFolder);
                folder.setPath(parentFolder.getPath() + "/" + folder.getName());
            }
        } else {
            folder.setPath(folder.getName());
        }
    }

    @Override
    public Folder create(Long idFolder, String name) throws Exception {
        Folder folder = new Folder();
        folder.setIdFolder(idFolder);
        folder.setName(name);
        folder.setCreated(new Date());
        hdrRepo.save(folder);
        return folder;
    }

    @Override
    public Folder update(Long idFolder, String newName) throws Exception {
        Folder folder = hdrRepo.findById(idFolder)
                .orElseThrow(() -> new Exception("Folder not found"));
        folder.setName(newName);
        folder.setModified(new Date());
        buildPath(folder);
        return hdrRepo.save(folder);
    }

    @Override
    public boolean delete(Long idFolder) throws Exception {
        Folder parentFolder = hdrRepo.findById(idFolder).orElse(null);
        if (parentFolder == null) {
            throw new Exception("Folder not found with id: " + idFolder);
        }
        List<File> files = dtlRepo.findAllByIdFolder(idFolder);
        for (File file : files) {
            dtlRepo.delete(file);
        }
        List<Folder> subFolders = hdrRepo.findAllByIdFolder(idFolder);
        for (Folder subFolder : subFolders) {
            delete(subFolder.getId());
        }
        hdrRepo.delete(parentFolder);
        return true;
    }
}
