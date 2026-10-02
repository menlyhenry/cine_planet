package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.enums.TipoDocumento;
import pe.edu.upeu.sysventas.model.Cliente;

public class ClienteRepository extends AbstractJpaRepository<Cliente, String> {
    @Override
    protected String getId(Cliente entity) {
        return entity.getDniruc();
    }

    @Override
    protected void setId(Cliente entity, String id) {
        entity.setDniruc(id);
    }

    @Override
    protected String generateId() {
        return null;
    }

    public void seedData() {
        if (findAll().isEmpty()) {
            save(new Cliente("70000001", "Cliente Demo", "", TipoDocumento.DNI));
        }
    }
}
