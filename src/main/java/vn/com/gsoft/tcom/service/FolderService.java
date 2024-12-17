package vn.com.gsoft.tcom.service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.gsoft.tcom.entity.Folder;

import java.util.List;

public interface FolderService {
    List<Folder> folderTree () throws Exception;

    Folder details(Long idFolder) throws Exception;

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    Folder create(Long idFolder, String name) throws Exception;

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    Folder update(Long idFolder, String newName) throws Exception;

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    boolean delete(Long idFolder) throws Exception;
}
