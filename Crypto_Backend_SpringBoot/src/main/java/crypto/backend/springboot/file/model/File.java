package crypto.backend.springboot.file.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
/**
 * the file model in the database
 */
public class File {


    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;
    private String fileName;
    private LocalDate dateOfCreation;
    private String clientName;
    private boolean encrypted;


    public File() {
    }
    public File(String fileName, LocalDate dateOfCreation) {
        this.fileName = fileName;
        this.dateOfCreation = dateOfCreation;
    }
    public File(String fileName, LocalDate dateOfCreation, String clientName) {
        this.fileName = fileName;
        this.dateOfCreation = dateOfCreation;
        this.clientName = clientName;
    }


    public File(String fileName, LocalDate dateOfCreation, String clientName, boolean encrypted) {
        this.fileName = fileName;
        this.dateOfCreation = dateOfCreation;
        this.clientName = clientName;
        this.encrypted = encrypted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    @Override
    public String toString() {
        return "File{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", dateOfCreation=" + dateOfCreation +
                ", clientName='" + clientName + '\'' +
                ", encrypted=" + encrypted +
                '}';
    }
}
