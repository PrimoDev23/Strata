package dev.leonp.strata.animated

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.leonp.strata.common.BackButton
import dev.leonp.strata.common.DemoIcons

private const val INITIAL_TASK_COUNT = 8
private const val LONGEST_TASK_MINUTES = 60f
private const val TRACK_ALPHA = 0.18f

private enum class Category(val label: String) {
    Work("Work"),
    Home("Home"),
    Fun("Fun"),
}

private enum class SortOrder(val label: String) {
    Manual("Newest"),
    Title("A to Z"),
    Duration("Shortest"),
    Category("Category"),
}

private data class Task(val id: Int, val title: String, val category: Category, val minutes: Int)

private data class TaskTemplate(val title: String, val category: Category, val minutes: Int)

private val TaskTemplates = listOf(
    TaskTemplate("Review a pull request", Category.Work, 20),
    TaskTemplate("Water the plants", Category.Home, 10),
    TaskTemplate("Play some guitar", Category.Fun, 25),
    TaskTemplate("Write the release notes", Category.Work, 35),
    TaskTemplate("Do the laundry", Category.Home, 40),
    TaskTemplate("Go for a run", Category.Fun, 45),
    TaskTemplate("Plan the next sprint", Category.Work, 55),
    TaskTemplate("Cook dinner", Category.Home, 50),
    TaskTemplate("Read a chapter", Category.Fun, 20),
    TaskTemplate("Answer the emails", Category.Work, 15),
    TaskTemplate("Vacuum the flat", Category.Home, 30),
    TaskTemplate("Call a friend", Category.Fun, 15),
)

/** The ids are the identity of the tasks, so a task can be rebuilt from its id alone. */
private fun taskOf(id: Int): Task {
    val template = TaskTemplates[id % TaskTemplates.size]
    return Task(
        id = id,
        title = template.title,
        category = template.category,
        minutes = template.minutes,
    )
}

private fun List<Task>.sortedBy(order: SortOrder): List<Task> = when (order) {
    SortOrder.Manual -> this
    SortOrder.Title -> sortedBy { it.title }
    SortOrder.Duration -> sortedBy { it.minutes }
    SortOrder.Category -> sortedBy { it.category }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun ReorderSample(onBack: () -> Unit, modifier: Modifier = Modifier) {
    // The newest task is the first one, so adding a task pushes the others down.
    var ids by rememberSaveable { mutableStateOf((INITIAL_TASK_COUNT - 1 downTo 0).toList()) }
    var nextId by rememberSaveable { mutableIntStateOf(INITIAL_TASK_COUNT) }
    var sortOrder by remember { mutableStateOf(SortOrder.Manual) }
    var hiddenCategories by remember { mutableStateOf(emptySet<Category>()) }

    val visibleTasks = ids
        .map(::taskOf)
        .filter { it.category !in hiddenCategories }
        .sortedBy(sortOrder)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Task list") },
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    ids = listOf(nextId) + ids
                    nextId += 1
                },
                icon = { Icon(imageVector = DemoIcons.Add, contentDescription = null) },
                text = { Text(text = "New task") },
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${visibleTasks.size} tasks",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = "${visibleTasks.sumOf { it.minutes }} minutes planned",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                TextButton(
                    onClick = {
                        ids = ids.shuffled()
                        sortOrder = SortOrder.Manual
                    },
                ) {
                    Text(text = "Shuffle")
                }
                TextButton(
                    onClick = {
                        ids = (INITIAL_TASK_COUNT - 1 downTo 0).toList()
                        nextId = INITIAL_TASK_COUNT
                        sortOrder = SortOrder.Manual
                        hiddenCategories = emptySet()
                    },
                ) {
                    Text(text = "Reset")
                }
            }

            ChipSection(title = "Sort by") {
                SortOrder.entries.forEach { order ->
                    FilterChip(
                        selected = sortOrder == order,
                        onClick = { sortOrder = order },
                        label = { Text(text = order.label) },
                    )
                }
            }

            ChipSection(title = "Show") {
                Category.entries.forEach { category ->
                    FilterChip(
                        selected = category !in hiddenCategories,
                        onClick = {
                            hiddenCategories = if (category in hiddenCategories) {
                                hiddenCategories - category
                            } else {
                                hiddenCategories + category
                            }
                        },
                        label = { Text(text = category.label) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(category.accent()),
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedColumn(
                items = visibleTasks,
                key = { it.id },
                modifier = Modifier.fillMaxWidth(),
            ) { task ->
                TaskCard(
                    task = task,
                    onRemove = { ids = ids - task.id },
                    modifier = Modifier.animateItem(),
                )
            }

            if (visibleTasks.isEmpty()) {
                Text(
                    text = "Nothing left to do. Add a task or show more categories.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                )
            }

            // Keeps the last card clear of the floating button.
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipSection(title: String, content: @Composable () -> Unit) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        content()
    }
}

@Composable
private fun TaskCard(task: Task, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    val accent = task.category.accent()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(accent),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 14.dp, bottom = 14.dp),
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "${task.category.label} · ${task.minutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                DurationBar(
                    fraction = task.minutes / LONGEST_TASK_MINUTES,
                    color = accent,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
            IconButton(
                onClick = onRemove,
                modifier = Modifier.padding(horizontal = 4.dp),
            ) {
                Icon(
                    imageVector = DemoIcons.Close,
                    contentDescription = "Remove ${task.title}",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** A thin bar that shows how long a task takes compared to the longest one. */
@Composable
private fun DurationBar(fraction: Float, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = TRACK_ALPHA)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(CircleShape)
                .background(color),
        )
    }
}

@Composable
private fun Category.accent(): Color = when (this) {
    Category.Work -> MaterialTheme.colorScheme.primary
    Category.Home -> MaterialTheme.colorScheme.tertiary
    Category.Fun -> MaterialTheme.colorScheme.secondary
}
