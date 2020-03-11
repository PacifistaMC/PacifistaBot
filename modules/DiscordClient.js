const config = require('../.env.json');
const Discord = require('discord.js');
const Log = require('./Log');

class DiscordClient {
    constructor() {
        console.log("Connection du bot Discord...");
        this.client = new Discord.Client();
        this.client.login(config.discordClient.token);
        this.client.on('ready', function() {
            console.log("Bot discord: PacifistaBot connecté !");
        });
        this.client.on('message', this.userMessage);
        this.client.on('guildMemberAdd', this.newGuildUser);
        this.client.on('guildMemberRemove', this.removeGuildUser);
        this.client.on('guildBanAdd', this.newBan);
        this.client.on('guildBanRemove', this.removeBan);
    }

    userMessage(message) {
        if (message.channel.type !== "text") return;
        const user = message.author;
        let text = message.content;
        const channelName = message.channel.name;
        if (user.bot) return;
        Log.log(user, text, channelName);
        text = text.replace(/\s\s+/g, ' ');
        text = text.toLowerCase();
        let args = text.split(' ');
        if (args[0].charAt(0) === config.defaults.prefix) {
            let cmd = args[0].substr(1);
            args.shift();
            switch (cmd) {
                case 'ip':
                    message.guild.channels.cache.get(message.channel.id).send(new Discord.MessageEmbed()
                        .setTitle(":bow_and_arrow: Pacifista Minecraft :bow_and_arrow:")
                        .setColor("BLUE")
                        .setDescription("Serveur Minecraft survie.")
                        .addField("Site web", "https://pacifista.fr", true)
                        .addField("IP de connection", "play.pacifista.fr", true)
                        .setFooter("Pacifista Minecraft")
                    );
                    break;
            }
        }
    }

    newGuildUser(guildMember) {
        const message = guildMember.user.tag + " vient de rejoindre le discord.";
        const dateNow = new Date();
        const memberAccountCreatedAt = guildMember.user.createdAt;
        let messageEmbed = new Discord.MessageEmbed()
            .setTitle("Nouveau membre !")
            .setAuthor(guildMember.user.tag, guildMember.user.avatarURL())
            .setColor("GREEN")
            .setDescription(message)
            .setThumbnail(guildMember.user.avatarURL());
        Log.logSystem(message);
        guildMember.guild.channels.cache.get(config.defaults.channelsId.bienvenue).send(messageEmbed);
        messageEmbed.addField("Création du compte:", getDate(memberAccountCreatedAt));
        if (dateNow.getTime() - memberAccountCreatedAt.getTime() <= 1800) {
            messageEmbed.addField(":warning: Attention :warning:", "Compte créé il y a moins de 30 minutes !");
            messageEmbed.setColor("GOLD");
            if (dateNow.getTime() - memberAccountCreatedAt.getTime() <= 600)
                guildMember.guild.channels.cache.get(config.defaults.channelsId.log).send("<@&" + config.defaults.groupsId.Admin + "> :warning: Compte créé il y a moins de 10 minutes ! " + guildMember.user.tag);
        }
        guildMember.guild.channels.cache.get(config.defaults.channelsId.log).send(messageEmbed);
        guildMember.roles.add(config.defaults.groupsId.Joueur).catch(console.error);
    }

    removeGuildUser(guildMember) {
        const message = guildMember.user.tag + " vient de quitter le discord.";
        let messageEmbed = new Discord.MessageEmbed()
            .setAuthor(guildMember.user.tag, guildMember.user.avatarURL())
            .setColor("RED")
            .setDescription("A quitté le discord")
            .setThumbnail(guildMember.user.avatarURL());
        Log.logSystem(message);
        guildMember.guild.channels.cache.get(config.defaults.channelsId.log).send(messageEmbed);
    }

    newBan(guild, user) {
        const message = user.tag + " est banni du discord."
        let messageEmbed = new Discord.MessageEmbed()
            .setColor("DARK_BLUE")
            .setAuthor(user.tag, user.avatarURL())
            .setThumbnail(user.avatarURL())
            .setDescription("Est banni du discord");
        Log.logSystem(message);
        guild.channels.cache.get(config.defaults.channelsId.log).send(messageEmbed);
    }

    removeBan(guild, user) {
        const message = user.tag + " n'est plus banni du discord."
        let messageEmbed = new Discord.MessageEmbed()
            .setColor("BLUE")
            .setAuthor(user.tag, user.avatarURL())
            .setThumbnail(user.avatarURL())
            .setDescription("N'est plus banni du discord");
        Log.logSystem(message);
        guild.channels.cache.get(config.defaults.channelsId.log).send(messageEmbed);
    }
}

function getDate(creationDate) {
    return (creationDate.getDate() < 10 ? '0' + creationDate.getDate() : creationDate.getDate()) + '/' +
        (creationDate.getMonth() < 10 ? '0' + (creationDate.getMonth() + 1).toString() : (creationDate.getMonth() + 1).toString()) + '/' +
        creationDate.getFullYear() +
        " " + (creationDate.getHours() < 10 ? '0' + creationDate.getHours().toString() : creationDate.getHours().toString()) + ':' +
        (creationDate.getMinutes() < 10 ? '0' + creationDate.getMinutes().toString() : creationDate.getMinutes().toString()) + ':' +
        (creationDate.getSeconds() < 10 ? '0' + creationDate.getSeconds().toString() : creationDate.getSeconds());
}

module.exports = DiscordClient;
