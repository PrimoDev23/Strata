package dev.leonp.strata.draggable.reveal

import androidx.compose.runtime.Immutable

/**
 * The texts accessibility services use to describe a [SwipeToReveal].
 *
 * The library does not translate them, so pass localized strings. A text that is `null` leaves out
 * what it describes.
 *
 * @param revealStartActions The label of the accessibility action that reveals the start actions.
 * The action is only offered while there are start actions and they are not revealed yet.
 * @param revealEndActions The label of the accessibility action that reveals the end actions. The
 * action is only offered while there are end actions and they are not revealed yet.
 * @param hideActions The label of the accessibility action that hides revealed actions. The action
 * is only offered while actions are revealed.
 * @param revealedStateDescription The text screen readers announce while actions are revealed, for
 * example "Actions shown".
 */
@Immutable
public class SwipeToRevealLabels(
    public val revealStartActions: String? = null,
    public val revealEndActions: String? = null,
    public val hideActions: String? = null,
    public val revealedStateDescription: String? = null,
)
