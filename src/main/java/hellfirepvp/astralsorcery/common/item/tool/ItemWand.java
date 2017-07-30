/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingDepthParticle;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.item.base.ISpecialInteractItem;
import hellfirepvp.astralsorcery.common.item.base.IWandInteract;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemWand
 * Created by HellFirePvP
 * Date: 23.09.2016 / 12:57
 */
public class ItemWand extends Item implements ISpecialInteractItem {

    private static final Random rand = new Random();

    public ItemWand() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    /*@Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        Block b = state.getBlock();
        if(b instanceof IWandInteract) {
            if(((IWandInteract) b).onInteract(worldIn, pos, playerIn, facing, playerIn.isSneaking())) {
                return EnumActionResult.SUCCESS;
            }
        }
        IWandInteract wandTe = MiscUtils.getTileAt(worldIn, pos, IWandInteract.class);
        if(wandTe != null) {
            if(wandTe.onInteract(worldIn, pos, playerIn, facing, playerIn.isSneaking())) {
                return EnumActionResult.SUCCESS;
            }
        }
        playerIn.swingArm(hand);
        return EnumActionResult.PASS;
    }*/

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        double dstr = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(worldIn);
        if(dstr <= 1E-4) return;

        if (!isSelected) isSelected = (entityIn instanceof EntityPlayer) && ((EntityPlayer) entityIn).getHeldItemOffhand() == stack;
        if(!worldIn.isRemote && isSelected && worldIn.getTotalWorldTime() % 20 == 0 && entityIn instanceof EntityPlayerMP) {
            //PlayerProgress progress = ResearchManager.getProgress((EntityPlayer) entityIn);
            //if(progress == null || !EnumGatedKnowledge.WAND_TYPE.canSee(progress.getViewCapability())) return;

            RockCrystalBuffer buf = WorldCacheManager.getOrLoadData(worldIn, WorldCacheManager.SaveKey.ROCK_CRYSTAL);
            ChunkPos pos = new ChunkPos(entityIn.getPosition());
            List<BlockPos> posList = buf.collectPositions(pos, 4);
            for (BlockPos rPos : posList) {
                IBlockState state = worldIn.getBlockState(rPos);
                if(!(state.getBlock() instanceof BlockCustomOre) || state.getValue(BlockCustomOre.ORE_TYPE) != BlockCustomOre.OreType.ROCK_CRYSTAL) {
                    buf.removeOre(rPos);
                    continue;
                }
                //BlockPos p = worldIn.getTopSolidOrLiquidBlock(rPos).up();
                BlockPos p = rPos.up();
                PktParticleEvent pkt = new PktParticleEvent(PktParticleEvent.ParticleEventType.WAND_CRYSTAL_HIGHLIGHT, p.getX(), p.getY(), p.getZ());
                PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) entityIn);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void highlightEffects(PktParticleEvent event) {
        BlockPos orePos = event.getVec().toBlockPos();
        BlockPos pos = Minecraft.getMinecraft().world.getTopSolidOrLiquidBlock(orePos).up();
        Vector3 display = new Vector3(pos);
        MiscUtils.applyRandomOffset(display, rand, 2);
        double velX = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);
        double velY = rand.nextFloat() * 0.3F;
        double velZ = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);
        double dstr = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(Minecraft.getMinecraft().world);
        for (int i = 0; i < 10; i++) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(display.getX(), display.getY(), display.getZ());
            particle.setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
            particle.motion(velX * (0.2 + 0.8 * rand.nextFloat()), velY * (0.4 + 0.6 * rand.nextFloat()), velZ * (0.2 + 0.8 * rand.nextFloat()));
            particle.scale(0.7F).setMaxAge(70);
            particle.enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID).setAlphaMultiplier((float) ((150 * dstr) / 255F));
        }
        dstr = Math.sqrt(dstr);
        dstr = Math.sqrt(dstr);
        Vector3 plVec = Vector3.atEntityCorner(Minecraft.getMinecraft().player);
        float dst = (float) event.getVec().distance(plVec);
        float dstMul = dst <= 25 ? 1F : (dst >= 50 ? 0F : (1F - (dst - 25F) / 25F));
        if(dstMul >= 1E-4) {
            EntityFXFacingDepthParticle p = EffectHelper.genericDepthIgnoringFlareParticle(
                    orePos.getX() - 1 + rand.nextFloat() * 3,
                    orePos.getY() - 1 + rand.nextFloat() * 3,
                    orePos.getZ() - 1 + rand.nextFloat() * 3);
            p.setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
            p.gravity(0.004).scale(0.4F).setAlphaMultiplier((float) ((150 * dstr) / 255F) * dstMul);
            p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).setMaxAge(30 + rand.nextInt(10));
        }
    }

    @Override
    public boolean needsSpecialHandling(World world, BlockPos at, EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public boolean onRightClick(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side, EnumHand hand, ItemStack stack) {
        IBlockState state = world.getBlockState(pos);
        Block b = state.getBlock();
        if(b instanceof IWandInteract) {
            ((IWandInteract) b).onInteract(world, pos, entityPlayer, side, entityPlayer.isSneaking());
            return true;
        }
        IWandInteract wandTe = MiscUtils.getTileAt(world, pos, IWandInteract.class, true);
        if(wandTe != null) {
            wandTe.onInteract(world, pos, entityPlayer, side, entityPlayer.isSneaking());
            return true;
        }
        return false;
    }
}
