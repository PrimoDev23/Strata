package dev.leonp.strata.draggable.baselineprofile

import android.graphics.Point
import android.os.SystemClock
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
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
private const val SETTLE_ATTEMPTS = 20
private const val SETTLE_INTERVAL_MILLIS = 100L

/** A component is scrolled below this fraction of the screen height, so its sub menus are visible. */
private const val VISIBLE_FRACTION = 0.4f
private const val MAX_SCROLLS = 10

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
    }
}

/** The titles of the components on the home screen. */
private enum class Component(val title: String) {
    CollapsibleHeader("Collapsible header"),
    ExpandableContent("Expandable content"),
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

/** Scrolls the home screen until the title of [component] is in its upper part. */
private fun UiDevice.scrollHomeTo(component: Component) {
    repeat(MAX_SCROLLS) {
        val title = findObject(By.text(component.title))
        if (title != null && title.visibleBounds.top < displayHeight * VISIBLE_FRACTION) {
            return
        }
        swipeVertically(fromFraction = 0.8f, toFraction = 0.4f, steps = FLING_STEPS)
    }
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
    var bounds = findObjects(By.text(text)).map { it.visibleBounds }
    repeat(SETTLE_ATTEMPTS) {
        SystemClock.sleep(SETTLE_INTERVAL_MILLIS)
        val nodes = findObjects(By.text(text))
        val currentBounds = nodes.map { it.visibleBounds }
        if (currentBounds == bounds) {
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

private fun UiDevice.dragVertically(from: Point, toFraction: Float) {
    drag(from.x, from.y, from.x, (displayHeight * toFraction).toInt(), DRAG_STEPS)
}

private fun UiDevice.findText(text: String): UiObject2 =
    checkNotNull(wait(Until.findObject(By.text(text)), TIMEOUT_MILLIS)) { "Text $text not found" }

private fun UiDevice.swipeVertically(fromFraction: Float, toFraction: Float, steps: Int) {
    val x = displayWidth / 2
    swipe(x, (displayHeight * fromFraction).toInt(), x, (displayHeight * toFraction).toInt(), steps)
}
