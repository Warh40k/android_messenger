package study.nikita.chat.repository

import study.nikita.chat.db.MessageDao
import study.nikita.chat.db.MessageEntity
import study.nikita.chat.network.rest.Image
import study.nikita.chat.network.rest.Message
import study.nikita.chat.network.rest.MessageData
import study.nikita.chat.network.rest.Text
import javax.inject.Inject

class MessageRepository @Inject constructor(private val dao : MessageDao) {
    suspend fun deleteAllMessages() {
        dao.deleteAllMessages()
    }

    suspend fun getChatMessages(chatId: String) : List<Message> {
        val messageEntities = dao.getMessages(chatId)
        val messages : MutableList<Message> = mutableListOf()

        for (i : MessageEntity in messageEntities) {
            val message = Message(
                id = i.id,
                from = i.from,
                to = i.to,
                data = MessageData(
                    text = Text(text = i.text),
                    image = if (i.image != null) Image(link = i.image) else null
                ),
                time = i.time,
            )
            messages.add(message)
        }

        return messages.reversed()
    }

    suspend fun insertMessage(msg : Message) {
        val entity = MessageEntity(
            id = msg.id,
            from = msg.from,
            to = msg.to,
            text = msg.data?.text?.text ?: "",
            image = msg.data?.image?.link,
            time = msg.time
        )
        return dao.insertMessage(entity)
    }

    suspend fun insertAllMessages(messages : List<Message>) {
        val entities : MutableList<MessageEntity> = mutableListOf()
        for (msg : Message in messages) {
            val entity = MessageEntity(
                id = msg.id,
                from = msg.from,
                to = msg.to,
                text = msg.data?.text?.text ?: "",
                image = msg.data?.image?.link,
                time = msg.time
            )
            entities.add(entity)
        }

        return dao.insertAllMessages(entities)
    }
}