/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeArmorEffect
 * Created by HellFirePvP
 * Date: 09.10.2017 / 23:23
 */
public abstract class CapeArmorEffect {

    private NBTTagCompound data = new NBTTagCompound();

    protected CapeArmorEffect() {}

    //Must not be overwritten. Called reflectively to access the armoreffects for a given cape.
    CapeArmorEffect(NBTTagCompound cmp) {
        this.data = cmp.copy();
    }

    public abstract IConstellation getAssociatedConstellation();

    @SideOnly(Side.CLIENT)
    public abstract float getActiveParticleChance();

    public final NBTTagCompound getData() {
        return this.data;
    }

    public final void flush(EntityLivingBase entity) {
        ItemStack is = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if(!is.isEmpty()) {
            NBTTagCompound cmp = NBTHelper.getPersistentData(is);
            flush(cmp);
        }
    }

    public final void flush(NBTTagCompound out) {
        for (String key : this.data.getKeySet()) {
            out.setTag(key, out.getTag(key));
        }
    }

}
