
package securifile.backend.springboot.client.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import securifile.backend.springboot.client.model.Client;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    /**
     * return only the names of the clinets
     * its used for security perposes
     * private and public key can t be returned and sent to the front
     *
     * @return
     */
    @Query(value = "select new securifile.backend.springboot.client.model.Client(e.clientName) from Client e  ")
    List<Client> findNameClient();
}
