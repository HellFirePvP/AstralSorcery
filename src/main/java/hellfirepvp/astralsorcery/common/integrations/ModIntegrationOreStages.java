/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import net.darkhax.orestages.api.OreTiersAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationOreStages
 * Created by HellFirePvP
 * Date: 19.05.2018 / 18:56
 */
public class ModIntegrationOreStages {

    @SideOnly(Side.CLIENT)
    @Optional.Method(modid = "orestages")
    public static boolean canSeeOreClient(IBlockState test) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(player == null) return false;
        Tuple<String, IBlockState> replacement;
        if((replacement = OreTiersAPI.getStageInfo(test)) != null) {
            return OreTiersAPI.hasStage(player, replacement.getFirst());
        }
        return true;
    }

}
