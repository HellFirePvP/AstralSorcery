/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.starmap;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.DrawnConstellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;

import java.awt.*;
import java.util.*;
import java.util.List;

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
    private Map<IConstellation, List<Point>> mapOffsets = new HashMap<>(); //Just kept here for drawing reasons...

    public static ActiveStarMap compile(List<DrawnConstellation> constellations) {
        ActiveStarMap asm = new ActiveStarMap();

        Collections.reverse(constellations);
        List<Rectangle> usedSpace = new LinkedList<>();

        int maxSize = 50 * 50; //w * h

        for (DrawnConstellation c : constellations) {
            Point center = c.point;
            Rectangle used = new Rectangle(
                    center.x - 25, center.y - 25,
                    50, 50);

            List<Rectangle> intersecting = new LinkedList<>();
            for (Rectangle other : usedSpace) {
                Rectangle r = used.intersection(other);
                if(!r.isEmpty()) {
                    intersecting.add(r);
                }
            }

            usedSpace.add(used);

            //I don't need to do deeper recursion than 2nd degree since it's only 3 constellations at most.
            //Thus there's only at most 1 intersection between the other 2.
            int intersectingSize = 0;
            for (int i = 0; i < intersecting.size(); i++) {
                Rectangle r = intersecting.get(i);
                intersectingSize += (r.width * r.height);
                for (int j = i; j < intersecting.size(); j++) {
                    Rectangle r2 = intersecting.get(j);
                    if (r2.equals(r)) continue;

                    Rectangle ir = r.intersection(r2);
                    if (!ir.isEmpty()) {
                        intersectingSize -= (ir.width * ir.height);
                    }
                }
            }

            float perc = 1F - (((float) intersectingSize) / ((float) maxSize));
            if(asm.starProportions.containsKey(c.constellation)) {
                asm.starProportions.put(c.constellation, asm.starProportions.get(c.constellation) + perc);
            } else {
                asm.starProportions.put(c.constellation, perc);
            }
        }

        //Crop it down for duplicates.
        Map<IConstellation, Float> multipliers = new HashMap<>();
        for (IConstellation c : asm.starProportions.keySet()) {
            int count = 0;
            for (DrawnConstellation dc : constellations) {
                if(dc.constellation.equals(c)) {
                    count++;
                }
            }
            if(count > 1) {
                multipliers.put(c, (float) count);
            }
        }
        for (IConstellation c : multipliers.keySet()) {
            asm.starProportions.put(c, asm.starProportions.get(c) / multipliers.get(c));
        }
        for (DrawnConstellation c : constellations) {
            if(asm.mapOffsets.containsKey(c.constellation)) {
                asm.mapOffsets.get(c.constellation).add(c.point);
            } else {
                asm.mapOffsets.put(c.constellation, Lists.newArrayList(c.point));
            }
        }
        shiftDistribution(asm);
        return asm;
    }

    private static void shiftDistribution(ActiveStarMap asm) {
        Map<IConstellation, Float> out = new HashMap<>();
        for (Map.Entry<IConstellation, Float> entry : asm.starProportions.entrySet()) {
            float perc = entry.getValue();
            perc = MathHelper.clamp(perc, 0F, 1F);
            if(perc >= 0.75F) {
                perc = (perc * 3F) - 2.25F;
                perc *= perc;
                perc = -perc + 1;
            } else {
                perc = (perc * (4F / 3F)) - 1F;
                perc *= perc;
                perc = -perc + 1;
            }
            out.put(entry.getKey(), perc);
        }
        asm.starProportions = out;
        out = new HashMap<>();
        for (Map.Entry<IConstellation, Float> entry : asm.starProportions.entrySet()) {
            float perc = entry.getValue();
            out.put(entry.getKey(), MathHelper.sqrt(perc));
        }
        asm.starProportions = out;
    }

    public boolean tryApplyEnchantments(ItemStack stack) {
        if(stack.isEmpty()) return false;
        Map<IConstellation, List<ConstellationMapEffectRegistry.EnchantmentMapEffect>> effectMap = new HashMap<>();

        for (IConstellation c : starProportions.keySet()) {
            ConstellationMapEffectRegistry.MapEffect e = ConstellationMapEffectRegistry.getEffects(c);
            if (e != null) {
                List<ConstellationMapEffectRegistry.EnchantmentMapEffect> applicable = new LinkedList<>();
                for (ConstellationMapEffectRegistry.EnchantmentMapEffect enchEffect : e.enchantmentEffects) {
                    Enchantment ench = enchEffect.ench;
                    if ((((stack.getItem() instanceof ItemBook || stack.getItem() instanceof ItemEnchantedBook) && ench.isAllowedOnBooks()) || ench.canApply(stack))) {
                        applicable.add(enchEffect);
                    }
                }
                effectMap.put(c, applicable);
            }
        }
        if (effectMap.isEmpty()) return false;

        boolean appliedSomething = false;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

        lblEnchantments:
        for (Map.Entry<IConstellation, List<ConstellationMapEffectRegistry.EnchantmentMapEffect>> entry : effectMap.entrySet()) {
            for (ConstellationMapEffectRegistry.EnchantmentMapEffect effect : entry.getValue()) {
                if (enchantments.containsKey(effect.ench)) continue;
                if (!effect.ignoreCompaibility) {
                    for (Enchantment existing : enchantments.keySet()) {
                        if(!existing.isCompatibleWith(effect.ench)) {
                            continue lblEnchantments;
                        }
                    }
                }

                Float perc = starProportions.get(entry.getKey());
                if (perc == null) continue;

                float p = MathHelper.clamp(perc, 0F, 1F);
                int lvl = effect.minEnchLevel + Math.round((effect.maxEnchLevel - effect.minEnchLevel) * p);
                if(stack.getItem() instanceof ItemEnchantedBook) {
                    ((ItemEnchantedBook) stack.getItem()).addEnchantment(stack, new EnchantmentData(effect.ench, lvl));
                } else {
                    stack.addEnchantment(effect.ench, lvl);
                }
                enchantments = EnchantmentHelper.getEnchantments(stack); //Update
                appliedSomething = true;
            }
        }
        return appliedSomething;
    }

    public void tryApplyPotionEffects(ItemStack stack) {
        if(stack.isEmpty()) return;

        List<PotionEffect> applicableEffects = new LinkedList<>();
        for (IConstellation c : starProportions.keySet()) {
            ConstellationMapEffectRegistry.MapEffect me = ConstellationMapEffectRegistry.getEffects(c);
            if (me != null) {
                for (ConstellationMapEffectRegistry.PotionMapEffect effect : me.potionEffects) {
                    float perc = starProportions.get(c);
                    perc = MathHelper.clamp(perc, 0F, 1F);
                    int amp = effect.minPotionAmplifier + Math.round((effect.maxPotionAmplifier - effect.minPotionAmplifier) * perc);
                    int tDuration = 4 * 1200 + Math.round(rand.nextFloat() * 2 * 1200);
                    applicableEffects.add(new PotionEffect(effect.potion, tDuration, amp, false, true));
                }
            }
        }
        if (rand.nextInt(30) == 0) {
            applicableEffects.add(new PotionEffect(RegistryPotions.potionCheatDeath, 2 * 1200 + Math.round(rand.nextFloat() * 6 * 1200), 0, false, true));
        }
        stack.setTranslatableName("potion.as.crafted.name");
        Collections.shuffle(applicableEffects);
        PotionUtils.appendEffects(stack, applicableEffects);
    }

    public Collection<IConstellation> getConstellations() {
        return Collections.unmodifiableCollection(this.starProportions.keySet());
    }

    public Map<IConstellation, List<Point>> getMapOffsets() {
        return Collections.unmodifiableMap(mapOffsets);
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

        l = new NBTTagList();
        for (Map.Entry<IConstellation, List<Point>> offsetEntry : mapOffsets.entrySet()) {
            NBTTagCompound c = new NBTTagCompound();
            c.setString("cst", offsetEntry.getKey().getUnlocalizedName());
            NBTTagList posList = new NBTTagList();
            for (Point p : offsetEntry.getValue()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("x", p.x);
                tag.setInteger("y", p.y);
                posList.appendTag(tag);
            }
            c.setTag("posList", posList);
            l.appendTag(c);
        }
        cmp.setTag("offsetMap", l);
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
        list = cmp.getTagList("offsetMap", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound c = list.getCompoundTagAt(i);
            String str = c.getString("cst");
            IConstellation cst = ConstellationRegistry.getConstellationByName(str);
            if(cst != null) {
                NBTTagList posList = c.getTagList("posList", Constants.NBT.TAG_COMPOUND);
                List<Point> positions = Lists.newArrayList();
                for (int j = 0; j < posList.tagCount(); j++) {
                    NBTTagCompound tag = posList.getCompoundTagAt(j);
                    positions.add(new Point(tag.getInteger("x"), tag.getInteger("y")));
                }
                map.mapOffsets.put(cst, positions);
            }
        }
        return map;
    }

}
