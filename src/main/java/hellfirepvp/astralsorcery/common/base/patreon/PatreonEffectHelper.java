/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon;

import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectTreeBeacon;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonEffectHelper
 * Created by HellFirePvP
 * Date: 20.06.2018 / 19:56
 */
public class PatreonEffectHelper {

    private static Map<UUID, PatreonEffect> effectMap = new HashMap<>();

    public static void init() {
        effectMap.put(
                UUID.fromString("2a6871c0-2dfa-41d8-af58-8608c81b8864"),
                new PtEffectTreeBeacon(null)
                        .setOverlayColor(0xFFC30711)
                        .setDrainColor(0xFFFF0000)
                        .setTreeColor(0xFFC30711));
    }

    @Nullable
    public static PatreonEffect getEffect(UUID uuid) {
        return effectMap.get(uuid);
    }

    public static class PatreonEffect {

        private final FlareColor chosenColor;

        public PatreonEffect(FlareColor chosenColor) {
            this.chosenColor = chosenColor;
        }

        @Nullable
        public FlareColor getChosenColor() {
            return chosenColor;
        }
    }

    public static enum FlareColor {

        BLUE,
        DARK_RED,
        DAWN,
        GOLD,
        GREEN,
        MAGENTA,
        RED,
        WHITE,
        YELLOW,
        ELDRITCH,
        DARK_GREEN,
        FIRE,
        WATER,
        EARTH,
        AIR,
        STANDARD

    }

}
