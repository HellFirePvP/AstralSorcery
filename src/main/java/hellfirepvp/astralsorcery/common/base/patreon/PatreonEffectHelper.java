/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon;

import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectTreeBeacon;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
        effectMap.put(
                UUID.fromString("7f6971c5-fb58-4519-a975-b1b5766e92d1"),
                new PatreonEffect(FlareColor.STANDARD));
    }

    @Nullable
    public static PatreonEffect getEffect(UUID uuid) {
        return effectMap.get(uuid);
    }

    public static <T extends EntityPlayer> Map<UUID, PatreonEffect> getFlarePatrons(Collection<T> players) {
        Collection<UUID> playerUUIDs = players.stream().map(Entity::getUniqueID).collect(Collectors.toList());
        return effectMap.entrySet()
                .stream()
                .filter(e -> e.getValue().getChosenColor() != null)
                .filter(e -> playerUUIDs.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
        STANDARD;

        public int spriteRowIndex() {
            return ordinal();
        }

    }

}
