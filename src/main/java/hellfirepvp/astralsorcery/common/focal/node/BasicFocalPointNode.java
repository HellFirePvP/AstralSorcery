/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.focal.node;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXScaleFunction;
import hellfirepvp.astralsorcery.client.effect.vfx.VFXLightBeam;
import hellfirepvp.astralsorcery.client.helper.RenderAstrolabeOverlay;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.component.AstrolabeAngleComponent;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyContext;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyHandler;
import hellfirepvp.astralsorcery.common.visual.type.FocalPointCombineSparkle;
import hellfirepvp.astralsorcery.common.item.AstrolabeItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.types.FocalNodeTypesAS;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.ActiveFocalCombineRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.FocalCombineCraftingInput;
import hellfirepvp.astralsorcery.common.recipe.focal.place.ActiveFocalTransmutationRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.place.ActiveTransmutationHandler;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.tile.TileStarlightFocusCrystal;
import hellfirepvp.astralsorcery.common.util.BiasedSuppliers;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.ColumnPos;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.TriState;

import java.util.*;
import java.util.function.BiFunction;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BasicFocalPointNode
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BasicFocalPointNode extends FocalPointNode {

    public static final MapCodec<BasicFocalPointNode> CODEC = RecordCodecBuilder.mapCodec(inst ->
            commonFields(inst).apply(inst, BasicFocalPointNode::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BasicFocalPointNode> STREAM_CODEC = StreamCodec.composite(
            ColumnPos.STREAM_CODEC,
            BasicFocalPointNode::getPos,
            ByteBufCodecs.registry(RegistriesAS.KEY_CONSTELLATIONS),
            BasicFocalPointNode::getConstellation,
            ByteBufCodecs.VAR_LONG,
            FocalPointNode::getTicksExisted,
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC),
            BasicFocalPointNode::getFocalPosition,
            BasicFocalPointNode::new);

    private final RandomSource rand = RandomSource.create();
    private final Set<BlockPos> activeTransmutationPositions = new HashSet<>();
    private ActiveFocalCombineRecipe activeCombineRecipe = null;

    public BasicFocalPointNode(ColumnPos pos, BaseConstellation constellation) {
        this(pos, constellation, 0L, Optional.empty());
    }

    protected BasicFocalPointNode(ColumnPos pos, BaseConstellation constellation, long ticksExisted, Optional<BlockPos> focalPosition) {
        super(pos, constellation, ticksExisted, focalPosition);
    }

    @Override
    public void tick(ServerLevel sLevel) {
        super.tick(sLevel);
        if (this.getTicksExisted() <= 1) {
            this.clearFlowers(sLevel);
        }

        if (DayTimeHelper.isNight(sLevel)) {
            this.tickCombine(sLevel);
            this.tickTransmutation(sLevel);
        } else {
            this.activeCombineRecipe = null;
            this.activeTransmutationPositions.clear();
        }
    }

    private void clearFlowers(ServerLevel sLevel) {
        int range = 4;
        BlockPos centerPos = this.getPos().toBlockPos(0);

        this.clearFoliagePatch(sLevel, centerPos, range);

        int additional = 6 + this.rand.nextInt(5);
        for (int i = 0; i < additional; i++) {
            double angle = this.rand.nextDouble() * Math.PI * 2;
            int distance = range + 1 + this.rand.nextInt(range + 3);
            BlockPos scatterCenter = centerPos.offset(
                    (int) Math.round(Math.cos(angle) * distance),
                    0,
                    (int) Math.round(Math.sin(angle) * distance));
            int patchRange = this.rand.nextInt(2);
            this.clearFoliagePatch(sLevel, scatterCenter, patchRange);
        }
    }

    private void clearFoliagePatch(ServerLevel sLevel, BlockPos center, int range) {
        BlockPos.betweenClosedStream(center.offset(-range, 0, -range), center.offset(range, 0, range))
                .forEach(pos -> this.clearFoliageColumn(sLevel, pos.getX(), pos.getZ()));
    }

    private void clearFoliageColumn(ServerLevel sLevel, int x, int z) {
        BlockPos surfaceTop = sLevel.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z));
        for (BlockPos checkPos : List.of(surfaceTop, surfaceTop.below(), surfaceTop.below(2))) {
            BlockState state = sLevel.getBlockState(checkPos);
            if (isFoliage(state)) {
                sLevel.setBlock(checkPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    private static boolean isFoliage(BlockState state) {
        if (state.isAir()) {
            return false;
        }
        if (state.is(BlockTags.SMALL_FLOWERS) || state.is(BlockTags.FLOWERS)) {
            return true;
        }
        Block block = state.getBlock();
        return block instanceof BushBlock || block instanceof VineBlock || block instanceof SaplingBlock;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickEffects(ClientLevel level) {
        super.tickEffects(level);
        if (DayTimeHelper.isDay(level)) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        RandomSource rand = level.getRandom();
        double playerDistance = this.getPos().toBlockPos(player.getBlockY()).distSqr(player.blockPosition());
        boolean hasDiscovered = ResearchManager.getClientProgress().hasDiscoveredConstellation(this.getConstellation());

        BiFunction<VFXLightBeam, Float, Float> distanceFn = (fx, scale) -> {
            Vector3 mid = fx.getToPos().setY(0).getMidpoint(fx.getPos().setY(0));
            return (float) mid.distance(player.position().multiply(1, 0, 1)) / scale;
        };

        TileStarlightFocusCrystal focusCrystal = this.getFocalPosition()
                .flatMap(pos -> MiscUtil.getTileAt(level, pos, TileStarlightFocusCrystal.class, true))
                .filter(crystal -> crystal.getTileData().getConstellation().map(cst -> cst.equals(this.getConstellation())).orElse(false))
                .orElse(null);
        int layers = focusCrystal == null ? 0 : focusCrystal.getTileData().getValidLayers().size();
        float minMultiplier = focusCrystal != null ? Mth.clamp(0.5F + layers * 0.06F, 0F, 1F) : 0F;

        BlockPos at = this.getPos().toBlockPos(0);
        at = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, at);
        Vector3 surface = new Vector3(at).add(0.5, -20, 0.5);
        FXScaleFunction<VFXLightBeam> distanceScale = (fx, scaleIn, pTicks) -> {
            float minScale = focusCrystal != null ? 0.3F + layers * 0.04F : 0.3F;
            return scaleIn * Mth.clamp(distanceFn.apply(fx, 60F), minScale, 3F);
        };
        FXAlphaFunction<VFXLightBeam> sextantAlpha = ((fx, alphaIn, pTicks) -> {
            Player pl = Minecraft.getInstance().player;
            if (AstrolabeItem.isUsingAstrolabe(pl)) {
                ItemStack sextant = pl.getUseItem();
                if (RenderAstrolabeOverlay.isDrawing()) {
                    sextant = RenderAstrolabeOverlay.getDrawingItem();
                }
                AstrolabeAngleComponent cmp = sextant.getOrDefault(DataComponentsAS.ASTROLABE_ANGLE, AstrolabeAngleComponent.DEFAULT);
                float requiredAngle = LevelSkyHandler.getContext(pl.level())
                        .map(LevelSkyContext::getConstellationHandler)
                        .map(cstHandler -> cstHandler.getAngle(this.getConstellation()))
                        .orElse(45F);
                return alphaIn * Math.max(cmp.getAngleVisibility(this.getConstellation(), requiredAngle), minMultiplier);
            }
            if (focusCrystal != null) {
                return alphaIn * minMultiplier;
            }
            return 0F;
        });
        FXAlphaFunction<VFXLightBeam> distanceAlpha = ((fx, alphaIn, pTicks) -> {
            float multiplier = Mth.clamp(distanceFn.apply(fx, 80F), 0F, 1F);
            if (focusCrystal != null) multiplier = Mth.sqrt(multiplier);
            return alphaIn * Math.max(0.25F, multiplier);
        });
        distanceAlpha = sextantAlpha.andThen(distanceAlpha)
                .andThen(FXAlphaFunction.visibleAtNight(1))
                .andThen(FXAlphaFunction.PYRAMID);

        if (ClientProxy.getClientTick() % 55 == 0) {
            this.playEffectBeams(surface, hasDiscovered, 0, distanceAlpha, distanceScale, focusCrystal != null);
        }
        if ((playerDistance > 256 || layers > 0) && ClientProxy.getClientTick() % 60 == 0) {
            this.playEffectBeams(surface, hasDiscovered, 100 + rand.nextInt(300), distanceAlpha, distanceScale, focusCrystal != null);
        }
        if ((playerDistance > 576 || layers > 0) && ClientProxy.getClientTick() % 40 == 0) {
            this.playEffectBeams(surface, hasDiscovered, 500 + rand.nextInt(50), distanceAlpha, distanceScale, focusCrystal != null);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playEffectBeams(Vector3 offset, boolean hasDiscovered, int randomOffset, FXAlphaFunction<?> alphaFn, FXScaleFunction<?> scaleFn, boolean display) {
        Vector3 up = VectorUtil.withRandomOffset(offset, rand, 0.4F).addY(75 + rand.nextInt(20) + randomOffset);
        float size = 5 + rand.nextFloat() * 3F;

        ColorWrapper color = ColorWrapper.WHITE;
        if (rand.nextInt(hasDiscovered ? 4 : 16) == 0) {
            color = this.getConstellation().getConstellationColor();
        }

        EffectHelper.of(EffectTemplatesAS.FOCAL_LIGHT_BEAM)
                .spawn(up)
                .setup(offset, size, size, display)
                .color(FXColorFunction.constant(color))
                .scale(scaleFn)
                .setAlpha(0.75F)
                .alpha(alphaFn);
        EffectHelper.of(EffectTemplatesAS.FOCAL_LIGHT_BEAM)
                .spawn(offset)
                .setup(up, size, size, display)
                .color(FXColorFunction.constant(color))
                .scale(scaleFn)
                .setAlpha(0.75F)
                .alpha(alphaFn);
    }

    private void tickCombine(ServerLevel sLevel) {
        if (this.activeCombineRecipe == null && sLevel.getGameTime() % 100 == 0) {
            int range = BiasedSuppliers.getLowest(2, () -> rand.nextInt(3) + 1).get();
            ActiveFocalCombineRecipe.tryFindAtOpenSky(sLevel, this.getConstellation(), this.getPos(), range).ifPresent(recipe -> {
                this.activeCombineRecipe = recipe;
            });
        }
        if (this.activeCombineRecipe != null) {
            Optional<FocalCombineCraftingInput> matchingInput = this.activeCombineRecipe.match(sLevel);
            if (matchingInput.isEmpty()) {
                this.activeCombineRecipe = null;
                return;
            }
            matchingInput.ifPresent(usedInput -> {
                this.activeCombineRecipe.tick();
                FocalPointCombineSparkle.at(new Vector3(usedInput.getCenter()), this.activeCombineRecipe.getRecipe().getColor()).sendToNearby(sLevel);
                if (this.activeCombineRecipe.isFinished()) {
                    this.activeCombineRecipe.finish(usedInput, sLevel);
                    this.activeCombineRecipe = null;
                }
            });
        }
    }

    private void tickTransmutation(ServerLevel sLevel) {
        this.activeTransmutationPositions.removeIf(pos ->
                ActiveTransmutationHandler.receiveStarlight(sLevel, pos, this.getConstellation(), 1F, false) != TriState.TRUE);

        if (sLevel.getGameTime() % 40 == 0) {
            int range = BiasedSuppliers.getLowest(5, () -> rand.nextInt(4) + 1).get();
            BlockPos centerPos = this.getPos().toBlockPos(0);
            Vec3i offset = new Vec3i(range, 0, range);
            BlockPos.betweenClosedStream(centerPos.subtract(offset), centerPos.offset(offset)).forEach(pos -> {
                ColumnPos colPos = ColumnPos.of(pos);
                if (this.activeTransmutationPositions.stream().anyMatch(trPos -> ColumnPos.of(trPos).equals(colPos))) {
                    return;
                }
                ActiveFocalTransmutationRecipe.tryFindAtOpenSky(sLevel, this.getConstellation(), colPos).ifPresent(recipe -> {
                    this.activeTransmutationPositions.add(recipe.getTransmutationPos());
                });
            });
        }
    }

    @Override
    public Type<?> getType() {
        return FocalNodeTypesAS.BASIC.get();
    }
}
