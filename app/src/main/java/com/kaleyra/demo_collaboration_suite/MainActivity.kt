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

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kaleyra.app_configuration.activities.ConfigurationActivity
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager
import com.kaleyra.collaboration_suite_core_ui.CollaborationUI
import com.kaleyra.demo_collaboration_suite.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val configuration = ConfigurationPrefsManager.getConfiguration(this)
        if (configuration.isMockConfiguration()) return
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        val configuration = ConfigurationPrefsManager.getConfiguration(this)
        if (!configuration.isMockConfiguration()) return
        ConfigurationActivity.showNew(
            this,
            currentConfiguration = ConfigurationPrefsManager.getConfiguration(this),
            qrConfigurationActivity = GlassesConfigurationActivity::class.java
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (Intent.ACTION_VIEW != intent.action) return
        CollaborationUI.phoneBox.join(intent.data.toString())
    }
}

