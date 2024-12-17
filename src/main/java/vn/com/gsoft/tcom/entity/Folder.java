package vn.com.gsoft.tcom.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Folder")
public class Folder{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;
    @Column(name = "Name")
    private String name;
    @Column(name = "Id_Folder")
    private Long idFolder;
    @CreatedDate
    @Column(name="Created")
    private Date created;
    @LastModifiedDate
    @Column(name="Modified")
    private Date modified;
    @Transient
    private List<Folder> children;
    @Transient
    private List<File> files;
    @Transient
    private String path;
}
