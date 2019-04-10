/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerObservatory
 * Created by HellFirePvP
 * Date: 27.05.2018 / 07:36
 */
//Dummy container to allow remote opening and easier handling on serverside for dismounting the observatory.
public class ContainerObservatory extends Container {

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

}
