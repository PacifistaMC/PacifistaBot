const fs = require('fs');
const config = require('../.env.json');
const logDir = 'logs/';

function getDateData() {
    const now = new Date();
    now.setHours(now.getHours() + config.defaults.timezoneConfig);
    return {
        time: (now.getHours() < 10 ? '0' + now.getHours().toString() : now.getHours().toString()) + ':' +
            (now.getMinutes() < 10 ? '0' + now.getMinutes().toString() : now.getMinutes().toString()) + ':' +
            (now.getSeconds() < 10 ? '0' + now.getSeconds().toString() : now.getSeconds()),
        todayDate: now.getFullYear() + '-' +
            (now.getMonth() < 10 ? '0' + (now.getMonth() + 1).toString() : (now.getMonth() + 1).toString()) + '-' +
            (now.getDate() < 10 ? '0' + now.getDate() : now.getDate())
    };
}

function checkLogDir() {
    if (!fs.existsSync(logDir)) {
        fs.mkdirSync(logDir);
    }
}

class Logs {
    static async log(user, msg, channel) {
        checkLogDir();
        const dataDate = getDateData();
        let messageLog = "INFO [" + dataDate.time + "] <" + user.tag + "> (" + channel + ") : " + msg + '\n';
        fs.appendFile(logDir + dataDate.todayDate + '.log', messageLog, function (err) {
            if (err) throw err;
        });
    }

    static async logSystem(msg) {
        checkLogDir();
        const dataDate = getDateData();
        let messageLog = "SYSTEM [" + dataDate.time + "] " + msg + '\n';
        fs.appendFile(logDir + dataDate.todayDate + '.log', messageLog, function (err) {
            if (err) throw err;
        });
    }

    static logError(msg) {
        checkLogDir();
        const dataDate = getDateData();
        let messageLog = "ERROR [" + dataDate.time + "] " + msg + '\n';
        fs.appendFileSync(logDir + dataDate.todayDate + '.log', messageLog, function (err) {
            if (err) throw err;
        });
    }
}

module.exports = Logs;
