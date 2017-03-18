/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.starmap;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ActiveStarMap
 * Created by HellFirePvP
 * Date: 18.03.2017 / 17:37
 */
public class ActiveStarMap {

    private static final Random rand = new Random();

    private Map<IConstellation, Float> starProportions = new HashMap<>();

    public boolean tryApplyEnchantments(ItemStack stack) {
        if(stack.isEmpty()) return false;
        Map<IConstellation, ConstellationMapEffectRegistry.MapEffect> effectMap = new HashMap<>();

        for (IConstellation c : starProportions.keySet()) {
            ConstellationMapEffectRegistry.MapEffect e = ConstellationMapEffectRegistry.getEffects(c);
            if (e != null) {
                Enchantment ench = e.ench;
                if (ench.canApply(stack)) {
                    effectMap.put(c, e);
                }
            }
        }
        if (effectMap.isEmpty()) return false;

        boolean appliedSomething = false;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        for (Map.Entry<IConstellation, ConstellationMapEffectRegistry.MapEffect> entry : effectMap.entrySet()) {
            if (enchantments.containsKey(entry.getValue().ench)) continue;

            ConstellationMapEffectRegistry.MapEffect me = entry.getValue();
            Float perc = starProportions.get(entry.getKey());
            if (perc == null) continue;

            float p = MathHelper.clamp(perc, 0F, 1F);
            int lvl = me.minEnchLevel + Math.round((me.maxEnchLevel - me.minEnchLevel) * p);
            stack.addEnchantment(entry.getValue().ench, lvl);
            appliedSomething = true;
        }
        return appliedSomething;
    }

    public void tryApplyPotionEffects(EntityLivingBase base) {
        if (base.isDead) return;

        for (IConstellation c : starProportions.keySet()) {
            ConstellationMapEffectRegistry.MapEffect me = ConstellationMapEffectRegistry.getEffects(c);
            if (me != null) {
                float perc = starProportions.get(c);
                perc = MathHelper.clamp(perc, 0F, 1F);
                int amp = me.minPotionAmplifier + Math.round((me.maxPotionAmplifier - me.minPotionAmplifier) * perc);
                int tDuration = 4 * 1200 + Math.round(rand.nextFloat() * 7 * 1200);
                PotionEffect pe = base.getActivePotionEffect(me.potion);
                if (pe != null) {
                    if (pe.getAmplifier() < amp) {
                        base.removePotionEffect(me.potion);
                        base.addPotionEffect(new PotionEffect(me.potion, tDuration, amp, pe.getIsAmbient(), pe.doesShowParticles()));
                    } else {
                        base.removePotionEffect(me.potion);
                        base.addPotionEffect(new PotionEffect(me.potion, pe.getDuration() + tDuration, pe.getAmplifier() > amp ? pe.getAmplifier() : amp, pe.getIsAmbient(), pe.doesShowParticles()));
                    }
                } else {
                    base.addPotionEffect(new PotionEffect(me.potion, tDuration, amp, true, false));
                }
            }
        }
        if (rand.nextInt(8) == 0) {
            base.addPotionEffect(new PotionEffect(RegistryPotions.potionCheatDeath, 2 * 1200 + Math.round(rand.nextFloat() * 12 * 1200), 0, true, false));
        }
    }

    public Collection<IConstellation> getConstellations() {
        return Collections.unmodifiableCollection(this.starProportions.keySet());
    }

    public float getPercentage(IConstellation c) {
        if (!starProportions.containsKey(c)) return 0F;
        return starProportions.get(c);
    }

    public NBTTagCompound serialize() {
        NBTTagCompound cmp = new NBTTagCompound();
        NBTTagList l = new NBTTagList();
        for (Map.Entry<IConstellation, Float> entry : starProportions.entrySet()) {
            NBTTagCompound c = new NBTTagCompound();
            c.setString("cst", entry.getKey().getUnlocalizedName());
            c.setFloat("perc", entry.getValue());
            l.appendTag(c);
        }
        cmp.setTag("starMap", l);
        return cmp;
    }

    public static ActiveStarMap deserialize(NBTTagCompound cmp) {
        ActiveStarMap map = new ActiveStarMap();
        NBTTagList list = cmp.getTagList("starMap", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound c = list.getCompoundTagAt(i);
            String str = c.getString("cst");
            IConstellation cst = ConstellationRegistry.getConstellationByName(str);
            if(cst != null) {
                float perc = c.getFloat("perc");
                map.starProportions.put(cst, perc);
            }
        }
        return map;
    }

}
