/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.eventmanager.musicmanager.sponsors;

import net.roxeez.advancement.Advancement;
import net.roxeez.advancement.AdvancementCreator;
import net.roxeez.advancement.Context;
import net.roxeez.advancement.display.FrameType;
import net.roxeez.advancement.trigger.TriggerType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class TenM implements AdvancementCreator {
    public static final String ID = "tenm";

    @Override
    public @NotNull Advancement create(@NotNull Context context) {
        Advancement advancement = new Advancement(context.getPlugin(), ID);

        advancement.setParent(new NamespacedKey(context.getPlugin(), Sponsors.ID));

        advancement.setDisplay(x -> {
            x.setTitle("T_en_M");
            x.setDescription("Why do I hear boss music?");
            x.setIcon(Material.NOTE_BLOCK);
            x.setAnnounce(false);
            x.setFrame(FrameType.CHALLENGE);
        });

        advancement.addCriteria("void", TriggerType.IMPOSSIBLE, impossible -> {
        });

        return advancement;
    }
}
