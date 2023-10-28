/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt;

import io.graversen.minecraft.rcon.MinecraftRcon;
import io.graversen.minecraft.rcon.RconCommandException;
import io.graversen.minecraft.rcon.commands.WhiteListCommand;
import io.graversen.minecraft.rcon.service.ConnectOptions;
import io.graversen.minecraft.rcon.service.MinecraftRconService;
import io.graversen.minecraft.rcon.service.RconDetails;
import io.graversen.minecraft.rcon.util.Selectors;
import io.graversen.minecraft.rcon.util.Target;
import io.graversen.minecraft.rcon.util.WhiteListModes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;

import static java.lang.Thread.sleep;
import static space.itoncek.csyt.DiscordBot.*;

public class ButtonInteraction extends ListenerAdapter {
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (Objects.requireNonNull(event.getButton().getId()).equalsIgnoreCase(buttonID)) {
            TextInput input = TextInput.create("nick", "Nick", TextInputStyle.SHORT)
                    .setPlaceholder("NexuSoveVidea")
                    .setMinLength(3)
                    .setMaxLength(16)
                    .build();
            event.replyModal(Modal.create(modalID, "CSYT Testing formulář")
                    .addComponents(ActionRow.of(input))
                    .build()).queue();
        } else if (event.getButton().getId().startsWith(acceptID)) {
            String mcname = event.getButton().getId().substring(acceptID.length()).split("\\r?_")[0];
            String snowflake = event.getButton().getId().substring(acceptID.length()).split("\\r?_")[1];
            System.out.println(event.getButton().getId() + " vs " + snowflake);
            event.deferReply().queue(x -> {
                try {
                    writeToFile(mcname, snowflake, () -> {
                        event.getMessage().delete().queue(r -> {
                            if (statusMSG != 0L) {
                                TextChannel textChannel = Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getChannelById(TextChannel.class, adminChannel));
                                textChannel.retrieveMessageById(statusMSG).queue(msg -> {
                                    User user = x.getInteraction().getGuild().getMemberById(snowflake).getUser();
                                    textChannel.editMessageById(statusMSG,
                                            msg.getContentRaw() + "\n " + user.getName() + " (as " + mcname + ")").queue();
                                });
                            }
                        });
                        x.deleteOriginal().queue();
                        return null;
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } else if (event.getButton().getId().startsWith("deny")) {
            event.deferReply().queue(x -> {
                x.deleteMessageById(event.getMessageId()).queue();
                x.deleteOriginal().queue();
            });
        }
        super.onButtonInteraction(event);
    }

    private void writeToFile(String mcname, String snowflake, Callable<Void> callback) throws Exception {
        JSONArray arr = new JSONArray();
        if (!new File("./db.json").exists()) {
            try (FileWriter fw = new FileWriter("./db.json")) {
                fw.write(arr.toString(4));
            }
        }
        try (Scanner sc = new Scanner(new File("./db.json"))) {
            StringJoiner js = new StringJoiner("\n");
            while (sc.hasNextLine()) js.add(sc.nextLine());
            arr = new JSONArray(js.toString());
            arr.put(new JSONObject().put("snowflake", Long.parseLong(snowflake)).put("mcname", mcname));
        } finally {
            try (FileWriter fw = new FileWriter("./db.json")) {
                fw.write(arr.toString(4));
            } finally {
                callback.call();
            }
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        String nick = event.getValue("nick").getAsString();

        if (nick.matches("^[A-Za-z_0-9]{3,16}$")) {
            TextChannel channel = jda.getGuildById(guild).getChannelById(TextChannel.class, adminChannel);
            MessageEmbed embed = new EmbedBuilder()
                    .setAuthor(jda.getSelfUser().getName(), null, jda.getSelfUser().getAvatarUrl())
                    .setTitle("CSYT Form")
                    .setDescription(nick + " requested access to CSYT")
                    .setThumbnail(getUrl(nick))
                    .setTimestamp(Instant.now())
                    .setColor(Color.green)
                    .build();
            assert channel != null;
            event.reply("Successful!").setEphemeral(true).queue();
            channel.sendMessageEmbeds(embed).addActionRow(Button.success(acceptID + nick + "_" + event.getUser().getId(), "Accept"), Button.danger("deny", "Deny")).complete();
        } else {
            event.reply("This nick is not valid?!").setEphemeral(true).queue();
        }

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String s = cmdMap.get(event.getCommandIdLong());
        switch (s) {
            case "cpm" -> {
                MessageEmbed embed = new EmbedBuilder()
                        .setAuthor(jda.getSelfUser().getName(), null, jda.getSelfUser().getAvatarUrl())
                        .setTitle("CSYT otevírá brány testování")
                        .setDescription("Za několik okamžiků se otevřou brány CSYT na testování. Na to abyste se mohli podílet na testování serverů, musí vás naši admini přidat na whitelist. Toto můžete udělat pomocí tlačítka níže. **CSYT Účastnící mají přísný zákaz účastnit se testování!**")
                        .setColor(new Color(227, 127, 55))
                        .build();
                event.reply("Created!").setEphemeral(true).queue();
                event.getChannel().asTextChannel().sendMessageEmbeds(embed).addActionRow(Button.success(buttonID, "Apply")).queue(msg -> Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getChannelById(TextChannel.class, adminChannel)).sendMessage("Applications relevant to message with ID: " + msg.getId() + "\n").queue(mssg -> {
                    statusMSG = mssg.getIdLong();
                }));
            }
            case "propagate" -> {
                event.deferReply().queue(x -> {
                    JSONArray arr = new JSONArray();
                    try (Scanner sc = new Scanner(new File("./db.json"))) {
                        StringJoiner js = new StringJoiner("\n");
                        while (sc.hasNextLine()) js.add(sc.nextLine());
                        arr = new JSONArray(js.toString());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    HashMap<User, String> params = new HashMap<>();
                    for (Object o : arr) {
                        JSONObject obj = (JSONObject) o;
                        User user = x.getInteraction().getGuild().getMemberById(obj.getLong("snowflake")).getUser();
                        String mcname = obj.getString("mcname");
                        params.put(user, mcname);
                    }
                    propagateTeams(params, x);
                });
            }
        }
    }

    private void propagateTeams(HashMap<User, String> params, InteractionHook x) {
        final MinecraftRconService minecraftRconService = new MinecraftRconService(new RconDetails("localhost", 25575, "NeXuSoVeViDeA"), ConnectOptions.defaults());
        minecraftRconService.connectBlocking(Duration.ofDays(1));
        MinecraftRcon rcon = minecraftRconService.minecraftRcon().get();
        try {
            rcon.query(new WhiteListCommand(Target.selector(Selectors.ALL_PLAYERS), WhiteListModes.LIST), rconResponse -> {
                String s = rconResponse.getResponseString();
                String[] arrs = Arrays.stream(s.split("\\r?:")[1].split("\\r?,")).map(String::strip).toArray(String[]::new);
                System.out.println(Arrays.toString(arrs));
                for (String playerName : arrs) {
                    rcon.sendSync(new WhiteListCommand(Target.player(playerName.strip()), WhiteListModes.REMOVE));
                    //x.editOriginal("Removing " + playerName + " from whitelist").queue();
                    try {
                        sleep(250);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                return null;
            });
        } catch (RconCommandException ignored) {
        }
        for (Map.Entry<User, String> entry : params.entrySet()) {
            final WhiteListCommand cmd = new WhiteListCommand(Target.player(entry.getValue()), WhiteListModes.ADD);
            rcon.sendSync(cmd);
            entry.getKey().openPrivateChannel().flatMap(channel -> {
                MessageEmbed embed = new EmbedBuilder()
                        .setColor(Color.ORANGE)
                        .setAuthor(jda.getSelfUser().getName(), null, jda.getSelfUser().getAvatarUrl())
                        .setTitle("CSYT TESTING")
                        .setDescription("Za několik momentů začne testing na CSYT. Testingu se můžete účastnit pomocí IP adresy níže.\n" +
                                "\n" +
                                "**IP Adresa:** `csyttesting.csyt.fun`\n" +
                                "**Verze:** `1.19.4`\n" +
                                "**Whitelisted nick:** `" + entry.getValue() + "`\n" +
                                "\n" +
                                "***Je nutné se připojit pomocí nicku výše, jelikož pro tento nick je nyní udělen whitelist***")
                        .build();
                return channel.sendMessageEmbeds(embed);
            }).queue();
            //x.editOriginal("Added " + entry.getValue() + " to whitelist").queue();
        }
        minecraftRconService.disconnect();
        x.deleteOriginal().queue();
    }

    private String getUrl(String nick) {
        JSONObject object = httpRequest("https://api.mojang.com/users/profiles/minecraft/" + nick);
        if (object == null) return "https://visage.surgeplay.com/full/384/X-Ghost";
        else {
            return "https://visage.surgeplay.com/full/384/" + object.getString("id") + ".png";
        }
    }

    private JSONObject httpRequest(String s) {
        StringJoiner js = new StringJoiner("\n");
        try (Scanner sc = new Scanner(new URL(s).openStream())) {
            while (sc.hasNextLine()) js.add(sc.nextLine());
            return new JSONObject(js.toString());
        } catch (IOException | JSONException e) {
            return null;
        }
    }
}
