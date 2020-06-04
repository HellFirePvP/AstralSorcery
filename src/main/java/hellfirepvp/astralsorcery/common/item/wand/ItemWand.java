/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.ore.BlockRockCrystalOre;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.data.world.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.item.base.OverrideInteractItem;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.TileRequiresMultiblock;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.client.preview.StructurePreview;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemWand
 * Created by HellFirePvP
 * Date: 17.08.2019 / 23:03
 */
public class ItemWand extends Item implements OverrideInteractItem {

    public ItemWand() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        boolean active = isSelected || (entity instanceof PlayerEntity && ((PlayerEntity) entity).getHeldItemOffhand() == stack);

        if (!world.isRemote()) {
            if (active) {
                if (entity instanceof ServerPlayerEntity) {
                    RockCrystalBuffer buf = DataAS.DOMAIN_AS.getData(world, DataAS.KEY_ROCK_CRYSTAL_BUFFER);

                    ChunkPos pos = new ChunkPos(entity.getPosition());
                    for (BlockPos rPos : buf.collectPositions(pos, 4)) {
                        BlockState state = world.getBlockState(rPos);
                        if (!(state.getBlock() instanceof BlockRockCrystalOre)) {
                            buf.removeOre(rPos);
                            continue;
                        }
                        if (!DayTimeHelper.isDay(world) && random.nextInt(600) == 0) {
                            PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.ROCK_CRYSTAL_COLUMN)
                                    .addData(b -> ByteBufUtils.writeVector(b, new Vector3(rPos.up())));
                            PacketChannel.CHANNEL.sendToPlayer((PlayerEntity) entity, pkt);
                        }
                        if (random.nextInt(800) == 0) {
                            PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.ROCK_CRYSTAL_SPARKS)
                                    .addData(b -> ByteBufUtils.writeVector(b, new Vector3(rPos.up())));
                            PacketChannel.CHANNEL.sendToPlayer((PlayerEntity) entity, pkt);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldInterceptInteract(LogicalSide side, PlayerEntity player, Hand hand, BlockPos pos, Direction face) {
        return true;
    }

    @Override
    public boolean doInteract(LogicalSide side, PlayerEntity player, Hand hand, BlockPos pos, Direction face) {
        World world = player.getEntityWorld();
        BlockState state = world.getBlockState(pos);
        Block b = state.getBlock();
        if (b instanceof WandInteractable) {
            if (((WandInteractable) b).onInteract(world, pos, player, face, player.isSneaking())) {
                return true;
            }
        }
        WandInteractable wandTe = MiscUtils.getTileAt(world, pos, WandInteractable.class, true);
        if (wandTe != null) {
            if (wandTe.onInteract(world, pos, player, face, player.isSneaking())) {
                return true;
            }
        }
        TileRequiresMultiblock mbTe = MiscUtils.getTileAt(world, pos, TileRequiresMultiblock.class, true);
        if (mbTe != null) {
            if (mbTe.getRequiredStructureType() != null &&
                    mbTe.getRequiredStructureType().getStructure() instanceof MatchableStructure &&
                    !((MatchableStructure) mbTe.getRequiredStructureType().getStructure()).matches(world, pos)) {
                if (world.isRemote()) {
                    this.displayClientStructurePreview(world, pos, mbTe.getRequiredStructureType());
                }
                return true;
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    private void displayClientStructurePreview(World world, BlockPos pos, StructureType type) {
        StructurePreview.newBuilder(world.getDimension().getType(), pos, (MatchableStructure) type.getStructure())
                .removeIfOutInDifferentWorld()
                .andPersistOnlyIf((inWorld, at) -> {
                    return MiscUtils.executeWithChunk(world, pos, () -> {
                        TileRequiresMultiblock tileFound = MiscUtils.getTileAt(world, pos, TileRequiresMultiblock.class, true);
                        if (tileFound == null) {
                            return false;
                        }
                        return tileFound.getRequiredStructureType() != null &&
                                tileFound.getRequiredStructureType().equals(type);
                    }, true);
                })
                .andPersistOnlyIf((inWorld, at) -> !((MatchableStructure) type.getStructure()).matches(world, pos))
                .showBar(type.getDisplayName())
                .buildAndSet();
    }

    @OnlyIn(Dist.CLIENT)
    public static void playUndergroundEffect(PktPlayEffect effect) {
        Vector3 at = ByteBufUtils.readVector(effect.getExtraData());

        World world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }

        float dstr = 0.4F + 0.6F * DayTimeHelper.getCurrentDaytimeDistribution(world);
        Vector3 plVec = Vector3.atEntityCorner(Minecraft.getInstance().player);
        float dst = (float) at.distance(plVec);
        float dstMul = dst <= 25 ? 1F : (dst >= 50 ? 0F : (1F - (dst - 25F) / 25F));
        for (int i = 0; i < 3; i++) {
            if (random.nextBoolean()) {
                EffectHelper.of(EffectTemplatesAS.GENERIC_DEPTH_PARTICLE)
                        .spawn(at.clone().add(-1 + random.nextFloat() * 3, -1 + random.nextFloat() * 3, -1 + random.nextFloat() * 3))
                        .color(VFXColorFunction.constant(ColorsAS.ROCK_CRYSTAL))
                        .setScaleMultiplier(0.4F)
                        .setAlphaMultiplier(((150F * dstr) / 255F) * dstMul)
                        .alpha(VFXAlphaFunction.FADE_OUT)
                        .setMaxAge(30 + random.nextInt(10));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void playEffect(PktPlayEffect effect) {
        Vector3 pos = ByteBufUtils.readVector(effect.getExtraData());

        World world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }

        BlockPos at = pos.toBlockPos();
        BlockPos top = world.getHeight(Heightmap.Type.WORLD_SURFACE, at);

        Vector3 columnDisplay = new Vector3(top);
        MiscUtils.applyRandomOffset(columnDisplay, random, 2F);

        double mX = random.nextFloat() * 0.01F * (random.nextBoolean() ? 1 : -1);
        double mY = random.nextFloat() * 0.5F;
        double mZ = random.nextFloat() * 0.01F * (random.nextBoolean() ? 1 : -1);
        float dstr = DayTimeHelper.getCurrentDaytimeDistribution(world);
        for (int i = 0; i < 8 + random.nextInt(10); i++) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(columnDisplay)
                    .setMotion(new Vector3(
                            mX * (0.2 + 0.8 * random.nextFloat()),
                            mY * (random.nextFloat()),
                            mZ * (0.2 + 0.8 * random.nextFloat())
                    ))
                    .color(VFXColorFunction.constant(ColorsAS.ROCK_CRYSTAL))
                    .setAlphaMultiplier((150 * dstr) / 255F)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .setScaleMultiplier(0.3F + 0.3F * random.nextFloat())
                    .setMaxAge(25 + random.nextInt(30));
        }
    }
}
