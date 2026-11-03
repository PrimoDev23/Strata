package dev.leonp.strata.draggable.baselineprofile

import android.graphics.Point
import android.graphics.Rect
import android.os.SystemClock
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
private const val TARGET_PACKAGE = "dev.leonp.strata"
private const val TIMEOUT_MILLIS = 5_000L

/** Steps of a slow drag, every step takes about 5 ms. */
private const val DRAG_STEPS = 60

/** Steps of a fast fling, which settles with velocity. */
private const val FLING_STEPS = 8

/** Fractions of the screen width a thumb is dragged to, below and above the confirmation threshold. */
private const val SHORT_DRAG_FRACTION = 0.4f
private const val LONG_DRAG_FRACTION = 0.95f

/** The time a fling needs to come to rest. */
private const val FLING_SETTLE_MILLIS = 1_000L
private const val SETTLE_ATTEMPTS = 20
private const val SETTLE_INTERVAL_MILLIS = 100L

/** The fractions of the screen height between which the title of a component is found, below the app bar. */
private const val BAND_START = 0.2f
private const val BAND_END = 0.6f
private const val RESET_FLINGS = 6
private const val MAX_SCROLLS = 30

/**
 * Collects the baseline profile of the draggable library.
 *
 * Opens one sample of every component in the demo app and drags it between both states, so the
 * measure, scroll and drag code of the library is recorded. The library filters the result down to
 * its own classes.
 */
@RunWith(AndroidJUnit4::class)
class DraggableBaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collect(
        packageName = TARGET_PACKAGE,
        // The library is not part of the app start, so a startup profile would only add noise
        includeInStartupProfile = false,
    ) {
        pressHome()
        startActivityAndWait()

        // AnimatableCollapsibleHeaderContainer with a nested scrolling list
        device.openSample(
            component = Component.CollapsibleHeader,
            category = "Animated",
            sample = "Shared title",
        )
        device.collapseAndExpandHeader()
        device.backToHome()

        // CollapsibleHeaderContainer with a nested scrolling list
        device.openSample(
            component = Component.CollapsibleHeader,
            category = "Non-animated",
            sample = "Switching headers",
        )
        device.collapseAndExpandHeader()
        device.backToHome()

        // AnimatableCollapsibleHeaderContainer dragged directly, without a scrollable child
        device.openSample(
            component = Component.CollapsibleHeader,
            category = "Configurations",
            sample = "Non-scrollable content",
        )
        device.collapseAndExpandHeader()
        device.backToHome()

        // ExpandableContent with shared elements
        device.openSample(
            component = Component.ExpandableContent,
            category = "Animated",
            sample = "Music player",
        )
        device.expandAndCollapsePlayer()
        device.backToHome()

        // SwipeToConfirm released before and after its threshold
        device.openSample(
            component = Component.SwipeToConfirm,
            category = "Non-animated",
            sample = "Slide to pay",
        )
        device.slideThumbBackAndConfirm()
        device.backToHome()
    }
}

/** The titles of the components on the home screen. */
private enum class Component(val title: String) {
    CollapsibleHeader("Collapsible header"),
    ExpandableContent("Expandable content"),
    SwipeToConfirm("Swipe to confirm"),
}

/**
 * Opens [sample] from the [category] sub menu of [component] on the home screen.
 *
 * Every component has sub menus with the same titles, so the home screen is scrolled to the
 * component first and the sub menu right below its title is picked.
 */
private fun UiDevice.openSample(component: Component, category: String, sample: String) {
    scrollHomeTo(component)

    val title = findSettled(component.title).first()
    val categoryEntry = findSettled(category)
        .sortedBy { it.visibleBounds.top }
        .first { it.visibleBounds.top > title.visibleBounds.top }
    categoryEntry.click()

    findSettled(sample).first().click()
    waitForIdle()
}

/**
 * Scrolls the home screen until the title of [component] is in the visible band below the app bar.
 *
 * The list is reset to its start first and then dragged in steps smaller than the band, because a
 * fling can skip a whole card and leave the title behind the app bar, where it cannot be found.
 */
private fun UiDevice.scrollHomeTo(component: Component) {
    repeat(RESET_FLINGS) {
        swipeVertically(fromFraction = 0.3f, toFraction = 0.9f, steps = FLING_STEPS)
    }

    repeat(MAX_SCROLLS) {
        SystemClock.sleep(FLING_SETTLE_MILLIS)
        val top = findObject(By.text(component.title))?.visibleBounds?.top
        if (top != null && top > displayHeight * BAND_START && top < displayHeight * BAND_END) {
            return
        }
        swipeVertically(fromFraction = 0.7f, toFraction = 0.5f, steps = DRAG_STEPS)
    }
    error("Title ${component.title} is not visible on the home screen")
}

/** Goes back from a sample through its sub menu to the home screen. */
private fun UiDevice.backToHome() {
    pressBack()
    waitForIdle()
    pressBack()
    waitForIdle()
}

/**
 * Finds all nodes with [text] once they stopped moving.
 *
 * A tap during a fling or a screen transition only stops the movement and does not click, so
 * nodes are clicked only after their bounds stayed the same between two reads.
 */
private fun UiDevice.findSettled(text: String): List<UiObject2> {
    findText(text)
    var bounds = emptyList<Rect>()
    repeat(SETTLE_ATTEMPTS) {
        SystemClock.sleep(SETTLE_INTERVAL_MILLIS)
        val nodes = findObjects(By.text(text))
        // A node can be recycled while the list moves, which only means it did not settle yet
        val currentBounds = try {
            nodes.map { it.visibleBounds }
        } catch (_: StaleObjectException) {
            return@repeat
        }
        if (nodes.isNotEmpty() && currentBounds == bounds) {
            return nodes
        }
        bounds = currentBounds
    }
    error("Text $text did not stop moving")
}

/** Collapses the header with a slow drag and a fling, then expands it again the same way. */
private fun UiDevice.collapseAndExpandHeader() {
    swipeVertically(fromFraction = 0.7f, toFraction = 0.4f, steps = DRAG_STEPS)
    swipeVertically(fromFraction = 0.7f, toFraction = 0.2f, steps = FLING_STEPS)
    waitForIdle()
    swipeVertically(fromFraction = 0.4f, toFraction = 0.7f, steps = DRAG_STEPS)
    swipeVertically(fromFraction = 0.2f, toFraction = 0.9f, steps = FLING_STEPS)
    swipeVertically(fromFraction = 0.2f, toFraction = 0.9f, steps = FLING_STEPS)
    waitForIdle()
}

/** Drags the mini player into the full player and back down again. */
private fun UiDevice.expandAndCollapsePlayer() {
    dragVertically(from = findText("Midnight Layers").visibleCenter, toFraction = 0.1f)
    waitForIdle()
    dragVertically(from = findText("Now playing").visibleCenter, toFraction = 0.9f)
    waitForIdle()
}

/** Drags the thumb a short way and lets it snap back, then drags it to the end of the track. */
private fun UiDevice.slideThumbBackAndConfirm() {
    val from = findDescription("Slide to pay").visibleCenter
    drag(from.x, from.y, (displayWidth * SHORT_DRAG_FRACTION).toInt(), from.y, DRAG_STEPS)
    waitForIdle()

    val restartedThumb = findDescription("Slide to pay").visibleCenter
    drag(
        restartedThumb.x,
        restartedThumb.y,
        (displayWidth * LONG_DRAG_FRACTION).toInt(),
        restartedThumb.y,
        DRAG_STEPS,
    )
    waitForIdle()
}

private fun UiDevice.dragVertically(from: Point, toFraction: Float) {
    drag(from.x, from.y, from.x, (displayHeight * toFraction).toInt(), DRAG_STEPS)
}

private fun UiDevice.findDescription(description: String): UiObject2 =
    checkNotNull(wait(Until.findObject(By.desc(description)), TIMEOUT_MILLIS)) {
        "Description $description not found"
    }

private fun UiDevice.findText(text: String): UiObject2 =
    checkNotNull(wait(Until.findObject(By.text(text)), TIMEOUT_MILLIS)) { "Text $text not found" }

private fun UiDevice.swipeVertically(fromFraction: Float, toFraction: Float, steps: Int) {
    val x = displayWidth / 2
    swipe(x, (displayHeight * fromFraction).toInt(), x, (displayHeight * toFraction).toInt(), steps)
}
