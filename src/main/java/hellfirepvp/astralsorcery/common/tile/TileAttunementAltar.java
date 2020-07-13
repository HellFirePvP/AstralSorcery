/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingSprite;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.util.sound.PositionedLoopSound;
import hellfirepvp.astralsorcery.common.base.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.constellation.world.ConstellationHandler;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.crafting.nojson.AttunementCraftingRegistry;
import hellfirepvp.astralsorcery.common.crafting.nojson.attunement.AttunementRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.entity.EntityFlare;
import hellfirepvp.astralsorcery.common.item.ItemConstellationPaper;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    //Client Misc visuals
    private Map<BlockPos, Object> activeStarSprites = new HashMap<>();

    //Client Misc sounds
    private Object attunementAltarIdleSound = null;

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

            if (this.activeConstellation != null) {
                for (BlockPos pos : this.getConstellationPositions(this.activeConstellation)) {
                    TileSpectralRelay relay = MiscUtils.getTileAt(getWorld(), pos, TileSpectralRelay.class, false);
                    if (relay != null && !relay.getInventory().getStackInSlot(0).isEmpty()) {
                        ItemUtils.dropInventory(relay.getInventory(), getWorld(), pos.up());
                        relay.getInventory().clearInventory();
                    }
                }
            }
        }
    }

    public void finishActiveRecipe() {
        if (this.currentRecipe != null) {
            this.currentRecipe.finishRecipe(this);
            this.currentRecipe.stopCrafting(this);
            this.currentRecipe = null;
            this.markForUpdate();

            EntityFlare.spawnAmbientFlare(getWorld(), getPos().add(-5 + rand.nextInt(11), 1 + rand.nextInt(3), -5 + rand.nextInt(11)));
            EntityFlare.spawnAmbientFlare(getWorld(), getPos().add(-5 + rand.nextInt(11), 1 + rand.nextInt(3), -5 + rand.nextInt(11)));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickEffects() {
        if (!hasMultiblock() || !doesSeeSky()) {
            this.tickEffectNonActive();
            return;
        }

        this.spawnAmbientEffects();
        this.spawnHighlightedEffects();
        this.tickEffectsConstellation();

        if (!this.canPlayConstellationActiveEffects()) {
            this.tickEffectNonActive();
            return;
        }

        this.tickEffectActive();
        this.tickConstellationBeams();

        if (this.getActiveRecipe() == null) {
            this.tickSoundIdle();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickEffectsConstellation() {
        if (this.activeConstellation != null) {
            Set<BlockPos> positions = this.getConstellationPositions(this.activeConstellation);
            for (BlockPos key : this.activeStarSprites.keySet()) {
                if (!positions.contains(key)) {
                    FXFacingSprite sprite = (FXFacingSprite) this.activeStarSprites.get(key);
                    if (!sprite.isRemoved()) {
                        sprite.requestRemoval();
                    }
                }
            }
            float night = DayTimeHelper.getCurrentDaytimeDistribution(getWorld());
            for (BlockPos key : positions) {
                if (!this.activeStarSprites.containsKey(key)) {
                    FXFacingSprite sprite = EffectHelper.of(EffectTemplatesAS.FACING_SPRITE)
                            .spawn(new Vector3(key).add(0.5, 0.5, 0.5))
                            .setSprite(SpritesAS.SPR_RELAY_FLARE)
                            .setScaleMultiplier(1.4F)
                            .refresh(fx -> this.canPlayConstellationActiveEffects());

                    this.activeStarSprites.put(key, sprite);
                } else {
                    FXFacingSprite spr = (FXFacingSprite) this.activeStarSprites.get(key);
                    if (spr.isRemoved()) {
                        EffectHelper.refresh(spr, EffectTemplatesAS.FACING_SPRITE);
                    }
                }

                if (night >= 0.1F && getActiveRecipe() == null) {
                    this.playConstellationHighlightParticles(this.activeConstellation, key, night);
                }
            }
            this.playAltarConstellationHighlightParticles(this.activeConstellation, night);
        } else if (!this.activeStarSprites.isEmpty()) {
            for (BlockPos key : this.activeStarSprites.keySet()) {
                FXFacingSprite sprite = (FXFacingSprite) this.activeStarSprites.get(key);
                if (!sprite.isRemoved()) {
                    sprite.requestRemoval();
                }
            }
            this.activeStarSprites.clear();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickSoundIdle() {
        if (SoundHelper.getSoundVolume(SoundCategory.BLOCKS) <= 0) {
            this.attunementAltarIdleSound = null;
            return;
        }

        if (this.attunementAltarIdleSound == null || ((PositionedLoopSound) this.attunementAltarIdleSound).hasStoppedPlaying()) {
            this.attunementAltarIdleSound = SoundHelper.playSoundLoopFadeInClient(SoundsAS.ATTUNEMENT_ATLAR_IDLE,
                    new Vector3(this).add(0.5, 1, 0.5),
                    1F,
                    1F,
                    false,
                    (s) -> !this.canPlayConstellationActiveEffects() ||
                            SoundHelper.getSoundVolume(SoundCategory.BLOCKS) <= 0 ||
                            this.getActiveRecipe() != null)
                    .setFadeInTicks(20)
                    .setFadeOutTicks(20);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickConstellationBeams() {
        VFXColorFunction<?> beamColor = VFXColorFunction.constant(ColorsAS.DEFAULT_GENERIC_PARTICLE);
        float beamSize = 0.8F;
        for (Tuple<BlockPos, BlockPos> conn : this.getConstellationConnectionPositions(this.activeConstellation)) {
            Vector3 from = new Vector3(conn.getA()).add(0.5, 0.5, 0.5);
            Vector3 to   = new Vector3(conn.getB()).add(0.5, 0.5, 0.5);

            if (this.getTicksExisted() % 45 == 0) {
                EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                        .spawn(from)
                        .setup(to, beamSize, beamSize)
                        .color(beamColor);
                EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                        .spawn(to)
                        .setup(from, beamSize, beamSize)
                        .color(beamColor);
            }

            if (rand.nextBoolean()) {
                Vector3 at = from.clone()
                        .subtract(to)
                        .multiply(rand.nextFloat())
                        .add(to)
                        .add(Vector3.random().multiply(rand.nextFloat() * 0.25F));

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(at)
                        .alpha(VFXAlphaFunction.FADE_OUT)
                        .color(VFXColorFunction.constant(this.activeConstellation.getConstellationColor()))
                        .setScaleMultiplier(0.2F + rand.nextFloat() * 0.05F)
                        .setMaxAge(15 + rand.nextInt(5));
            }
        }
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
    public boolean canPlayConstellationActiveEffects() {
        WorldContext ctx = SkyHandler.getContext(getWorld(), LogicalSide.CLIENT);

        return ctx != null &&
                !this.isRemoved() &&
                this.hasMultiblock() &&
                this.doesSeeSky() &&
                this.getActiveConstellation() != null &&
                DayTimeHelper.isNight(getWorld()) &&
                ctx.getConstellationHandler().isActiveCurrently(getActiveConstellation(), MoonPhase.fromWorld(getWorld()));
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
    private void spawnHighlightedEffects() {
        if (this.canPlayConstellationActiveEffects()) {
            return;
        }
        WorldContext ctx = SkyHandler.getContext(getWorld(), LogicalSide.CLIENT);
        if (ctx == null) {
            return;
        }

        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null || player.getDistanceSq(new Vec3d(getPos())) >= 256) {
            return;
        }
        Tuple<Hand, ItemStack> heldTpl = MiscUtils.getMainOrOffHand(player, stack -> stack.getItem() instanceof ItemConstellationPaper);
        if (heldTpl != null) {
            ItemStack cstPaper = heldTpl.getB();
            IConstellation cst = ((ItemConstellationPaper) cstPaper.getItem()).getConstellation(cstPaper);
            if (cst != null &&
                    ResearchHelper.getClientProgress().hasConstellationDiscovered(cst) &&
                    ctx.getConstellationHandler().isActiveCurrently(cst, MoonPhase.fromWorld(getWorld()))) {
                float night = DayTimeHelper.getCurrentDaytimeDistribution(getWorld());
                if (night >= 0.1F) {
                    for (BlockPos pos : this.getConstellationPositions(cst)) {
                        this.playConstellationHighlightParticles(cst, pos, night);
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playConstellationHighlightParticles(IConstellation cst, BlockPos pos, float nightPercent) {
        Vector3 at = new Vector3(pos).add(0.5, 0, 0.5);
        Vector3 offset = Vector3.random().multiply(0.5F).setY(0);
        if (rand.nextInt(3) == 0) {
            offset.multiply(0.5);
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at.add(offset))
                    .color(VFXColorFunction.constant(cst.getConstellationColor()))
                    .setGravityStrength(-0.002F)
                    .setMotion(Vector3.random().addY(3).normalize().multiply(0.03 + rand.nextFloat() * 0.01))
                    .setAlphaMultiplier(0.6F * nightPercent)
                    .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                    .alpha(VFXAlphaFunction.FADE_OUT);
        } else if (rand.nextInt(3) == 0) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at.add(offset))
                    .setGravityStrength(-0.0005F)
                    .setMotion(Vector3.random().addY(3).normalize().multiply(0.005))
                    .setAlphaMultiplier(0.6F * nightPercent)
                    .setScaleMultiplier(0.4F + rand.nextFloat() * 0.2F)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .setMaxAge(60 + rand.nextInt(40));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playAltarConstellationHighlightParticles(IConstellation cst, float nightPercent) {
        Vector3 at = new Vector3(getPos())
                .add(0.5, 0, 0.5)
                .add(Vector3.random().setY(0).multiply(0.65F));

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(at)
                .color(VFXColorFunction.constant(cst.getConstellationColor().brighter()))
                .setGravityStrength(-0.0015F)
                .setMotion(Vector3.random().addY(3).normalize().multiply(0.03 + rand.nextFloat() * 0.015))
                .setAlphaMultiplier(0.85F * nightPercent)
                .setScaleMultiplier(0.2F + rand.nextFloat() * 0.1F)
                .alpha(VFXAlphaFunction.FADE_OUT);
    }

    @Nullable
    public IConstellation getActiveConstellation() {
        return activeConstellation;
    }

    @Nullable
    public AttunementRecipe.Active getActiveRecipe() {
        return currentRecipe;
    }

    @Nullable
    @Override
    public StructureType getRequiredStructureType() {
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
        if (match != null && cstHandler.isActiveCurrently(match, MoonPhase.fromWorld(getWorld()))) {
            return match;
        }
        return null;
    }

    public Set<BlockPos> getConstellationPositions(IConstellation cst) {
        Set<BlockPos> offsetPositions = new HashSet<>();
        for (StarLocation sl : cst.getStars()) {
            int x = sl.x / 2;
            int z = sl.y / 2;
            offsetPositions.add(new BlockPos(x - 7, 0, z - 7).add(getPos()));
        }
        return offsetPositions;
    }

    private Set<Tuple<BlockPos, BlockPos>> getConstellationConnectionPositions(IConstellation cst) {
        Set<Tuple<BlockPos, BlockPos>> offsetPositions = new HashSet<>();
        for (StarConnection conn : cst.getStarConnections()) {
            StarLocation from = conn.from;
            StarLocation to = conn.to;
            int fX = from.x / 2;
            int fZ = from.y / 2;
            int tX = to.x / 2;
            int tZ = to.y / 2;
            offsetPositions.add(
                    new Tuple<>(new BlockPos(fX - 7, 0, fZ - 7).add(getPos()),
                            new BlockPos(tX - 7, 0, tZ - 7).add(getPos()))
            );
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

        if (this.activeConstellation != null) {
            compound.putString("activeConstellation", this.activeConstellation.getRegistryName().toString());
        }

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

        if (compound.contains("activeConstellation")) {
            this.activeConstellation = ConstellationRegistry.getConstellation(new ResourceLocation(compound.getString("activeConstellation")));
        } else {
            this.activeConstellation = null;
        }

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
