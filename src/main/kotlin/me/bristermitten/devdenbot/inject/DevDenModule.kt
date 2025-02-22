package me.bristermitten.devdenbot.inject

import com.google.inject.Singleton
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import dev.misfitlabs.kotlinguice4.KotlinModule
import me.bristermitten.devdenbot.serialization.DDBConfig
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.SelfUser

/**
 * @author Alexander Wood (BristerMitten)
 */
class DevDenModule(private val DDBConfig: DDBConfig) : KotlinModule() {

    override fun configure() {
        bind<JDA>().toProvider<JDAProvider>().`in`<Singleton>()
        bind<EventWaiter>().asEagerSingleton()
        bind<SelfUser>().toProvider<SelfUserProvider>()
        bind<DDBConfig>().toInstance(DDBConfig)
    }
}
