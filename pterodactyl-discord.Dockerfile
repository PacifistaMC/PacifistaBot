FROM openjdk:17-jdk

ENV PACIFISTA_BOT_VERSION=2.0.0

RUN useradd -m -d /home/container container
USER container
ENV USER=container HOME=/home/container
WORKDIR /home/container

COPY ./discord-bot/bot/target/pacifista-bot-discord-service-${PACIFISTA_BOT_VERSION}.jar /home/java/pacifistabot.jar

COPY entrypoint.sh /entrypoint.sh
CMD ["/bin/bash", "/entrypoint.sh"]
