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

package com.kaleyra.demo_collaboration_suite

import android.content.Context
import com.kaleyra.app_utilities.MultiDexApplication
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager
import com.kaleyra.app_utilities.storage.LoginManager
import com.kaleyra.collaboration_suite.Collaboration
import com.kaleyra.collaboration_suite.utils.logger.INPUTS
import com.kaleyra.collaboration_suite.utils.logger.PHONE_BOX
import com.kaleyra.collaboration_suite.utils.logger.PHONE_CALL
import com.kaleyra.collaboration_suite.utils.logger.STREAMS
import com.kaleyra.collaboration_suite_networking.Environment
import com.kaleyra.collaboration_suite_networking.Region
import com.kaleyra.collaboration_suite_utils.logging.BaseLogger
import com.kaleyra.collaboration_suite_utils.logging.androidPrioryLogger
import java.util.Date

class MyApplication : MultiDexApplication() {

    override fun create() {
        // init
    }

}

fun Context.configuration(): Collaboration.Configuration? {
    val appConfiguration = ConfigurationPrefsManager.getConfiguration(this)
    if (!LoginManager.isUserLogged(this)) return null
    return Collaboration.Configuration(
        LoginManager.getLoggedUser(this),
        appConfiguration.appId,
        Environment.create(appConfiguration.environment),
        Region.create(appConfiguration.region),
        httpStack = MultiDexApplication.okHttpClient,
        logger = androidPrioryLogger(BaseLogger.ERROR, PHONE_CALL or PHONE_BOX or STREAMS or INPUTS)
    )
}

suspend fun requestToken(date: Date = Date()) = MultiDexApplication.restApi.getAccessToken()