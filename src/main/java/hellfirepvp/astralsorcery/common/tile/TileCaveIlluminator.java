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
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.FlareLightBlock;
import hellfirepvp.astralsorcery.common.component.ColorComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.ChunkUtil;
import hellfirepvp.astralsorcery.common.util.ColorReference;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileCaveIlluminator
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileCaveIlluminator extends TileEntityTick<TileCaveIlluminator.Data> {

    public static final int SEARCH_RADIUS = 64;
    public static final int STEP_WIDTH = 2;
    private static final int LAYER_HEIGHT = 7;
    private static final int NORMAL_TICK_INTERVAL = 180;
    private static final int BOOSTED_TICK_INTERVAL = 30;
    private static final int BOOST_DURATION_TICKS = 10 * 60 * 20; // 10 minutes
    private static final int MAX_LIGHT_LEVEL = 8;
    private static final int MAX_SKY_LIGHT = 4;

    private List<List<BlockPos>> layerPositions = null;
    private boolean doRecalculation = false;
    private int ticksUntilNextPlacement = NORMAL_TICK_INTERVAL;

    public TileCaveIlluminator(BlockPos pos, BlockState blockState) {
        super(TileEntitiesAS.CAVE_ILLUMINATOR, pos, blockState);
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        if (!this.getTileData().isPlayerPlaced()) {
            return;
        }

        if (layerPositions == null) {
            recalculate();
        }

        placeFlare(level);
        placeFlare(level);
        placeFlare(level);
        if (rand.nextInt(3) == 0 && placeFlare(level)) {
            doRecalculation = true;
        }

        Data data = this.getTileData();
        if (data.getBoostedTicks() > 0) {
            data.setBoostedTicks(data.getBoostedTicks() - 1);
        }

        ticksUntilNextPlacement--;
        if (ticksUntilNextPlacement <= 0) {
            ticksUntilNextPlacement = data.getBoostedTicks() > 0 ? BOOSTED_TICK_INTERVAL : NORMAL_TICK_INTERVAL;
            if (doRecalculation) {
                doRecalculation = false;
                recalculate();
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        super.clientTick(level);

        if (!this.doesSeeSky() && this.getTileData().getBoostedTicks() <= 0) {
            return;
        }

        ColorWrapper c = ColorsAS.DYE_COLORS[this.getTileData().getColor().getId()];

        Vector3 center = new Vector3(this).add(0.5, 0.5, 0.5);
        ColorWrapper display = MiscUtil.getRandomEntry(rand, ColorWrapper.WHITE, c.brighter(), c).orElse(ColorWrapper.WHITE);

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(center)
                .setScale(0.25F)
                .color(FXColorFunction.constant(display))
                .setMotion(new Vector3(
                        rand.nextFloat() * 0.025F * (rand.nextBoolean() ? 1 : -1),
                        rand.nextFloat() * 0.025F * (rand.nextBoolean() ? 1 : -1),
                        rand.nextFloat() * 0.025F * (rand.nextBoolean() ? 1 : -1)))
                .setMaxAge(20 + rand.nextInt(10));

        if (this.getTileData().getBoostedTicks() > 0 && this.getTileData().getTicksExisted() % 4 == 0) {
            float radius = 0.8F + rand.nextFloat() * 0.1F;
            int points = 20 + rand.nextInt(10);
            for (int i = 0; i < points; i++) {
                double angle = (2 * Math.PI * i) / points;
                Vector3 at = center.copy().add(
                        Math.cos(angle) * radius,
                        0,
                        Math.sin(angle) * radius);

                display = MiscUtil.getRandomEntry(rand, ColorWrapper.WHITE, c.brighter(), c).orElse(ColorWrapper.WHITE);
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(at)
                        .setScale(0.15F)
                        .color(FXColorFunction.constant(display))
                        .setGravity(Vector3.y((rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.01F))
                        .setMaxAge(20 + rand.nextInt(10));
            }
        }
    }

    private void recalculate() {
        int height = Math.max(0, this.getBlockPos().getY() - LAYER_HEIGHT);
        int parts = height / LAYER_HEIGHT;
        layerPositions = new ArrayList<>(parts);
        for (int i = 0; i < parts; i++) {
            int yPart = 3 + i * LAYER_HEIGHT;
            BlockPos layerCenter = new BlockPos(this.getBlockPos().getX(), yPart, this.getBlockPos().getZ());
            layerPositions.add(generatePositions(layerCenter));
        }
    }

    private List<BlockPos> generatePositions(BlockPos center) {
        int xPos = center.getX();
        int yPos = center.getY();
        int zPos = center.getZ();

        HashSet<BlockPos> seen = new HashSet<>();
        List<BlockPos> positions = new ArrayList<>();

        BlockPos currentPos = center;
        seen.add(currentPos);
        positions.add(currentPos);

        Direction dir = Direction.NORTH;
        while (Math.abs(currentPos.getX() - xPos) <= SEARCH_RADIUS &&
                Math.abs(currentPos.getY() - yPos) <= SEARCH_RADIUS &&
                Math.abs(currentPos.getZ() - zPos) <= SEARCH_RADIUS) {
            currentPos = currentPos.relative(dir, STEP_WIDTH);
            if (seen.add(currentPos)) {
                positions.add(currentPos);
            }
            Direction tryDirNext = dir.getClockWise();
            if (!seen.contains(currentPos.relative(tryDirNext, STEP_WIDTH))) {
                dir = tryDirNext;
            }
        }
        return positions;
    }

    private boolean placeFlare(ServerLevel level) {
        boolean recalc = false;
        for (List<BlockPos> list : layerPositions) {
            if (list.isEmpty()) {
                recalc = true;
                continue;
            }

            int index = rand.nextInt(list.size());
            BlockPos at = list.remove(index);
            if (!recalc && list.isEmpty()) {
                recalc = true;
            }
            at = at.offset(rand.nextInt(5) - 2, rand.nextInt(13) - 6, rand.nextInt(5) - 2);

            BlockPos finalAt = at;
            ChunkUtil.executeWithChunk(level, at, () -> {
                if (this.doesSeeSky() && isValidLightPosition(level, finalAt)) {
                    DyeColor color = this.getTileData().getColor();
                    BlockState toPlace = BlocksAS.FLARE_LIGHT.get().defaultBlockState()
                            .setValue(FlareLightBlock.COLOR, color);
                    level.setBlockAndUpdate(finalAt, toPlace);
                }
            });
        }
        return recalc;
    }

    private static boolean isValidLightPosition(Level level, BlockPos pos) {
        return level.isEmptyBlock(pos) &&
                !MiscUtil.canSeeSky(level, pos, false, false) &&
                level.getMaxLocalRawBrightness(pos) < MAX_LIGHT_LEVEL &&
                level.getBrightness(LightLayer.SKY, pos) < MAX_SKY_LIGHT;
    }

    public void onWandUsed(DyeColor wandColor) {
        this.getTileData().setBoostedTicks(BOOST_DURATION_TICKS);
        this.getTileData().setColor(wandColor);
        this.getTileData().markForUpdate();
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);

        ColorComponent colorCmp = componentInput.get(DataComponentsAS.COLOR);
        if (colorCmp != null) {
            colorCmp.reference().asDyeColor().ifPresent(dyeColor -> {
                this.getTileData().setColor(dyeColor);
            });
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        components.set(DataComponentsAS.COLOR, new ColorComponent(ColorReference.Dye.of(this.getTileData().getColor())));
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    public static class Data extends TileEntityTick.Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> illuminatorFields(inst).apply(inst, Data::new));

        protected static <T extends Data> Products.P6<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, Boolean, DyeColor, Integer> illuminatorFields(RecordCodecBuilder.Instance<T> instance) {
            return TileEntityTick.Data.tickFields(instance).and(instance.group(
                    CodecUtil.defaulted(Codec.BOOL, "playerPlaced", () -> false, Data::isPlayerPlaced),
                    CodecUtil.defaulted(DyeColor.CODEC, "color", () -> DyeColor.YELLOW, Data::getColor),
                    CodecUtil.defaulted(Codec.INT, "boostedTicks", () -> 0, Data::getBoostedTicks)
            ));
        }

        private boolean playerPlaced;
        private DyeColor color;
        private int boostedTicks;

        protected Data(long ticksExisted,
                       boolean hasStructure,
                       Map<BlockPos, Boolean> skyObstructions,
                       boolean playerPlaced,
                       DyeColor color,
                       int boostedTicks) {
            super(ticksExisted, hasStructure, skyObstructions);
            this.playerPlaced = playerPlaced;
            this.color = color;
            this.boostedTicks = boostedTicks;
        }

        public boolean isPlayerPlaced() {
            return this.playerPlaced;
        }

        public void setPlayerPlaced(boolean playerPlaced) {
            this.playerPlaced = playerPlaced;
        }

        public DyeColor getColor() {
            return this.color;
        }

        public void setColor(DyeColor color) {
            this.color = color;
        }

        public int getBoostedTicks() {
            return this.boostedTicks;
        }

        public void setBoostedTicks(int boostedTicks) {
            this.boostedTicks = boostedTicks;
        }
    }
}
