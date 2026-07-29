package com.threesa.billing.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.threesa.billing.presentation.common.components.BadgeStyle
import com.threesa.billing.presentation.common.components.MessageDialog
import com.threesa.billing.presentation.common.components.StatusBadge
import com.threesa.billing.ui.theme.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = { viewModel.loadProfileAndPermissions() },
                indicator = {},
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundCream)
                        .verticalScroll(scrollState)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState.isLoading && uiState.profile == null) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryOrange)
                        }
                    } else {
                        val profile = uiState.profile
                        val name = profile?.name ?: "Super Admin"
                        val email = profile?.email ?: "billingsys@3sawebx.com"
                        val role = profile?.role ?: "super_admin"

                        // User Avatar Header
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(SurfaceWhite)
                                .border(2.dp, PrimaryOrange, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = TextSecondary
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(Modifier.height(2.dp))
                        Text(email, fontSize = 13.sp, color = TextSecondary)
                        Spacer(Modifier.height(8.dp))
                        StatusBadge(
                            text = role.replace("_", " ").uppercase(),
                            style = if (role.contains("admin")) BadgeStyle.SUCCESS else BadgeStyle.WARNING
                        )

                        Spacer(Modifier.height(24.dp))

                        // Permissions Card Section (Collapsible - Open by Default)
                        var isPermissionsExpanded by remember { mutableStateOf(true) }
                        val permissionsMap = uiState.permissions?.roles
                        if (!permissionsMap.isNullOrEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(Color.White)
                                    .border(1.dp, BorderLight, MaterialTheme.shapes.medium)
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isPermissionsExpanded = !isPermissionsExpanded }
                                ) {
                                    Icon(Icons.Filled.Security, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Role Permissions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = if (isPermissionsExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                        contentDescription = if (isPermissionsExpanded) "Collapse" else "Expand",
                                        tint = TextSecondary
                                    )
                                }

                                androidx.compose.animation.AnimatedVisibility(visible = isPermissionsExpanded) {
                                    Column {
                                        Spacer(Modifier.height(12.dp))
                                        HorizontalDivider(color = BorderLight)
                                        Spacer(Modifier.height(12.dp))

                                        permissionsMap.forEach { (roleKey, roleDetail) ->
                                            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                                                Text(
                                                    text = roleDetail.display_name ?: roleKey.uppercase(),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = TextPrimary
                                                )
                                                Spacer(Modifier.height(6.dp))

                                                FlowRow(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    val perms = roleDetail.permissions.orEmpty()
                                                    if (perms.contains("*")) {
                                                        AssistChip(
                                                            onClick = {},
                                                            label = { Text("All Permissions (*)", fontSize = 11.sp, color = PrimaryOrange) },
                                                            colors = AssistChipDefaults.assistChipColors(containerColor = PeachBg)
                                                        )
                                                    } else {
                                                        perms.forEach { perm ->
                                                            AssistChip(
                                                                onClick = {},
                                                                label = { Text(perm.replace("_", " "), fontSize = 11.sp) },
                                                                colors = AssistChipDefaults.assistChipColors(containerColor = SurfaceMuted)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(20.dp))
                        }

                        // Action Menu Options
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(Color.White)
                                .border(1.dp, BorderLight, MaterialTheme.shapes.medium)
                        ) {
                            ProfileMenuItem(
                                icon = Icons.Filled.PersonAdd,
                                text = "Create New User",
                                onClick = viewModel::openCreateUserModal
                            )
                            HorizontalDivider(color = BorderLight)
                            ProfileMenuItem(
                                icon = Icons.Filled.Lock,
                                text = "Change Password",
                                onClick = viewModel::openChangePasswordModal
                            )
                        }

                        Spacer(Modifier.height(28.dp))

                        // Logout Button
                        Button(
                            onClick = { viewModel.logout(onLogoutClick) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftRedBg),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = SoftRedIcon)
                            Spacer(Modifier.width(8.dp))
                            Text("Log Out", color = SoftRedIcon, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(Modifier.height(30.dp))
                    }
                }
            }

            // Modal 1: Create User Modal
            if (uiState.showCreateUserModal) {
                CreateUserBottomSheet(
                    stores = uiState.stores,
                    isCreating = uiState.isCreatingUser,
                    errorMessage = uiState.errorMessage,
                    onDismiss = viewModel::closeCreateUserModal,
                    onSubmit = { name, email, pass, role, shopId ->
                        viewModel.createUser(name, email, pass, role, shopId)
                    }
                )
            }

            // Modal 2: Change Password Modal
            if (uiState.showChangePasswordModal) {
                ChangePasswordBottomSheet(
                    isChanging = uiState.isChangingPassword,
                    errorMessage = uiState.errorMessage,
                    onDismiss = viewModel::closeChangePasswordModal,
                    onSubmit = { current, newPass, confirmPass ->
                        viewModel.changePassword(current, newPass, confirmPass)
                    }
                )
            }

            if (!uiState.successMessage.isNullOrBlank()) {
                MessageDialog(
                    isSuccess = true,
                    title = "Success",
                    message = uiState.successMessage!!,
                    onDismiss = viewModel::clearMessages
                )
            } else if (!uiState.errorMessage.isNullOrBlank() && !uiState.showCreateUserModal && !uiState.showChangePasswordModal) {
                MessageDialog(
                    isSuccess = false,
                    title = "Action Failed",
                    message = uiState.errorMessage!!,
                    onDismiss = viewModel::clearMessages
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateUserBottomSheet(
    stores: List<com.threesa.billing.data.remote.dto.StoreDto>,
    isCreating: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, Int?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("staff") }
    var selectedStore by remember { mutableStateOf<com.threesa.billing.data.remote.dto.StoreDto?>(null) }
    var roleExpanded by remember { mutableStateOf(false) }
    var storeExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            Text("Create New User", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(16.dp))

            if (!errorMessage.isNullOrBlank()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Role Selector Dropdown
            ExposedDropdownMenuBox(
                expanded = roleExpanded,
                onExpandedChange = { roleExpanded = !roleExpanded }
            ) {
                OutlinedTextField(
                    value = role.replace("_", " ").uppercase(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Role") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = roleExpanded,
                    onDismissRequest = { roleExpanded = false }
                ) {
                    listOf("staff", "admin", "super_admin").forEach { r ->
                        DropdownMenuItem(
                            text = { Text(r.replace("_", " ").uppercase()) },
                            onClick = {
                                role = r
                                roleExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // Store / Shop Selector Dropdown
            ExposedDropdownMenuBox(
                expanded = storeExpanded,
                onExpandedChange = { storeExpanded = !storeExpanded }
            ) {
                OutlinedTextField(
                    value = selectedStore?.name ?: "Select Store (Optional)",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Store / Shop") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = storeExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = storeExpanded,
                    onDismissRequest = { storeExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None (All Stores / Main)") },
                        onClick = {
                            selectedStore = null
                            storeExpanded = false
                        }
                    )
                    stores.forEach { store ->
                        DropdownMenuItem(
                            text = { Text(store.name ?: "Store #${store.id}") },
                            onClick = {
                                selectedStore = store
                                storeExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            val isEmailValid = remember(email) {
                email.isBlank() || (email.contains("@") && email.contains(".") && email.length >= 5)
            }
            val isPasswordValid = remember(password) { password.isBlank() || password.length >= 6 }
            val isFormValid = name.isNotBlank() && email.isNotBlank() && isEmailValid && password.isNotBlank() && password.length >= 6

            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("Email Address") },
                isError = email.isNotBlank() && !isEmailValid,
                supportingText = {
                    if (email.isNotBlank() && !isEmailValid) {
                        Text("Enter a valid email (e.g. user@3sawebx.com)", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (Min 6 chars)") },
                isError = password.isNotBlank() && !isPasswordValid,
                supportingText = {
                    if (password.isNotBlank() && !isPasswordValid) {
                        Text("Password must be at least 6 characters", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onDismiss, enabled = !isCreating) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = { onSubmit(name, email, password, role, selectedStore?.id) },
                    enabled = !isCreating && isFormValid,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    if (isCreating) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Create User")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordBottomSheet(
    isChanging: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            Text("Change Password", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(16.dp))

            if (!errorMessage.isNullOrBlank()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                singleLine = true,
                visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                        Icon(
                            imageVector = if (currentPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (currentPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                singleLine = true,
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(
                            imageVector = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (newPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm New Password") },
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onDismiss, enabled = !isChanging) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = { onSubmit(currentPassword, newPassword, confirmPassword) },
                    enabled = !isChanging && currentPassword.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    if (isChanging) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Change Password")
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BackgroundCream),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextSecondary)
    }
}
