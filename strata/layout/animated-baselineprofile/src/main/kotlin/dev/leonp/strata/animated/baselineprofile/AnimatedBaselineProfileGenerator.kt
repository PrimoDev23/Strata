package dev.leonp.strata.animated.baselineprofile

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

/** Steps of a swipe, every step takes about 5 ms. */
private const val SWIPE_STEPS = 8
private const val MAX_SCROLLS = 10
private const val SETTLE_ATTEMPTS = 20
private const val SETTLE_INTERVAL_MILLIS = 100L

/** Long enough for the placement and fade animations of the cards to finish. */
private const val ANIMATION_MILLIS = 900L

/** Where the "New task" button is, as a fraction of the display. */
private const val NEW_TASK_X_FRACTION = 0.82f
private const val NEW_TASK_Y_FRACTION = 0.93f

/** A component is scrolled below this fraction of the screen height, so its sub menus are visible. */
private const val VISIBLE_FRACTION = 0.4f

/**
 * Collects the baseline profile of the animated library.
 *
 * Opens the task list of the demo app and exercises every change an `AnimatedColumn` animates:
 * sorting, shuffling, inserting, removing and filtering items. The library filters the result down
 * to its own classes.
 */
@RunWith(AndroidJUnit4::class)
class AnimatedBaselineProfileGenerator {
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

        device.openSample(
            component = "Animated column",
            category = "Animated",
            sample = "Task list",
        )

        // Placement: every sort order moves the cards, shuffling moves them all at once
        device.tapAndWait("A to Z")
        device.tapAndWait("Shortest")
        device.tapAndWait("Category")
        device.tapAndWait("Shuffle")
        device.tapAndWait("Newest")

        // Insertion: the new card fades in while the others make room
        device.tapNewTask()
        device.tapNewTask()

        // Removal: the card fades out, then the others close the gap
        device.removeFirstTask()
        device.removeFirstTask()

        // Filtering removes and restores several cards at once
        device.tapAndWait("Work")
        device.tapAndWait("Home")
        device.tapAndWait("Work")
        device.tapAndWait("Home")

        device.tapAndWait("Reset")
        device.pressBack()
        device.waitForIdle()
    }
}

/**
 * Opens [sample] from the [category] sub menu of the [component] on the home screen.
 *
 * Every component has sub menus with the same titles, so the home screen is scrolled to the
 * component first and the sub menu right below its title is picked.
 */
private fun UiDevice.openSample(component: String, category: String, sample: String) {
    scrollHomeTo(component)

    val title = findSettled(component).first()
    val categoryEntry = findSettled(category)
        .sortedBy { it.visibleBounds.top }
        .first { it.visibleBounds.top > title.visibleBounds.top }
    categoryEntry.click()

    findSettled(sample).first().click()
    waitForIdle()
}

/** Scrolls the home screen up until the title of [component] is in its upper part. */
private fun UiDevice.scrollHomeTo(component: String) {
    repeat(MAX_SCROLLS) {
        val title = findObject(By.text(component))
        if (title != null && title.visibleBounds.top < displayHeight * VISIBLE_FRACTION) {
            return
        }
        swipeVertically(fromFraction = 0.8f, toFraction = 0.4f)
    }
}

/**
 * Finds all nodes with [text] once they stopped moving.
 *
 * A tap during a fling or a screen transition only stops the movement and does not click, so
 * nodes are used only after their bounds stayed the same between two reads.
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

/** Taps the node with [text] and gives the animations it starts time to finish. */
private fun UiDevice.tapAndWait(text: String) {
    findSettled(text).first().click()
    waitForIdle()
    SystemClock.sleep(ANIMATION_MILLIS)
}

/**
 * Taps the floating "New task" button in the bottom end corner.
 *
 * The button is not part of the view tree UiAutomator sees, so it can't be found by its text.
 */
private fun UiDevice.tapNewTask() {
    click(
        (displayWidth * NEW_TASK_X_FRACTION).toInt(),
        (displayHeight * NEW_TASK_Y_FRACTION).toInt(),
    )
    waitForIdle()
    SystemClock.sleep(ANIMATION_MILLIS)
}

/** Taps the remove button of the first task card. */
private fun UiDevice.removeFirstTask() {
    val button = checkNotNull(
        wait(Until.findObject(By.descStartsWith("Remove ")), TIMEOUT_MILLIS),
    ) { "No task to remove" }
    button.click()
    waitForIdle()
    SystemClock.sleep(ANIMATION_MILLIS)
}

private fun UiDevice.findText(text: String): UiObject2 =
    checkNotNull(wait(Until.findObject(By.text(text)), TIMEOUT_MILLIS)) { "Text $text not found" }

private fun UiDevice.swipeVertically(fromFraction: Float, toFraction: Float) {
    val x = displayWidth / 2
    val fromY = (displayHeight * fromFraction).toInt()
    val toY = (displayHeight * toFraction).toInt()
    swipe(x, fromY, x, toY, SWIPE_STEPS)
}
