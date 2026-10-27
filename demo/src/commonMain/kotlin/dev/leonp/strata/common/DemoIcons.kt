package dev.leonp.strata.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** The few Material icons the demo needs, so it does not depend on the icon libraries. */
internal object DemoIcons {
    val ArrowBack = icon(
        name = "ArrowBack",
        pathData = "M20,11H7.83l5.59,-5.59L12,4l-8,8 8,8 1.41,-1.41L7.83,13H20v-2z",
        autoMirror = true,
    )
    val ChevronRight = icon(
        name = "ChevronRight",
        pathData = "M10,6L8.59,7.41 13.17,12l-4.58,4.59L10,18l6,-6z",
        autoMirror = true,
    )
    val AutoAwesome = icon(
        name = "AutoAwesome",
        pathData = "M19,9l1.25,-2.75L23,5l-2.75,-1.25L19,1l-1.25,2.75L15,5l2.75,1.25L19,9z" +
            "M11.5,9.5L9,4 6.5,9.5 1,12l5.5,2.5L9,20l2.5,-5.5L17,12l-5.5,-2.5z" +
            "M19,15l-1.25,2.75L15,19l2.75,1.25L19,23l1.25,-2.75L23,19l-2.75,-1.25L19,15z",
    )
    val SwapVert = icon(
        name = "SwapVert",
        pathData = "M16,17.01V10h-2v7.01h-3L15,21l4,-3.99h-3zM9,3L5,6.99h3V14h2V6.99h3L9,3z",
    )
    val Tune = icon(
        name = "Tune",
        pathData =
            "M3,17v2h6v-2H3zM3,5v2h10V5H3zM13,21v-2h8v-2h-8v-2h-2v6h2zM7,9v2H3v2h4v2h2V9H7z" +
                "M21,13v-2H11v2h10zM15,9h2V7h4V5h-4V3h-2v6z",
    )
    val Play = icon(
        name = "Play",
        pathData = "M8,5v14l11,-7z",
    )
    val Pause = icon(
        name = "Pause",
        pathData = "M6,19h4V5H6v14zM14,5v14h4V5h-4z",
    )
    val SkipNext = icon(
        name = "SkipNext",
        pathData = "M6,18l8.5,-6L6,6v12zM16,6v12h2V6h-2z",
    )
    val SkipPrevious = icon(
        name = "SkipPrevious",
        pathData = "M6,6h2v12H6zM9.5,12l8.5,6V6z",
    )
    val Home = icon(
        name = "Home",
        pathData = "M10,20v-6h4v6h5v-8h3L12,3 2,12h3v8z",
    )
    val Search = icon(
        name = "Search",
        pathData =
            "M15.5,14h-0.79l-0.28,-0.27C15.41,12.59 16,11.11 16,9.5 16,5.91 13.09,3 9.5,3S3,5.91 3,9.5 " +
                "5.91,16 9.5,16c1.61,0 3.09,-0.59 4.23,-1.57l0.27,0.28v0.79l5,4.99L20.49,19l-4.99,-5z" +
                "M9.5,14C7.01,14 5,11.99 5,9.5S7.01,5 9.5,5 14,7.01 14,9.5 11.99,14 9.5,14z",
    )
    val Library = icon(
        name = "Library",
        pathData =
            "M20,2H8c-1.1,0 -2,0.9 -2,2v12c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2V4c0,-1.1 -0.9,-2 -2,-2z" +
                "M18,7h-3v5.5c0,1.38 -1.12,2.5 -2.5,2.5S10,13.88 10,12.5s1.12,-2.5 2.5,-2.5" +
                "c0.57,0 1.08,0.19 1.5,0.51V5h4v2z" +
                "M4,6H2v14c0,1.1 0.9,2 2,2h14v-2H4V6z",
    )
}

private fun icon(name: String, pathData: String, autoMirror: Boolean = false): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
        autoMirror = autoMirror,
    ).addPath(
        pathData = addPathNodes(pathData),
        fill = SolidColor(Color.Black),
    ).build()
