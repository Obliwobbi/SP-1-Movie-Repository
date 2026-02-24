package app.daos.interfaces;

import java.util.List;

public interface IDAO<T>
{
    T create(T t);

    T get(Integer id);

    List<T> getAll();

    T update(T t);

    void delete(Integer id);
}
