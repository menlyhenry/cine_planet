package pe.edu.upeu.sysventas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.model.Sala;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.repository.SalaRepository;
import pe.edu.upeu.sysventas.service.ISalaService;

import java.util.List;

@RequiredArgsConstructor
public class SalaServiceImp
        extends CrudGenericoServiceImp<Sala, Long>
        implements ISalaService {

    private final SalaRepository salaRepository;

    private boolean sembrado;

    @Override
    protected ICrudGenericoRepository<Sala, Long> getRepo() {
        return salaRepository;
    }
    @Override
    public List<Sala> findAll() {
        if (!sembrado) {
            sembrado = true;
            if (salaRepository.findAll().isEmpty()) {
                salaRepository.seedData();
            }
        }

        return salaRepository.findAll();
    }
}
