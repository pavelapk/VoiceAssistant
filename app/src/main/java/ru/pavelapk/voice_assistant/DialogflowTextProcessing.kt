package ru.pavelapk.voice_assistant

import android.content.Context
import android.util.Log
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2.*
import com.google.protobuf.Value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.lang.RuntimeException
import java.util.*

class DialogflowTextProcessing(context: Context, private val languageCode: String) {

    init {
        initDialogflowV2(context, R.raw.dialogflow_key, UUID.randomUUID().toString())
    }

    private var dialogflowSessionsClient: SessionsClient? = null
    private var dialogflowSessionName: SessionName? = null

    private fun initDialogflowV2(context: Context, credentialsRawRes: Int, sessionUuid: String) {
        try {
            val stream: InputStream = context.resources.openRawResource(credentialsRawRes)
            val credentials = GoogleCredentials.fromStream(stream)
            val projectId = (credentials as ServiceAccountCredentials).projectId
            val settingsBuilder = SessionsSettings.newBuilder()
            val sessionsSettings = settingsBuilder
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build()
            dialogflowSessionsClient = SessionsClient.create(sessionsSettings)
            dialogflowSessionName = SessionName.of(projectId, sessionUuid)
        } catch (t: Throwable) {
            throw t
        }
    }

    suspend fun process(input: String): Triple<Action, String, MutableMap<String, Value>?> {
        val textInput = TextInput.newBuilder().setText(input).setLanguageCode(languageCode)

        val queryInput: QueryInput = QueryInput.newBuilder().setText(textInput).build()

        val response = withContext(Dispatchers.IO) {
            dialogflowSessionsClient?.detectIntent(dialogflowSessionName, queryInput)
        } ?: return Triple(Action.ERROR, "Нет ответа", null)

        val queryResult = response.queryResult
        val action = Action.fromName(queryResult.action)
        return Triple(
            action, if (queryResult.fulfillmentMessagesCount > 0) {
                queryResult.getFulfillmentMessages(0).text.getText(0)
            } else {
                ""
            },
            queryResult.parameters.fieldsMap
        )
    }

    fun close() {
        dialogflowSessionsClient?.close()
    }
}