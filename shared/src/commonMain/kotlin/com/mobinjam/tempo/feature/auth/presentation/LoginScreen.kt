package com.mobinjam.tempo.feature.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import tempo.shared.generated.resources.Res
import tempo.shared.generated.resources.logo

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // when login succeeds, notify the navigator
    if (state.isLoginSuccessful) {
        onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "Tempo logo",
                modifier = Modifier.size(64.dp),
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Login",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(4.dp))

            Row {
                Text(
                    text = "or ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                )
                Text(
                    text = "create an account",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onCreateAccountClick() },
                )
            }

            Spacer(Modifier.height(28.dp))

            TempoTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "Email or username",
                keyboardType = KeyboardType.Email,
            )

            Spacer(Modifier.height(12.dp))

            TempoTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "Password",
                keyboardType = KeyboardType.Password,
                visualTransformation =
                    if (state.isPasswordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailing = {
                    Text(
                        text = if (state.isPasswordVisible) "Hide" else "Show",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { viewModel.togglePasswordVisibility() },
                    )
                },
            )

            // error message
            if (state.errorMessage != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = state.errorMessage!!,
                    color = Color(0xFFE57373),
                    fontSize = 13.sp,
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { viewModel.onLoginClick() },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("Enter", color = Color.White, fontSize = 15.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Forgot password?",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onForgotPasswordClick() },
            )
        }
    }
}

@Composable
private fun TempoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = trailing,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.White,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        ),
    )
}