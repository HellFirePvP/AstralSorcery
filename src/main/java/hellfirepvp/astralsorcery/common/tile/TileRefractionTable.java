/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.function.RefreshFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.vfx.FXSpritePlane;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.common.constellation.DrawnConstellation;
import hellfirepvp.astralsorcery.common.constellation.engraving.EngravedStarMap;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.item.ItemInfusedGlass;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import hellfirepvp.astralsorcery.common.util.tile.NamedInventoryTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileRefractionTable
 * Created by HellFirePvP
 * Date: 26.04.2020 / 20:18
 */
public class TileRefractionTable extends TileEntityTick implements NamedInventoryTile {

    private static float RUN_TIME = 10 * 20F;

    private int runTick = 0;

    private ItemStack glassStack = ItemStack.EMPTY;
    //Either have parchment or an input. not both.
    private int parchmentCount = 0;
    private ItemStack inputStack = ItemStack.EMPTY;

    private Object effectHalo;

    public TileRefractionTable() {
        super(TileEntityTypesAS.REFRACTION_TABLE);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isRemote()) {
            playEngravingEffects();
        } else {
            if (DayTimeHelper.isNight(getWorld()) &&
                    this.doesSeeSky() &&
                    isValidGlassStack(this.getGlassStack())) {

                EngravedStarMap starMap = ItemInfusedGlass.getEngraving(this.getGlassStack());
                if (starMap != null &&
                        !this.hasParchment() &&
                        !this.getInputStack().isEmpty() &&
                        starMap.canAffect(this.getInputStack())) {
                    runTick++;
                    if (runTick > RUN_TIME) {
                        this.setInputStack(starMap.applyEffects(this.getInputStack()));
                        ItemStack glassStack = this.getGlassStack();
                        if (glassStack.attemptDamageItem(1, rand, null)) {
                            glassStack.shrink(1);
                            this.setGlassStack(glassStack);
                            SoundHelper.playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, this.getWorld(), this.getPos(), rand.nextFloat() * 0.5F + 1F, rand.nextFloat() * 0.2F + 0.8F);
                        }
                        this.resetWorkTick();
                    }
                    markForUpdate();
                } else {
                    this.resetWorkTick();
                }
            } else {
                this.resetWorkTick();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playEngravingEffects() {
        if (this.runTick <= 0) {
            return;
        }

        if (this.effectHalo != null && ((FXSpritePlane) this.effectHalo).isRemoved()) {
            EffectHelper.refresh((FXSpritePlane) this.effectHalo, EffectTemplatesAS.TEXTURE_SPRITE);
        }
        if (this.effectHalo == null) {
            this.effectHalo = EffectHelper.of(EffectTemplatesAS.TEXTURE_SPRITE)
                    .spawn(new Vector3(this).add(0.5, 0.8, 0.5))
                    .setSprite(SpritesAS.SPR_HALO_INFUSION)
                    .setAxis(Vector3.RotAxis.Y_AXIS)
                    .setNoRotation(0)
                    .setScaleMultiplier(0.8F)
                    .setAlphaMultiplier(0.8F)
                    .alpha(((fx, alpha, pTicks) -> MathHelper.clamp(alpha * getRunProgress(), 0F, 1F)))
                    .refresh(RefreshFunction.tileExistsAnd(this, (thisTile, fx) -> thisTile.getRunProgress() > 0));
        }

        Vector3 offset = new Vector3(-5.0 / 16.0, 1.505, -3.0 / 16.0);
        int random = rand.nextInt(ColorsAS.REFRACTION_TABLE_COLORS.length);
        if (random >= ColorsAS.REFRACTION_TABLE_COLORS.length / 2) { //0-5 is left, 6-11 is right
            offset.addX(24.0 / 16.0);
        }
        offset.addZ((random % (ColorsAS.REFRACTION_TABLE_COLORS.length / 2)) * (4.0 / 16.0));
        offset.add(rand.nextFloat() * 0.1, 0, rand.nextFloat() * 0.1).add(pos);
        Color color = ColorsAS.REFRACTION_TABLE_COLORS[random];

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(offset)
                .setGravityStrength(-0.002F)
                .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                .alpha(VFXAlphaFunction.FADE_OUT)
                .color(VFXColorFunction.constant(color))
                .setMaxAge(30 + rand.nextInt(30));

        if (rand.nextFloat() < (getRunProgress() * 2F)) {
            Vector3 target = new Vector3(this).add(0.5, 0.9, 0.5);

            EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                    .spawn(offset)
                    .makeDefault(target)
                    .color(VFXColorFunction.constant(color));

            FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(offset)
                    .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                    .alpha(VFXAlphaFunction.proximity(target::clone, 1F))
                    .color(VFXColorFunction.constant(color))
                    .setMaxAge(45);

            Vector3 mov = target.clone().subtract(offset).normalize().multiply(0.05 * rand.nextFloat());
            p.setMotion(mov);
        }

        if (rand.nextInt(3) == 0) {
            EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                    .spawn(offset)
                    .setup(offset.clone().addY(0.56F + rand.nextFloat() * 0.2F), 0.25F, 0.25F)
                    .color(VFXColorFunction.constant(color));
        }

        if (rand.nextInt(4) == 0) {
            Color beamColor = MiscUtils.eitherOf(rand,
                    ColorsAS.CONSTELLATION_TYPE_MAJOR,
                    ColorsAS.CONSTELLATION_TYPE_WEAK,
                    ColorsAS.CONSTELLATION_TYPE_MINOR);

            Vector3 beamOffset = new Vector3(this).add(0.1 + rand.nextFloat() * 0.8, 0.8, 0.1 + rand.nextFloat() * 0.8F);
            EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                    .spawn(beamOffset)
                    .setup(beamOffset.clone().addY(1 + rand.nextFloat() * 0.5), 0.5F, 0.5F)
                    .color(VFXColorFunction.constant(beamColor))
                    .setMaxAge(25 + rand.nextInt(5));
        }
    }

    private void resetWorkTick() {
        if (this.runTick > 0) {
            this.runTick = 0;
            this.markForUpdate();
        }
    }

    public int addParchment(int toAdd) {
        if (this.inputStack.isEmpty()) {
            int overflow = Math.max(this.parchmentCount + toAdd - 64, 0);
            int addable = toAdd - overflow;
            this.parchmentCount += addable;
            this.markForUpdate();
            return overflow;
        }
        return toAdd;
    }

    public int getParchmentCount() {
        return this.parchmentCount;
    }

    public boolean hasParchment() {
        return this.parchmentCount > 0;
    }

    public void engraveGlass(List<DrawnConstellation> constellations) {
        if (this.hasParchment() && this.hasUnengravedGlass()) {
            this.parchmentCount--;
            ItemInfusedGlass.setEngraving(this.getGlassStack(), EngravedStarMap.buildStarMap(this.getWorld(), constellations));
            this.markForUpdate();
        }
    }

    @Nonnull
    public ItemStack setInputStack(@Nonnull ItemStack inputStack) {
        ItemStack prevInput = this.inputStack.copy();
        if (this.parchmentCount > 0) {
            prevInput = new ItemStack(ItemsAS.PARCHMENT, this.parchmentCount);
            this.parchmentCount = 0;
        }
        this.inputStack = inputStack.copy();
        this.markForUpdate();
        return prevInput;
    }

    @Nonnull
    public ItemStack getInputStack() {
        return this.hasParchment() ? new ItemStack(ItemsAS.PARCHMENT, this.getParchmentCount()) : this.inputStack.copy();
    }

    public static boolean isValidGlassStack(@Nonnull ItemStack glassStack) {
        return !glassStack.isEmpty() && glassStack.getItem() instanceof ItemInfusedGlass;
    }

    @Nonnull
    public ItemStack setGlassStack(@Nonnull ItemStack glassStack) {
        if (!glassStack.isEmpty() && !isValidGlassStack(glassStack)) {
            return ItemStack.EMPTY;
        }
        ItemStack prevStack = this.glassStack;
        this.glassStack = glassStack;
        this.markForUpdate();
        return prevStack;
    }

    @Nonnull
    public ItemStack getGlassStack() {
        return glassStack;
    }

    public boolean hasUnengravedGlass() {
        return isValidGlassStack(this.getGlassStack()) &&
                ItemInfusedGlass.getEngraving(this.getGlassStack()) == null;
    }

    public float getRunProgress() {
        return MathHelper.clamp(this.runTick / RUN_TIME, 0F, 1F);
    }

    public void dropContents() {
        Vector3 at = new Vector3(this).add(0.5, 0.5, 0.5);
        if (!this.getGlassStack().isEmpty()) {
            ItemUtils.dropItemNaturally(this.getWorld(), at.getX(), at.getY(), at.getZ(), this.getGlassStack());
            this.setGlassStack(ItemStack.EMPTY);
        }
        if (!this.getInputStack().isEmpty()) {
            ItemUtils.dropItemNaturally(this.getWorld(), at.getX(), at.getY(), at.getZ(), this.getInputStack());
            this.setInputStack(ItemStack.EMPTY);
        }
        this.markForUpdate();
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("screen.astralsorcery.refraction_table");
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.runTick = compound.getInt("runTick");

        this.parchmentCount = compound.getInt("parchmentCount");
        this.inputStack = NBTHelper.getStack(compound, "inputStack");
        this.glassStack = NBTHelper.getStack(compound, "glassStack");
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.putInt("runTick", this.runTick);

        compound.putInt("parchmentCount", this.parchmentCount);
        NBTHelper.setStack(compound, "inputStack", this.inputStack);
        NBTHelper.setStack(compound, "glassStack", this.glassStack);
    }
}
