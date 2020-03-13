const DiscordInstance = require('./modules/DiscordClient');
const ConsoleCmdsInstance = require('./modules/ConsoleCommands');

const bot = new DiscordInstance();
const consoleCommands = new ConsoleCmdsInstance();