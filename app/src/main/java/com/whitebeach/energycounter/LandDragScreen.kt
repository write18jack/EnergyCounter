package com.whitebeach.energycounter

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun LandDragScreen(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        CounterField()
        DraggableItemImmediate()
    }
}

@Composable
fun CounterField(){
    val spacerSize = 24.dp

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
                verticalArrangement = Arrangement.Top
            ) {
                MyDropTarget(
                    id = 0,
                    modifier = Modifier.size(100.dp)
                ) {
                    Text(text = "0", fontSize = 60.sp)
                }
            }

            // MARK: - 3x3 Grid (中央配置)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1.5f), // 横方向は全体の50%の重み付け
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
                                modifier = Modifier.size(100.dp)
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

            // MARK: - Right Drop Target
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f) // 横方向は全体の25%の重み付け
                    .padding(start = spacerSize),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom
            ) {
                MyDropTarget(
                    id = 10,
                    modifier = Modifier.size(100.dp)
                ) {
                    Text(text = "10", fontSize = 60.sp)
                }
            }
        }
    }
}

@Composable
fun DraggableItemImmediate() {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(80.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // ドラッグが開始された瞬間に呼ばれる
                        // ここでドラッグの初期位置などを設定
                        // offsetX, offsetY は既にアイテムの位置を反映しているため、ここでは初期値として使用
                        // たとえば、ドラッグ開始時の指の位置を基準にしたい場合は、
                        // startX = offsetX - offset.x, startY = offsetY - offset.y
                        // のように調整が必要になる場合があります。
                    },
                    onDragEnd = {
                        // ドラッグ終了時の処理
                    },
                    onDragCancel = {
                        // ドラッグキャンセル時の処理
                    },
                    onDrag = { change, dragAmount ->
                        // 指の動きに合わせて位置を更新
                        change.consume() // イベントを消費して、他の要素に伝播しないようにする
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                )
            }
    ){
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2
            drawCircle(color = Color.Red, radius = radius)
        }
    }
}