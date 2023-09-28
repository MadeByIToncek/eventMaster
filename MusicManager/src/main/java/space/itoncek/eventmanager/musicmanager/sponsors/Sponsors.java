package space.itoncek.eventmanager.musicmanager.sponsors;

import net.roxeez.advancement.Advancement;
import net.roxeez.advancement.AdvancementCreator;
import net.roxeez.advancement.Context;
import net.roxeez.advancement.display.BackgroundType;
import net.roxeez.advancement.display.FrameType;
import net.roxeez.advancement.trigger.TriggerType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class Sponsors implements AdvancementCreator {
    public static final String ID = "sponsors";

    @Override
    public @NotNull Advancement create(@NotNull Context context) {
        Advancement advancement = new Advancement(context.getPlugin(), ID);

        advancement.setDisplay(x -> {
            x.setTitle("Sponsors of CSYT");
            x.setDescription("OMG are we famous?");
            x.setBackground(BackgroundType.CHISELED_STONE_BRICKS);
            x.setIcon(Material.GOLD_BLOCK);
            x.setAnnounce(false);
            x.setToast(false);
            x.setFrame(FrameType.CHALLENGE);
        });

        advancement.addCriteria("void", TriggerType.IMPOSSIBLE, impossible -> {
        });

        return advancement;
    }
}