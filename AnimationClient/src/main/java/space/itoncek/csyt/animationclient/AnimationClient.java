/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.animationclient;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class AnimationClient extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        PluginCommand cmd = getCommand("anim");
        assert cmd != null;
        cmd.setTabCompleter(new AnimHelper());
        cmd.setExecutor(new AnimCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
