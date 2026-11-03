package dev.leonp.strata.confirm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.leonp.strata.common.BackButton
import dev.leonp.strata.common.DemoIcons
import dev.leonp.strata.draggable.confirm.SwipeToConfirm
import dev.leonp.strata.draggable.confirm.SwipeToConfirmState
import dev.leonp.strata.draggable.confirm.rememberSwipeToConfirmState

private val PaidColor = Color(0xFF2E7D32)

private val TrackHeight = 64.dp
private val TrackPadding = 8.dp

private class OrderLine(val label: String, val price: String)

private val OrderLines = listOf(
    OrderLine(label = "2 x Ethiopia Guji, 250 g", price = "36.80 EUR"),
    OrderLine(label = "Shipping", price = "4.90 EUR"),
)

private const val ORDER_TOTAL = "41.70 EUR"

/**
 * A checkout where the payment is only triggered by sliding the thumb to the end of the track, so
 * it cannot be sent by an accidental tap.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SwipeToConfirmSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var payment by remember { mutableIntStateOf(0) }
    var isPaid by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Checkout") },
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            OrderSummary()

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Payment is final once the thumb reaches the end of the track.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            )

            // A new payment needs a fresh state, as the confirmed state cannot be reset
            key(payment) {
                PaymentSlider(onPaid = { isPaid = true })
            }

            AnimatedVisibility(
                visible = isPaid,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                TextButton(
                    onClick = {
                        isPaid = false
                        payment++
                    },
                ) {
                    Text(text = "Start another payment")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OrderSummary(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(all = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Basalt Coffee Roasters",
                style = MaterialTheme.typography.titleMedium,
            )

            OrderLines.forEach { line ->
                PriceRow(
                    label = line.label,
                    price = line.price,
                )
            }

            HorizontalDivider()

            PriceRow(
                label = "Total",
                price = ORDER_TOTAL,
                emphasized = true,
            )
        }
    }
}

@Composable
private fun PriceRow(
    label: String,
    price: String,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
) {
    val style = if (emphasized) {
        MaterialTheme.typography.titleMedium
    } else {
        MaterialTheme.typography.bodyMedium
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = style,
        )
        Text(
            text = price,
            style = style,
        )
    }
}

/**
 * The track that pays [ORDER_TOTAL]. It fades its label into a confirmation and turns green while the
 * thumb is dragged, and calls [onPaid] once the thumb reached the end.
 */
@Composable
private fun PaymentSlider(onPaid: () -> Unit, modifier: Modifier = Modifier) {
    val state = rememberSwipeToConfirmState()
    val progress = state.progress
    val trackColor = lerp(
        start = MaterialTheme.colorScheme.secondaryContainer,
        stop = PaidColor,
        fraction = progress,
    )

    SwipeToConfirm(
        thumb = { PaymentThumb() },
        modifier = modifier
            .fillMaxWidth()
            .height(TrackHeight)
            .clip(CircleShape)
            .background(trackColor),
        state = state,
        contentPadding = PaddingValues(all = TrackPadding),
        onConfirm = onPaid,
        positionalThreshold = {
            it * 0.7f
        },
    ) {
        Text(
            text = "Slide to pay $ORDER_TOTAL",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(1f - progress),
        )
        Text(
            text = "Paid",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(progress),
        )
    }
}

@Composable
private fun PaymentThumb(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
            )
            .fillMaxHeight()
            .aspectRatio(1f)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = DemoIcons.ChevronRight,
            contentDescription = "Slide to pay",
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

/** The progress of the thumb, which is zero until the track is measured and has its anchors. */
private fun SwipeToConfirmState.safeProgress(): Float = if (offset.isNaN()) 0f else progress
