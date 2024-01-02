/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.datapacktoolbox;

import io.papermc.paper.datapack.Datapack;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import space.itoncek.csyt.DRMLib;
import space.itoncek.csyt.UpdateLib;

import java.io.File;
import java.util.Arrays;

public final class DataPackToolBox extends JavaPlugin {
    public static DataPackToolBox pl;
    @Override
    public void onEnable() {
        pl = this;
        new DRMLib() {
            @Override
            public void callback() {
                Bukkit.shutdown();
            }
        };
        boolean isPresent = Arrays.stream(Bukkit.getDatapackManager().getPacks().toArray(new Datapack[0]))
                .filter(d -> d.getName().startsWith("file/CSYT_"))
                .map((d) -> d.getName() + " ? " + d.isEnabled()).findAny().isPresent();

        for (World world : Bukkit.getWorlds()) {
            File dpf = new File(world.getWorldFolder() + "/datapacks/");
            File dp = new File(dpf + "/csyt_loot.zip");
            dpf.mkdirs();
            if (isPresent) {
                dp.delete();
            }
            UpdateLib.downloadAssetFile("csyt_loot.zip", dp);
        }

        getCommand("dptb").setTabCompleter(new DPTBCommandController());
        getCommand("dptb").setExecutor(new DPTBCommandController());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
