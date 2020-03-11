const config = require('../.env.json');
const Discord = require('discord.js');
const Log = require('./Log');

class DiscordClient {
    constructor() {
        console.log("Connection du bot Discord...");
        this.client = new Discord.Client();
        this.client.login(config.discordClient.token);
        this.client.on('ready', function() {
            console.log("Bot discord: PacifistaBot prêt !");
        });
        this.client.on('message', this.userMessage);
    }

    userMessage(message) {
        const user = message.author;
        const text = message.content;
        const channelId = message.channel.id;
        const channelName = message.channel.name;
        const isPrivateMessage = message.channel.type === "dm";
        if (user.bot) return;
        Log.log(user, text, channelName);
    }
}

module.exports = DiscordClient;
