package dev.leonp.strata.animated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Stable
internal class AnimatedColumnState<T>(items: List<T>, private val key: (T) -> Any) {
    private val _entries = MutableStateFlow(items.toEntries())
    internal val entries = _entries.asStateFlow()

    internal fun updateItems(items: List<T>) {
        _entries.update { entries ->
            val aliveEntries = items.toEntries()
            val aliveKeys = aliveEntries.mapTo(HashSet()) { it.key }
            require(aliveKeys.size == items.size) { "Keys must be unique" }

            // Whatever is no longer in items keeps its last index and fades out there
            val removedEntries = entries
                .filter { it.key !in aliveKeys }
                .map { it.copy(removed = true) }

            return@update (aliveEntries + removedEntries).sortEntries()
        }
    }

    internal fun removeKey(key: Any) {
        _entries.update {
            it.filterNot { it.key == key }
        }
    }

    private fun List<Entry<T>>.sortEntries() = sortedWith(
        comparator = compareBy(
            { it.index },
            { !it.removed },
        ),
    )

    private fun List<T>.toEntries() = mapIndexed { index, item ->
        Entry(
            index = index,
            key = key(item),
            item = item,
            removed = false,
        )
    }

    @Immutable
    internal data class Entry<T>(val index: Int, val key: Any, val item: T, val removed: Boolean)
}

@Composable
internal fun <T> rememberAnimatedColumnState(
    items: List<T>,
    key: (T) -> Any,
): AnimatedColumnState<T> = remember(key) {
    AnimatedColumnState(
        items = items,
        key = key,
    )
}
