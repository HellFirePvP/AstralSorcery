/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.spell.plague;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpellPlague
 * Created by HellFirePvP
 * Date: 07.07.2017 / 11:06
 */
public class SpellPlague implements INBTSerializable<NBTTagCompound> {

    public static final ResourceLocation CAPABILITY_NAME = new ResourceLocation(AstralSorcery.MODID, "spell_plague_cap");

    @CapabilityInject(SpellPlague.class)
    public static Capability<SpellPlague> CAPABILITY_SPELL_PLAGUE = null;

    private List<PlagueEffect> effects = Lists.newLinkedList();

    private SpellPlague() {}

    public boolean onTick(EntityLivingBase affected) {
        Iterator<PlagueEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            PlagueEffect effect = iterator.next();
            if(!effect.hasExpired()) {
                effect.onTick(affected);
            } else {
                iterator.remove();
            }
        }
        return effects.isEmpty();
    }

    public void addEffect(EntityLivingBase entity, PlagueEffect effect) {
        this.effects.add(effect);
        if(!entity.isPotionActive(RegistryPotions.potionSpellPlague)) {
            entity.addPotionEffect(new PotionEffect(RegistryPotions.potionSpellPlague, 100, 0, true, false));
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound cmp = new NBTTagCompound();
        NBTTagList effects = new NBTTagList();
        for (PlagueEffect effect : this.effects) {
            NBTTagCompound cmpEffect = new NBTTagCompound();
            effect.writeNBT(cmpEffect);
            effects.appendTag(cmpEffect);
        }
        cmp.setTag("effects", effects);
        return cmp;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        NBTTagList effects = nbt.getTagList("effects", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < effects.tagCount(); i++) {
            this.effects.add(new PlagueEffect(effects.getCompoundTagAt(i)));
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

        private final SpellPlague defaultImpl = new SpellPlague();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CAPABILITY_SPELL_PLAGUE;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return hasCapability(capability, facing) ? CAPABILITY_SPELL_PLAGUE.cast(defaultImpl) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return defaultImpl.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            defaultImpl.deserializeNBT(nbt);
        }

    }

    public static class Factory implements Callable<SpellPlague> {

        @Override
        public SpellPlague call() throws Exception {
            return new SpellPlague();
        }

    }

}
