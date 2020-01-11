/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeHelper
 * Created by HellFirePvP
 * Date: 08.08.2019 / 18:24
 */
public class PerkAttributeHelper {

    private static Map<UUID, PerkAttributeMap> playerPerkAttributes = new HashMap<>();
    private static Map<UUID, PerkAttributeMap> playerPerkAttributesClient = new HashMap<>();

    private PerkAttributeHelper() {}

    @Nonnull
    public static PerkAttributeMap getOrCreateMap(PlayerEntity player, LogicalSide dist) {
        if (dist.isClient()) {
            return playerPerkAttributesClient.computeIfAbsent(player.getUniqueID(), (uuid) -> new PerkAttributeMap(dist));
        } else {
            return playerPerkAttributes.computeIfAbsent(player.getUniqueID(), (uuid) -> new PerkAttributeMap(dist));
        }
    }

    public static PerkAttributeMap getMockInstance(LogicalSide side) {
        return new PerkAttributeMap(side);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clearClient() {
        playerPerkAttributesClient.clear();
    }

    public static void clearServer() {
        playerPerkAttributes.clear();
    }

}
