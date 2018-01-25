/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.block.EffectTranslucentFallingBlock;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktOreScan;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
import hellfirepvp.astralsorcery.common.util.struct.OreDiscoverer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemChargedCrystalPickaxe
 * Created by HellFirePvP
 * Date: 12.03.2017 / 23:25
 */
public class ItemChargedCrystalPickaxe extends ItemCrystalPickaxe implements ChargedCrystalToolBase {

    private static int idx = 0;

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        if (hand == EnumHand.MAIN_HAND && !itemStackIn.isEmpty() && scanForOres(worldIn, playerIn)) {
            return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
        }
        return super.onItemRightClick(worldIn, playerIn, hand);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (hand == EnumHand.MAIN_HAND && scanForOres(worldIn, playerIn)) {
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    private boolean scanForOres(World world, EntityPlayer player) {
        if (!world.isRemote && player instanceof EntityPlayerMP && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) player)) {
            if (!player.getCooldownTracker().hasCooldown(ItemsAS.chargedCrystalPickaxe)) {
                Thread tr = new Thread(() -> {
                    BlockArray foundOres = OreDiscoverer.startSearch(world, Vector3.atEntityCorner(player), 14);
                    if (!foundOres.isEmpty()) {
                        List<BlockPos> positions = new LinkedList<>();
                        BlockPos plPos = player.getPosition();
                        for (BlockPos pos : foundOres.getPattern().keySet()) {
                            if(pos.distanceSq(plPos) < 350) {
                                positions.add(pos);
                            }
                        }
                        PktOreScan scan = new PktOreScan(positions, true);
                        PacketChannel.CHANNEL.sendTo(scan, (EntityPlayerMP) player);
                    }
                });
                tr.setName("Ore Scan " + idx);
                idx++;
                tr.start();
                if(!ChargedCrystalToolBase.tryRevertMainHand(player, player.getHeldItemMainhand())) {
                    player.getCooldownTracker().setCooldown(ItemsAS.chargedCrystalPickaxe, 150);
                }
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static void playClientEffects(Collection<BlockPos> positions, boolean tumble) {
        for (BlockPos at : positions) {
            Vector3 atPos = new Vector3(at).add(0.5, 0.5, 0.5);
            atPos.add(itemRand.nextFloat() - itemRand.nextFloat(), itemRand.nextFloat() - itemRand.nextFloat(), itemRand.nextFloat() - itemRand.nextFloat());
            IBlockState state = Minecraft.getMinecraft().world.getBlockState(at);
            EffectTranslucentFallingBlock bl = EffectHandler.getInstance().translucentFallingBlock(atPos, state);
            bl.setDisableDepth(true).setScaleFunction(new EntityComplexFX.ScaleFunction.Shrink<>());
            bl.setMotion(0, 0.03, 0).setAlphaFunction(EntityComplexFX.AlphaFunction.PYRAMID);
            if (tumble) {
                bl.tumble();
            }
            bl.setMaxAge(35);
        }
    }

    @Nonnull
    @Override
    public Item getInertVariant() {
        return ItemsAS.crystalPickaxe;
    }

}
