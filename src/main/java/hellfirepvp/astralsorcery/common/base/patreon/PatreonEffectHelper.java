/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.client.util.resource.*;
import hellfirepvp.astralsorcery.common.base.patreon.entity.PartialEntityFlare;
import hellfirepvp.astralsorcery.common.base.patreon.flare.PatreonPartialEntity;
import hellfirepvp.astralsorcery.common.data.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonEffectHelper
 * Created by HellFirePvP
 * Date: 20.06.2018 / 19:56
 */
public class PatreonEffectHelper {

    static boolean loadingFinished = false;
    static Map<UUID, List<PatreonEffect>> effectMap = new HashMap<>();

    @Nonnull
    public static List<PatreonEffect> getPatreonEffects(Side side, UUID uuid) {
        if (side == Side.CLIENT && !Config.enablePatreonEffects) {
            return Collections.emptyList(); //That config is to be applied clientside
        }
        if (!loadingFinished) {
            return Collections.emptyList();
        }
        return effectMap.getOrDefault(uuid, Collections.emptyList());
    }

    public static <T extends EntityPlayer> Map<UUID, List<PatreonEffect>> getPatreonEffects(Collection<T> players) {
        if (!loadingFinished) {
            return Maps.newHashMap();
        }
        Collection<UUID> playerUUIDs = players.stream().map(Entity::getUniqueID).collect(Collectors.toList());
        return effectMap.entrySet()
                .stream()
                .filter(e -> playerUUIDs.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static class PatreonEffect {

        protected static Random rand = new Random();
        private final FlareColor chosenColor;
        private final UUID sessionEffectId;

        public PatreonEffect(UUID sessionEffectId, FlareColor chosenColor) {
            this.chosenColor = chosenColor;
            this.sessionEffectId = sessionEffectId;
        }

        @Nullable
        public FlareColor getChosenColor() {
            return chosenColor;
        }

        public boolean hasPartialEntity() {
            return this.chosenColor != null;
        }

        public UUID getId() {
            return sessionEffectId;
        }

        public void initialize() {}

        @Nullable
        public PatreonPartialEntity createEntity(UUID playerUUID) {
            if (hasPartialEntity()) {
                return new PartialEntityFlare(getChosenColor(), playerUUID);
            }
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PatreonEffect that = (PatreonEffect) o;
            return Objects.equals(sessionEffectId, that.sessionEffectId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sessionEffectId);
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
