package dev.leonp.strata.animated

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.unit.IntOffset

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
 * @param animationSpec The animation used to move an item to its new position.
 * @param itemContent The content of a single item.
 */
@Composable
public fun <T> AnimatedColumn(
    items: List<T>,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    animationSpec: FiniteAnimationSpec<IntOffset> = spring(),
    itemContent: @Composable (T) -> Unit,
) {
    LookaheadScope {
        Column(
            modifier = modifier,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
        ) {
            items.forEach { item ->
                val calculatedKey = key(item)

                key(calculatedKey) {
                    Box(
                        modifier = Modifier.animatePlacement(
                            scope = this@LookaheadScope,
                            animationSpec = animationSpec,
                        ),
                    ) {
                        itemContent(item)
                    }
                }
            }
        }
    }
}
