package io.github.goldeneas.cosmicmining.events;

import io.github.goldeneas.cosmicmining.CosmicMining;
import io.github.goldeneas.cosmicmining.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.OptionalInt;

public class PlayerAddToDatabase implements Listener {
    private final Database database;

    public PlayerAddToDatabase(CosmicMining plugin) {
        this.database = plugin.getDatabase();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        OptionalInt playerLevel = database.getLevel(player);

        if(playerLevel.isEmpty())
            database.setLevel(player, 0);
    }

}
