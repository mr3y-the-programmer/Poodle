package com.mr3y.poodle.ui.component

import androidx.annotation.FloatRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mr3y.poodle.ui.theme.PoodleTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Possible values of [PoodleModalBottomSheetState].
 */
@ExperimentalMaterialApi
enum class PoodleModalBottomSheetValue {
    /**
     * The bottom sheet is visible, and collapsed.
     */
    Collapsed,

    /**
     * The bottom sheet is visible at full height.
     */
    Expanded,

    /**
     * The bottom sheet is partially visible at 50% of the screen height. This state is only
     * enabled if the height of the bottom sheet is more than 50% of the screen height.
     */
    HalfExpanded
}

/**
 * State of the [PoodleModalBottomSheetLayout] composable.
 *
 * @param initialValue The initial value of the state. <b>Must not be set to
 * [PoodleModalBottomSheetValue.HalfExpanded] if [isSkipHalfExpanded] is set to true.</b>
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param isSkipHalfExpanded Whether the half expanded state, if the sheet is tall enough, should
 * be skipped. If true, the sheet will always expand to the [PoodleModalBottomSheetValue.Expanded] state and move to the
 * [PoodleModalBottomSheetValue.Collapsed] state when collapsing the sheet, either programmatically or by user interaction.
 * <b>Must not be set to true if the @param initialValue is [PoodleModalBottomSheetValue.HalfExpanded].</b>
 * If supplied with [PoodleModalBottomSheetValue.HalfExpanded] for the @param initialValue, an
 * [IllegalArgumentException] will be thrown.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@ExperimentalMaterialApi
class PoodleModalBottomSheetState(
    initialValue: PoodleModalBottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    internal val isSkipHalfExpanded: Boolean,
    confirmStateChange: (PoodleModalBottomSheetValue) -> Boolean = { true }
) : SwipeableState<PoodleModalBottomSheetValue>(
    initialValue = initialValue,
    animationSpec = animationSpec,
    confirmStateChange = confirmStateChange
) {
    /**
     * Whether the bottom sheet is expanded.
     */
    val isExpanded: Boolean
        get() = currentValue != PoodleModalBottomSheetValue.Collapsed

    internal val hasHalfExpandedState: Boolean
        get() = anchors.values.contains(PoodleModalBottomSheetValue.HalfExpanded)

    constructor(
        initialValue: PoodleModalBottomSheetValue,
        animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
        confirmStateChange: (PoodleModalBottomSheetValue) -> Boolean = { true }
    ) : this(initialValue, animationSpec, isSkipHalfExpanded = false, confirmStateChange)

    init {
        if (isSkipHalfExpanded) {
            require(initialValue != PoodleModalBottomSheetValue.HalfExpanded) {
                "The initial value must not be set to HalfExpanded if skipHalfExpanded is set to" +
                    " true."
            }
        }
    }

    /**
     * Expand the bottom sheet with animation and suspend until it's expanded. If the sheet is taller
     * than 50% of the parent's height, the bottom sheet will be half expanded. Otherwise it will be
     * fully expanded.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun expand() {
        val targetValue = when {
            hasHalfExpandedState -> PoodleModalBottomSheetValue.HalfExpanded
            else -> PoodleModalBottomSheetValue.Expanded
        }
        animateTo(targetValue = targetValue)
    }

    /**
     * Half expand the bottom sheet if half expand is enabled with animation and suspend until it
     * animation is complete or cancelled
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    internal suspend fun halfExpand() {
        if (!hasHalfExpandedState) {
            return
        }
        animateTo(PoodleModalBottomSheetValue.HalfExpanded)
    }

    /**
     * Fully expand the bottom sheet with animation and suspend until it if fully expanded or
     * animation has been cancelled.
     * *
     * @throws [CancellationException] if the animation is interrupted
     */
    internal suspend fun fullExpand() = animateTo(PoodleModalBottomSheetValue.Expanded)

    /**
     * Collapse the bottom sheet with animation and suspend until it if fully collapsed or animation has
     * been cancelled.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun collapse() = animateTo(PoodleModalBottomSheetValue.Collapsed)

    internal val nestedScrollConnection = this.PreUpPostDownNestedScrollConnection

    companion object {
        /**
         * The default [Saver] implementation for [PoodleModalBottomSheetState].
         */
        fun Saver(
            animationSpec: AnimationSpec<Float>,
            skipHalfExpanded: Boolean,
            confirmStateChange: (PoodleModalBottomSheetValue) -> Boolean
        ): Saver<PoodleModalBottomSheetState, *> = Saver(
            save = { it.currentValue },
            restore = {
                PoodleModalBottomSheetState(
                    initialValue = it,
                    animationSpec = animationSpec,
                    isSkipHalfExpanded = skipHalfExpanded,
                    confirmStateChange = confirmStateChange
                )
            }
        )
    }
}

/**
 * Create a [PoodleModalBottomSheetState] and [remember] it.
 *
 * @param initialValue The initial value of the state.
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param skipHalfExpanded Whether the half expanded state, if the sheet is tall enough, should
 * be skipped. If true, the sheet will always expand to the [PoodleModalBottomSheetValue.Expanded] state and move to the
 * [PoodleModalBottomSheetValue.Collapsed] state when hiding the sheet, either programmatically or by user interaction.
 * <b>Must not be set to true if the [initialValue] is [PoodleModalBottomSheetValue.HalfExpanded].</b>
 * If supplied with [PoodleModalBottomSheetValue.HalfExpanded] for the [initialValue], an
 * [IllegalArgumentException] will be thrown.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@Composable
@ExperimentalMaterialApi
fun rememberPoodleModalBottomSheetState(
    initialValue: PoodleModalBottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    skipHalfExpanded: Boolean,
    confirmStateChange: (PoodleModalBottomSheetValue) -> Boolean = { true }
): PoodleModalBottomSheetState {
    return rememberSaveable(
        initialValue, animationSpec, skipHalfExpanded, confirmStateChange,
        saver = PoodleModalBottomSheetState.Saver(
            animationSpec = animationSpec,
            skipHalfExpanded = skipHalfExpanded,
            confirmStateChange = confirmStateChange
        )
    ) {
        PoodleModalBottomSheetState(
            initialValue = initialValue,
            animationSpec = animationSpec,
            isSkipHalfExpanded = skipHalfExpanded,
            confirmStateChange = confirmStateChange
        )
    }
}

/**
 * Create a [PoodleModalBottomSheetState] and [remember] it.
 *
 * @param initialValue The initial value of the state.
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@Composable
@ExperimentalMaterialApi
fun rememberPoodleModalBottomSheetState(
    initialValue: PoodleModalBottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmStateChange: (PoodleModalBottomSheetValue) -> Boolean = { true }
): PoodleModalBottomSheetState = rememberPoodleModalBottomSheetState(
    initialValue = initialValue,
    animationSpec = animationSpec,
    skipHalfExpanded = false,
    confirmStateChange = confirmStateChange
)

/**
 * Modified implementation of [ModalBottomSheetLayout] that can be Expanded, HalfExpanded, or **Collapsed** unlike
 * [ModalBottomSheetLayout] that can be Expanded, HalfExpanded, or **Hidden**.
 * @param sheetCollapsedOffsetFraction A fraction of screen height value that will be used to offset the sheet
 * when it is collapsed, For Example: if sheetCollapsedOffsetFraction is 0.8f, it means that a sheet content will occupy
 * 20% of screen height when sheet is collapsed, and the other 80% will be occupied by screen's content
 */
@ExperimentalMaterialApi
@Composable
fun PoodleModalBottomSheetLayout(
    sheetContent: @Composable ColumnScope.() -> Unit,
    @FloatRange(from = 0.1, to = 0.99)
    sheetCollapsedOffsetFraction: Float,
    modifier: Modifier = Modifier,
    sheetState: PoodleModalBottomSheetState =
        rememberPoodleModalBottomSheetState(PoodleModalBottomSheetValue.Collapsed),
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = PoodleModalBottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colors.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    scrimColor: Color = PoodleModalBottomSheetDefaults.scrimColor,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    BoxWithConstraints(modifier) {
        val fullHeight = constraints.maxHeight.toFloat()
        val sheetHeightState = remember { mutableStateOf<Float?>(null) }
        Box(Modifier.fillMaxSize()) {
            content()
            Scrim(
                color = scrimColor,
                onDismiss = {
                    if (sheetState.confirmStateChange(PoodleModalBottomSheetValue.Collapsed)) {
                        scope.launch { sheetState.collapse() }
                    }
                },
                visible = sheetState.targetValue != PoodleModalBottomSheetValue.Collapsed
            )
        }
        val collapsedSheetOffset = fullHeight * sheetCollapsedOffsetFraction
        Surface(
            Modifier
                .fillMaxWidth()
                .nestedScroll(sheetState.nestedScrollConnection)
                .offset {
                    val y = if (sheetState.anchors.isEmpty()) {
                        // if we don't know our anchors yet, render the sheet as collapsed
                        collapsedSheetOffset.roundToInt()
                    } else {
                        // if we do know our anchors, respect them
                        sheetState.offset.value.roundToInt()
                    }
                    IntOffset(0, y)
                }
                .bottomSheetSwipeable(sheetState, collapsedSheetOffset, sheetHeightState)
                .onGloballyPositioned {
                    sheetHeightState.value = it.size.height.toFloat()
                }
                .semantics {
                    if (sheetState.isExpanded) {
                        dismiss {
                            if (sheetState.confirmStateChange(PoodleModalBottomSheetValue.Collapsed)) {
                                scope.launch { sheetState.collapse() }
                            }
                            true
                        }
                        if (sheetState.currentValue == PoodleModalBottomSheetValue.HalfExpanded) {
                            expand {
                                if (sheetState.confirmStateChange(PoodleModalBottomSheetValue.Expanded)) {
                                    scope.launch { sheetState.fullExpand() }
                                }
                                true
                            }
                        } else if (sheetState.hasHalfExpandedState) { // collapse it to half expanded state but not to collapsed state
                            collapse {
                                if (sheetState.confirmStateChange(PoodleModalBottomSheetValue.HalfExpanded)) {
                                    scope.launch { sheetState.halfExpand() }
                                }
                                true
                            }
                        }
                    }
                },
            shape = sheetShape,
            elevation = sheetElevation,
            color = sheetBackgroundColor,
            contentColor = sheetContentColor
        ) {
            Column(content = sheetContent)
        }
    }
}

@Suppress("ModifierInspectorInfo")
@OptIn(ExperimentalMaterialApi::class)
private fun Modifier.bottomSheetSwipeable(
    sheetState: PoodleModalBottomSheetState,
    fullHeight: Float,
    sheetHeightState: State<Float?>
): Modifier {
    val sheetHeight = sheetHeightState.value
    val modifier = if (sheetHeight != null) {
        val anchors = if (sheetHeight < fullHeight / 2 || sheetState.isSkipHalfExpanded) {
            mapOf(
                fullHeight to PoodleModalBottomSheetValue.Collapsed,
                fullHeight - sheetHeight to PoodleModalBottomSheetValue.Expanded
            )
        } else {
            mapOf(
                fullHeight to PoodleModalBottomSheetValue.Collapsed,
                fullHeight / 2 to PoodleModalBottomSheetValue.HalfExpanded,
                max(0f, fullHeight - sheetHeight) to PoodleModalBottomSheetValue.Expanded
            )
        }
        Modifier.swipeable(
            state = sheetState,
            anchors = anchors,
            orientation = Orientation.Vertical,
            resistance = null
        )
    } else {
        Modifier
    }

    return this.then(modifier)
}

@Composable
private fun Scrim(
    color: Color,
    onDismiss: () -> Unit,
    visible: Boolean
) {
    if (color.isSpecified) {
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = TweenSpec()
        )
        val dismissModifier = if (visible) {
            Modifier
                .pointerInput(onDismiss) { detectTapGestures { onDismiss() } }
                .semantics(mergeDescendants = true) {
                    contentDescription = "Close sheet"
                    onClick { onDismiss(); true }
                }
        } else {
            Modifier
        }

        Canvas(
            Modifier
                .fillMaxSize()
                .then(dismissModifier)
        ) {
            drawRect(color = color, alpha = alpha)
        }
    }
}

/**
 * Contains useful Defaults for [PoodleModalBottomSheetLayout].
 */
object PoodleModalBottomSheetDefaults {

    /**
     * The default elevation used by [PoodleModalBottomSheetLayout].
     */
    val Elevation = 16.dp

    /**
     * The default scrim color used by [PoodleModalBottomSheetLayout].
     */
    val scrimColor: Color
        @Composable
        get() = MaterialTheme.colors.onSurface.copy(alpha = 0.32f)
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun PoodleModalBottomSheetLayoutPreview() {
    PoodleTheme(false) {
        PoodleModalBottomSheetLayout(
            modifier = Modifier.fillMaxSize(),
            sheetCollapsedOffsetFraction = 0.95f,
            sheetContent = {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .height(120.dp)
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 8.dp),
                    ) {}
                    val childModifier = Modifier
                        .height(136.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                    Row(modifier = childModifier) {}
                    Row(modifier = childModifier) {}
                    Row(modifier = childModifier) {}
                    Column(modifier = childModifier) {}
                    Text(text = "", modifier = childModifier)
                }
            }
        ) {
            Box() {
                Text(text = "Preview")
            }
        }
    }
}
