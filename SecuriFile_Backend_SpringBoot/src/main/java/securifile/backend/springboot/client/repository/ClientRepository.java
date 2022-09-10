
package securifile.backend.springboot.client.repository;


import securifile.backend.springboot.client.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    @Query(value = "select new securifile.backend.springboot.client.model.Client(e.clientName) from Client e  ")
    List<Client> findNameClient();
}