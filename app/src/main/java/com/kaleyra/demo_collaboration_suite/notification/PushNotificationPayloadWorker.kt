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

package com.kaleyra.demo_collaboration_suite.notification

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager
import com.kaleyra.collaboration_suite.Collaboration
import com.kaleyra.collaboration_suite_core_ui.CollaborationUI
import com.kaleyra.collaboration_suite_glass_ui.utils.extensions.setUpWithGlassUI
import com.kaleyra.demo_collaboration_suite.configuration
import com.kaleyra.demo_collaboration_suite.requestToken
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Sample implementation of a worker object used to manage the push notification payload.
 * Using worker interface ensures that the payload parsing and process will be executed even if
 * the application is killed by the system.
 */
class PushNotificationPayloadWorker(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        try {
            val payload = inputData.getString("payload") ?: return Result.failure()
            Log.d(TAG, "Received payload\n$payload\nready to be processed.")

            MainScope().launch {
                val configuration = context.configuration() ?: return@launch
                CollaborationUI.setUpWithGlassUI(Collaboration.Credentials(requestToken(), onExpire = ::requestToken), configuration)
                CollaborationUI.phoneBox.connect()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            return Result.failure()
        }
        return Result.success()
    }

    companion object {
        private val TAG = PushNotificationPayloadWorker::class.java.simpleName
    }
}
