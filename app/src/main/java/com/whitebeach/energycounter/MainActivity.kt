package com.whitebeach.energycounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.whitebeach.energycounter.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                App()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        modifier = Modifier,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(
                            fontSize = 30.sp, // 文字の大きさを調整（見やすくするため）
                            color = MaterialTheme.colorScheme.tertiary, // 文字の色
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
                    containerColor =  MaterialTheme.colorScheme.secondary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.surface,
                    actionIconContentColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        GridWithTopBottomTargets(
            paddingValues = innerPadding
        )
    }
}
