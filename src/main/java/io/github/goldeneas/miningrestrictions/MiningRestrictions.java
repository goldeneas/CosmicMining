package io.github.goldeneas.miningrestrictions;

import io.github.goldeneas.miningrestrictions.events.PlayerBreakBlock;
import io.github.goldeneas.miningrestrictions.events.PlayerLeftClick;
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

        getServer().getPluginManager().registerEvents(new PlayerLeftClick(database, itemHelper), this);
        getServer().getPluginManager().registerEvents(new PlayerBreakBlock(database, blockHelper), this);
    }
}
