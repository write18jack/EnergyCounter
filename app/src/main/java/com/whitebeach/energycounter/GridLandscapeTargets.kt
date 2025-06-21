package com.whitebeach.energycounter

import android.content.ClipData
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whitebeach.energycounter.ui.theme.EnergyCounterTheme
import kotlin.math.roundToInt

// Experimental API のオプトイン
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun GridLandscapeTargets() {
    val circleDiameter = 80.dp
    val cardSize = 100.dp
    val spacerSize = 8.dp

    var currentDropTargetId by remember { mutableStateOf<Int?>(0) }
    val dropTargetBounds = remember { mutableStateMapOf<Int, androidx.compose.ui.geometry.Rect>() }

    val density = LocalDensity.current
    val circleDiameterPx = with(density) { circleDiameter.toPx() }
    val circleRadiusPx = circleDiameterPx / 2

    val animatedCircleOffset by animateOffsetAsState(
        targetValue = run {
            val targetBounds = dropTargetBounds[currentDropTargetId]
            if (targetBounds != null) {
                val targetCenterX = targetBounds.left + targetBounds.width / 2
                val targetCenterY = targetBounds.top + targetBounds.height / 2
                // 円の左上隅がターゲットの中心になるように調整
                Offset(targetCenterX - circleRadiusPx, targetCenterY - circleRadiusPx)
            } else {
                Offset.Zero
            }
        },
        animationSpec = tween(durationMillis = 300),
        label = "animatedCircleOffset"
    )

    // デバッグ用の状態変数 (必要に応じて残してください)
    var debugTargetTopLeft by remember { mutableStateOf<Offset?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(), // Scaffoldがないため、直接fillMaxSize()
        color = MaterialTheme.colorScheme.background
    ) {
        // 全体をカバーするBox。この中に全てのコンテンツと赤い丸を配置します。
        Box(modifier = Modifier.fillMaxSize()) {
            // 横画面用のメインレイアウトをRowで作成
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), // 全体的なパディング
                horizontalArrangement = Arrangement.Center, // 要素間に均等なスペース
                verticalAlignment = Alignment.CenterVertically // 垂直方向中央揃え
            ) {
                // MARK: - Left Drop Target
                Column(
                    modifier = Modifier
                        .fillMaxHeight() // 縦方向にいっぱい
                     .weight(1f) // 横方向は全体の25%の重み付け
                        .padding(end = spacerSize),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    MyDropTarget(
                        id = 0,
                        modifier = Modifier.size(cardSize),
                        onPositioned = { id, bounds ->
                            dropTargetBounds.put(id, bounds)
                            if (id == currentDropTargetId) {
                                debugTargetTopLeft = bounds.topLeft
                            }
                        },
                        onDrop = { droppedItemId ->
                            if (droppedItemId == "red_circle") {
                                currentDropTargetId = 0
                            }
                        },
                        isCurrentlyHoldingItem = currentDropTargetId == 0
                    ) {
                        Text(text = "0", fontSize = 60.sp)
                    }
                }

              //  Spacer(modifier = Modifier.width(spacerSize))

                // MARK: - 3x3 Grid (中央配置)
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1.3f), // 横方向は全体の50%の重み付け
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    repeat(3) { rowIndex ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(3) { colIndex ->
                                val targetId = (rowIndex * 3) + colIndex + 1
                                MyDropTarget(
                                    id = targetId,
                                    modifier = Modifier.size(cardSize),
                                    onPositioned = { id, bounds ->
                                        dropTargetBounds.put(id, bounds)
                                        if (id == currentDropTargetId) {
                                            debugTargetTopLeft = bounds.topLeft
                                        }
                                    },
                                    onDrop = { droppedItemId ->
                                        if (droppedItemId == "red_circle") {
                                            currentDropTargetId = targetId
                                        }
                                    },
                                    isCurrentlyHoldingItem = currentDropTargetId == targetId
                                ) {
                                    Text(text = "$targetId", fontSize = 60.sp)
                                }
                                if (colIndex < 2) {
                                    Spacer(modifier = Modifier.width(spacerSize))
                                }
                            }
                        }
                        if (rowIndex < 2) {
                            Spacer(modifier = Modifier.height(spacerSize))
                        }
                    }
                }

               // Spacer(modifier = Modifier.width(spacerSize))

                // MARK: - Right Drop Target
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f) // 横方向は全体の25%の重み付け
                        .padding(start = spacerSize),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    MyDropTarget(
                        id = 10,
                        modifier = Modifier.size(cardSize),
                        onPositioned = { id, bounds ->
                            dropTargetBounds.put(id, bounds)
                            if (id == currentDropTargetId) {
                                debugTargetTopLeft = bounds.topLeft
                            }
                        },
                        onDrop = { droppedItemId ->
                            if (droppedItemId == "red_circle") {
                                currentDropTargetId = 10
                            }
                        },
                        isCurrentlyHoldingItem = currentDropTargetId == 10
                    ) {
                        Text(text = "10", fontSize = 60.sp)
                    }
                }
            }

            // ドラッグ可能な赤い丸
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = animatedCircleOffset.x.roundToInt(),
                            y = animatedCircleOffset.y.roundToInt()
                        )
                    }
                    .size(circleDiameter)
                    .dragAndDropSource(
                        transferData = {
                            DragAndDropTransferData(
                                clipData = ClipData.newPlainText("item_id", "red_circle")
                            )
                        }
                    )
            ) {
                // 通常表示時の赤い丸
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2
                    drawCircle(color = Color.Red, radius = radius)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
    )
@Composable
fun PreviewGridLandscapeTargets() {
    EnergyCounterTheme {
        GridLandscapeTargets()
    }
}