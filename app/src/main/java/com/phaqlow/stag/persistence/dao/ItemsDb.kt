package com.phaqlow.stag.persistence.dao

import io.reactivex.Completable
import io.reactivex.Single


interface ItemsDb<T> {

    fun insertItem(item: T): Single<Long>

    fun updateItem(item: T): Completable

    fun deleteItem(item: T): Completable

    fun getItem(itemId: Long): Single<T>

    fun getAllItems(): Single<List<T>>
}