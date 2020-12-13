/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.lens;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.util.PartialEffectExecutor;
import hellfirepvp.astralsorcery.common.util.RecipeHelper;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemColoredLensFire
 * Created by HellFirePvP
 * Date: 21.09.2019 / 17:51
 */
public class ItemColoredLensFire extends ItemColoredLens {

    private static final ColorTypeFire COLOR_TYPE_FIRE = new ColorTypeFire();

    public ItemColoredLensFire() {
        super(COLOR_TYPE_FIRE);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playParticles(PktPlayEffect event) {
        Vector3 at = ByteBufUtils.readVector(event.getExtraData());
        for (int i = 0; i < 5; i++) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at.clone().add(random.nextFloat(), 0.2, random.nextFloat()))
                    .setMotion(new Vector3(0, 0.016 + random.nextFloat() * 0.02, 0))
                    .setScaleMultiplier(0.2F)
                    .color(VFXColorFunction.constant(ColorsAS.COLORED_LENS_FIRE));
        }
    }

    private static class ColorTypeFire extends LensColorType {

        private ColorTypeFire() {
            super(AstralSorcery.key("fire"),
                    TargetType.ANY,
                    () -> new ItemStack(ItemsAS.COLORED_LENS_FIRE),
                    ColorsAS.COLORED_LENS_FIRE,
                    0.1F,
                    false);
        }

        @Override
        public void entityInBeam(World world, Vector3 origin, Vector3 target, Entity entity, PartialEffectExecutor executor) {
            if (world.isRemote()) {
                return;
            }
            if (entity instanceof ItemEntity) {
                ItemStack current = ((ItemEntity) entity).getItem();

                ItemStack result = RecipeHelper.findSmeltingResult(entity.getEntityWorld(), current).orElse(ItemStack.EMPTY);
                if (result.isEmpty()) {
                    return;
                }
                while (executor.canExecute()) {
                    executor.markExecution();

                    if (random.nextInt(10) != 0) {
                        continue;
                    }
                    Vector3 entityPos = Vector3.atEntityCorner(entity);
                    ItemUtils.dropItemNaturally(entity.getEntityWorld(), entityPos.getX(), entityPos.getY(), entityPos.getZ(), ItemUtils.copyStackWithSize(result, result.getCount()));
                    if (current.getCount() > 1) {
                        current.shrink(1);
                        ((ItemEntity) entity).setItem(current);
                    } else {
                        entity.remove();
                    }
                    return;
                }
            } else if (entity instanceof LivingEntity) {
                if (entity instanceof PlayerEntity) {
                    if (!GeneralConfig.CONFIG.doColoredLensesAffectPlayers.get() ||
                            entity.getServer() == null ||
                            !entity.getServer().isPVPEnabled()) {
                        return;
                    }
                }
                entity.attackEntityFrom(DamageSource.ON_FIRE, 0.5F);
                entity.setFire(5);
            }
        }

        @Override
        public void blockInBeam(World world, BlockPos pos, BlockState state, PartialEffectExecutor executor) {
            if (!(world instanceof ServerWorld)) {
                return;
            }

            ItemStack blockStack = ItemUtils.createBlockStack(state);
            if (blockStack.isEmpty()) {
                return;
            }
            ItemStack result = RecipeHelper.findSmeltingResult(world, blockStack).orElse(ItemStack.EMPTY);
            if (result.isEmpty()) {
                return;
            }

            PktPlayEffect ev = new PktPlayEffect(PktPlayEffect.Type.MELT_BLOCK)
                    .addData(buf -> ByteBufUtils.writeVector(buf, new Vector3(pos)));
            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, pos, 16));

            while (executor.canExecute()) {
                executor.markExecution();
                if (random.nextInt(6) != 0) {
                    continue;
                }

                BlockState resState = ItemUtils.createBlockState(result);
                if (resState != null) {
                    world.setBlockState(pos, resState, Constants.BlockFlags.DEFAULT);
                } else if (world.setBlockState(pos, Blocks.AIR.getDefaultState(), Constants.BlockFlags.DEFAULT)) {
                    ItemUtils.dropItemNaturally(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, result);
                }
                return;
            }
        }
    }
}
