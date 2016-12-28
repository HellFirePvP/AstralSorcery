package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.entity.player.EntityPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemAlignmentChargeConsumer
 * Created by HellFirePvP
 * Date: 27.12.2016 / 21:52
 */
public interface ItemAlignmentChargeConsumer extends ItemAlignmentChargeRevealer {

    default public void modifyAlignmentCharge(EntityPlayer player, double charge) {
        ResearchManager.modifyAlignmentCharge(player, charge);
    }

}
