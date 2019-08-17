/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
import hellfirepvp.astralsorcery.common.block.ore.BlockRockCrystalOre;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.data.world.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemWand
 * Created by HellFirePvP
 * Date: 17.08.2019 / 23:03
 */
public class ItemWand extends Item {

    public ItemWand() {
        super(new Properties()
                .maxStackSize(1)
                .group(RegistryItems.ITEM_GROUP_AS));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        boolean active = isSelected || (entity instanceof PlayerEntity && ((PlayerEntity) entity).getHeldItemOffhand() == stack);

        if (!world.isRemote()) {
            if (active) {
                if (world.getGameTime() % 20 == 0 &&
                        entity instanceof ServerPlayerEntity &&
                        !DayTimeHelper.isDay(world)) {

                    RockCrystalBuffer buf = DataAS.DOMAIN_AS.getData(world, DataAS.KEY_ROCK_CRYSTAL_BUFFER);

                    ChunkPos pos = new ChunkPos(entity.getPosition());
                    for (BlockPos rPos : buf.collectPositions(pos, 4)) {
                        BlockState state = world.getBlockState(rPos);
                        if (!(state.getBlock() instanceof BlockRockCrystalOre)) {
                            buf.removeOre(rPos);
                            continue;
                        }
                        BlockPos p = rPos.up();
                        PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.ROCK_CRYSTAL_HIGHTLIGHT).setPos(new Vector3(p));
                        PacketChannel.CHANNEL.sendToPlayer((PlayerEntity) entity, pkt);
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void playEffect(PktPlayEffect effect) {
        World world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }

        BlockPos at = effect.getPos().toBlockPos();
        BlockPos top = world.getHeight(Heightmap.Type.WORLD_SURFACE, at);

        Vector3 columnDisplay = new Vector3(top);
        MiscUtils.applyRandomOffset(columnDisplay, random, 2F);

        double mX = random.nextFloat() * 0.01F * (random.nextBoolean() ? 1 : -1);
        double mY = random.nextFloat() * 0.3F;
        double mZ = random.nextFloat() * 0.01F * (random.nextBoolean() ? 1 : -1);
        float dstr = DayTimeHelper.getCurrentDaytimeDistribution(world);
        for (int i = 0; i < 10; i++) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(columnDisplay)
                    .setMotion(new Vector3(
                            mX * (0.2 + 0.8 * random.nextFloat()),
                            mY * (0.4 + 0.6 * random.nextFloat()),
                            mZ * (0.2 + 0.8 * random.nextFloat())
                    ))
                    .color(VFXColorFunction.constant(ColorsAS.ROCK_CRYSTAL))
                    .setAlphaMultiplier((150 * dstr) / 255F)
                    .setScaleMultiplier(0.7F)
                    .setMaxAge(70);
        }

        dstr = MathHelper.sqrt(dstr);
        dstr = MathHelper.sqrt(dstr);
        Vector3 plVec = Vector3.atEntityCorner(Minecraft.getInstance().player);
        float dst = (float) effect.getPos().distance(plVec);
        float dstMul = dst <= 25 ? 1F : (dst >= 50 ? 0F : (1F - (dst - 25F) / 25F));
        if (dstMul >= 1E-4) {
            for (int i = 0; i < 3; i++) {
                if (random.nextBoolean()) {
                    EffectHelper.of(EffectTemplatesAS.GENERIC_DEPTH_PARTICLE)
                            .spawn(effect.getPos().clone().add(-1 + random.nextFloat() * 3, -1 + random.nextFloat() * 3, -1 + random.nextFloat() * 3))
                            .color(VFXColorFunction.constant(ColorsAS.ROCK_CRYSTAL))
                            .setScaleMultiplier(0.4F)
                            .setAlphaMultiplier(((150 * dstr) / 255F) * dstMul)
                            .alpha(VFXAlphaFunction.FADE_OUT)
                            .setMaxAge(30 + random.nextInt(10));
                }
            }
        }
    }
}
