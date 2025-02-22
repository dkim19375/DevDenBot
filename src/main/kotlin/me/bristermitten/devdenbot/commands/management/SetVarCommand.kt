package me.bristermitten.devdenbot.commands.management

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.StatsUser
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.extensions.arguments
import me.bristermitten.devdenbot.extensions.commands.awaitReply
import me.bristermitten.devdenbot.extensions.commands.firstMentionedUser
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.serialization.DDBConfig
import java.math.BigInteger
import javax.inject.Inject
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties


/**
 * @author AlexL
 */
@Used
class SetVarCommand @Inject constructor(
    val config: DDBConfig,
) : DevDenCommand(
    name = "set",
    help = "Set the data of a user",
    ownerCommand = true,
    arguments = "<user> <data> <amount>"
) {

    override suspend fun CommandEvent.execute() {
        val arguments = arguments()
        val args = arguments.args

        arguments.validateArgLength(3) {
            awaitReply("Not enough arguments.")
            return
        }

        val targetUser = firstMentionedUser() ?: event.message.author

        val field = args[1].content

        @Suppress("UNCHECKED_CAST")
        val stat = StatsUser::class.memberProperties.firstOrNull {
            it.name.equals(field, true)
        } as KProperty1<StatsUser, Any>?

        if (stat == null) {
            awaitReply("I don't recognise $field")
            return
        }
        if (stat !is KMutableProperty1) {
            awaitReply("I can't change the value of ${stat.name}")
            return
        }

        val amount = args[2]
        amount.validate({ it.toBigIntegerOrNull() != null }) {
            awaitReply("Invalid amount - must be an integer")
            return
        }

        val value = amount.content.parseTo(stat.returnType)

        val statsUser = StatsUsers.get(targetUser.idLong)
        stat.set(statsUser, value)
        awaitReply("Successfully set value of `${stat.name}` for ${targetUser.asMention} to `$value`")
    }

    private fun String.parseTo(type: KType) = when (type) {
        String::class.createType() -> this
        Int::class.createType() -> toInt()
        Long::class.createType() -> toLong()
        BigInteger::class.createType() -> toBigInteger()
        else -> throw IllegalArgumentException("Cannot parse $type")
    }
}
