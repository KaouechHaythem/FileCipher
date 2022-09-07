package crypto.backend.springboot.file.repository;


import crypto.backend.springboot.file.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, String> {


    List<File> findByClientName(String clientName);
}
