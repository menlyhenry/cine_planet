package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.Pelicula;

public class PeliculaRepository extends AbstractJpaRepository<Pelicula, Long> {
    private long sequence = 1;

    @Override
    protected Long getId(Pelicula entity) {
        return entity.getIdPelicula();
    }

    @Override
    protected void setId(Pelicula entity, Long id) {
        entity.setIdPelicula(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    public void seedData() {
        if (findAll().isEmpty()) {
            save(new Pelicula(generateId(), "Película Demo", "Acción", 120));
        }
    }
}
