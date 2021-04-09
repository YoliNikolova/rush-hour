package com.prime.rushhour.services;

import java.util.List;

public interface BaseService<T, E> {
    List<T> getAll();

    T getById(int id);

    void add(E dto);

    T updateById(E dto,int id);

    void delete(int id);
}
