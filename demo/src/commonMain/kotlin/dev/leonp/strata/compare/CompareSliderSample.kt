package dev.leonp.strata.compare

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import dev.leonp.strata.common.BackButton
import dev.leonp.strata.common.DemoIcons
import dev.leonp.strata.common.InfoCard
import dev.leonp.strata.draggable.compare.CompareSlider

private const val SCENE_ASPECT_RATIO = 3f / 4f
private const val HORIZON = 0.74f
private const val SUN_CENTER_X = 0.66f
private const val SUN_CENTER_Y = 0.36f
private const val SUN_RADIUS = 0.13f
private const val GLOW_SCALE = 1.9f
private const val GLOW_ALPHA = 0.25f
private const val REFLECTION_ALPHA = 0.55f
private const val REFLECTION_LINES = 6
private const val LABEL_BACKGROUND_ALPHA = 0.45f

/** Turns the right pointing chevron around, so the thumb has an arrow to each side. */
private const val LEFT_ARROW_ROTATION = 180f

/** The ridges of the mountains from left to right as fractions of the scene, from far to near. */
private val FarRidge = listOf(
    Offset(0f, 0.58f),
    Offset(0.18f, 0.46f),
    Offset(0.34f, 0.56f),
    Offset(0.52f, 0.42f),
    Offset(0.74f, 0.58f),
    Offset(0.9f, 0.48f),
    Offset(1f, 0.55f),
)
private val MidRidge = listOf(
    Offset(0f, 0.66f),
    Offset(0.22f, 0.54f),
    Offset(0.44f, 0.68f),
    Offset(0.64f, 0.56f),
    Offset(0.84f, 0.67f),
    Offset(1f, 0.6f),
)
private val NearRidge = listOf(
    Offset(0f, 0.72f),
    Offset(0.16f, 0.66f),
    Offset(0.38f, 0.74f),
    Offset(0.7f, 0.64f),
    Offset(1f, 0.74f),
)

/** The colors of one version of the scene, so both sides of the slider draw the same landscape. */
private class ScenePalette(
    val skyTop: Color,
    val skyBottom: Color,
    val sun: Color,
    val farRidge: Color,
    val midRidge: Color,
    val nearRidge: Color,
    val waterTop: Color,
    val waterBottom: Color,
)

private val OvercastPalette = ScenePalette(
    skyTop = Color(0xFF5E6B7A),
    skyBottom = Color(0xFFB9C2CB),
    sun = Color(0xFFE3E6E8),
    farRidge = Color(0xFF8A949E),
    midRidge = Color(0xFF6F7A85),
    nearRidge = Color(0xFF4F5963),
    waterTop = Color(0xFF55616C),
    waterBottom = Color(0xFF3E4852),
)

private val SunsetPalette = ScenePalette(
    skyTop = Color(0xFF2B2A6B),
    skyBottom = Color(0xFFFF9A62),
    sun = Color(0xFFFFE08A),
    farRidge = Color(0xFF9B4F7C),
    midRidge = Color(0xFF5E3366),
    nearRidge = Color(0xFF2F1C45),
    waterTop = Color(0xFFE0735C),
    waterBottom = Color(0xFF3A2152),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CompareSliderSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Before and after") },
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            CompareSlider(
                before = {
                    Scene(
                        palette = OvercastPalette,
                        label = "Before",
                        labelAlignment = Alignment.TopStart,
                    )
                },
                after = {
                    Scene(
                        palette = SunsetPalette,
                        label = "After",
                        labelAlignment = Alignment.TopEnd,
                    )
                },
                thumb = { SliderThumb() },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(SCENE_ASPECT_RATIO)
                    .clip(RoundedCornerShape(28.dp)),
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoCard(
                title = "Drag to compare",
                text =
                    "Both versions are drawn at full size. The slider only decides how much of " +
                        "each one is visible, so nothing is scaled.",
            )
        }
    }
}

/** One version of the landscape with its [label] pinned to the corner at [labelAlignment]. */
@Composable
private fun Scene(
    palette: ScenePalette,
    label: String,
    labelAlignment: Alignment,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawScene(palette = palette)
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            modifier = Modifier
                .align(labelAlignment)
                .padding(all = 16.dp)
                .background(
                    color = Color.Black.copy(alpha = LABEL_BACKGROUND_ALPHA),
                    shape = CircleShape,
                )
                .padding(horizontal = 14.dp, vertical = 6.dp),
        )
    }
}

/** The round handle with an arrow to each side that hints that the slider can be dragged. */
@Composable
private fun SliderThumb(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
            )
            .background(
                color = Color.White,
                shape = CircleShape,
            )
            .size(44.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = DemoIcons.ChevronRight,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier
                .size(18.dp)
                .rotate(LEFT_ARROW_ROTATION),
        )
        Icon(
            imageVector = DemoIcons.ChevronRight,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(18.dp),
        )
    }
}

private fun DrawScope.drawScene(palette: ScenePalette) {
    val horizonY = size.height * HORIZON
    val sunCenter = Offset(
        x = size.width * SUN_CENTER_X,
        y = size.height * SUN_CENTER_Y,
    )
    val sunRadius = size.width * SUN_RADIUS

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(palette.skyTop, palette.skyBottom),
            endY = horizonY,
        ),
    )
    drawCircle(
        color = palette.sun.copy(alpha = GLOW_ALPHA),
        radius = sunRadius * GLOW_SCALE,
        center = sunCenter,
    )
    drawCircle(
        color = palette.sun,
        radius = sunRadius,
        center = sunCenter,
    )

    drawRidge(points = FarRidge, color = palette.farRidge)
    drawRidge(points = MidRidge, color = palette.midRidge)
    drawRidge(points = NearRidge, color = palette.nearRidge)

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(palette.waterTop, palette.waterBottom),
            startY = horizonY,
            endY = size.height,
        ),
        topLeft = Offset(x = 0f, y = horizonY),
        size = Size(width = size.width, height = size.height - horizonY),
    )

    // The reflection gets shorter and darker bars the further it is from the horizon
    val reflectionStep = (size.height - horizonY) / (REFLECTION_LINES + 1)
    repeat(REFLECTION_LINES) { index ->
        val barWidth = sunRadius * 2f * (1f - index / (REFLECTION_LINES * 1.4f))
        val barHeight = reflectionStep * 0.35f

        drawRoundRect(
            color = palette.sun.copy(
                alpha = REFLECTION_ALPHA * (1f - index / REFLECTION_LINES.toFloat()),
            ),
            topLeft = Offset(
                x = sunCenter.x - barWidth / 2f,
                y = horizonY + reflectionStep * (index + 1),
            ),
            size = Size(width = barWidth, height = barHeight),
            cornerRadius = CornerRadius(barHeight / 2f),
        )
    }
}

private fun DrawScope.drawRidge(points: List<Offset>, color: Color) {
    val path = Path().apply {
        moveTo(0f, size.height)
        points.forEach { point ->
            lineTo(point.x * size.width, point.y * size.height)
        }
        lineTo(size.width, size.height)
        close()
    }

    drawPath(
        path = path,
        color = color,
    )
}
