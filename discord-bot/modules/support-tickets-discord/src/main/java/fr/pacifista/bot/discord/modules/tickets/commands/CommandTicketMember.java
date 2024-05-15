package fr.pacifista.bot.discord.modules.tickets.commands;

import fr.pacifista.bot.discord.modules.core.commands.BotCommand;
import fr.pacifista.bot.discord.modules.tickets.config.BotTicketConfig;
import fr.pacifista.bot.discord.modules.tickets.utils.TicketUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

@Service
public class CommandTicketMember extends BotCommand {
    private final BotTicketConfig botConfig;

    public CommandTicketMember(JDA jda,
                         BotTicketConfig botConfig) {
        super(jda, List.of(
                new SubcommandData("add", "Ajouter un membre au ticket !")
                        .addOption(OptionType.USER, "membre", "Le membre à ajouter", true),
                new SubcommandData("remove", "Supprimer un membre du ticket !")
                        .addOption(OptionType.USER, "membre", "Le membre à supprimer", true)
        ));
        this.botConfig = botConfig;
    }

    @Override
    public String getCommandName() {
        return "ticketmember";
    }

    @Override
    public String getCommandDescription() {
        return "Commande liée aux tickets !";
    }

    @Override
    public DefaultMemberPermissions getCommandPermissions() {
        return DefaultMemberPermissions.ENABLED;
    }

    @Override
    public void onCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {
        if (interactionEvent.getSubcommandName() == null) return;

        switch (interactionEvent.getSubcommandName()) {
            case "add":
                addMember(interactionEvent);
                break;
            case "remove":
                removeMember(interactionEvent);
                break;
        }
    }

    private void addMember(@NonNull SlashCommandInteractionEvent interactionEvent) {
        if (!TicketUtils.handleTicketChannel(interactionEvent, this.botConfig.getTicketsCategoryId())) return;

        final OptionMapping option = interactionEvent.getOption("membre");
        if (option == null) return;

        final Member member = option.getAsMember();
        if (member == null) return;

        final TextChannel channel = (TextChannel) interactionEvent.getChannel();
        channel.getManager().putPermissionOverride(member, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null).queue();

        interactionEvent.reply(String.format("Le membre <@%s> a été ajouté au ticket.", member.getId())).queue();
    }

    private void removeMember(@NonNull SlashCommandInteractionEvent interactionEvent) {
        if (!TicketUtils.handleTicketChannel(interactionEvent, this.botConfig.getTicketsCategoryId())) return;

        final OptionMapping option = interactionEvent.getOption("membre");
        if (option == null) return;

        final Member member = option.getAsMember();
        if (member == null) return;

        final TextChannel channel = (TextChannel) interactionEvent.getChannel();
        channel.getManager().removePermissionOverride(member).queue();

        interactionEvent.reply(String.format("Le membre <@%s> a été supprimé du ticket.", member.getId())).queue();
    }
}
