package com.kaleyra.demo_collaboration_suite

import android.content.Context
import android.net.Uri
import com.kaleyra.collaboration_suite.Collaboration
import com.kaleyra.collaboration_suite_core_ui.CallUI
import com.kaleyra.collaboration_suite_core_ui.ChatUI
import com.kaleyra.collaboration_suite_core_ui.CollaborationService
import com.kaleyra.collaboration_suite_core_ui.CollaborationUI
import com.kaleyra.collaboration_suite_core_ui.model.UsersDescription
import com.kaleyra.collaboration_suite_glass_ui.utils.extensions.configureGlassUI
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CollaborationUIService : CollaborationService() {

    companion object {

        suspend fun configure(context: Context) {
            if (!CollaborationUI.isConfigured) {
                val configuration = context.configuration() ?: return
                CollaborationUI.configureGlassUI(Collaboration.Credentials(requestToken(), onExpire = ::requestToken), configuration)
                CollaborationUI.usersDescription = UsersDescription(
                    name = {
                        it.joinToString { userId -> "Unknown $userId" }
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
                CollaborationUI.phoneBox.call.onEach {
                    it.actions.value = CallUI.Action.all
                }.launchIn(MainScope())
                CollaborationUI.chatBox.chats.onEach { chats ->
                    chats.forEach { it.actions.value = ChatUI.Action.all }
                }.launchIn(MainScope())
            }
            CollaborationUI.connect()
        }

    }

    override suspend fun onRequestNewCollaborationConfigure(): Unit = let { configure(applicationContext) }
}