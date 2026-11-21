package com.nova.app.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.nova.app.R
import com.nova.app.ui.settings.AppLanguage
import com.nova.app.ui.settings.AppThemeMode

class Strings(private val context: Context) {

    private fun get(id: Int): String = context.getString(id)

    private fun format(id: Int, vararg args: Any): String = context.getString(id, *args)

    val languageSystem: String get() = get(R.string.language_system)
    val languageChinese: String get() = get(R.string.language_chinese)
    val languageEnglish: String get() = get(R.string.language_english)

    val languageRestartTitle: String get() = get(R.string.language_restart_title)
    val languageRestartMessage: String get() = get(R.string.language_restart_message)
    val languageRestartNow: String get() = get(R.string.language_restart_confirm)
    val languageRestartLater: String get() = get(R.string.language_restart_later)

    val chatExpand: String get() = get(R.string.chat_expand)
    val chatExpandTitle: String get() = get(R.string.chat_expand_title)

    val themeSystem: String get() = get(R.string.theme_system)
    val themeLight: String get() = get(R.string.theme_light)
    val themeDark: String get() = get(R.string.theme_dark)

    fun languageLabel(language: AppLanguage): String = get(language.labelRes)

    fun themeLabel(mode: AppThemeMode): String = get(mode.labelRes)

    val confirm: String get() = get(R.string.action_confirm)
    val cancel: String get() = get(R.string.action_cancel)
    val delete: String get() = get(R.string.action_delete)
    val back: String get() = get(R.string.action_back)
    val send: String get() = get(R.string.action_send)
    val clear: String get() = get(R.string.action_clear)
    val rename: String get() = get(R.string.action_rename)
    val pin: String get() = get(R.string.action_pin)
    val unpin: String get() = get(R.string.action_unpin)
    val multiSelect: String get() = get(R.string.action_multi_select)
    val grant: String get() = get(R.string.action_grant)
    val notNow: String get() = get(R.string.action_not_now)

    val settingsTitle: String get() = get(R.string.settings_title)
    val providersTitle: String get() = get(R.string.providers_title)
    val providersEmpty: String get() = get(R.string.providers_empty)
    val providersEmptyHint: String get() = get(R.string.providers_empty_hint)
    val providersAdd: String get() = get(R.string.providers_add)
    val providerEditTitle: String get() = get(R.string.provider_edit_title)
    val providerName: String get() = get(R.string.provider_name)
    val providerNamePlaceholder: String get() = get(R.string.provider_name_placeholder)
    val providerApiKey: String get() = get(R.string.provider_api_key)
    val providerApiKeyPlaceholder: String get() = get(R.string.provider_api_key_placeholder)
    val providerEndpoint: String get() = get(R.string.provider_endpoint)
    val providerEndpointPlaceholder: String get() = get(R.string.provider_endpoint_placeholder)
    val providerLocal: String get() = get(R.string.provider_local)
    val providerLocalHint: String get() = get(R.string.provider_local_hint)
    val providerModels: String get() = get(R.string.provider_models)
    val providerModelsFetch: String get() = get(R.string.provider_models_fetch)
    val providerModelsFetching: String get() = get(R.string.provider_models_fetching)
    val providerModelsManual: String get() = get(R.string.provider_models_manual)
    val providerModelsEmpty: String get() = get(R.string.provider_models_empty)
    val providerModelPlaceholder: String get() = get(R.string.provider_model_placeholder)
    val providerModelTest: String get() = get(R.string.provider_model_test)
    val providerModelTesting: String get() = get(R.string.provider_model_testing)
    val providerModelRemove: String get() = get(R.string.provider_model_remove)
    val providerModelsFailed: String get() = get(R.string.provider_models_failed)
    val providerDelete: String get() = get(R.string.provider_delete)
    val providerTestOk: String get() = get(R.string.provider_test_ok)
    val providerTestFailed: String get() = get(R.string.provider_test_failed)
    val errorTitle: String get() = get(R.string.error_title)
    val errorClose: String get() = get(R.string.error_close)
    fun providerModelsOk(count: Int): String = format(R.string.provider_models_ok, count)
    val modelsPick: String get() = get(R.string.provider_models_pick)
    val modelsAll: String get() = get(R.string.provider_models_all)
    val modelsNone: String get() = get(R.string.provider_models_none)
    val modelsImport: String get() = get(R.string.provider_models_import)
    val modelsLoading: String get() = get(R.string.provider_models_loading)
    fun modelsSelected(count: Int): String = format(R.string.provider_models_selected, count)
    fun modelsCount(count: Int): String = format(R.string.provider_models_count, count)
    fun providerTestResult(state: String, ms: Int): String = format(R.string.provider_test_result, state, ms)
    val sectionAccount: String get() = get(R.string.settings_section_account)
    val sectionApp: String get() = get(R.string.settings_section_app)
    val sectionAbout: String get() = get(R.string.settings_section_about)
    val assistantCapabilities: String get() = get(R.string.settings_assistant_capabilities)
    val promptSection: String get() = get(R.string.assistant_prompt_section)
    val promptEnable: String get() = get(R.string.assistant_prompt_enable)
    val promptHint: String get() = get(R.string.assistant_prompt_hint)
    val promptPlaceholder: String get() = get(R.string.assistant_prompt_placeholder)
    val memorySection: String get() = get(R.string.assistant_memory_section)
    val memoryEnable: String get() = get(R.string.assistant_memory_enable)
    val memoryHint: String get() = get(R.string.assistant_memory_hint)
    val memoryPlaceholder: String get() = get(R.string.assistant_memory_placeholder)
    val memoryEmpty: String get() = get(R.string.assistant_memory_empty)
    val memoryDelete: String get() = get(R.string.assistant_memory_delete)
    val memoryAdd: String get() = get(R.string.assistant_memory_add)
    fun charCount(count: Int): String = format(R.string.assistant_char_count, count)

    val profileDefaultNickname: String get() = get(R.string.profile_default_nickname)
    val nicknameTitle: String get() = get(R.string.profile_nickname_title)
    val nicknamePlaceholder: String get() = get(R.string.profile_nickname_placeholder)
    val avatarChange: String get() = get(R.string.profile_avatar_change)
    val avatarRemove: String get() = get(R.string.profile_avatar_remove)
    val avatarHint: String get() = get(R.string.profile_avatar_hint)
    val profileSaved: String get() = get(R.string.profile_saved)

    val promptPreviewEmpty: String get() = get(R.string.assistant_prompt_preview_empty)
    val promptEditTitle: String get() = get(R.string.assistant_prompt_edit_title)
    val memoryAddAction: String get() = get(R.string.assistant_memory_add_action)

    val emptyConversations: String get() = get(R.string.empty_conversations)
    val emptyConversationsHint: String get() = get(R.string.empty_conversations_hint)
    val emptySearch: String get() = get(R.string.empty_search)
    val promptTapHint: String get() = get(R.string.assistant_prompt_tap_hint)

    val dataManagement: String get() = get(R.string.settings_data_management)
    val language: String get() = get(R.string.settings_language)
    val appearance: String get() = get(R.string.settings_appearance)
    val fontSize: String get() = get(R.string.settings_font_size)
    val personalization: String get() = get(R.string.settings_personalization)
    val checkUpdate: String get() = get(R.string.settings_check_update)
    val runtimeLogs: String get() = get(R.string.settings_runtime_logs)
    val disclaimer: String get() = get(R.string.settings_disclaimer)

    val exportAll: String get() = get(R.string.data_export_all)
    val deleteAll: String get() = get(R.string.data_delete_all)
    val upToDate: String get() = get(R.string.toast_up_to_date)
    fun exported(count: Int): String = format(R.string.toast_exported, count)
    val cleared: String get() = get(R.string.toast_cleared)

    val logsTitle: String get() = get(R.string.logs_title)
    val logsClear: String get() = get(R.string.logs_clear)
    val logsTabEvents: String get() = get(R.string.logs_tab_events)
    val logsTabCrashes: String get() = get(R.string.logs_tab_crashes)
    val logsEmptyEvents: String get() = get(R.string.logs_empty_events)
    val logsEmptyCrashes: String get() = get(R.string.logs_empty_crashes)

    val autoCollapse: String get() = get(R.string.personalize_auto_collapse)
    val autoCollapseHint: String get() = get(R.string.personalize_auto_collapse_hint)

    val fontSizePreview: String get() = get(R.string.font_size_preview)
    val fontSizeDescription: String get() = get(R.string.font_size_description)
    val fontSizeDescriptionExtra: String get() = get(R.string.font_size_description_extra)
    val followSystem: String get() = get(R.string.font_size_follow_system)
    val sizeSmall: String get() = get(R.string.font_size_small)
    val sizeStandard: String get() = get(R.string.font_size_standard)
    val sizeLarge: String get() = get(R.string.font_size_large)

    val inputPlaceholder: String get() = get(R.string.chat_input_placeholder)
    val addAttachment: String get() = get(R.string.chat_add_attachment)

    val attachCamera: String get() = get(R.string.attach_camera)
    val attachGallery: String get() = get(R.string.attach_gallery)
    val attachFile: String get() = get(R.string.attach_file)

    val searchPlaceholder: String get() = get(R.string.search_placeholder)
    val searchEmptyHint: String get() = get(R.string.search_empty_hint)
    fun searchNoResult(query: String): String = format(R.string.search_no_result, query)

    val drawerSearchPlaceholder: String get() = get(R.string.drawer_search_placeholder)
    val selectConversations: String get() = get(R.string.drawer_select_conversations)
    val exitSelection: String get() = get(R.string.drawer_exit_selection)
    val selectAll: String get() = get(R.string.drawer_select_all)
    fun pinnedCount(count: Int): String = format(R.string.drawer_pinned_count, count)
    val collapsePinned: String get() = get(R.string.drawer_collapse_pinned)
    val expandPinned: String get() = get(R.string.drawer_expand_pinned)
    val renameTitle: String get() = get(R.string.drawer_rename_title)
    fun deleteTitle(count: Int): String = format(R.string.drawer_delete_title, count)
    val deleteMessage: String get() = get(R.string.drawer_delete_message)
    val newChat: String get() = get(R.string.drawer_new_chat)
    val openMenu: String get() = get(R.string.drawer_open_menu)
    val newConversation: String get() = get(R.string.drawer_new_conversation)

    val groupPinned: String get() = get(R.string.group_pinned)
    val groupToday: String get() = get(R.string.group_today)
    val groupYesterday: String get() = get(R.string.group_yesterday)
    val groupLastWeek: String get() = get(R.string.group_last_week)
    val groupEarlier: String get() = get(R.string.group_earlier)

    val permissionCameraTitle: String get() = get(R.string.permission_camera_title)
    val permissionCameraMessage: String get() = get(R.string.permission_camera_message)
    val permissionMediaTitle: String get() = get(R.string.permission_media_title)
    val permissionMediaMessage: String get() = get(R.string.permission_media_message)

    val greetingMorningTitle: String get() = get(R.string.greeting_morning_title)
    val greetingMorningHint: String get() = get(R.string.greeting_morning_hint)
    val greetingForenoonTitle: String get() = get(R.string.greeting_forenoon_title)
    val greetingForenoonHint: String get() = get(R.string.greeting_forenoon_hint)
    val greetingNoonTitle: String get() = get(R.string.greeting_noon_title)
    val greetingNoonHint: String get() = get(R.string.greeting_noon_hint)
    val greetingAfternoonTitle: String get() = get(R.string.greeting_afternoon_title)
    val greetingAfternoonHint: String get() = get(R.string.greeting_afternoon_hint)
    val greetingEveningTitle: String get() = get(R.string.greeting_evening_title)
    val greetingEveningHint: String get() = get(R.string.greeting_evening_hint)
    val greetingNightTitle: String get() = get(R.string.greeting_night_title)
    val greetingNightHint: String get() = get(R.string.greeting_night_hint)

}

val LocalStrings = staticCompositionLocalOf<Strings> { error("LocalStrings not provided") }

object NovaStrings {

    val strings: Strings
        @Composable
        @ReadOnlyComposable
        get() = LocalStrings.current

    @Composable
    @ReadOnlyComposable
    fun current(): Strings = LocalContext.current.let { Strings(it) }
}
