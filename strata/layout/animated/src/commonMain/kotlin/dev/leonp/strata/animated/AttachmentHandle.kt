package dev.leonp.strata.animated

import androidx.compose.runtime.Stable

/**
 * Tells whether the item of a [AttachmentElement] is currently attached to the composition tree, so
 * a removed item is only dropped from the column once it is gone.
 */
@Stable
internal class AttachmentHandle {
    internal var isAttached: Boolean = false
}