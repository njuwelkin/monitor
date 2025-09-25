package com.example.mornitor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.mornitor.ChangePasswordScreen
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mornitor.ui.theme.MornitorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MornitorTheme {
                // 添加一个状态来控制当前显示的屏幕
                var currentScreen by remember { mutableStateOf("main") }
                
                // 根据当前屏幕状态显示不同内容
                when (currentScreen) {
                    "main" -> {
                        Scaffold(modifier = Modifier.fillMaxSize()) {
                            AppContent(
                                modifier = Modifier.padding(it),
                                onNavigateToChangePassword = { currentScreen = "changePassword" }
                            )
                        }
                    }
                    "changePassword" -> {
                        ChangePasswordScreen(
                            onBack = { currentScreen = "main" }
                        )
                    }
                }
            }
        }
    }
}

// 定义菜单项数据类
data class MenuItem(
    val id: String,
    val title: String,
    val icon: @Composable () -> Unit
)

@Composable
fun AppContent(
    modifier: Modifier = Modifier,
    onNavigateToChangePassword: () -> Unit
) {
    val context = LocalContext.current
    val menuItems = remember {
        listOf(
            MenuItem(
                id = "change_password",
                title = "修改密码",
                icon = { Icon(Icons.Filled.Lock, contentDescription = "修改密码") }
            ),
            MenuItem(
                id = "settings",
                title = "设置",
                icon = { Icon(Icons.Filled.Settings, contentDescription = "设置") }
            ),
            MenuItem(
                id = "browse_photos",
                title = "浏览照片",
                icon = { Icon(Icons.Filled.Camera, contentDescription = "浏览照片") }
            ),
            MenuItem(
                id = "clear_cache",
                title = "清空缓存",
                icon = { Icon(Icons.Filled.Delete, contentDescription = "清空缓存") }
            )
        )
    }

    // 缓存状态
    var cacheSize by remember { mutableStateOf("50MB") }

    Column(modifier = modifier.fillMaxSize()) {
        // 标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "应用设置",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 菜单项列表
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(menuItems) {
                MenuItem(
                    item = it,
                    onClick = {
                        when (it.id) {
                            "change_password" -> {
                                // 导航到修改密码页面
                                onNavigateToChangePassword()
                            }
                            "settings" -> {
                                Toast.makeText(context, "打开设置页面", Toast.LENGTH_SHORT).show()
                                // 实际应用中这里应该导航到设置页面
                            }
                            "browse_photos" -> {
                                Toast.makeText(context, "打开照片浏览页面", Toast.LENGTH_SHORT).show()
                                // 实际应用中这里应该导航到照片浏览页面
                            }
                            "clear_cache" -> {
                                Toast.makeText(context, "缓存已清空", Toast.LENGTH_SHORT).show()
                                cacheSize = "0MB"
                                // 实际应用中这里应该实现清空缓存的逻辑
                            }
                        }
                    }
                )
            }

            // 显示缓存大小
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "当前缓存大小:")
                    Text(text = cacheSize, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun MenuItem(
    item: MenuItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 直接调用icon Composable函数，而不是尝试类型转换
        item.icon()
        Text(text = item.title, fontSize = 18.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun AppContentPreview() {
    MornitorTheme {
        AppContent(onNavigateToChangePassword = {})
    }
}