package com.nova.app.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nova.app.data.provider.FetchResult
import com.nova.app.data.provider.Provider
import com.nova.app.data.provider.ProviderClient
import com.nova.app.data.provider.ProviderModel
import com.nova.app.data.provider.normalizeEndpoint
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.components.ErrorDialog
import com.nova.app.ui.components.ModelPickerDialog
import com.nova.app.ui.components.NovaIcons
import com.nova.app.ui.components.NovaSwitch
import com.nova.app.ui.theme.NovaTheme
import kotlinx.coroutines.launch

@Composable
fun ProviderDetailScreen(
    provider: Provider,
    onProviderChange: (Provider) -> Unit,
    onDelete: () -> Unit,
) {
    val strings = NovaStrings.strings
    val scope = rememberCoroutineScope()
    var fetching by remember { mutableStateOf(false) }
    var fetchError by remember { mutableStateOf("") }
    var modelDraft by remember { mutableStateOf("") }
    var speedError by remember { mutableStateOf("") }
    var pickerOpen by remember { mutableStateOf(false) }
    var pickerModels by remember { mutableStateOf<List<String>>(emptyList()) }
    var pickerLoading by remember { mutableStateOf(false) }
    var testingModel by remember { mutableStateOf<String?>(null) }

    fun applyModels(ids: List<String>) {
        val existing = provider.models.associateBy { it.id }
        val merged = ids.map { id -> existing[id] ?: ProviderModel(id) }
        onProviderChange(provider.copy(models = merged))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        FieldCard {
            FieldRow(
                label = strings.providerName,
                value = provider.name,
                placeholder = strings.providerNamePlaceholder,
                onValueChange = { onProviderChange(provider.copy(name = it)) },
            )
            FieldDivider()
            FieldRow(
                label = strings.providerApiKey,
                value = provider.apiKey,
                placeholder = strings.providerApiKeyPlaceholder,
                secret = true,
                onValueChange = { onProviderChange(provider.copy(apiKey = it.trim())) },
            )
            FieldDivider()
            FieldRow(
                label = strings.providerEndpoint,
                value = provider.endpoint,
                placeholder = strings.providerEndpointPlaceholder,
                keyboardType = KeyboardType.Uri,
                onValueChange = { onProviderChange(provider.copy(endpoint = it.trim())) },
            )
            FieldDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.providerLocal,
                        style = MaterialTheme.typography.bodyLarge,
                        color = NovaTheme.colors.textPrimary,
                    )
                    Text(
                        text = strings.providerLocalHint,
                        style = MaterialTheme.typography.labelSmall,
                        color = NovaTheme.colors.textTertiary,
                    )
                }
                NovaSwitch(
                    checked = provider.allowHttp,
                    onCheckedChange = { onProviderChange(provider.copy(allowHttp = it)) },
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        val normalized = normalizeEndpoint(provider.endpoint, provider.allowHttp)
        if (normalized.isNotEmpty()) {
            Text(
                text = normalized,
                style = MaterialTheme.typography.labelSmall,
                color = NovaTheme.colors.textTertiary,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Spacer(Modifier.height(22.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = strings.providerModels,
                style = MaterialTheme.typography.labelLarge,
                color = NovaTheme.colors.textSecondary,
                modifier = Modifier.weight(1f),
            )
            if (fetchError.isNotBlank()) {
                Text(
                    text = fetchError,
                    style = MaterialTheme.typography.labelSmall,
                    color = NovaTheme.colors.danger,
                )
                Spacer(Modifier.width(8.dp))
            }
            SmallAction(
                text = if (fetching) strings.providerModelsFetching else strings.providerModelsFetch,
                enabled = !fetching && normalized.isNotEmpty(),
                onClick = {
                    fetching = true
                    fetchError = ""
                    pickerModels = emptyList()
                    pickerLoading = true
                    pickerOpen = true
                    scope.launch {
                        when (val result = ProviderClient.fetchModels(provider)) {
                            is FetchResult.Success -> pickerModels = result.models
                            is FetchResult.Failure -> {
                                pickerOpen = false
                                fetchError = result.message
                            }
                        }
                        pickerLoading = false
                        fetching = false
                    }
                },
            )
        }

        Spacer(Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = NovaTheme.colors.card,
        ) {
            Column {
                ModelInput(
                    value = modelDraft,
                    onValueChange = { modelDraft = it },
                    onAdd = {
                        val id = modelDraft.trim()
                        if (id.isNotEmpty() && provider.models.none { it.id == id }) {
                            onProviderChange(
                                provider.copy(models = provider.models + ProviderModel(id)),
                            )
                        }
                        modelDraft = ""
                    },
                )
                if (provider.models.isEmpty()) {
                    Text(
                        text = strings.providerModelsEmpty,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NovaTheme.colors.textTertiary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    )
                } else {
                    HorizontalDivider(thickness = 0.7.dp, color = NovaTheme.colors.divider)
                    provider.models.forEachIndexed { index, model ->
                        if (index > 0) {
                            HorizontalDivider(
                                thickness = 0.7.dp,
                                color = NovaTheme.colors.divider,
                                modifier = Modifier.padding(start = 16.dp),
                            )
                        }
                        ModelRow(
                            model = model,
                            testing = testingModel == model.id,
                            onTest = {
                                testingModel = model.id
                                scope.launch {
                                    val result = ProviderClient.testModel(provider, model.id)
                                    if (!result.reachable && result.message.isNotBlank()) {
                                        speedError = model.id + "\n" + result.message
                                    }
                                    onProviderChange(
                                        provider.copy(
                                            models = provider.models.map {
                                                if (it.id == model.id) {
                                                    it.copy(
                                                        latencyMs = result.latencyMs,
                                                        reachable = result.reachable,
                                                    )
                                                } else {
                                                    it
                                                }
                                            },
                                        ),
                                    )
                                    testingModel = null
                                }
                            },
                            onRemove = {
                                onProviderChange(
                                    provider.copy(
                                        models = provider.models.filterNot { it.id == model.id },
                                    ),
                                )
                            },
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        Surface(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = NovaTheme.colors.card,
        ) {
            Text(
                text = strings.providerDelete,
                style = MaterialTheme.typography.bodyLarge,
                color = NovaTheme.colors.danger,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            )
        }
        Spacer(Modifier.height(28.dp))
    }

    if (pickerOpen) {
        ModelPickerDialog(
            models = pickerModels,
            alreadyAdded = provider.models.map { it.id }.toSet(),
            loading = pickerLoading,
            onImport = { picked ->
                applyModels(picked)
                pickerOpen = false
            },
            onDismiss = { pickerOpen = false },
        )
    }

    if (fetchError.isNotBlank()) {
        ErrorDialog(
            title = strings.errorTitle,
            message = fetchError,
            onDismiss = { fetchError = "" },
        )
    }

    if (speedError.isNotBlank()) {
        ErrorDialog(
            title = strings.errorTitle,
            message = speedError,
            onDismiss = { speedError = "" },
        )
    }
}

@Composable
private fun FieldCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NovaTheme.colors.card,
    ) {
        Column { content() }
    }
}

@Composable
private fun FieldDivider() {
    HorizontalDivider(
        thickness = 0.7.dp,
        color = NovaTheme.colors.divider,
        modifier = Modifier.padding(start = 16.dp),
    )
}

@Composable
private fun FieldRow(
    label: String,
    value: String,
    placeholder: String,
    secret: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = NovaTheme.colors.textTertiary,
        )
        Spacer(Modifier.height(6.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = NovaTheme.colors.inputBackground,
            border = BorderStroke(1.dp, NovaTheme.colors.divider),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 11.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = NovaTheme.colors.textPrimary,
                ),
                cursorBrush = SolidColor(NovaTheme.colors.accent),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (secret) KeyboardType.Password else keyboardType,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = NovaTheme.colors.textTertiary,
                            )
                        }
                        innerTextField()
                    }
                },
            )
        }
    }
}

@Composable
private fun SmallAction(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Surface(
            onClick = { if (enabled) onClick() },
            enabled = enabled,
            shape = RoundedCornerShape(11.dp),
            color = if (enabled) NovaTheme.colors.accent else NovaTheme.colors.containerHigh,
            border = if (enabled) null else BorderStroke(0.8.dp, NovaTheme.colors.divider),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = if (enabled) NovaTheme.colors.onAccent else NovaTheme.colors.textTertiary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            )
        }
    }
}

@Composable
private fun ModelInput(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit,
) {
    val strings = NovaStrings.strings
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            color = NovaTheme.colors.inputBackground,
            border = BorderStroke(0.8.dp, NovaTheme.colors.border),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = NovaTheme.colors.textPrimary,
                ),
                cursorBrush = SolidColor(NovaTheme.colors.accent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAdd() }),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = strings.providerModelPlaceholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NovaTheme.colors.textTertiary,
                        )
                    }
                    innerTextField()
                },
            )
        }
        Spacer(Modifier.width(8.dp))
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            Surface(
                onClick = onAdd,
                enabled = value.isNotBlank(),
                shape = CircleShape,
                color = if (value.isNotBlank()) {
                    NovaTheme.colors.accent
                } else {
                    NovaTheme.colors.containerHigh
                },
            ) {
                Box(
                    modifier = Modifier.size(34.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = NovaIcons.Plus,
                        contentDescription = strings.providerModelsManual,
                        modifier = Modifier.size(14.dp),
                        tint = if (value.isNotBlank()) {
                            NovaTheme.colors.onAccent
                        } else {
                            NovaTheme.colors.textDisabled
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelRow(
    model: ProviderModel,
    testing: Boolean,
    onTest: () -> Unit,
    onRemove: () -> Unit,
) {
    val strings = NovaStrings.strings
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 10.dp, top = 9.dp, bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = model.id,
                style = MaterialTheme.typography.bodyMedium,
                color = NovaTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (model.tested) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (model.reachable) {
                        strings.providerTestResult(
                            strings.providerTestOk,
                            model.latencyMs.toInt(),
                        )
                    } else {
                        strings.providerTestResult(
                            strings.providerTestFailed,
                            model.latencyMs.toInt(),
                        )
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (model.reachable) {
                        NovaTheme.colors.textTertiary
                    } else {
                        NovaTheme.colors.danger
                    },
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        SmallAction(
            text = if (testing) strings.providerModelTesting else strings.providerModelTest,
            enabled = !testing,
            onClick = onTest,
        )
        Spacer(Modifier.width(6.dp))
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            Surface(
                onClick = onRemove,
                shape = CircleShape,
                color = NovaTheme.colors.inputBackground,
            ) {
                Box(
                    modifier = Modifier.size(26.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = NovaIcons.Close,
                        contentDescription = strings.providerModelRemove,
                        modifier = Modifier.size(11.dp),
                        tint = NovaTheme.colors.textTertiary,
                    )
                }
            }
        }
    }
}
