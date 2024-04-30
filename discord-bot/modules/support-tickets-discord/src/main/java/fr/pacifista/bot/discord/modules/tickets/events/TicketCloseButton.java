package fr.pacifista.bot.discord.modules.tickets.events;

import fr.pacifista.api.support.tickets.client.clients.PacifistaSupportTicketClient;
import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketDTO;
import fr.pacifista.api.support.tickets.client.enums.TicketStatus;
import fr.pacifista.bot.discord.modules.core.events.buttons.ButtonEvent;
import fr.pacifista.bot.discord.modules.core.utils.Colors;
import fr.pacifista.bot.discord.modules.tickets.config.BotTicketConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class TicketCloseButton extends ButtonEvent {
    private final PacifistaSupportTicketClient ticketClient;
    private final BotTicketConfig botConfig;

    public TicketCloseButton(JDA jda,
                             BotTicketConfig botConfig,
                             PacifistaSupportTicketClient ticketClient) {
        super(jda, "ticket-close");
        this.ticketClient = ticketClient;
        this.botConfig = botConfig;
    }

    @Override
    public Button createButton() {
        return Button.danger(getButtonId(), "Fermer le ticket")
                .withEmoji(Emoji.fromUnicode("⚠️"));
    }

    @Override
    public void onButtonEvent(@NotNull ButtonInteractionEvent event) {
        Role ticketModRole = event.getJDA().getRoleById(botConfig.getTicketsModRoleId());
        Channel channel = event.getChannel();
        Member member = event.getMember();
        String ticketOwnerUsername = channel.getName().split("-")[1];
        Member ticketOwner = event.getGuild().getMembersByName(ticketOwnerUsername, true).get(0);

        if (channel.getType() != ChannelType.TEXT || !channel.getName().contains("ticket-")) {
            event.reply(":warning: Ce salon n'est pas un ticket !").queue();
        }

        TextChannel ticketChannel = (TextChannel) channel;
        Category ticketsLogsCategory = event.getJDA().getCategoryById(botConfig.getTicketsLogsCategoryId());

        if (!member.getRoles().contains(ticketModRole)) {
            ticketChannel.getManager().removePermissionOverride(event.getMember().getIdLong()).queue();
        }

        event.getMessage().delete().queue();

        ticketChannel.getManager().setParent(ticketsLogsCategory).queue();

        Date archivedDate = Date.from(event.getTimeCreated().toInstant());
        String archiveFormattedDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(archivedDate);

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.PACIFISTA_COLOR)
                .setTitle("Ticket archivé")
                .setDescription(String.format("Ticket archivé le `%s` par `%s` (`%s`)", archiveFormattedDate, member.getUser().getName(), member.getUser().getId()));

        ticketChannel.sendMessageEmbeds(embed.build()).queue();
        closeTicket(ticketOwner.getId(), archivedDate);
    }

    private void closeTicket(String ticketOwnerId, Date updatedAt) {
        PacifistaSupportTicketDTO ticketDTO = this.ticketClient.getAll(
                "0",
                "1",
                String.format("createdById:like:%s", ticketOwnerId),
                "").getContent().get(0);

        ticketDTO.setStatus(TicketStatus.SOLVED);
        ticketDTO.setUpdatedAt(updatedAt);
        this.ticketClient.update(ticketDTO);
    }
}
