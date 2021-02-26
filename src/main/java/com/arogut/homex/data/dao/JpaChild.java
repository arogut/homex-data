package com.arogut.homex.data.dao;

public interface JpaChild<T> {

    void setId(String id);

    void setParent(T entity);
}
