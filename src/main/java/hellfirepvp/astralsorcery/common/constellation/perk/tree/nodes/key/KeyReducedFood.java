/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyReducedFood
 * Created by HellFirePvP
 * Date: 12.08.2018 / 09:24
 */
public class KeyReducedFood extends KeyPerk implements IPlayerTickPerk {

    public KeyReducedFood(String name, int x, int y) {
        super(name, x, y);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if(side == Side.SERVER) {
            FoodStats stats = player.getFoodStats();
            if(stats.foodExhaustionLevel > -10F) {
                stats.addExhaustion(-0.01F);
            }
        }
    }
}
