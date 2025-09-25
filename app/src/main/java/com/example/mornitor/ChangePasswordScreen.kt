package com.example.mornitor

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { DataBaseHelper(context) }
    val coroutineScope = rememberCoroutineScope()
    
    // 检查密码是否存在
    val isPasswordExists = remember {
        dbHelper.doesPasswordExist()
    }
    
    // 状态管理
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    // 密码可见性状态
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    // 错误信息状态
    var errorMessage by remember { mutableStateOf("") }
    
    // 处理密码提交
    fun handleSubmit() {
        // 重置错误信息
        errorMessage = ""
        
        // 验证新密码和确认密码
        if (newPassword.isEmpty()) {
            errorMessage = "请输入新密码"
            return
        }
        
        if (newPassword != confirmPassword) {
            errorMessage = "两次输入的密码不一致"
            return
        }
        
        // 验证密码长度
        if (newPassword.length < 6) {
            errorMessage = "密码长度至少为6位"
            return
        }
        
        coroutineScope.launch {
            if (isPasswordExists) {
                // 如果密码已存在，验证原密码
                if (currentPassword.isEmpty()) {
                    errorMessage = "请输入原密码"
                    return@launch
                }
                
                if (!dbHelper.verifyPassword(currentPassword)) {
                    errorMessage = "原密码错误"
                    return@launch
                }
                
                // 更新密码
                if (dbHelper.updatePassword(newPassword)) {
                    Toast.makeText(context, "密码修改成功", Toast.LENGTH_SHORT).show()
                    onBack()
                } else {
                    errorMessage = "密码修改失败"
                }
            } else {
                // 如果密码不存在，初始化密码
                if (dbHelper.initPassword(newPassword)) {
                    Toast.makeText(context, "密码设置成功", Toast.LENGTH_SHORT).show()
                    onBack()
                } else {
                    errorMessage = "密码设置失败"
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isPasswordExists) "修改密码" else "设置初始密码"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 页面标题
            Text(
                text = if (isPasswordExists) "请输入原密码和新密码" else "请设置您的初始密码",
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 24.dp)
            )
            
            // 如果密码已存在，显示原密码输入框
            if (isPasswordExists) {
                PasswordTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = "原密码",
                    isPasswordVisible = currentPasswordVisible,
                    onToggleVisibility = { currentPasswordVisible = !currentPasswordVisible },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // 新密码输入框
            PasswordTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "新密码",
                isPasswordVisible = newPasswordVisible,
                onToggleVisibility = { newPasswordVisible = !newPasswordVisible },
                modifier = Modifier.fillMaxWidth()
            )
            
            // 确认密码输入框
            PasswordTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "确认密码",
                isPasswordVisible = confirmPasswordVisible,
                onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                modifier = Modifier.fillMaxWidth()
            )
            
            // 错误信息显示
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            
            // 提交按钮
            Button(
                onClick = ::handleSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isPasswordExists) "修改密码" else "设置密码",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPasswordVisible: Boolean,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.padding(vertical = 8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (isPasswordVisible) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    },
                    contentDescription = if (isPasswordVisible) "隐藏密码" else "显示密码"
                )
            }
        }
    )
}