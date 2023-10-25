/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
import java.time.Instant;
import java.util.Objects;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.concurrent.Callable;

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

            try {
                writeToFile(mcname, snowflake, () -> {
                    event.getMessage().delete().queue(r -> {
                        if (statusMSG != 0L) {
                            TextChannel textChannel = Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getChannelById(TextChannel.class, adminChannel));
                            textChannel.retrieveMessageById(statusMSG).queue(msg -> {
                                User user = jda.getUserById(snowflake);
                                assert user != null;
                                textChannel.editMessageById(statusMSG,
                                        msg.getContentRaw() + "\n" + user.getName() + " (as " + mcname + ")").queue();
                            });
                        }
                    });
                    return null;
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (event.getButton().getId().equalsIgnoreCase("deny")) {
            event.getMessage().delete().queue();
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
            arr.put(new JSONObject().put("snowflake", snowflake).put("mcname", mcname));
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
                event.deferReply().queue();
                JSONArray arr = new JSONArray();
                try (Scanner sc = new Scanner(new File("./db.json"))) {
                    StringJoiner js = new StringJoiner("\n");
                    while (sc.hasNextLine()) js.add(sc.nextLine());
                    arr = new JSONArray(js.toString());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                for (Object o : arr) {
                    JSONObject obj = new JSONObject();
                }
            }
        }
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
