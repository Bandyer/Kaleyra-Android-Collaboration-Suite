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
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kaleyra.app_utilities.storage.LoginManager
import com.kaleyra.collaboration_suite.BuddyUser
import com.kaleyra.collaboration_suite.Collaboration
import com.kaleyra.collaboration_suite.phonebox.Call
import com.kaleyra.collaboration_suite.phonebox.PhoneBox
import com.kaleyra.collaboration_suite_core_ui.CollaborationUI
import com.kaleyra.collaboration_suite_core_ui.PhoneBoxUI
import com.kaleyra.collaboration_suite_core_ui.model.UsersDescription
import com.kaleyra.collaboration_suite_extension_audio.extensions.CollaborationAudioExtensions.enableAudioRouting
import com.kaleyra.collaboration_suite_glass_ui.utils.extensions.setUpWithGlassUI
import com.kaleyra.demo_collaboration_suite.databinding.FragmentCallBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class CallFragment : Fragment() {

    private var _binding: FragmentCallBinding? = null
    private val binding: FragmentCallBinding get() = _binding!!

    private val args: CallFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCallBinding.inflate(inflater, container, false).apply {
            val userId = args.userId
            var phoneBox: PhoneBoxUI? = null

            dialButton.isEnabled = false
            loggedUser.text = resources.getString(R.string.logged_as, userId)

            lifecycleScope.launch {

                val configuration = requireContext().configuration() ?: return@launch

                CollaborationUI.setUpWithGlassUI(Collaboration.Credentials(requestToken(), onExpire = ::requestToken), configuration)
                CollaborationUI.usersDescription = UsersDescription(
                    name = {
                        it.joinToString { userId ->
                            when (userId) {
                                "user1" -> "Mario Rossi"
                                else    -> "Unknown guy"
                            }
                        }
                    },
                    image = {
                        if (it.count() == 1) {
                            when (it.first()) {
                                "user1" -> Uri.parse("https://randomuser.me/api/portraits/men/86.jpg")
                                else    -> Uri.EMPTY
                            }
                        } else Uri.EMPTY
                    }
                )

                phoneBox = CollaborationUI.phoneBox
                phoneBox!!.enableAudioRouting()
                phoneBox!!.connect()

                val intent = requireActivity().intent
                if (Intent.ACTION_VIEW == intent.action)
                    phoneBox!!.join(intent.data.toString())

                phoneBox!!.state
                    .takeWhile { it !is PhoneBox.State.Connected }
                    .onCompletion { dialButton.isEnabled = true }
                    .launchIn(this)
            }

            dialButton.setOnClickListener {
                val users = otherUsers.editText!!.text.split(",")
                if (users.any { it.isEmpty() }) {
                    Toast.makeText(requireContext(), "Users not valid", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                phoneBox?.call(users.map { BuddyUser(it.trim()) }) {
                    preferredType =
                        Call.PreferredType(audio = Call.Audio.Enabled, video = Call.Video.Enabled)
                }
            }

            logoutButton.setOnClickListener {
                CollaborationUI.dispose()
                LoginManager.logout(requireContext())
                findNavController().navigate(CallFragmentDirections.actionCallFragmentToLoginFragment())
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}