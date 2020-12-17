/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.lens;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.auxiliary.CropHelper;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.util.PartialEffectExecutor;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemColoredLensGrowth
 * Created by HellFirePvP
 * Date: 21.09.2019 / 19:19
 */
public class ItemColoredLensGrowth extends ItemColoredLens {

    private static final ColoredTypeGrowth COLORED_TYPE_GROWTH = new ColoredTypeGrowth();

    public ItemColoredLensGrowth() {
        super(COLORED_TYPE_GROWTH);
    }

    private static class ColoredTypeGrowth extends LensColorType {

        private ColoredTypeGrowth() {
            super(AstralSorcery.key("growth"),
                    TargetType.BLOCK,
                    () -> new ItemStack(ItemsAS.COLORED_LENS_GROWTH),
                    ColorsAS.COLORED_LENS_GROWTH,
                    0.1F,
                    false);
        }

        @Override
        public void entityInBeam(World world, Vector3 origin, Vector3 target, Entity entity, PartialEffectExecutor executor) {}

        @Override
        public void blockInBeam(World world, BlockPos pos, BlockState state, PartialEffectExecutor executor) {
            if (world.isRemote()) {
                return;
            }
            CropHelper.GrowablePlant plant = CropHelper.wrapPlant(world, pos);
            if (plant != null) {
                executor.executeAll(() -> {
                    if (random.nextInt(18) == 0) {
                        plant.tryGrow(world, random);
                        PktPlayEffect packet = new PktPlayEffect(PktPlayEffect.Type.CROP_GROWTH)
                                .addData(buf -> ByteBufUtils.writeVector(buf, new Vector3(pos)));
                        PacketChannel.CHANNEL.sendToAllAround(packet, PacketChannel.pointFromPos(world, pos, 16));
                    }
                });
            }
        }
    }
}
