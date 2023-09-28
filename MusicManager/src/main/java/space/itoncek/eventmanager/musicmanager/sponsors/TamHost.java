package space.itoncek.eventmanager.musicmanager.sponsors;

import net.roxeez.advancement.Advancement;
import net.roxeez.advancement.AdvancementCreator;
import net.roxeez.advancement.Context;
import net.roxeez.advancement.display.FrameType;
import net.roxeez.advancement.trigger.TriggerType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class TamHost implements AdvancementCreator {
    public static final String ID = "tamhost";

    @Override
    public @NotNull Advancement create(@NotNull Context context) {
        Advancement advancement = new Advancement(context.getPlugin(), ID);

        advancement.setParent(new NamespacedKey(context.getPlugin(), Sponsors.ID));

        advancement.setDisplay(x -> {
            x.setTitle("TamHost.cz");
            x.setDescription("Those guys have all the beefy computers");
            x.setIcon(Material.COMMAND_BLOCK);
            x.setAnnounce(false);
            x.setFrame(FrameType.CHALLENGE);
        });

        advancement.addCriteria("void", TriggerType.IMPOSSIBLE, impossible -> {
        });

        return advancement;
    }
}
