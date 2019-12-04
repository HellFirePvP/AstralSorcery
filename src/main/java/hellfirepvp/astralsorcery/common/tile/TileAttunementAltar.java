/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.constellation.world.ConstellationHandler;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.crafting.nojson.AttunementCraftingRegistry;
import hellfirepvp.astralsorcery.common.crafting.nojson.attunement.AttunementRecipe;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAttunementAltar
 * Created by HellFirePvP
 * Date: 17.11.2019 / 07:49
 */
public class TileAttunementAltar extends TileEntityTick {

    private IConstellation activeConstellation = null;
    private AttunementRecipe.Active currentRecipe = null;

    //Client Misc sounds and visuals
    private IConstellation highlightConstellation = null;
    private Object activeSound = null;
    private List<Object> activeStarSprites = new ArrayList<>();

    //TESR
    public static final int MAX_START_ANIMATION_TICK = 60;
    public static final int MAX_START_ANIMATION_SPIN = 100;
    public int activationTick = 0;
    public int prevActivationTick = 0;
    public boolean animate = false, tesrLocked = true;

    public TileAttunementAltar() {
        super(TileEntityTypesAS.ATTUNEMENT_ALTAR);
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isRemote()) {
            if (!doesSeeSky() || !hasMultiblock()) {

                if (this.activeConstellation != null) {
                    this.activeConstellation = null;
                    markForUpdate();
                }
                if (this.currentRecipe != null) {
                    this.currentRecipe = null;
                    markForUpdate();
                }
                return;
            }
            this.updateActiveConstellation();
            if (this.currentRecipe == null) {
                this.searchAndStartRecipe();
            } else {
                this.currentRecipe.tick(LogicalSide.SERVER, this);
                if (!this.currentRecipe.matches(this)) {
                    this.currentRecipe = null;
                    this.markForUpdate();
                } else if (this.currentRecipe.isFinished(this)) {
                    this.finishActiveRecipe();
                }
            }
        } else {
            tickEffects();
            if (this.currentRecipe != null) {
                this.currentRecipe.tick(LogicalSide.CLIENT, this);
            }
        }
    }

    private void updateActiveConstellation() {
        if (getTicksExisted() % 20 == 0) {
            IConstellation found = this.searchActiveConstellation();
            if (this.activeConstellation == null) {
                if (found != null) {
                    this.activeConstellation = found;
                    markForUpdate();
                }
            } else if (found != null) {
                if (!this.activeConstellation.equals(found)) {
                    this.activeConstellation = found;
                    markForUpdate();
                }
            } else {
                this.activeConstellation = null;
                markForUpdate();
            }
        }
    }

    public void finishActiveRecipe() {
        if (this.currentRecipe != null) {
            this.currentRecipe.finishRecipe(this);
            this.currentRecipe.stopCrafting(this);
            this.currentRecipe = null;
            this.markForUpdate();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickEffects() {
        if (!hasMultiblock() ||!doesSeeSky()) {
            tickEffectNonActive();
            return;
        }

        this.spawnAmbientEffects();

        if (getActiveConstellation() == null || !DayTimeHelper.isNight(getWorld())) {
            tickEffectNonActive();
            return;
        }

        tickEffectActive();
    }

    @OnlyIn(Dist.CLIENT)
    private void tickEffectNonActive() {
        animate = false;

        prevActivationTick = activationTick;
        if (activationTick > 0) {
            activationTick--;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickEffectActive() {
        animate = true;

        prevActivationTick = activationTick;
        if (activationTick < MAX_START_ANIMATION_TICK) {
            activationTick++;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnAmbientEffects() {
        if (rand.nextBoolean()) {
            Vector3 pos = new Vector3(this).add(
                    rand.nextFloat() * 15 - 7,
                    0.01,
                    rand.nextFloat() * 15 - 7);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(VFXColorFunction.WHITE)
                    .setAlphaMultiplier(0.7F)
                    .setScaleMultiplier(0.3F + rand.nextFloat() * 0.1F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void finishCraftingEffects(PktPlayEffect effect) {

    }

    @Nullable
    public IConstellation getActiveConstellation() {
        return ConstellationsAS.aevitas;
    }

    @Nullable
    public AttunementRecipe.Active getActiveRecipe() {
        return currentRecipe;
    }

    @Nullable
    @Override
    protected StructureType getRequiredStructureType() {
        return StructureTypesAS.PTYPE_ATTUNEMENT_ALTAR;
    }

    private void searchAndStartRecipe() {
        if (this.currentRecipe != null) {
            return;
        }
        AttunementRecipe match = this.searchMatchingRecipe();
        if (match != null) {
            this.currentRecipe = match.createRecipe(this);
            this.markForUpdate();
        }
    }

    @Nullable
    private AttunementRecipe searchMatchingRecipe() {
        for (AttunementRecipe recipe : AttunementCraftingRegistry.INSTANCE.getRecipes()) {
            if (recipe.canStartCrafting(this)) {
                return recipe;
            }
        }
        return null;
    }

    @Nullable
    private IConstellation searchActiveConstellation() {
        WorldContext ctx = SkyHandler.getContext(getWorld());
        if (ctx == null) {
            return null;
        }
        ConstellationHandler cstHandler = ctx.getConstellationHandler();
        IConstellation match = null;
        for (IConstellation cst : RegistriesAS.REGISTRY_CONSTELLATIONS.getValues()) {
            boolean isValid = true;
            for (BlockPos expectedRelayPos : getConstellationPositions(cst)) {
                if (expectedRelayPos.equals(this.getPos())) {
                    continue;
                }

                TileEntity tile = MiscUtils.getTileAt(getWorld(), expectedRelayPos, TileEntity.class, true);
                if (!(tile instanceof TileSpectralRelay) && !(tile instanceof TileAttunementAltar)) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                match = cst;
                break;
            }
        }
        if (match != null && cstHandler.isActive(match, getWorld())) {
            return match;
        }
        return null;
    }

    private Set<BlockPos> getConstellationPositions(IConstellation cst) {
        Set<BlockPos> offsetPositions = new HashSet<>();
        for (StarLocation sl : cst.getStars()) {
            int x = sl.x / 2;
            int z = sl.y / 2;
            offsetPositions.add(new BlockPos(x - 7, 0, z - 7).add(getPos()));
        }
        return offsetPositions;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(3.5, 2, 3.5);
    }

    @Override
    public void writeNetNBT(CompoundNBT compound) {
        super.writeNetNBT(compound);

        if (this.currentRecipe != null) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("recipe", this.currentRecipe.getRecipe().getKey().toString());
            this.currentRecipe.writeToNBT(nbt);
            compound.put("currentRecipe", nbt);
        }
    }

    @Override
    public void readNetNBT(CompoundNBT compound) {
        super.readNetNBT(compound);

        if (compound.contains("currentRecipe")) {
            CompoundNBT nbt = compound.getCompound("currentRecipe");
            AttunementRecipe recipe = AttunementCraftingRegistry.INSTANCE.getRecipe(new ResourceLocation(nbt.getString("recipe")));
            if (recipe != null) {
                this.currentRecipe = recipe.deserialize(this, nbt, this.currentRecipe);
            } else if (this.currentRecipe != null) {
                this.currentRecipe.stopEffects(this);
                this.currentRecipe = null;
            }
        } else if (this.currentRecipe != null) {
            this.currentRecipe.stopEffects(this);
            this.currentRecipe = null;
        }
    }
}
