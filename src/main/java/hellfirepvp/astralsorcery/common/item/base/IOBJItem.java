/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IOBJItem
 * Created by HellFirePvP
 * Date: 22.01.2017 / 15:01
 */
public interface IOBJItem {

    //If false is returned, getOBJModelNames will be queried to applyServer OBJ resource locations directly instead of remotely.
    @SideOnly(Side.CLIENT)
    default public boolean hasOBJAsSubmodelDefinition() {
        return false;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    default public String[] getOBJModelNames() {
        return null;
    }

}
