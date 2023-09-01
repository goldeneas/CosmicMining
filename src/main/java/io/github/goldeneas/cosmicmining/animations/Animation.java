package io.github.goldeneas.cosmicmining.animations;

import io.github.goldeneas.cosmicmining.CosmicMining;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

public abstract class Animation extends BukkitRunnable {
    private static Logger log;
    private static CosmicMining plugin;


    private int delay;
    private int radius;
    private long period;
    private long maxLifetime;
    private long currentLifetime;
    private boolean isPlaying;

    private Player player;
    private Location location;

    // TODO: make stronger location and player checking
    // some animations might use location, others might use player tracking
    // we want to avoid them bugging because one uses one or the other, or at
    // least try to make them as safe as possible
    public Animation(Location location, CosmicMining _plugin) {
        plugin = _plugin;
        log = plugin.getLogger();

        this.location = location;
    }

    public Animation(Player player, CosmicMining _plugin) {
        plugin = _plugin;
        log = plugin.getLogger();

        this.player = player;
    }

    public void play() {
        if(radius == 0) {
            log.warning("Animation " + this.getClass().getName() + "'s radius is 0! Animation will not play.");
            return;
        }

        if(period == 0) {
            log.warning("Animation " + this.getClass().getName() + "'s period is 0! Animation will not play.");
            return;
        }

        if(maxLifetime == -1 || maxLifetime == 0) {
            maxLifetime = Integer.MAX_VALUE;
            log.warning("Animation " + this.getClass().getName() + "'s max lifetime has been set to infinite!");
        }

        isPlaying = true;
        this.runTaskTimer(plugin, delay, period);
    }

    @Override
    public void run() {
        if(!isPlaying) {
            log.severe("Tried running animation " + this.getClass().getName() + " without calling 'play()'!");
            return;
        }

        if(location == null && player == null) {
            log.severe("Tried running animation " + this.getClass().getName() + " without setting a location!");
            return;
        }

        currentLifetime += period;

        if(currentLifetime < maxLifetime) {
            update();
        } else {
            isPlaying = false;

            cleanup();
            cancel();
        }
    };

    protected void setDelay(int delay) {
        this.delay = delay;
    }

    protected int getDelay() {
        return delay;
    }

    protected void setPeriod(long period) {
        this.period = period;
    }

    protected long getPeriod() {
        return period;
    }

    protected void setRadius(int radius) {
        this.radius = radius;
    }

    protected int getRadius() {
        return radius;
    }

    protected void setMaxLifetime(int seconds) {
        this.maxLifetime = seconds * 20L;
    }

    protected long getMaxLifetime() {
        return maxLifetime;
    }

    protected long getCurrentLifetime() {
        return currentLifetime;
    }


    protected Location getLocation() {
        return location;
    }

    protected Player getPlayer() {
        return player;
    }

    protected abstract void update();

    protected abstract void cleanup();
}
