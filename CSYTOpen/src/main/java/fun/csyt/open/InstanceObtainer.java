/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.open;

import com.gmail.val59000mc.events.UhcEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static fun.csyt.open.CSYTOpen.gmmgr;
import static fun.csyt.open.CSYTOpen.log;

public class InstanceObtainer implements Listener {
    @EventHandler
    public void onUhcEvent(UhcEvent event) {
        log.fine("Obtained GameManager! Current state:" + event.getGameManager().getGameState());
        gmmgr = event.getGameManager();
    }
}
