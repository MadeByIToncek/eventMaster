/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.HashMap;

public class DiscordBot {
    public static JDA jda;
    public static String buttonID = "csyt_testappl";
    public static String acceptID = "csyt_acc_";
    public static String modalID = "csyt_testmodal";
    public static long guild = 709697349064196178L;
    public static HashMap<Long, String> cmdMap = new HashMap<>();
    public static long adminChannel = 709700166520668281L;
    public static long statusMSG = 0L;

    public static void main(String[] args) throws InterruptedException {
        if (DRMLib.checkDRM()) System.exit(69420);
        //TODO: Restore
        //new File("./db.json").delete();
        jda = JDABuilder
                .createDefault(args[0])
                .setActivity(Activity.watching("you!"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new ButtonInteraction())
                .build().awaitReady();
        System.out.println("System ready, setting up!");
        setup();
    }

    private static void setup() {
        for (Guild guild : jda.getGuilds()) {
            guild.updateCommands().addCommands(
                    Commands.slash("cpm", "Creates testing message")
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                    Commands.slash("propagate", "Propagates testing messages")
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
            ).queue(commands -> {
                for (Command command : commands) {
                    cmdMap.put(command.getIdLong(), command.getName());
                }
            });
        }
    }
}