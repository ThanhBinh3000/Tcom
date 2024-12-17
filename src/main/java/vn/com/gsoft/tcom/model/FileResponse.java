package vn.com.gsoft.tcom.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileResponse {
    private String name;
    private String type;
    private byte[] fileData;
    public FileResponse(String name, String type, byte[] fileData) {
        this.name = name;
        this.type = type;
        this.fileData = fileData;
    }
}
