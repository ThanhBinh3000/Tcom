package vn.com.gsoft.tcom.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.gsoft.tcom.entity.Folder;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findAllByIdFolderIsNull();

    List<Folder> findAllByIdFolder(Long idFolder);
}
