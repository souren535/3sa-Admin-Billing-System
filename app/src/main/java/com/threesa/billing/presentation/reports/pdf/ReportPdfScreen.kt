package com.threesa.billing.presentation.reports.pdf

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.threesa.billing.ui.theme.BackgroundCream
import com.threesa.billing.ui.theme.PrimaryOrange
import com.threesa.billing.ui.theme.TextPrimary
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPdfScreen(
    onBackClick: () -> Unit,
    viewModel: ReportPdfViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report PDF", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
                        Icon(Icons.Default.Download, contentDescription = "Download Report PDF")
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
                        onClick = { viewModel.loadReportPdf() },
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
    }
}

@Composable
fun PdfRendererView(base64Data: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var pageBitmaps by remember(base64Data) { mutableStateOf<List<Bitmap>>(emptyList()) }

    LaunchedEffect(base64Data) {
        try {
            val bytes = Base64.decode(base64Data, Base64.DEFAULT)
            val tempFile = File(context.cacheDir, "report_preview.pdf")
            tempFile.writeBytes(bytes)

            val fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(fileDescriptor)

            val bitmaps = mutableListOf<Bitmap>()
            for (i in 0 until pdfRenderer.pageCount) {
                val page = pdfRenderer.openPage(i)
                val width = page.width * 2
                val height = page.height * 2
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)
                page.close()
            }
            pdfRenderer.close()
            fileDescriptor.close()

            pageBitmaps = bitmaps
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    if (pageBitmaps.isNotEmpty()) {
        LazyColumn(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(pageBitmaps) { bitmap ->
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "PDF Page",
                        modifier = Modifier.fillMaxWidth().aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat())
                    )
                }
            }
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryOrange)
        }
    }
}
