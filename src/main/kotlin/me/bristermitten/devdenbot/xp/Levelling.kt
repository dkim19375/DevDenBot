package me.bristermitten.devdenbot.xp

import me.bristermitten.devdenbot.discord.BOT_COMMANDS_CHANNEL_ID
import me.bristermitten.devdenbot.extensions.await
import mu.KotlinLogging
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel

private val log = KotlinLogging.logger("Levelling")

suspend fun processLevelUp(user: Member, level: Int) {
    val channel = user.jda.getGuildChannelById(BOT_COMMANDS_CHANNEL_ID) as? TextChannel ?: return
    channel.sendMessage("${user.asMention}, you levelled up to level **$level**!").await()
    val tier = tierOf(level)
    val tierRole = tierRole(user.jda, tier)
    if (tierRole !in user.roles) {
        if (tier - 1 != 0) { //We can't remove @everyone
            val oldTier = tierRole(user.jda, tier - 1)
            user.guild.removeRoleFromMember(user, oldTier).await()
        }
        user.guild.addRoleToMember(user, tierRole).await()
    }
    log.trace { "Processed level up for ${user.user.name}" }
}
