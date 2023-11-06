/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.eventMaster;

import org.json.JSONArray;
import org.json.JSONObject;
import space.itoncek.csyt.DRMLib;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        new DRMLib() {
            @Override
            public void callback() {
                System.exit(69420);
            }
        };
        File file = null;
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Insert file path: ");
            while (true) {
                if (sc.hasNextLine()) {
                    file = new File(sc.nextLine());
                    break;
                } else {
                    sleep(50);
                }
            }
        }
        StringJoiner json = new StringJoiner("\n");
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                json.add(sc.nextLine());
            }
        }
        HashMap<String, Integer> playersMap = new HashMap<>();
        HashMap<String, Integer> teamsMap = new HashMap<>();
        HashMap<Integer, List<Long>> patternTiming = new HashMap<>();
        JSONArray array = new JSONArray(json.toString());
        for (Object o : array) {
            JSONObject out = (JSONObject) o;
            for (Object playershare : out.getJSONArray("playershare")) {
                JSONObject playerPoints = (JSONObject) playershare;
                playersMap.put(playerPoints.getString("name"), playersMap.getOrDefault(playerPoints.getString("name"), 0) + (playerPoints.getInt("pts") * out.getInt("partPTS")));
            }
            teamsMap.put(out.getString("team"), teamsMap.getOrDefault(out.getString("team"), 0) + out.getInt("totalPTS"));

            if (!patternTiming.containsKey(out.getInt("patternid")))
                patternTiming.put(out.getInt("patternid"), new ArrayList<>());
            patternTiming.get(out.getInt("patternid")).add(out.getJSONObject("time").getLong("duration"));
        }

        System.out.println("--------------------------------------------------------------------------------------------------------");
        for (Map.Entry<String, Integer> e : playersMap.entrySet()) {
            System.out.println(e.getKey() + " -- " + e.getValue());
        }
        System.out.println("--------------------------------------------------------------------------------------------------------");
        for (Map.Entry<String, Integer> e : teamsMap.entrySet()) System.out.println(e.getKey() + " -- " + e.getValue());
        System.out.println("--------------------------------------------------------------------------------------------------------");
        for (Map.Entry<Integer, List<Long>> e : patternTiming.entrySet()) {
            long avg = 0;
            long amt = 0;
            for (Long l : e.getValue()) {
                avg += l;
                amt++;
            }
            System.out.println(e.getKey() + " -- " + ((avg) / amt) / 1000d);
        }
    }
}