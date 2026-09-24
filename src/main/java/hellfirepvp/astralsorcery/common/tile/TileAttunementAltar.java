/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityFX;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.vfx.VFXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.client.util.SoundUtil;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.level.ConstellationHandler;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.recipe.attunement.AttunementRecipe;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.ClientObject;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ObserverRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileAttunementAltar
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileAttunementAltar extends TileEntityTick<TileAttunementAltar.Data> {

    protected static final Tuple<BlockPos, BlockPos> ATTUNEMENT_SKY_CHECK_AREA =
            new Tuple<>(new BlockPos(-7, 1, -7), new BlockPos(7, 1, 7));

    //Client effects
    private final ClientObject<PlayableSoundInstance> idleSoundLoop = new ClientObject<>();
    private final Map<BlockPos, ClientObject<VFXFacingParticle>> starSprites = new HashMap<>();

    //TESR
    public static final int MAX_START_ANIMATION_TICK = 60;
    public static final int MAX_START_ANIMATION_SPIN = 100;
    public int activationTick = 0;
    public int prevActivationTick = 0;
    public boolean animate = false, tesrLocked = true;

    public TileAttunementAltar(BlockPos pos, BlockState blockState) {
        super(TileEntitiesAS.ATTUNEMENT_ALTAR, pos, blockState);
    }

    protected TileAttunementAltar(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        if (!this.hasStructure() || !this.doesSeeSky()) {
            this.getTileData().getActiveRecipe().ifPresent(recipe -> {
                this.getTileData().setActiveRecipe(null);
                this.getTileData().markForUpdate();
            });
            this.getTileData().getActiveConstellation().ifPresent(cst -> {
                this.getTileData().setActiveConstellation(null);
                this.getTileData().markForUpdate();
            });
            return;
        }

        this.updateActiveConstellation(level);
        if (!this.getTileData().getActiveRecipe().isPresent()) {
            this.searchActiveRecipe();
        } else {
            this.getTileData().getActiveRecipe().ifPresent(active -> {
                active.tick(LogicalSide.SERVER, this);
                if (!active.matches(this)) {
                    this.getTileData().setActiveRecipe(null);
                    this.getTileData().markForUpdate();
                } else if (active.isFinished(this)) {
                    this.finishActiveRecipe();
                }
            });
        }
    }

    private void searchActiveRecipe() {
        if (this.getTileData().getActiveRecipe().isPresent()) return;

        for (AttunementRecipe<?> recipe : AttunementRecipe.getAllRecipes()) {
            if (recipe.canStartCrafting(this)) {
                AttunementRecipe.Active<?, ?> active = recipe.createActiveRecipe(this);
                this.getTileData().setActiveRecipe(active);
                this.getTileData().markForUpdate();
                return;
            }
        }
    }

    public void finishActiveRecipe() {
        this.getTileData().getActiveRecipe().ifPresent(active -> {
            active.finishRecipe(this);
            active.stopCrafting(this);
            this.getTileData().setActiveRecipe(null);
            this.getTileData().markForUpdate();
        });
    }

    protected void updateActiveConstellation(ServerLevel level) {
        if (this.getTileData().getTicksExisted() % 20 != 0) return;
        AttunementConstellationFinder finder = new AttunementConstellationFinder(level, this.getBlockPos());
        BaseConstellation active = finder.searchActiveConstellation(level).orElse(null);
        BaseConstellation previous = this.getTileData().getActiveConstellation().orElse(null);
        if (active != previous) {
            this.getTileData().setActiveConstellation(active);
            this.getTileData().markForUpdate();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        super.clientTick(level);
        this.getTileData().getActiveRecipe().ifPresent(active -> {
            active.tick(LogicalSide.CLIENT, this);
        });

        if (!this.hasStructure() || !this.doesSeeSky()) {
            this.tickAnimationInactive();
            return;
        }

        this.spawnStructureEffects();
        if (!this.canPlayConstellationEffects(level)) {
            this.spawnConstellationPaperHighlightEffects(level);
            this.tickAnimationInactive();
            return;
        }

        this.tickAnimationActive();
        this.spawnActiveConstellationEffects(level);
        this.spawnActiveConstellationLightBeamEffects(level);
        if (!this.getTileData().getActiveRecipe().isPresent()) {
            this.tickPlaySoundIdle();
        }
    }

    private void tickAnimationInactive() {
        this.animate = false;

        this.prevActivationTick = this.activationTick;
        if (this.activationTick > 0) {
            this.activationTick--;
        }
    }

    private void tickAnimationActive() {
        this.animate = true;

        this.prevActivationTick = this.activationTick;
        if (this.activationTick < MAX_START_ANIMATION_TICK) {
            this.activationTick++;
        }
    }

    public boolean canPlayConstellationEffects(Level level) {
        return LevelSkyHandler.getContext(level).map(ctx -> {
            return !this.isRemoved() &&
                    this.hasStructure() &&
                    this.doesSeeSky() &&
                    this.getTileData().getActiveConstellation().isPresent() &&
                    DayTimeHelper.isNight(level) &&
                    ctx.getConstellationHandler().isCurrentlyActive(this.getTileData().getActiveConstellation().get());
        }).orElse(false);
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnStructureEffects() {
        if (rand.nextBoolean()) return;

        Vector3 pos = new Vector3(this).add(
                0.5F + (rand.nextFloat() - 0.5F) * 14F,
                0.01F,
                0.5F + (rand.nextFloat() - 0.5F) * 14F
        );

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(pos)
                .color(FXColorFunction.WHITE)
                .setAlpha(0.7F)
                .setScale(0.3F + rand.nextFloat() * 0.1F);
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnActiveConstellationEffects(Level level) {
        BaseConstellation cst = this.getTileData().getActiveConstellation().orElse(null);
        if (cst == null) {
            //Remove all star sprites
            if (!this.starSprites.isEmpty()) {
                this.starSprites.values().stream()
                        .map(ClientObject::get)
                        .filter(sprite -> !sprite.isRemoved())
                        .forEach(EntityFX::requestRemoval);
                this.starSprites.clear();
            }
            return;
        }

        float night = DayTimeHelper.getCurrentDaytimeDistribution(level);
        AttunementConstellationFinder finder = new AttunementConstellationFinder(Minecraft.getInstance().level, this.getBlockPos());
        Set<BlockPos> cstPositions = finder.getConstellationPositions(cst);

        //Clean up lingering star sprites
        Iterator<BlockPos> iterator = this.starSprites.keySet().iterator();
        while (iterator.hasNext()) {
            BlockPos key = iterator.next();
            if (!cstPositions.contains(key)) {
                VFXFacingParticle particle = this.starSprites.get(key).get();
                if (!particle.isRemoved()) {
                    particle.requestRemoval();
                }
                iterator.remove();
            }
        }

        for (BlockPos cstPos : cstPositions) {
            if (!this.starSprites.containsKey(cstPos)) {
                VFXFacingParticle particle = EffectHelper.of(EffectTemplatesAS.ATTUNEMENT_RELAY_FLARE)
                        .spawn(Vector3.atCenter(cstPos))
                        .setScale(1.4F)
                        .refresh(fx -> this.canPlayConstellationEffects(level));

                this.starSprites.put(cstPos, new ClientObject<>(particle));
            } else {
                VFXFacingParticle particle = this.starSprites.get(cstPos).get();
                if (particle.isRemoved()) {
                    EffectHelper.refresh(EffectTemplatesAS.ATTUNEMENT_RELAY_FLARE, particle);
                }
            }

            if (night >= 0.1F && !this.getTileData().getActiveRecipe().isPresent()) {
                this.spawnConstellationHighlightEffects(cst, cstPos, night);
            }
        }

        Vector3 at = Vector3.atBottomCenter(this)
                .add(Vector3.random(rand).setY(0).multiply(0.65F));
        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(at)
                .color(FXColorFunction.constant(cst.getConstellationColor()))
                .alpha(FXAlphaFunction.FADE_OUT)
                .setAlpha(0.85F * night)
                .setScale(0.2F + rand.nextFloat() * 0.1F)
                .setGravity(Vector3.y(0.0015F))
                .setMotion(Vector3.random(rand).addY(3).normalize().multiply(0.03 + rand.nextFloat() * 0.015));
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnActiveConstellationLightBeamEffects(Level level) {
        AttunementConstellationFinder finder = new AttunementConstellationFinder(level, this.getBlockPos());
        FXColorFunction<?> beamColor;
        if (this.getTileData().getActiveRecipe().isPresent()) {
            beamColor = this.getTileData().getActiveConstellation()
                    .<FXColorFunction<?>>map(cst -> FXColorFunction.constant(cst.getConstellationColor()))
                    .orElse(FXColorFunction.constant(ColorsAS.ATTUNEMENT_ALTAR_BEAM));
        } else {
             beamColor = FXColorFunction.constant(ColorsAS.ATTUNEMENT_ALTAR_BEAM);
        }
        float beamSize = 0.8F;

        this.getTileData().getActiveConstellation().ifPresent(cst -> {
            finder.getConstellationConnectionPositions(cst).forEach(conn -> {
                Vector3 from = Vector3.atCenter(conn.getA());
                Vector3 to   = Vector3.atCenter(conn.getB());

                if (this.getTileData().getTicksExisted() % 50 == 0) {
                    EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                            .spawn(from)
                            .setup(to, beamSize, beamSize)
                            .color(beamColor);
                    EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                            .spawn(to)
                            .setup(from, beamSize, beamSize)
                            .color(beamColor);
                }

                if (rand.nextBoolean()) {
                    Vector3 beamHighlightPos = from.copy()
                            .subtract(to)
                            .multiply(rand.nextFloat())
                            .add(to)
                            .add(Vector3.random(rand).multiply(rand.nextFloat() * 0.2F));

                    EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .spawn(beamHighlightPos)
                            .alpha(FXAlphaFunction.FADE_OUT)
                            .color(FXColorFunction.constant(cst.getConstellationColor()))
                            .setScale(0.2F + rand.nextFloat() * 0.1F)
                            .setMaxAge(20 + rand.nextInt(10));
                }
            });
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnConstellationPaperHighlightEffects(Level level) {
        LevelSkyHandler.getContext(level, LogicalSide.CLIENT).ifPresent(ctx -> {
            Player player = Minecraft.getInstance().player;
            if (player == null || player.distanceToSqr(Vec3.atCenterOf(this.getBlockPos())) >= 256) {
                return;
            }

            MiscUtil.getMainOrOffHand(player, stack -> stack.has(DataComponentsAS.CONSTELLATION_PAPER)).ifPresent(tpl -> {
                ItemStack held = tpl.getB();
                held.get(DataComponentsAS.CONSTELLATION_PAPER).getConstellation().ifPresent(cst -> {
                    PlayerProgress progress = ResearchManager.getProgress(player, LogicalSide.CLIENT);
                    if (progress.hasDiscoveredConstellation(cst)) {
                        float night = DayTimeHelper.getCurrentDaytimeDistribution(level);
                        if (night >= 0.1F) {
                            AttunementConstellationFinder finder = new AttunementConstellationFinder(level, this.getBlockPos());
                            Set<BlockPos> cstPositions = finder.getConstellationPositions(cst);
                            for (BlockPos cstPos : cstPositions) {
                                this.spawnConstellationHighlightEffects(cst, cstPos, night);
                            }
                        }
                    }
                });
            });
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnConstellationHighlightEffects(BaseConstellation cst, BlockPos pos, float alpha) {
        Vector3 at = Vector3.atBottomCenter(pos);
        Vector3 offset = Vector3.random(rand).multiply(0.5F).setY(0);

        if (rand.nextInt(3) == 0) {
            offset.multiply(0.5);
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at.add(offset))
                    .color(FXColorFunction.constant(cst.getConstellationColor()))
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setAlpha(0.6F * alpha)
                    .setScale(0.15F + rand.nextFloat() * 0.1F)
                    .setGravity(Vector3.y(0.002F))
                    .setMotion(Vector3.random(rand).addY(3).normalize().multiply(0.03 + rand.nextFloat() * 0.01));
        } else if (rand.nextInt(9) == 0) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at.add(offset))
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setAlpha(0.6F * alpha)
                    .setScale(0.4F + rand.nextFloat() * 0.2F)
                    .setGravity(Vector3.y(0.0005F))
                    .setMotion(Vector3.random(rand).addY(3).normalize().multiply(0.005))
                    .setMaxAge(60 + rand.nextInt(40));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickPlaySoundIdle() {
        if (SoundUtil.getSoundVolume(SoundSource.BLOCKS) <= 0) {
            this.idleSoundLoop.set(null);
            return;
        }

        if (this.idleSoundLoop.isNull() || this.idleSoundLoop.get().hasStoppedPlaying()) {
            PlayableSoundInstance idle = PlayableSoundInstance.of(SoundsAS.ATTUNEMENT_ALTAR_IDLE_LOOP)
                    .pos(Vector3.atCenter(this))
                    .volume(0.3F)
                    .loop(true)
                    .stopFunction(sound -> {
                        return this.isRemoved() ||
                                !this.canPlayConstellationEffects(Minecraft.getInstance().level) ||
                                SoundUtil.getSoundVolume(SoundSource.BLOCKS) <= 0 ||
                                this.getTileData().getActiveRecipe().isPresent();
                    })
                    .fadeInTicks(60)
                    .fadeOutTicks(60)
                    .play();
            this.idleSoundLoop.set(idle);
        }
    }

    @Override
    protected Tuple<BlockPos, BlockPos> getSkyCheckArea() {
        return ATTUNEMENT_SKY_CHECK_AREA;
    }

    @Nullable
    @Override
    public ObserverRegistryObject getRequiredObserver() {
        return ObserversAS.STRUCTURE_ATTUNEMENT_ALTAR;
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    @Override
    protected void onClientDataUpdated(Data previousData) {
        super.onClientDataUpdated(previousData);

        previousData.getActiveRecipe().ifPresent(prevRecipe -> {
            AttunementRecipe.Active<?, ?> thisRecipe = this.getTileData().getActiveRecipe().orElse(null);

            if (thisRecipe != null) {
                ((AttunementRecipe.Active) prevRecipe).copyEffectDataTo(thisRecipe);
            } else {
                prevRecipe.stopEffects(this);
            }
        });
    }

    public static class Data extends TileEntityTick.Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> attunementFields(inst).apply(inst , Data::new));

        protected static <T extends TileAttunementAltar.Data> Products.P5<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, Optional<AttunementRecipe.Active<?, ?>>, Optional<BaseConstellation>> attunementFields(RecordCodecBuilder.Instance<T> inst) {
            return tickFields(inst).and(inst.group(
                    AttunementRecipe.ACTIVE_CODEC.optionalFieldOf("activeRecipe").forGetter(Data::getActiveRecipe),
                    RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().optionalFieldOf("activeConstellation").forGetter(Data::getActiveConstellation)
            ));
        }

        private AttunementRecipe.Active<?, ?> activeRecipe;
        private BaseConstellation activeConstellation;

        protected Data(long ticksExisted,
                       boolean hasStructure,
                       Map<BlockPos, Boolean> skyObstructions,
                       Optional<AttunementRecipe.Active<?, ?>> activeRecipe,
                       Optional<BaseConstellation> activeConstellation) {
            super(ticksExisted, hasStructure, skyObstructions);
            this.activeRecipe = activeRecipe.orElse(null);
            this.activeConstellation = activeConstellation.orElse(null);
        }

        public void setActiveRecipe(@Nullable AttunementRecipe.Active<?, ?> activeRecipe) {
            this.activeRecipe = activeRecipe;
        }

        public Optional<AttunementRecipe.Active<?, ?>> getActiveRecipe() {
            return Optional.ofNullable(this.activeRecipe);
        }

        public void setActiveConstellation(@Nullable BaseConstellation activeConstellation) {
            this.activeConstellation = activeConstellation;
        }

        public Optional<BaseConstellation> getActiveConstellation() {
            return Optional.ofNullable(this.activeConstellation);
        }
    }

    public static class AttunementConstellationFinder {

        private final Level level;
        private final BlockPos altarPos;

        public AttunementConstellationFinder(Level level, BlockPos altarPos) {
            this.level = level;
            this.altarPos = altarPos;
        }

        protected Optional<BaseConstellation> searchActiveConstellation(Level level) {
            return LevelSkyHandler.getContext(level).map(ctx -> {
                ConstellationHandler cstHandler = ctx.getConstellationHandler();

                BaseConstellation matchedConstellation = null;
                for (BaseConstellation cst : RegistriesAS.REGISTRY_CONSTELLATIONS) {
                    boolean isValid = true;
                    Set<BlockPos> cstPositions = this.getConstellationPositions(cst);
                    for (BlockPos cstPos : cstPositions) {
                        if (cstPos.equals(this.altarPos)) {
                            continue;
                        }

                        BlockEntity te = MiscUtil.getTileAt(this.level, cstPos, BlockEntity.class, true).orElse(null);
                        if (!(te instanceof TileFocusRelay)) {
                            isValid = false;
                            break;
                        }
                    }
                    if (isValid) {
                        matchedConstellation = cst;
                        break;
                    }
                }
                if (!cstHandler.isCurrentlyActive(matchedConstellation)) {
                    return null;
                }

                return matchedConstellation;
            });
        }

        public Set<BlockPos> getConstellationPositions(BaseConstellation cst) {
            Set<BlockPos> offsetPositions = new HashSet<>();
            for (StarLocation sl : cst.getStars()) {
                int x = sl.x() / 2;
                int z = sl.y() / 2;
                offsetPositions.add(new BlockPos(x - 7, 0, z - 7).offset(this.altarPos));
            }
            return offsetPositions;
        }

        public Set<Tuple<BlockPos, BlockPos>> getConstellationConnectionPositions(BaseConstellation cst) {
            Set<Tuple<BlockPos, BlockPos>> offsetPositions = new HashSet<>();
            for (StarConnection conn : cst.getStarConnections()) {
                StarLocation from = conn.getLeft();
                StarLocation to = conn.getRight();
                int fX = from.x() / 2;
                int fZ = from.y() / 2;
                int tX = to.x() / 2;
                int tZ = to.y() / 2;
                offsetPositions.add(
                        new Tuple<>(new BlockPos(fX - 7, 0, fZ - 7).offset(this.altarPos),
                                new BlockPos(tX - 7, 0, tZ - 7).offset(this.altarPos))
                );
            }
            return offsetPositions;
        }
    }
}
