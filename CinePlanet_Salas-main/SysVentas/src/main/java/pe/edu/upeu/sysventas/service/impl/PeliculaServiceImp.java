package pe.edu.upeu.sysventas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.model.Pelicula;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.repository.PeliculaRepository;
import pe.edu.upeu.sysventas.service.IPeliculaService;

import java.util.List;

@RequiredArgsConstructor
public class PeliculaServiceImp extends CrudGenericoServiceImp<Pelicula, Long> implements IPeliculaService {
    private final PeliculaRepository peliculaRepository;

    private boolean sembrado;

    @Override
    protected ICrudGenericoRepository<Pelicula, Long> getRepo() {
        return peliculaRepository;
    }

    @Override
    public List<Pelicula> findAll() {
        if (!sembrado) {
            sembrado = true;
            if (peliculaRepository.findAll().isEmpty()) {
                peliculaRepository.seedData();
            }
        }
        return peliculaRepository.findAll();
    }
}
