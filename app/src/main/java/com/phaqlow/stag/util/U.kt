package com.phaqlow.stag.util

import android.view.View


fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun String.orIfBlank(alternative: String) = if (isBlank()) alternative else this

fun <T, U, V> List<T>.productWith(other: List<U>, combiner: (T, U) -> V): List<V> =
        this.flatMap { t -> other.map { u -> combiner(t, u) } }
