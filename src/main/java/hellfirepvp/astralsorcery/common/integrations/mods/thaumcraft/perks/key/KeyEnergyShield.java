/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.thaumcraft.perks.key;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.integrations.mods.thaumcraft.perks.KeyPerkThaumcraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyEnergyShield
 * Created by HellFirePvP
 * Date: 18.11.2018 / 22:42
 */
public class KeyEnergyShield extends KeyPerkThaumcraft {

    public KeyEnergyShield(String name, int x, int y) {
        super(name, x, y);
    }

    @SubscribeEvent
    public void on(AttributeEvent.PostProcessVanilla event) {
        EntityPlayer owner = event.getPlayer();
        if (owner != null && event.getAttribute().equals(SharedMonsterAttributes.MAX_HEALTH)) {
            if (owner.getEntityWorld() == null) {
                return; //Srsly, fck you ExU2. do your stuff correlty for once.
            }
            Side side = owner.getEntityWorld().isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(owner, side);
            if (prog.hasPerkEffect(this)) {
                event.setValue(1);
            }
        }
    }

    @Override
    public boolean addLocalizedTooltip(Collection<String> tooltip) {
        super.addLocalizedTooltip(tooltip);
        return false;
    }
}
