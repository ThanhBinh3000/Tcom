package vn.com.gsoft.tcom.service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.com.gsoft.tcom.model.FileResponse;

import java.io.IOException;
import java.util.List;

public interface FileService {
    String uploadFiles(List<MultipartFile> files, Long folderId) throws IOException;

    FileResponse downloadFile(Long idFile) throws Exception;

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    boolean delete(Long idFile) throws Exception;
}
