package me.bristermitten.devdenbot.discord

const val CLASH_OF_CODE_ROLE_ID = 831987774499454997
const val SHE_HER_ROLE_ID = 837576267922538516
const val HE_HIM_ROLE_ID = 837576282454622218
const val THEY_THEM_ROLE_ID = 837584481526874153
const val BUMP_NOTIFICATIONS_ROLE_ID = 838500233268691005

const val BOT_CONTRIBUTOR_ROLE_ID = 838733085947723786

const val MODERATOR_ROLE_ID = 821819572134608938
const val ADMIN_ROLE_ID = 821814446749646853
const val HELPFUL_ROLE_ID = 821815023223308300

/**
 * Roles that can be self assigned with the `role` command [me.bristermitten.devdenbot.commands.roles.RoleCommand]
 */
val SELF_ROLES = setOf(
    SHE_HER_ROLE_ID,
    HE_HIM_ROLE_ID,
    THEY_THEM_ROLE_ID,
    CLASH_OF_CODE_ROLE_ID,
    BUMP_NOTIFICATIONS_ROLE_ID
)

