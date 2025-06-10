package com.whitebeach.energycounter

import android.content.ClipData
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

// Experimental API のオプトイン
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun GridWithTopBottomTargets(
    paddingValues: PaddingValues
) {
    val circleDiameter = 80.dp // 正方形のサイズを直径として使用
    val cardSize = 100.dp // グリッド内のカードと同じサイズ
    val spacerSize = 8.dp // グリッド間のスペーサー

    // 赤い正方形が現在どのドロップターゲットに属しているか (ID)
    var currentDropTargetId by remember { mutableStateOf<Int?>(0) } // 初期は上部のターゲットに配置 (ID: 0)

    // 全てのドロップターゲットそれぞれの画面上の境界 (Rect) を管理するMutableState
    // Rect に変更することで、位置だけでなくサイズも取得できる
    val dropTargetBounds = remember { mutableStateMapOf<Int, androidx.compose.ui.geometry.Rect>() }

    val density = LocalDensity.current
    // 赤い正方形のサイズをピクセル単位で取得
    val circleDiameterPx = with(density) { circleDiameter.toPx() }
    val circleRadiusPx = circleDiameterPx / 2

    // Scaffoldのトップパディングのピクセル値
    val topPaddingPx = with(density) { paddingValues.calculateTopPadding().toPx() }

    // 赤い正方形の実際の表示位置 (アニメーション用)
    val animatedCircleOffset by animateOffsetAsState(
        targetValue = run {
            val targetBounds = dropTargetBounds[currentDropTargetId]
            if (targetBounds != null) {
                // targetBounds.top は物理的な画面の左上を基準としていると仮定
                // そのため、topBarの高さ分をY座標から差し引いて補正する
                val correctedTargetTop = targetBounds.top - topPaddingPx

                // ドロップターゲットの中心位置から、赤い丸の左上隅の座標を計算
                val targetCenterX = targetBounds.left + targetBounds.width / 2
                val targetCenterY = correctedTargetTop + targetBounds.height / 2
                // 円の左上隅がターゲットの中心になるようにオフセットを計算
                // ここもピクセル値で計算しています。
                Offset(targetCenterX - circleRadiusPx, targetCenterY - circleRadiusPx)
            } else {
                Offset.Zero // ターゲットが見つからない場合のデフォルト位置
            }
        },
        animationSpec = tween(durationMillis = 300),
        label = "animatedCircleOffset"
    )

    Surface(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // --- 上部のドロップターゲット ---
                // Boxで囲んでAlignmentを制御
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) { // 左端寄せ
                    MyDropTarget(
                        id = 0, // ユニークなID
                        modifier = Modifier.size(cardSize),
                        onPositioned = { id, bounds ->
                            dropTargetBounds[id] = bounds
                        }, // Rect を受け取るように変更
                        onDrop = { droppedItemId ->
                            if (droppedItemId == "red_square") {
                                currentDropTargetId = 0
                            }
                        },
                        isCurrentlyHoldingItem = currentDropTargetId == 0
                    ) {
                        Text(text = "0", fontSize = 60.sp)
                    }
                }
                Spacer(modifier = Modifier.height(spacerSize))

                // --- 3x3 グリッド ---
                Column(
                    modifier = Modifier.fillMaxWidth(),
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
                                val targetId = (rowIndex * 3) + colIndex + 1 // 1から9のID
                                MyDropTarget(
                                    id = targetId,
                                    modifier = Modifier.size(cardSize),
                                    onPositioned = { id, bounds ->
                                        dropTargetBounds[id] = bounds
                                    }, // Rect を受け取るように変更
                                    onDrop = { droppedItemId ->
                                        if (droppedItemId == "red_square") {
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

                Spacer(modifier = Modifier.height(spacerSize))

                // --- 下部のドロップターゲット ---
                // Boxで囲んでAlignmentを制御
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) { // 左端寄せ
                    MyDropTarget(
                        id = 10, // ユニークなID
                        modifier = Modifier.size(cardSize),
                        onPositioned = { id, bounds ->
                            dropTargetBounds[id] = bounds
                        }, // Rect を受け取るように変更
                        onDrop = { droppedItemId ->
                            if (droppedItemId == "red_square") {
                                currentDropTargetId = 10
                            }
                        },
                        isCurrentlyHoldingItem = currentDropTargetId == 10
                    ) {
                        Text(text = "10", fontSize = 60.sp)
                    }
                }
            }

            // ドラッグ可能な赤い正方形
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = animatedCircleOffset.x.toInt(),
                            y = animatedCircleOffset.y.toInt()
                        )
                    }
                    .size(circleDiameter)
                    .dragAndDropSource
                        (
                        transferData = {
                            DragAndDropTransferData(
                                clipData = ClipData.newPlainText("image item_id", "red_square")
                            )
                        }
                    )
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2
                    drawCircle(color = Color.Red, radius = radius)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewGridWithTopBottomTargets() {
    EnergyCounterTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {

                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "ENERGY COUNTER",
                            modifier = Modifier,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(
                                fontSize = 30.sp, // 文字の大きさを調整（見やすくするため）
                                color = Color.White, // 文字の色
                                shadow = Shadow(
                                    color = Color.Black, // 影の色
                                    offset = Offset(7f, 8f), // 影のオフセット
                                    blurRadius = 0f // 影のぼかし具合
                                )
                            )
                        )
                    },
                    modifier = Modifier,
                    colors = TopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface,
                        navigationIconContentColor = Color.White,
                        titleContentColor = MaterialTheme.colorScheme.tertiary,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { innerPadding ->
            GridWithTopBottomTargets(
                paddingValues = innerPadding
            )
        }
    }
}
