package space.itoncek.eventmanager.capturepoint;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import space.itoncek.eventmanager.capturepoint.utils.BlockState;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;

public final class CapturePoint extends JavaPlugin {
    public static List<CapturePointInstance> instances = new ArrayList<>();
    public static BlockState[][][] blockStates;
    @Override
    public void onEnable() {
        // Plugin startup logic
        blockStates = loadPattern();
    }

    private BlockState[][][] loadPattern() {
        BlockState[][][] out = new BlockState[8][5][5];
        try (Scanner sc = new Scanner(new URL("https://raw.githubusercontent.com/MadeByIToncek/eventMaster/master/loading.json").openStream())) {
            StringJoiner js = new StringJoiner("\n");
            while (sc.hasNextLine()) js.add(sc.nextLine());

            JSONArray array = new JSONArray(js.toString());
            int y = 0;
            for (Object o : array) {
                JSONArray a = (JSONArray) o;
                int x = 0;
                for (Object object : a) {
                    JSONArray b = (JSONArray) object;
                    int z = 0;

                    for (Object in : b) {
                        int i = (int) in;
                        out[y][x][z] = switch (i) {
                            case 1 -> BlockState.BASE;
                            case 2 -> BlockState.ACCENT;
                            default -> BlockState.KEEP;
                        };
                        z++;
                    }
                    x++;
                }
                y++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
