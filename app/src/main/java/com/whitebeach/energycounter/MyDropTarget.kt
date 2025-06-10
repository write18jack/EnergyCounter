package com.whitebeach.energycounter

import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned

// ドロップターゲットをカプセル化するComposable関数
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MyDropTarget(
    id: Int,
    modifier: Modifier = Modifier,
    onPositioned: (id: Int, bounds: androidx.compose.ui.geometry.Rect) -> Unit, // Offset から Rect に変更
    onDrop: (droppedItemId: String) -> Unit,
    isCurrentlyHoldingItem: Boolean,
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                // onPositioned で Rect (境界) を渡すように変更
                onPositioned(id, layoutCoordinates.boundsInWindow())
            }
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    event.mimeTypes().contains("text/plain")
                },
                target = remember(id) {
                    object : DragAndDropTarget {
                        override fun onDrop(event: DragAndDropEvent): Boolean {
                            val clipData = event.toAndroidDragEvent().clipData
                            if (clipData.itemCount > 0) {
                                val itemId = clipData.getItemAt(0).text.toString()
                                onDrop(itemId)
                                return true
                            }
                            return false
                        }

                        override fun onStarted(event: DragAndDropEvent) {}
                        override fun onEnded(event: DragAndDropEvent) {}
                    }
                }
            ),
        colors = CardColors(
            containerColor = Color.LightGray,
            contentColor = Color.White,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}