package com.threesa.billing.presentation.reports.pdf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.threesa.billing.presentation.common.components.MessageDialog
import com.threesa.billing.ui.theme.BackgroundCream
import com.threesa.billing.ui.theme.PrimaryOrange
import com.threesa.billing.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicePdfScreen(
    onBackClick: () -> Unit,
    viewModel: InvoicePdfViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoice PDF", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundCream),
                windowInsets = WindowInsets(0.dp)
            )
        },
        floatingActionButton = {
            if (uiState.pdfData != null) {
                FloatingActionButton(
                    onClick = { viewModel.downloadPdfToDevice(context) },
                    containerColor = PrimaryOrange,
                    contentColor = Color.White
                ) {
                    if (uiState.isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Icon(Icons.Default.Download, contentDescription = "Download Invoice PDF")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundCream)
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryOrange
                )
            } else if (uiState.errorMessage != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.loadInvoicePdf() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                    ) {
                        Text("Retry")
                    }
                }
            } else if (uiState.pdfData != null) {
                val base64 = uiState.pdfData?.base64_data
                if (!base64.isNullOrBlank()) {
                    PdfRendererView(base64Data = base64, modifier = Modifier.fillMaxSize())
                } else {
                    Text("No PDF content available", modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        uiState.dialogState?.let { dialog ->
            MessageDialog(
                isSuccess = dialog.isSuccess,
                title = dialog.title,
                message = dialog.message,
                onDismiss = viewModel::dismissDialog
            )
        }
    }
}
