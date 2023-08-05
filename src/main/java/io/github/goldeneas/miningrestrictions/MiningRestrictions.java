package io.github.goldeneas.miningrestrictions;

import io.github.goldeneas.miningrestrictions.events.PlayerGainExperience;
import io.github.goldeneas.miningrestrictions.events.PlayerCancelMining;
import io.github.goldeneas.miningrestrictions.helpers.BlockHelper;
import io.github.goldeneas.miningrestrictions.helpers.ItemHelper;
import org.bukkit.plugin.java.JavaPlugin;

public final class MiningRestrictions extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Database database = new Database(this);
        ItemHelper itemHelper = new ItemHelper(this);
        BlockHelper blockHelper = new BlockHelper(this);

        getServer().getPluginManager().registerEvents(new PlayerCancelMining(database, itemHelper), this);
        getServer().getPluginManager().registerEvents(new PlayerGainExperience(database, blockHelper), this);
    }
}
