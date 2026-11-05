package dev.leonp.strata.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Buttons to expand and collapse a component from code, next to the settled state of it and an
 * optional switch to enable dragging. Lays out in two rows, so it fits narrow screens.
 *
 * @param settledText describes the settled state, for example `isCollapsed = true`.
 * @param onExpand called when the expand button is clicked.
 * @param onCollapse called when the collapse button is clicked.
 * @param expandLabel the text of the expand button.
 * @param collapseLabel the text of the collapse button.
 * @param enabled whether dragging is enabled, or `null` to hide the switch for it.
 * @param onEnabledChange called when the switch for [enabled] is toggled.
 * @param extraLabel the text of an additional button between expand and collapse, or `null` to hide
 * it.
 * @param onExtra called when the additional button is clicked.
 */
@Composable
internal fun StateControls(
    settledText: String,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    modifier: Modifier = Modifier,
    expandLabel: String = "Expand",
    collapseLabel: String = "Collapse",
    enabled: Boolean? = null,
    onEnabledChange: (Boolean) -> Unit = {},
    extraLabel: String? = null,
    onExtra: () -> Unit = {},
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = settledText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )

            if (enabled != null) {
                Text(
                    text = "Drag enabled",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChange,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onExpand,
                modifier = Modifier.weight(1f),
            ) {
                Text(text = expandLabel)
            }

            if (extraLabel != null) {
                OutlinedButton(
                    onClick = onExtra,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = extraLabel)
                }
            }

            OutlinedButton(
                onClick = onCollapse,
                modifier = Modifier.weight(1f),
            ) {
                Text(text = collapseLabel)
            }
        }
    }
}
