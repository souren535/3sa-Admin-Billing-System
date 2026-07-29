package com.threesa.billing.presentation.common.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.threesa.billing.ui.theme.MintIcon
import com.threesa.billing.ui.theme.SoftRedIcon

@Composable
fun MessageDialog(
    isSuccess: Boolean,
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        icon = {
            Icon(
                imageVector = if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Error,
                contentDescription = null,
                tint = if (isSuccess) MintIcon else SoftRedIcon
            )
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        }
    )
}
