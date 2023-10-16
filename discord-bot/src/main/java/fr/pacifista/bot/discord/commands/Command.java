package fr.pacifista.bot.discord.commands;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Slf4j(topic = "Command")
public abstract class Command extends ListenerAdapter {

    protected Command(final JDA jda) {
        CommandCreateAction cmd = jda.upsertCommand(getCommandName(), getCommandDescription())
                .setDefaultPermissions(getCommandPermissions());

        if (getSubCommands() != null) {
            for (SubcommandData subCmd : getSubCommands()) {
                cmd.addSubcommands(subCmd);
            }
        }

        cmd.queue();

        jda.addEventListener(this);
        log.info("Command {} registered", getCommandName());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals(getCommandName())) {
            onCommand(event);
        }
    }

    public abstract String getCommandName();
    public abstract String getCommandDescription();
    public abstract DefaultMemberPermissions getCommandPermissions();
    public abstract List<SubcommandData> getSubCommands();
    public abstract void onCommand(@NonNull final SlashCommandInteractionEvent interactionEvent);

}
