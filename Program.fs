﻿open DSharpPlus
open DevDenBot.Configuration
open DevDenBot.Formatting
open System.Threading.Tasks
open DevDenBot.HasteClient
open Hopac
open FSharp.Control.Tasks

let welcomeChannelId = 821743171942744114UL
let ddServerId = 821743100203368458UL
let pasteEmojiId = 822217736012693575UL

let discordConfig =
    let mainConfig = loadConfig "config.json"
    let config = DiscordConfiguration()
    config.set_Token (getToken mainConfig)
    config.set_TokenType TokenType.Bot
    config.set_Intents DiscordIntents.All
    config

let client = new DiscordClient(discordConfig)

let onReady (client: DiscordClient) _ =
    task {
        let user = client.CurrentUser in printfn
                                             $"Successfully logged in as %s{userAndDiscriminator user} (%d{user.Id})"
    }


let doJoinMessage (client: DiscordClient) (event: EventArgs.GuildMemberAddEventArgs) =
    task {
        let! ddServer = client.GetGuildAsync ddServerId |> Async.AwaitTask
        let welcomeChannel = ddServer.GetChannel welcomeChannelId

        do!
            welcomeChannel.SendMessageAsync
                $"Welcome %s{event.Member.Mention} to the Developer's Den! There are now %d{event.Guild.MemberCount} users."
            |> Async.AwaitTask
            |> Async.Ignore
    }



let processPasteReaction (_: DiscordClient) (event: EventArgs.MessageReactionAddEventArgs) =
    task {
        if event.Emoji.Id <> pasteEmojiId then
            return ()
        else
            let! reactionMember = event.Guild.GetMemberAsync(event.User.Id)

            let permissions =
                reactionMember.PermissionsIn(event.Channel)

            if permissions &&& Permissions.ManageMessages = Permissions.None then
                return ()
            else
                let content = trimCodeBlocks event.Message.Content
                let! paste = createPaste content |> Job.toAsync
                do! event.Message.DeleteAsync()

                let pasteMessage =
                    $"""
{paste} {event.Message.Author.Mention}, an admin has converted your message to a paste to keep the channels clean.
Please use https://paste.bristermitten.me when sharing large blocks of code.
                    """

                let! _ = event.Channel.SendMessageAsync pasteMessage
                return ()
    }

let add elem f =
    elem (fun client e -> f client e :> Task)

let mainTask =
    task {
        add client.add_Ready onReady
        add client.add_GuildMemberAdded doJoinMessage
        add client.add_MessageReactionAdded processPasteReaction

        do! client.ConnectAsync()

        do! Task.Delay(-1)
    }



[<EntryPoint>]
let main _ =
    Async.AwaitTask mainTask |> Async.RunSynchronously
    0 // return an integer exit code
