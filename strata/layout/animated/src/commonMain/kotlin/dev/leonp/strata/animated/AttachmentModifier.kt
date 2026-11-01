package dev.leonp.strata.animated

import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

/** Keeps [AttachmentHandle.isAttached] in sync with the attach state of the modified layout. */
internal class AttachmentElement(private val handle: AttachmentHandle) :
    ModifierNodeElement<AttachmentNode>() {
    override fun create(): AttachmentNode = AttachmentNode(handle = handle)

    override fun update(node: AttachmentNode) = node.update(handle = handle)

    override fun InspectorInfo.inspectableProperties() = Unit

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AttachmentElement) return false

        return handle == other.handle
    }

    override fun hashCode(): Int = handle.hashCode()
}

internal class AttachmentNode(var handle: AttachmentHandle) : Modifier.Node() {
    override fun onAttach() {
        handle.isAttached = true
    }

    override fun onDetach() {
        handle.isAttached = false
    }

    fun update(handle: AttachmentHandle) {
        if (this.handle == handle) {
            return
        }

        // The node stays attached, so the flag has to move over to the new handle.
        this.handle.isAttached = false
        this.handle = handle
        handle.isAttached = true
    }
}
