/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXMotionFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.RenderSpriteUtil;
import hellfirepvp.astralsorcery.common.block.tile.ChaliceBlock;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.LiquidInteractionRecipe;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.tank.FluidContainerList;
import hellfirepvp.astralsorcery.common.util.tank.FluidTankView;
import hellfirepvp.astralsorcery.common.util.tank.FluidTankViewFactory;
import hellfirepvp.astralsorcery.common.visual.type.ChaliceLiquidInteractionEffect;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileChalice
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
public class TileChalice extends TileEntityTick<TileChalice.Data> {

    private static final int TANK_CAPACITY = 64 * FluidType.BUCKET_VOLUME;
    private static final int LIQUID_STARLIGHT_DRAW_AMOUNT = 400;
    private static final int LIQUID_STARLIGHT_MIN_DRAW = 100;
    private static final int CHALICE_SEARCH_RANGE = 16;
    private static final int INTERACTION_COOLDOWN_MIN = 20;
    private static final int INTERACTION_COOLDOWN_RANDOM = 40;
    private static final float ROTATION_SPEED_SCALAR = 1.5F;

    private long nextInteractionTick = -1L;

    private Vector3 rotation = new Vector3();
    private Vector3 prevRotation = new Vector3();
    private Vector3 rotationVec;

    public TileChalice(BlockPos pos, BlockState state) {
        this(TileEntitiesAS.CHALICE, pos, state);
    }

    protected TileChalice(TileRegistryObject<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public Vec3 getChaliceCenter() {
        return getChaliceCenter(this.worldPosition);
    }

    public static Vec3 getChaliceCenter(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 1.4, pos.getZ() + 0.5);
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        if (this.getTileData().getTicksExisted() % 20 == 0) {
            FluidStack stack = this.getTileData().getContainedFluid();
            if (!stack.isEmpty()) {
                this.setLight(level, stack.getFluidType().getLightLevel(stack));
            } else {
                this.setLight(level, 0);
            }
        }

        if (level.hasNeighborSignal(this.worldPosition)) {
            return;
        }

        long ticks = this.getTileData().getTicksExisted();
        if (this.nextInteractionTick == -1L) {
            this.nextInteractionTick = ticks + INTERACTION_COOLDOWN_MIN + this.rand.nextInt(INTERACTION_COOLDOWN_RANDOM);
        }
        if (ticks < this.nextInteractionTick) {
            return;
        }
        this.nextInteractionTick = ticks + INTERACTION_COOLDOWN_MIN + this.rand.nextInt(INTERACTION_COOLDOWN_RANDOM);

        if (this.tickLightwellDraw(level)) {
            return;
        }
        this.tickChaliceInteractions(level);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        super.clientTick(level);

        if (this.rotationVec == null) {
            this.rotationVec = Vector3.random(rand).normalize().multiply(ROTATION_SPEED_SCALAR);
        }
        this.prevRotation = this.rotation.copy();
        this.rotation.add(this.rotationVec);
    }

    private boolean tickLightwellDraw(ServerLevel level) {
        FluidStack tankContent = this.getContainedFluid();
        boolean tankEmpty = tankContent.isEmpty();
        boolean tankIsStarlight = !tankEmpty && tankContent.getFluid() == FluidsAS.LIQUID_STARLIGHT.getSource().get();
        if (!tankEmpty && (!tankIsStarlight || tankContent.getAmount() + LIQUID_STARLIGHT_MIN_DRAW >= TANK_CAPACITY)) {
            return false;
        }

        Vec3 chaliceCenter = TileChalice.getChaliceCenter(this.worldPosition);
        List<BlockPos> lightwells = BlockFinder.findNearbyBlocks(level, this.worldPosition, CHALICE_SEARCH_RANGE,
                (lvl, pos, state) -> state.is(BlocksAS.LIGHTWELL.get()));
        lightwells.removeIf(pos -> RayTraceUtil.clip(level, chaliceCenter, Vec3.atCenterOf(pos)).getType() == HitResult.Type.BLOCK);
        MiscUtil.shuffle(lightwells, this.rand);

        for (BlockPos wellPos : lightwells) {
            IFluidHandler wellHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, wellPos, Direction.DOWN);
            if (wellHandler == null) {
                continue;
            }
            FluidStack drainable = wellHandler.drain(LIQUID_STARLIGHT_DRAW_AMOUNT, IFluidHandler.FluidAction.SIMULATE);
            if (drainable.getAmount() <= LIQUID_STARLIGHT_MIN_DRAW) {
                continue;
            }
            if (drainable.getFluid() != FluidsAS.LIQUID_STARLIGHT.getSource().get()) {
                continue;
            }
            FluidTankView ownTank = this.getTankView();
            int acceptable = ownTank.fill(drainable, IFluidHandler.FluidAction.SIMULATE);
            if (acceptable <= 0) {
                return false;
            }
            FluidStack actual = wellHandler.drain(drainable.copyWithAmount(acceptable), IFluidHandler.FluidAction.EXECUTE);
            if (actual.isEmpty()) {
                continue;
            }
            ownTank.fill(actual, IFluidHandler.FluidAction.EXECUTE);

            ChaliceLiquidInteractionEffect.lightwellDraw(Vec3.atCenterOf(wellPos), chaliceCenter, actual)
                    .sendToNearby(level, this.worldPosition);
            return true;
        }
        return false;
    }

    private void tickChaliceInteractions(ServerLevel level) {
        FluidStack thisFluid = this.getContainedFluid();
        if (thisFluid.isEmpty()) {
            return;
        }

        List<BlockPos> chalicePositions = findNearbyChalices(level, this.worldPosition, CHALICE_SEARCH_RANGE);
        Vec3 chaliceCenter = this.getChaliceCenter();
        MiscUtil.shuffle(chalicePositions, this.rand);

        for (BlockPos otherPos : chalicePositions) {
            TileChalice otherChalice = MiscUtil.getTileAt(level, otherPos, TileChalice.class, false).orElse(null);
            if (otherChalice == null) {
                continue;
            }
            FluidStack otherFluid = otherChalice.getContainedFluid();
            if (otherFluid.isEmpty()) {
                continue;
            }

            List<LiquidInteractionRecipe> candidateRecipes = LiquidInteractionRecipe.findMatching(level, thisFluid, otherFluid);
            while (!candidateRecipes.isEmpty()) {
                LiquidInteractionRecipe recipe = LiquidInteractionRecipe.pickWeighted(candidateRecipes, this.rand);
                if (recipe == null) {
                    break;
                }
                if (recipe.consumeInputs(this.rand, this.getTankView(), otherChalice.getTankView())) {
                    Vec3 otherCenter = TileChalice.getChaliceCenter(otherPos);
                    Vec3 midpoint = chaliceCenter.add(otherCenter).scale(0.5);
                    recipe.getResult().apply(level, midpoint);

                    ChaliceLiquidInteractionEffect.chaliceReaction(chaliceCenter, otherCenter, midpoint, thisFluid, otherFluid)
                            .sendToNearby(level, BlockPos.containing(midpoint));
                    return;
                }
                candidateRecipes.remove(recipe);
            }
        }
    }

    @Nonnull
    public FluidTankView getTankView() {
        return this.getTileData().getFluidTank();
    }

    public FluidStack getContainedFluid() {
        return this.getTileData().getFluidContents().getTank(0).getContent();
    }

    public float getTankFillPercentage() {
        FluidStack content = this.getContainedFluid();
        if (content.isEmpty()) {
            return 0F;
        }
        return Math.min(1F, content.getAmount() / (float) TANK_CAPACITY);
    }

    @OnlyIn(Dist.CLIENT)
    public Vector3 getRotation() {
        return this.rotation == null ? new Vector3() : this.rotation;
    }

    @OnlyIn(Dist.CLIENT)
    public Vector3 getPrevRotation() {
        return this.prevRotation == null ? new Vector3() : this.prevRotation;
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    @Nonnull
    public static List<BlockPos> findNearbyChalices(Level level, BlockPos origin, int distance) {
        Vec3 originCenter = TileChalice.getChaliceCenter(origin);
        List<BlockPos> found = BlockFinder.findNearbyBlocks(level, origin, Mth.clamp(distance, 0, 16),
                (lvl, pos, state) -> !pos.equals(origin)
                        && state.getBlock() instanceof ChaliceBlock
                        && !lvl.hasNeighborSignal(pos));

        found.removeIf(pos -> RayTraceUtil.clip(level, originCenter, TileChalice.getChaliceCenter(pos)).getType() == HitResult.Type.BLOCK);
        return found;
    }

    @Nonnull
    public static List<TileChalice> findNearbyChalicesContaining(Level level, BlockPos origin, FluidStack expected, int distance) {
        List<TileChalice> out = new LinkedList<>();
        for (BlockPos chalicePos : findNearbyChalices(level, origin, distance)) {
            TileChalice chalice = MiscUtil.getTileAt(level, chalicePos, TileChalice.class, true).orElse(null);
            if (chalice == null) {
                continue;
            }
            if (chalice.getTankView().drain(expected, IFluidHandler.FluidAction.SIMULATE).getAmount() >= expected.getAmount()) {
                out.add(chalice);
            }
        }
        return out;
    }

    @Nonnull
    public static Optional<List<TileChalice>> findNearbyChalicesCombined(Level level, BlockPos origin, FluidStack expected, int distance) {
        FluidStack required = expected.copy();
        List<TileChalice> out = new LinkedList<>();
        for (BlockPos chalicePos : findNearbyChalices(level, origin, distance)) {
            TileChalice chalice = MiscUtil.getTileAt(level, chalicePos, TileChalice.class, true).orElse(null);
            if (chalice == null) {
                continue;
            }
            FluidStack drained = chalice.getTankView().drain(expected, IFluidHandler.FluidAction.SIMULATE);
            if (!drained.isEmpty()) {
                required.shrink(drained.getAmount());
                out.add(chalice);
            }
        }
        return required.isEmpty() ? Optional.of(out) : Optional.empty();
    }

    public static boolean doChalicesContainCombined(Level level, Collection<BlockPos> chalicePositions, FluidStack expected) {
        FluidStack required = expected.copy();
        for (BlockPos pos : chalicePositions) {
            TileChalice chalice = MiscUtil.getTileAt(level, pos, TileChalice.class, true).orElse(null);
            if (chalice == null) {
                continue;
            }
            FluidStack drained = chalice.getTankView().drain(expected, IFluidHandler.FluidAction.SIMULATE);
            if (!drained.isEmpty()) {
                required.shrink(drained.getAmount());
            }
        }
        return required.isEmpty();
    }

    public static class Data extends TileEntityTick.Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> tickFields(inst).and(
                CodecUtil.defaulted(FluidContainerList.CODEC, "fluidContents", FluidContainerList::create, Data::getFluidContents)
        ).apply(inst, Data::new));

        private final FluidContainerList fluidContents;

        protected Data(long ticksExisted,
                       boolean hasStructure,
                       Map<BlockPos, Boolean> skyObstructions,
                       FluidContainerList fluidContents) {
            super(ticksExisted, hasStructure, skyObstructions);
            this.fluidContents = fluidContents;
        }
        public FluidStack getContainedFluid() {
            return this.fluidContents.getTank(0).getContent();
        }

        public FluidContainerList getFluidContents() {
            return this.fluidContents;
        }

        protected FluidTankViewFactory newFluidTank() {
            return FluidTankViewFactory.builder(1)
                    .tankCapacity(tank -> TANK_CAPACITY)
                    .accessibleSides(Direction.DOWN);
        }

        public FluidTankView getFluidTank() {
            return this.newFluidTank().createTileView(this, this.fluidContents);
        }
    }

    public static class LiquidDrawInstance {

        public static final Codec<LiquidDrawInstance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                SetCodec.of(BlockPos.CODEC).fieldOf("chalices").forGetter(LiquidDrawInstance::getChalices)
        ).apply(inst, LiquidDrawInstance::new));

        private final Set<BlockPos> chalices = new HashSet<>();

        private LiquidDrawInstance(Set<BlockPos> chalices) {
            this.chalices.addAll(chalices);
        }

        public static LiquidDrawInstance newInstance() {
            return new LiquidDrawInstance(Collections.emptySet());
        }

        public Set<BlockPos> getChalices() {
            return Collections.unmodifiableSet(this.chalices);
        }

        public void update(Level level, BlockPos pos, FluidStack search) {
            this.chalices.clear();

            TileChalice.findNearbyChalicesCombined(level, pos, search, 16)
                    .ifPresent(chalices -> {
                        chalices.forEach(chalice -> this.chalices.add(chalice.getBlockPos()));
                    });
        }

        public boolean consumeLiquid(Level level, BlockPos pos, FluidStack search, boolean simulate) {
            if (this.chalices.isEmpty()) return false;

            IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
            var optChalices = TileChalice.findNearbyChalicesCombined(level, pos, search, 16);
            if (optChalices.isPresent()) {
                FluidStack remaining = search.copy();
                for (TileChalice chalice : optChalices.get()) {
                    remaining.shrink(chalice.getTileData().getFluidTank().drain(remaining, action).getAmount());
                    if (remaining.isEmpty()) break;
                }
                return remaining.isEmpty();
            }
            return false;
        }

        @OnlyIn(Dist.CLIENT)
        public void playLiquidDrawEffect(Level level, Vector3 target, FluidStack requiredInput) {
            this.playLiquidDrawEffect(level, () -> target, requiredInput, 2F, 0.08F);
        }

        @OnlyIn(Dist.CLIENT)
        public void playLiquidDrawEffect(Level level, Supplier<Vector3> target, FluidStack requiredInput, float proximityAlphaThreshold, float motionVelocity) {
            if (this.chalices.isEmpty()) return;

            RandomSource rand = RandomSource.create();
            TextureAtlasSprite fluidSprite = RenderSpriteUtil.getTexture(requiredInput);
            FXColorFunction<?> colorFn = FXColorFunction.constant(ColorUtil.getOverlayColor(requiredInput));

            for (int i = 0; i < 2 * chalices.size(); i++) {
                MiscUtil.getRandomEntry(chalices, rand).ifPresent(pos -> {
                    MiscUtil.getTileAt(level, pos, TileChalice.class, true).ifPresent(chalice -> {
                        Vector3 chalicePos = new Vector3(chalice.getChaliceCenter());
                        float fillPerc = Math.max(chalice.getTankFillPercentage(), 0.2F);

                        int maxAge = Mth.ceil(30 * Math.max(chalicePos.distance(target.get()) / 3, 1));

                        if (rand.nextBoolean()) {
                            chalicePos = VectorUtil.withRandomOffset(chalicePos, rand, 0.25F * fillPerc);

                            EffectHelper.of(EffectTemplatesAS.BLOCK_PARTICLE)
                                    .spawn(chalicePos)
                                    .setSprite(fluidSprite)
                                    .setSpriteFraction(0.2F)
                                    .setScale(0.01F + rand.nextFloat() * 0.04F)
                                    .color(colorFn)
                                    .alpha(FXAlphaFunction.proximity(target, proximityAlphaThreshold).andThen(FXAlphaFunction.FADE_OUT))
                                    .motion(FXMotionFunction.target(target, motionVelocity))
                                    .setMaxAge(maxAge);
                        } else {
                            chalicePos = VectorUtil.withRandomOffset(chalicePos, rand, 0.4F * fillPerc);

                            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                                    .spawn(chalicePos)
                                    .setScale(0.15F + rand.nextFloat() * 0.1F)
                                    .color(colorFn)
                                    .alpha(FXAlphaFunction.proximity(target, proximityAlphaThreshold).andThen(FXAlphaFunction.FADE_OUT))
                                    .motion(FXMotionFunction.target(target, motionVelocity))
                                    .setMaxAge(maxAge);
                        }
                    });
                });
            }
        }
    }
}
