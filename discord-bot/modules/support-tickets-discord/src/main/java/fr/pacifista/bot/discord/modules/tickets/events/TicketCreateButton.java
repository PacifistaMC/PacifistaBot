package fr.pacifista.bot.discord.modules.tickets.events;

import com.funixproductions.core.exceptions.ApiException;
import fr.pacifista.api.support.tickets.client.clients.PacifistaSupportTicketClient;
import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketDTO;
import fr.pacifista.api.support.tickets.client.enums.TicketCreationSource;
import fr.pacifista.api.support.tickets.client.enums.TicketStatus;
import fr.pacifista.api.support.tickets.client.enums.TicketType;
import fr.pacifista.bot.discord.modules.core.events.buttons.ButtonEvent;
import fr.pacifista.bot.discord.modules.core.utils.Colors;
import fr.pacifista.bot.discord.modules.tickets.config.BotTicketConfig;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Slf4j(topic = "TicketCreateButton")
@Service
public class TicketCreateButton extends ButtonEvent {
    private final List<SelectOption> options = List.of(
            SelectOption
                    .of("Achat en ligne", "online_purchase")
                    .withDescription("Un problème avec un achat en ligne ?")
                    .withEmoji(Emoji.fromUnicode("🛒")),
            SelectOption
                    .of("Réclamation", "reclamation")
                    .withDescription("Réclamer un achat")
                    .withEmoji(Emoji.fromUnicode("🎁")),
            SelectOption
                    .of("Signalement", "report")
                    .withDescription("Signaler un joueur")
                    .withEmoji(Emoji.fromUnicode("🚩")),
            SelectOption
                    .of("Bug", "bug")
                    .withDescription("Signaler un bug")
                    .withEmoji(Emoji.fromUnicode("🐛")),
            SelectOption
                    .of("Autre", "other")
                    .withDescription("Contacter l'équipe de Pacifista")
                    .withEmoji(Emoji.fromUnicode("☎️"))
    );

    private final BotTicketConfig botConfig;
    private final PacifistaSupportTicketClient ticketClient;
    private final TicketCloseButton ticketCloseButton;

    public TicketCreateButton(final JDA jda,
                              final BotTicketConfig botConfig,
                              final PacifistaSupportTicketClient ticketClient,
                              final TicketCloseButton ticketCloseButton) {
        super(jda, "ticket-create");
        this.botConfig = botConfig;
        this.ticketClient = ticketClient;
        this.ticketCloseButton = ticketCloseButton;
    }

    @Override
    public void onButtonEvent(@NonNull ButtonInteractionEvent event) {
        final SelectMenu select = StringSelectMenu.create(getButtonId())
                .setPlaceholder("Sélectionne le type de ticket")
                .setId(getButtonId())
                .setRequiredRange(1, 1)
                .addOptions(this.options)
                .build();

        event.reply("Merci de choisir un type de ticket.")
                .setEphemeral(true)
                .setActionRow(select)
                .queue();
    }

    @Override
    public void onStringSelectInteraction(@NonNull StringSelectInteractionEvent event) {
        final String selectId = event.getInteraction().getComponentId();

        if (selectId.equals(getButtonId())) {
            String ticketType = event.getValues().get(0);

            final TextInput object = TextInput.create("object", "Objet", TextInputStyle.SHORT)
                    .setPlaceholder("Objet du ticket")
                    .setMinLength(10)
                    .setMaxLength(100)
                    .setRequired(true)
                    .build();

            final Modal modal = Modal.create(String.format("ticket-create:%s", ticketType), "Crée un ticket")
                    .addActionRow(object)
                    .build();

            event.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(@NonNull ModalInteractionEvent event) {
        final String interactionId = event.getModalId();
        if (!interactionId.contains(":")) return;
        final String modalId = interactionId.split(":")[0];
        final String type = interactionId.split(":")[1];
        final User user = event.getUser();

        if (modalId.equals("ticket-create")) {
            final TicketType ticketType = TicketType.valueOf(type.toUpperCase());
            String object = event.getValue("object").getAsString();

            PacifistaSupportTicketDTO ticketDTO = new PacifistaSupportTicketDTO();
            ticketDTO.setCreatedAt(new Date());
            ticketDTO.setCreatedById(user.getId());
            ticketDTO.setCreatedByName(user.getName());
            ticketDTO.setCreationSource(TicketCreationSource.DISCORD);
            ticketDTO.setType(ticketType);
            ticketDTO.setObject(object);
            ticketDTO.setStatus(TicketStatus.CREATED);

            try {
                ticketDTO = this.ticketClient.create(ticketDTO);
            } catch (ApiException e) {
                event.reply(":warning: Impossible de créer le ticket").queue();
                log.error("Impossible de créer le ticket", e);
            }
            createTicket(event, ticketType, ticketDTO.getId());
        }
    }

    @Override
    public Button createButton() {
        return Button.primary(getButtonId(), "Créer un ticket");
    }

    private void createTicket(@NonNull ModalInteractionEvent event, TicketType ticketType, UUID ticketId) {
        final Category category = event.getJDA().getCategoryById(botConfig.getTicketsCategoryId());
        final User ticketOwner = event.getUser();
        final TextChannel ticketChannel = category.createTextChannel(String.format("ticket-%s", ticketOwner.getGlobalName()))
                .setTopic(ticketId.toString())
                .complete();

        final Role modRole = event.getJDA().getRoleById(botConfig.getTicketsModRoleId());
        final Role everyoneRole = event.getGuild().getRolesByName("@everyone", true).get(0);

        ticketChannel.getManager()
                .putPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null)
                .putPermissionOverride(modRole, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null)
                .putPermissionOverride(everyoneRole, null, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND))
                .queue();

        event.editMessage(String.format("Ticket crée avec succès ! <#%s>", ticketChannel.getId()))
                .setComponents()
                .queue();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.PACIFISTA_COLOR)
                .setTitle(String.format("Ticket de %s (%s)", ticketOwner.getEffectiveName(), ticketOwner.getName()))
                .setDescription("Ton ticket à été crée. Merci de patienter, un modérateur viendra y répondre rapidement.")
                .addField(new MessageEmbed.Field("Type", ticketType.name(), true))
                .addField(new MessageEmbed.Field("Objet", event.getValue("object").getAsString(), true));

        ticketChannel.sendMessageEmbeds(embed.build())
                .addActionRow(this.ticketCloseButton.createButton())
                .queue();
    }
}
