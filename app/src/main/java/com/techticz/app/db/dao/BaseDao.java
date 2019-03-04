package com.techticz.app.db.dao;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public abstract class BaseDao<T> {
    /**
     * Insert an object in the database.
     *
     * @param obj the object to be inserted.
     * @return The SQLite row id
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract Single<Long> insert(T obj);

    /**
     * Insert an array of objects in the database.
     *
     * @param obj the objects to be inserted.
     * @return The SQLite row ids
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Maybe<List<Long>> insert(List<T> obj);

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    public abstract Single<Integer> update(T obj);

    /**
     * Update an array of objects from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    public abstract void update(List<T> obj);

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    public abstract void delete(T obj);

    /*@Transaction
    public void upsert(T obj) {
        long id = insert(obj).;
        if (id == -1) {
            update(obj);
        }
    }*/

   /* @Transaction
    public void upsert(List<T> objList) {
        List<Long> insertResult = insert(objList);
        List<T> updateList = new ArrayList<>();

        for (int i = 0; i < insertResult.size(); i++) {
            if (insertResult.get(i) == -1) {
                updateList.add(objList.get(i));
            }
        }

        if (!updateList.isEmpty()) {
            update(updateList);
        }
    }*/

    public String getTableName() {
        // Below is based on your inheritance chain
        Class clazz = (Class)
                ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
        // tableName = StringUtil.toSnakeCase(clazz.getSimpleName());
        String tableName = clazz.getSimpleName();
        return tableName;
    }

    public Maybe<List<T>> findAll() {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "select * from " + getTableName()
        );
        Log.d("ROOM",query.getSql());
        return doFindAll(query);
    }

    @NotNull
    public Maybe<List<T>> findAllByProperty(@NotNull String property, @NotNull String value) {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "select * from " + getTableName()+" WHERE "+property+" = "+value
        );
        Log.d("ROOM",query.getSql());
        return doFindAllByProperty(query);
    }

    @NotNull
    public Maybe<List<T>> browseAllByProperty(@NotNull String property, @NotNull String value) {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "select * FROM " + getTableName()+" WHERE "+property+" LIKE \"%"+value+"%\""
        );
        Log.d("ROOM",query.getSql());
        return doFindAllByProperty(query);
    }



    public Single<T> findByProperty(@NotNull String property, @NotNull String value) {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "select * from " + getTableName()+" WHERE "+property+" = "+value
        );
        Log.d("ROOM",query.getSql());
        return doFindByProperty(query);
    }

    @RawQuery
    protected abstract Maybe<List<T>> doFindAll(SupportSQLiteQuery query);

    @RawQuery
    protected abstract Maybe<List<T>> doFindAllByProperty(SupportSQLiteQuery query);

    @RawQuery
    protected abstract Single<T> doFindByProperty(SupportSQLiteQuery query);


}