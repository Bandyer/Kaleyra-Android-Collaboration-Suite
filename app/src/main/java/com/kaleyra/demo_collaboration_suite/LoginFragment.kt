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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kaleyra.app_configuration.activities.ConfigurationActivity
import com.kaleyra.app_utilities.storage.ConfigurationPrefsManager
import com.kaleyra.app_utilities.storage.LoginManager
import com.kaleyra.demo_collaboration_suite.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        var userId = if (LoginManager.isUserLogged(requireContext())) LoginManager.getLoggedUser(requireContext()) else null
        userId?.also { findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment(it)) }

        binding.procedeButton.setOnClickListener {
            userId = binding.sessionUser.editText?.text?.toString()
            if (userId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "User not valid", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            LoginManager.login(requireContext(), userId.toString())
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment(userId!!))
        }

        binding.changeConfigurationButton.setOnClickListener {
            ConfigurationActivity.show(
                requireContext(),
                currentConfiguration = ConfigurationPrefsManager.getConfiguration(requireContext()),
                qrConfigurationActivity = GlassesConfigurationActivity::class.java
            )
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}