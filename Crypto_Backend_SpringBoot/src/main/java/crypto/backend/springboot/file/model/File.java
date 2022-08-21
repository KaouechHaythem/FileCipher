package crypto.backend.springboot.file.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table
/**
 * the file model in the database
 */
public class File {
    private String userName;
    @Id
    @SequenceGenerator(name = "files_sequence",
            sequenceName = "files_sequence",
            allocationSize = 1)
    @GeneratedValue()
    @Column(
            name = "id", updatable = false
    )
    private UUID id;
    private String fileName;


    private LocalDate dateOfCreation;


    public File(UUID id, String fileName, LocalDate dateOfCreation) {
        this.id = id;
        this.fileName = fileName;
        this.dateOfCreation = dateOfCreation;
    }

    public File() {
    }

    public File(String fileName, LocalDate dateOfCreation) {

        this.dateOfCreation = dateOfCreation;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public UUID getId() {
        return id;
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id + "filename" + fileName +
                ", dateOfCreation=" + dateOfCreation +
                '}';
    }
}
