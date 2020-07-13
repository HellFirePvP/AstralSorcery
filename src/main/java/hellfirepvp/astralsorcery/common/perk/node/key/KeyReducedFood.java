/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tick.PlayerTickPerk;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyReducedFood
 * Created by HellFirePvP
 * Date: 31.08.2019 / 22:13
 */
public class KeyReducedFood extends KeyPerk implements PlayerTickPerk {

    public KeyReducedFood(ResourceLocation name, int x, int y) {
        super(name, x, y);
    }

    @Override
    public void onPlayerTick(PlayerEntity player, LogicalSide side) {
        if (side.isServer() && rand.nextFloat() < 0.01) {
            FoodStats stats = player.getFoodStats();
            if (stats.getFoodLevel() < 20 || stats.getSaturationLevel() < 5) {
                stats.addStats(1, 0.3F);
            }
        }
    }
}
