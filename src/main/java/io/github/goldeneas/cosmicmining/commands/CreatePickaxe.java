package io.github.goldeneas.cosmicmining.commands;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.goldeneas.cosmicmining.CosmicMining;
import io.github.goldeneas.cosmicmining.feedback.FeedbackMessage;
import io.github.goldeneas.cosmicmining.utils.ConfigPaths;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreatePickaxe implements CommandExecutor {
    private final CosmicMining plugin;
    private final YamlDocument config;

    public CreatePickaxe(CosmicMining plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig("config.yml");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player))
            return true;

        Player player = (Player) commandSender;
        String adminPermission = config.getString(ConfigPaths.ADMIN_PERMISSION_PATH);
        if(!commandSender.hasPermission(adminPermission)) {
            sendFeedback(player, "insufficient-permissions");
            return true;
        }

        if(args.length < 1) {
            sendFeedback(player, "incorrect-arguments");
            return true;
        }


        return true;
    }

    private void sendFeedback(Player player, String messageName) {
        new FeedbackMessage(plugin)
                .load(messageName)
                .sendTo(player);
    }

}
