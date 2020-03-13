class ConsoleCommands {
    constructor() {
        process.stdin.resume();
        process.stdin.setEncoding('utf8');
        process.stdin.on('data', function (msg) {
            msg = msg.replace(/(\r\n|\n|\r)/gm, "");
            msg = msg.replace(/\s\s+/g, ' ');
            const args = msg.split(' ');
            const cmd = args[0];
            args.shift();
            switch (cmd) {
                case "stop":
                    console.log("\x1b[33mArrêt du bot.\x1b[0m");
                    process.exit(0);
                    break;
                default:
                    console.log("Commande non reconnue");
            }
        });
    }
}

module.exports = ConsoleCommands;