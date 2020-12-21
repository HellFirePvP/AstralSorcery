/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LootAS
 * Created by HellFirePvP
 * Date: 02.05.2020 / 15:41
 */
public class LootAS {

    private LootAS() {}

    public static final ResourceLocation SHRINE_CHEST = AstralSorcery.key("shrine_chest");

    public static final ResourceLocation STARFALL_SHOOTING_STAR_REWARD = AstralSorcery.key("gameplay/starfall/shooting_star");

    public static class Functions {

        public static LootFunctionType LINEAR_LUCK_BONUS;
        public static LootFunctionType RANDOM_CRYSTAL_PROPERTIES;
        public static LootFunctionType COPY_CRYSTAL_PROPERTIES;
        public static LootFunctionType COPY_CONSTELLATION;
        public static LootFunctionType COPY_GATEWAY_COLOR;

    }
}
