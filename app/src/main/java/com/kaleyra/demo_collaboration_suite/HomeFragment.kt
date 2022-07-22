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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textview.MaterialTextView
import com.kaleyra.app_utilities.storage.LoginManager
import com.kaleyra.collaboration_suite.BuddyUser
import com.kaleyra.collaboration_suite.chatbox.ChatBox
import com.kaleyra.collaboration_suite.phonebox.Call
import com.kaleyra.collaboration_suite.phonebox.PhoneBox
import com.kaleyra.collaboration_suite_core_ui.ChatBoxUI
import com.kaleyra.collaboration_suite_core_ui.CollaborationUI
import com.kaleyra.collaboration_suite_core_ui.CollaborationUI.chatBox
import com.kaleyra.collaboration_suite_core_ui.CollaborationUI.phoneBox
import com.kaleyra.collaboration_suite_core_ui.PhoneBoxUI
import com.kaleyra.demo_collaboration_suite.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    private val args: HomeFragmentArgs by navArgs()

    private val model = CollaborationModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            val userId = args.userId
            loggedUser.text = resources.getString(R.string.logged_as, userId)
            lifecycleScope.launch {

                CollaborationUIService.configure(requireContext())

                collaboration = model

                lifecycleOwner = requireActivity()

                val intent = requireActivity().intent
                if (Intent.ACTION_VIEW == intent.action) {
                    phoneBox.join(intent.data.toString())
                    intent.action = null
                }
            }

            dialButton.setOnClickListener {
                val users = otherUsers.editText!!.text.split(",")
                if (users.any { it.isEmpty() }) {
                    Toast.makeText(requireContext(), "Users not valid", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                phoneBox.call(users.map { BuddyUser(it.trim()) }) {
                    preferredType = Call.PreferredType(audio = Call.Audio.Enabled, video = Call.Video.Enabled)
                    // recordingType = Call.Recording.Type.OnConnect
                }
            }

            chatButton.setOnClickListener {
                val user = otherUsers.editText!!.text.split(",").firstOrNull() ?: return@setOnClickListener
                if (user.isEmpty()) {
                    Toast.makeText(requireContext(), "Users not valid", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                chatBox.chat(requireActivity(), BuddyUser(user.trim()))
            }

            logoutButton.setOnClickListener {
                CollaborationUI.dispose(clearSavedData = true)
                LoginManager.logout(requireContext())
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

class CollaborationModel : ViewModel() {
    val phoneBox: PhoneBoxUI by lazy { CollaborationUI.phoneBox }
    val chatBox: ChatBoxUI by lazy { CollaborationUI.chatBox }
}

@BindingAdapter("state")
fun setBoxState(view: MaterialTextView, state: String?) {
    state ?: return
    val color = when (state) {
        PhoneBox.State.Disconnected.toString(), ChatBox.State.Disconnected.toString()                                                  -> ContextCompat.getColor(view.context, R.color.stateDisconnected)
        PhoneBox.State.Connected.toString(), ChatBox.State.Connected.toString()                                                        -> ContextCompat.getColor(view.context, R.color.stateConnected)
        PhoneBox.State.Connecting.toString(), ChatBox.State.Connecting.toString(), ChatBox.State.Initialized.toString()                -> ContextCompat.getColor(view.context, R.color.stateConnecting)
        PhoneBox.State.Disconnecting.toString(), ChatBox.State.Disconnecting.toString(), ChatBox.State.Disconnected.Unknown.toString() -> ContextCompat.getColor(view.context, R.color.stateDisconnecting)
        else                                                                                                                           -> ContextCompat.getColor(view.context, R.color.stateError)
    }
    view.setTextColor(color)
    view.post { view.compoundDrawables[0]?.setTint(color) }
}