package com.dot.gallery.feature_node.presentation.edit.components.editor

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dot.gallery.core.Constants.Animation.navigateInAnimation
import com.dot.gallery.core.Constants.Animation.navigateUpAnimation
import com.dot.gallery.feature_node.domain.model.editor.Adjustment
import com.dot.gallery.feature_node.domain.model.editor.DrawMode
import com.dot.gallery.feature_node.domain.model.editor.EditorDestination
import com.dot.gallery.feature_node.domain.model.editor.EditorItems.Adjust
import com.dot.gallery.feature_node.domain.model.editor.EditorItems.Crop
import com.dot.gallery.feature_node.domain.model.editor.EditorItems.Filters
import com.dot.gallery.feature_node.domain.model.editor.EditorItems.Markup
import com.dot.gallery.feature_node.domain.model.editor.ImageFilter
import com.dot.gallery.feature_node.domain.model.editor.PathProperties
import com.dot.gallery.feature_node.presentation.edit.adjustments.varfilter.VariableFilterTypes
import com.dot.gallery.feature_node.presentation.edit.components.adjustment.AdjustScrubber
import com.dot.gallery.feature_node.presentation.edit.components.adjustment.AdjustSection
import com.dot.gallery.feature_node.presentation.edit.components.cropper.CropperSection
import com.dot.gallery.feature_node.presentation.edit.components.filters.FiltersSelector
import com.dot.gallery.feature_node.presentation.edit.components.markup.MarkupColorSelector
import com.dot.gallery.feature_node.presentation.edit.components.markup.MarkupDrawSelector
import com.dot.gallery.feature_node.presentation.edit.components.markup.MarkupEraseSelector
import com.dot.gallery.feature_node.presentation.edit.components.markup.MarkupSelector
import com.dot.gallery.feature_node.presentation.edit.components.markup.MarkupSizeSelector
import kotlin.math.roundToInt

@Composable
fun EditorNavigator(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    appliedAdjustments: List<Adjustment>,
    targetImage: Bitmap?,
    targetUri: Uri?,
    onAdjustItemLongClick: (VariableFilterTypes) -> Unit = {},
    onAdjustmentChange: (Adjustment) -> Unit = {},
    onAdjustmentPreview: (Adjustment) -> Unit = {},
    onToggleFilter: (ImageFilter) -> Unit = {},
    startCropping: () -> Unit = {},
    undoLastPath: () -> Unit,
    redoLastPath: () -> Unit,
    drawMode: DrawMode,
    setDrawMode: (DrawMode) -> Unit,
    paths: List<Pair<Path, PathProperties>>,
    pathsUndone: List<Pair<Path, PathProperties>>,
    currentPathProperty: PathProperties,
    setCurrentPathProperty: (PathProperties) -> Unit,
    isSupportingPanel: Boolean = false
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = EditorDestination.Editor,
        enterTransition = { navigateInAnimation },
        exitTransition = { navigateUpAnimation },
        popEnterTransition = { navigateInAnimation },
        popExitTransition = { navigateUpAnimation }
    ) {
        composable<EditorDestination.Editor> {
            EditorSelector(
                isSupportingPanel = isSupportingPanel,
                onItemClick = { editorItem ->
                    val dest = when (editorItem) {
                        Adjust -> EditorDestination.Adjust
                        Crop -> EditorDestination.Crop
                        Filters -> EditorDestination.Filters
                        Markup -> EditorDestination.Markup
                    }
                    navController.navigate(dest)
                }
            )
        }

        composable<EditorDestination.Adjust> {
            AdjustSection(
                appliedAdjustments = appliedAdjustments,
                isSupportingPanel = isSupportingPanel,
                onItemClick = { adjustment ->
                    navController.navigate(EditorDestination.AdjustDetail(adjustment))
                },
                onLongItemClick = onAdjustItemLongClick
            )
        }

        composable<EditorDestination.AdjustDetail> {
            val params = it.toRoute<EditorDestination.AdjustDetail>()
            val isRotate = params.adjustment == VariableFilterTypes.Rotate

            AdjustScrubber(
                modifier = Modifier.padding(bottom = 16.dp),
                adjustment = params.adjustment,
                displayValue = { value ->
                    (value * if (isRotate) 1f else 100f).roundToInt()
                        .toString() + if (isRotate) "°" else ""
                },
                onAdjustmentChange = onAdjustmentChange,
                onAdjustmentPreview = onAdjustmentPreview,
                appliedAdjustments = appliedAdjustments,
                isSupportingPanel = isSupportingPanel
            )
        }

        composable<EditorDestination.Filters> {
            FiltersSelector(
                bitmap = targetImage!!,
                onClick = onToggleFilter,
                appliedAdjustments = appliedAdjustments,
                isSupportingPanel = isSupportingPanel
            )
        }

        composable<EditorDestination.Crop> {
            CropperSection(
                isSupportingPanel = isSupportingPanel,
                onActionClick = {
                    val adjustment = it.asAdjustment()
                    if (adjustment != null) {
                        onAdjustmentChange(adjustment)
                    } else {
                        startCropping()
                    }
                }
            )
        }

        composable<EditorDestination.Markup> {
            MarkupSelector(
                drawMode = drawMode,
                setDrawMode = setDrawMode,
                undoLastPath = undoLastPath,
                redoLastPath = redoLastPath,
                paths = paths,
                pathsUndone = pathsUndone,
                isSupportingPanel = isSupportingPanel,
                navigate = { navController.navigate(it) }
            )
        }

        composable<EditorDestination.MarkupDraw> {
            MarkupDrawSelector(
                paths = paths,
                pathsUndone = pathsUndone,
                undoLastPath = undoLastPath,
                redoLastPath = redoLastPath,
                isSupportingPanel = isSupportingPanel,
                navigate = { navController.navigate(it) }
            )
        }

        composable<EditorDestination.MarkupDrawSize> {
            MarkupSizeSelector(
                currentPathProperty = currentPathProperty,
                setCurrentPathProperty = setCurrentPathProperty,
                isSupportingPanel = isSupportingPanel,
            )
        }

        composable<EditorDestination.MarkupDrawColor> {
            MarkupColorSelector(
                currentPathProperty = currentPathProperty,
                setCurrentPathProperty = setCurrentPathProperty,
                isSupportingPanel = isSupportingPanel,
            )
        }

        composable<EditorDestination.MarkupErase> {
            MarkupEraseSelector(
                paths = paths,
                pathsUndone = pathsUndone,
                undoLastPath = undoLastPath,
                redoLastPath = redoLastPath,
                isSupportingPanel = isSupportingPanel,
                navigate = { navController.navigate(it) }
            )
        }

        composable<EditorDestination.MarkupEraseSize> {
            MarkupSizeSelector(
                currentPathProperty = currentPathProperty,
                setCurrentPathProperty = {
                    setCurrentPathProperty(it.copy(eraseMode = true))
                },
                isSupportingPanel = isSupportingPanel,
            )
        }

        composable<EditorDestination.ExternalEditor> {
            ExternalEditor(
                currentUri = targetUri,
                isSupportingPanel = isSupportingPanel
            )
        }
    }
}
