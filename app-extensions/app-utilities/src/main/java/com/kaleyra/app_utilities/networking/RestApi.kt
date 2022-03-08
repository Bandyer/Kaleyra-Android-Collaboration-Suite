/*
 * Copyright 2022 Kaleyra @ https://www.kaleyra.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kaleyra.app_utilities.networking

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.kaleyra.collaboration_suite_utils.ContextRetainer.Companion.context
import com.kaleyra.collaboration_suite_utils.logging.PriorityLogger
import com.kaleyra.app_configuration.model.Configuration
import com.kaleyra.app_configuration.model.PushProvider
import com.kaleyra.app_utilities.MultiDexApplication.Companion.okHttpClient
import com.kaleyra.app_utilities.networking.models.AccessToken
import com.kaleyra.app_utilities.networking.models.BandyerUsers
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager.getConfiguration
import com.kaleyra.app_utilities.storage.LoginManager
import com.kaleyra.collaboration_suite_networking.Environment
import com.kaleyra.collaboration_suite_networking.Region
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType.Application
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import okhttp3.OkHttpClient


/**
 * WARNING!!!
 * The networking package is used only to fetch the users, to make the demo app run out of the box,
 * with the least efforts.
 *
 *
 * MockedNetwork
 *
 * @author kristiyan
 */
class RestApi {

    data class RestConfiguration(
        override val userId: String,
        val apiKey: String,
        override val appId: String,
        override val environment: Environment,
        override val region: Region,
        override val httpStack: OkHttpClient
    ) : com.kaleyra.collaboration_suite_networking.Configuration {
        override val logger: PriorityLogger? = null
    }

    private var restConfiguration: RestConfiguration? = null

    private val scope =
        CoroutineScope(Dispatchers.IO) + CoroutineName("MockedNetwork") + SupervisorJob()

    private val configurationListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key != "configuration") return@OnSharedPreferenceChangeListener
            restConfiguration = getConfiguration(context).toRest()
        }

    init {
        restConfiguration = getConfiguration(context).toRest()
        context
            .getSharedPreferences(
                ConfigurationPrefsManager.CONFIGURATION_PREFS,
                Context.MODE_PRIVATE
            )
            .registerOnSharedPreferenceChangeListener(configurationListener)
    }

    private fun Configuration.toRest() =
        getConfiguration(context).takeIf { LoginManager.isUserLogged(context) }?.let {
            RestConfiguration(
                LoginManager.getLoggedUser(context),
                apiKey,
                appId,
                Environment.create(environment),
                Region.create(region),
                okHttpClient
            )
        }


    private val client by lazy {
        HttpClient(OkHttp) {
            engine {
                preconfigured = okHttpClient
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
        }
    }

    private var endpoint = restConfiguration?.endpoint() ?: ""
    private var apiKey = restConfiguration?.apiKey ?: ""
    private var userId = restConfiguration?.userId ?: ""
    private var appId = restConfiguration?.appId ?: ""

    private var configurationHeaders: HeadersBuilder.() -> Unit = {
        append("apiKey", apiKey)
    }

    suspend fun getAccessToken(): String {
        return try {
            val response: HttpResponse = client.post("$endpoint/rest/sdk/credentials") {
                headers(configurationHeaders)
                contentType(Application.Json)
                body = AccessToken.Request(userId)
            }
            response.receive<AccessToken.Response>().access_token
        } catch (t: Throwable) {
            Log.e("GetAccessToken", t.message, t)
            ""
        }
    }

    suspend fun listUsers(): List<String> {
        return try {
            val response: HttpResponse = client.get("$endpoint/rest/user/list") {
                headers(configurationHeaders)
                contentType(Application.Json)
            }
            response.receive<BandyerUsers>().user_id_list
        } catch (t: Throwable) {
            Log.e("GetListUsers", t.message, t)
            emptyList()
        }
    }

    fun registerDeviceForPushNotification(pushProvider: PushProvider, devicePushToken: String) {
        scope.launch {
            kotlin.runCatching {
                val response: HttpResponse =
                    client.post("$endpoint/mobile_push_notifications/rest/device") {
                        headers(configurationHeaders)
                        contentType(Application.Json)
                        body = DeviceRegistrationInfo(
                            userId,
                            appId,
                            devicePushToken,
                            pushProvider.name
                        )
                    }

                if (response.status != HttpStatusCode.OK) {
                    Log.e("PushNotification", "Failed to register device for push notifications!")
                }
            }.onFailure { Log.e("PushNotification", it.message, it) }
        }
    }

    fun unregisterDeviceForPushNotification(devicePushToken: String) {
        scope.launch {
            kotlin.runCatching {
                val response: HttpResponse =
                    client.delete("$endpoint/mobile_push_notifications/rest/device/$userId/$appId/$devicePushToken") {
                        headers(configurationHeaders)
                        contentType(Application.Json)
                    }
                if (response.status != HttpStatusCode.OK) {
                    Log.e("PushNotification", "Failed to unregister device for push notifications!")
                }
            }.onFailure { Log.e("PushNotification", it.message, it) }
        }
    }

    fun cancel() {
        scope.coroutineContext.cancelChildren()
    }
}