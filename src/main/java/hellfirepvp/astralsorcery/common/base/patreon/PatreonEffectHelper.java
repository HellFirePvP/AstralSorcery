/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon;

import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectTreeBeacon;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        effectMap.put( //i, myself, hellfire :thonk:
                UUID.fromString("7f6971c5-fb58-4519-a975-b1b5766e92d1"),
                new PatreonEffect(FlareColor.WATER));

        effectMap.put( //ChosenArchitect
                UUID.fromString("e3298bd7-61bf-427f-af89-4e418a20bf57"),
                new PatreonEffect(FlareColor.STANDARD));

        effectMap.put( //tree_of_chaos
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

        BLUE(0x157AFF, 0xC1D8FF),
        DARK_RED(0xFF0739, 0xFF5555),
        DAWN(0xFF5186, 0xE95C47),
        GOLD(0xFF9116, 0xFFF26E),
        GREEN(0x5BFF37, 0x63FFA3),
        MAGENTA(0xFC7FFC, 0xFFC6FF),
        RED(0xFF0F2B, 0xFF0F59),
        WHITE(0xBFFFFF, 0xFFFFFF),
        YELLOW(0xFFFF55, 0xFDC71F),
        ELDRITCH(0x620280, 0xAE22FF),
        DARK_GREEN(0x00C601, 0x22FF8F),
        FIRE(0xFF4006, 0xFF9900),
        WATER(0x89DFFF, 0x587ADD),
        EARTH(0xD0863D, 0xCEB392),
        AIR(0xFFFFD1, 0xB2DABD),
        STANDARD(0x9918B9, 0x5E5DD6);

        public final Color color1, color2;

        FlareColor(int c1, int c2) {
            this.color1 = new Color(c1);
            this.color2 = new Color(c2);
        }

        public int spriteRowIndex() {
            return ordinal();
        }

        @SideOnly(Side.CLIENT)
        public SpriteSheetResource getTexture() {
            AbstractRenderableTexture texture = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "patreonflares/" + name().toLowerCase());
            return new SpriteSheetResource(texture, 1, 48);
        }

    }

}
