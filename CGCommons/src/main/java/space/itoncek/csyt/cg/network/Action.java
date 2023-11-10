/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg.network;

import space.itoncek.csyt.cg.logic.Processor;
import space.itoncek.csyt.cg.logic.graphicActions.GraphicDisabledAction;
import space.itoncek.csyt.cg.logic.graphicActions.GraphicEnabledAction;
import space.itoncek.csyt.cg.logic.housekeepingActions.EchoAction;
import space.itoncek.csyt.cg.logic.housekeepingActions.UnimplementedAction;
import space.itoncek.csyt.cg.logic.minigameActions.DecisionDomeDownlinkAction;
import space.itoncek.csyt.cg.logic.minigameActions.MinigameChangedAction;

public enum Action {
    GRAPHIC_ENABLED(true, false, GraphicEnabledAction.class),
    GRAPHIC_DISABLED(true, false, GraphicDisabledAction.class),
    MINIGAME_CHANGED(true, false, MinigameChangedAction.class),
    DD_DOWNLINK(true, false, DecisionDomeDownlinkAction.class),
    ECHO(true, true, EchoAction.class),
    ENABLE_GRAPHIC(false, true, UnimplementedAction.class),
    DISABLE_GRAPHIC(false, true, UnimplementedAction.class);

    private static Processor<?> processor;
    public final boolean downlink;
    public final boolean uplink;

    <T> Action(boolean downlink, boolean uplink, Class<T> actionClass) {

        this.downlink = downlink;
        this.uplink = uplink;
    }
}
