package pe.edu.upeu.sysventas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.model.Cliente;
import pe.edu.upeu.sysventas.repository.ClienteRepository;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.service.IClienteService;

import java.util.List;

@RequiredArgsConstructor
public class ClienteServiceImp extends CrudGenericoServiceImp<Cliente, String> implements IClienteService {
    private final ClienteRepository clienteRepository;

    private boolean sembrado;

    @Override
    protected ICrudGenericoRepository<Cliente, String> getRepo() {
        return clienteRepository;
    }

    @Override
    public List<Cliente> findAll() {
        if (!sembrado) {
            sembrado = true;
            if (clienteRepository.findAll().isEmpty()) {
                clienteRepository.seedData();
            }
        }
        return clienteRepository.findAll();
    }
}
