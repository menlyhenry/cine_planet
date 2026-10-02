package pe.edu.upeu.sysventas.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractJpaRepository<T, ID> implements ICrudGenericoRepository<T, ID> {
    protected final List<T> data = new ArrayList<>();
    protected abstract ID getId(T entity);
    protected abstract void setId(T entity, ID id);
    protected abstract ID generateId();

    @Override
    public T save(T entity) {
        if (getId(entity) == null) {
            ID id = generateId();
            if (id == null) {
                throw new IllegalArgumentException("El ID es obligatorio");
            }
            setId(entity, id);
        }
        if (existsById(getId(entity))) {
            throw new IllegalArgumentException("Ya existe un registro con el ID: " + getId(entity));
        }
        data.add(entity);
        return entity;
    }

    @Override
    public T update(ID id, T entity) {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio para actualizar");
        }
        setId(entity, id);
        for (int i = 0; i < data.size(); i++) {
            T current = data.get(i);
            if (id.equals(getId(current))) {
                data.set(i, entity);
                return entity;
            }
        }
        throw new RuntimeException("No se encontro la entidad con ID: " + id);
    }

    @Override
    public Optional<T> findById(ID id) {
        return data.stream().filter(entity -> id.equals(getId(entity))).findFirst();
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void deleteById(ID id) {
        data.removeIf(entity -> id.equals(getId(entity)));
    }

    @Override
    public boolean existsById(ID id) {
        return data.stream().anyMatch(entity -> id.equals(getId(entity)));
    }
}
