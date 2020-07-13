/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.engraving;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.DrawnConstellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EngravedStarMap
 * Created by HellFirePvP
 * Date: 01.05.2020 / 07:31
 */
public class EngravedStarMap {

    private static final Random rand = new Random();

    private Map<IConstellation, Float> distributions;
    private List<DrawnConstellation> drawInformation;

    private EngravedStarMap(Map<IConstellation, Float> distributions, List<DrawnConstellation> drawnConstellations) {
        this.distributions = distributions;
        this.drawInformation = drawnConstellations;
    }

    public static EngravedStarMap buildStarMap(World world, List<DrawnConstellation> constellations) {
        float nightPerc = DayTimeHelper.getCurrentDaytimeDistribution(world);

        Map<DrawnConstellation, List<Rectangle.Double>> cstCoordinates = new HashMap<>();
        for (DrawnConstellation drawnCst : constellations) {
            cstCoordinates.put(drawnCst, createConstellationOffsets(drawnCst));
        }

        Map<IConstellation, Float> distributionMap = new HashMap<>();
        for (DrawnConstellation cst : cstCoordinates.keySet()) {
            List<Rectangle.Double> positions = cstCoordinates.get(cst);
            Set<Rectangle.Double> foundPositions = new HashSet<>();

            for (DrawnConstellation otherCst : cstCoordinates.keySet()) {
                if (cst.equals(otherCst)) {
                    continue;
                }

                List<Rectangle.Double> otherPositions = cstCoordinates.get(otherCst);

                for (Rectangle.Double starPos : positions) {
                    for (Rectangle.Double otherStarPos : otherPositions) {
                        if (starPos.intersects(otherStarPos)) {
                            foundPositions.add(starPos);
                        }
                    }
                }
            }

            float percent = 0.1F + 0.9F * MathHelper.clamp(((foundPositions.size() * 1.5F) / positions.size()) * nightPerc, 0F, 1F);
            float existingPercent = distributionMap.getOrDefault(cst.getConstellation(), 0.1F);
            if (percent >= existingPercent) {
                distributionMap.put(cst.getConstellation(), percent);
            }
        }
        return new EngravedStarMap(distributionMap, constellations);
    }

    private static List<Rectangle.Double> createConstellationOffsets(DrawnConstellation cst) {
        float width = DrawnConstellation.CONSTELLATION_STAR_SIZE;

        List<Rectangle.Double> positions = new ArrayList<>();
        for (StarLocation star : cst.getConstellation().getStars()) {
            //Rescale from 32 to 20 width, move to drawn offset coordinates
            double starX = star.x * DrawnConstellation.CONSTELLATION_SIZE_PART
                    + cst.getPoint().getX()
                    - DrawnConstellation.CONSTELLATION_DRAW_SIZE / 2F
                    - (width / 2F);
            double starY = star.y * DrawnConstellation.CONSTELLATION_SIZE_PART
                    + cst.getPoint().getY()
                    - DrawnConstellation.CONSTELLATION_DRAW_SIZE / 2F
                    - (width / 2F);

            positions.add(new Rectangle.Double(starX, starY, width, width));
        }
        return positions;
    }

    public boolean canAffect(@Nonnull ItemStack stack) {
        for (IConstellation cst : this.getConstellations()) {
            EngravingEffect effect = cst.getEngravingEffect();
            if (effect != null && !effect.getApplicableEffects(stack).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    public ItemStack applyEffects(@Nonnull ItemStack stack) {
        Map<IConstellation, List<EngravingEffect.ApplicableEffect>> effects = new HashMap<>();
        for (IConstellation cst : this.getConstellations()) {
            EngravingEffect effect = cst.getEngravingEffect();
            if (effect != null) {
                List<EngravingEffect.ApplicableEffect> applicable = effect.getApplicableEffects(stack);
                if (!applicable.isEmpty()) {
                    effects.put(cst, applicable);
                }
            }
        }
        for (IConstellation cst : effects.keySet()) {
            List<EngravingEffect.ApplicableEffect> applicable = effects.get(cst);
            float distribution = this.getDistribution(cst);
            for (EngravingEffect.ApplicableEffect effect : applicable) {
                stack = effect.apply(stack, distribution, rand);
            }
        }
        return stack;
    }

    public Collection<DrawnConstellation> getDrawnConstellations() {
        return Collections.unmodifiableCollection(this.drawInformation);
    }

    public Collection<IConstellation> getConstellations() {
        return Collections.unmodifiableCollection(this.distributions.keySet());
    }

    public float getDistribution(IConstellation cst) {
        return this.distributions.getOrDefault(cst, 0F);
    }

    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        ListNBT list = new ListNBT();
        distributions.forEach((cst, percent) -> {
            CompoundNBT cstTag = new CompoundNBT();
            NBTHelper.setResourceLocation(cstTag, "cst", cst.getRegistryName());
            cstTag.putFloat("percent", percent);
            list.add(cstTag);
        });
        tag.put("distributions", list);
        ListNBT listDrawn = new ListNBT();
        drawInformation.forEach(drawCst -> {
            CompoundNBT cstTag = new CompoundNBT();
            NBTHelper.setResourceLocation(cstTag, "cst", drawCst.getConstellation().getRegistryName());
            cstTag.putInt("x", drawCst.getPoint().x);
            cstTag.putInt("y", drawCst.getPoint().y);
            listDrawn.add(cstTag);
        });
        tag.put("drawInformation", listDrawn);
        return tag;
    }

    public static EngravedStarMap deserialize(CompoundNBT tag) {
        Map<IConstellation, Float> distributionMap = new HashMap<>();
        ListNBT list = tag.getList("distributions", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT cstTag = list.getCompound(i);
            IConstellation cst = ConstellationRegistry.getConstellation(new ResourceLocation(cstTag.getString("cst")));
            float percent = cstTag.getFloat("percent");
            if (cst != null && percent > 0) {
                distributionMap.put(cst, percent);
            }
        }
        List<DrawnConstellation> drawnConstellations = new ArrayList<>();
        ListNBT listDrawn = tag.getList("drawInformation", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < listDrawn.size(); i++) {
            CompoundNBT cstTag = listDrawn.getCompound(i);
            IConstellation cst = ConstellationRegistry.getConstellation(new ResourceLocation(cstTag.getString("cst")));
            Point offset = new Point(cstTag.getInt("x"), cstTag.getInt("y"));
            drawnConstellations.add(new DrawnConstellation(offset, cst));
        }
        return new EngravedStarMap(distributionMap, drawnConstellations);
    }
}
