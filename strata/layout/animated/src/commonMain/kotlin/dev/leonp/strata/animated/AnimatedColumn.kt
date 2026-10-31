package dev.leonp.strata.animated

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * A column that animates its items to their new position whenever [items] changes, for example
 * when the list is sorted or filtered, or when an item is inserted or removed.
 *
 * Unlike `LazyColumn`, every item is composed, so the column can be used inside any scrollable
 * container. It does not scroll on its own and has no gestures, it only reacts to layout changes.
 *
 * Every item is keyed, so its state survives a reorder.
 *
 * @param items The items to show, in order.
 * @param key Returns a stable and unique key for an item.
 * @param modifier The modifier applied to the column.
 * @param verticalArrangement The arrangement of the items inside the column.
 * @param horizontalAlignment The alignment of the items inside the column.
 * @param itemContent The content of a single item. Call `animateItem` on its modifier to animate it.
 */
@Composable
public fun <T> AnimatedColumn(
    items: List<T>,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    itemContent: @Composable AnimatedColumnScope.(T) -> Unit,
) {
    LookaheadScope {
        Column(
            modifier = modifier,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
        ) {
            val state = rememberAnimatedColumnState(
                items = items,
                key = key,
            )
            val entries by state.entries.collectAsStateWithLifecycle()

            LaunchedEffect(items) {
                state.updateItems(items)
            }

            entries.forEach { entry ->
                key(entry.key) {
                    val handle = remember {
                        AttachmentHandle()
                    }

                    LaunchedEffect(entry.removed) {
                        if (entry.removed && !handle.isAttached) {
                            state.removeKey(entry.key)
                        }
                    }

                    val scope =
                        remember(this, this@LookaheadScope, entry.removed, state, handle) {
                            AnimatedColumnScopeImpl(
                                parent = this,
                                lookaheadScope = this@LookaheadScope,
                                handle = handle,
                                removed = entry.removed,
                                onRemovalFinish = {
                                    state.removeKey(entry.key)
                                },
                            )
                        }
                    scope.itemContent(entry.item)
                }
            }
        }
    }
}