package data

data class NotificationQueue (
        val id: Int,
        var userId: Int,
        var content: String,
        var read: Boolean
)