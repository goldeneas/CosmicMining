package io.github.goldeneas.cosmicmining.animations;

import io.github.goldeneas.cosmicmining.CosmicMining;
import org.bukkit.Location;
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
    private final Location location;
    private boolean isPlaying;

    public Animation(Location location, CosmicMining _plugin) {
        this.location = location;

        plugin = _plugin;
        log = plugin.getLogger();
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

    protected abstract void update();

    protected abstract void cleanup();
}
