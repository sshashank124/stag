package com.phaqlow.stag.model.dao

import com.phaqlow.stag.util.contracts.Item
import io.reactivex.Completable
import io.reactivex.Single


interface ItemsDb<T: Item> {

    fun insertItem(item: T): Single<Long>

    fun insertItems(vararg items: T): Single<List<Long>>

    fun insertItems(items: List<T>): Single<List<Long>>

    fun updateItem(item: T): Completable

    fun deleteItem(item: T): Completable

    fun getItem(itemId: Long): Single<T>

    fun getAllItems(): Single<List<T>>
}