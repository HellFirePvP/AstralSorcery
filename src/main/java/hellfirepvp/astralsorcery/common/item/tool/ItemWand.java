package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.data.research.EnumGatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.item.base.ISpecialInteractItem;
import hellfirepvp.astralsorcery.common.item.base.IWandInteract;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
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
    @SideOnly(Side.CLIENT)
    public String getUnlocalizedName(ItemStack stack) {
        if(EnumGatedKnowledge.WAND_TYPE.canSee(ResearchManager.clientProgress.getTierReached())) {
            return super.getUnlocalizedName();
        }
        return "item.ItemWand.obf";
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!worldIn.isRemote && isSelected && worldIn.getTotalWorldTime() % 20 == 0 && entityIn instanceof EntityPlayerMP) {
            //PlayerProgress progress = ResearchManager.getProgress((EntityPlayer) entityIn);
            //if(progress == null || !EnumGatedKnowledge.WAND_TYPE.canSee(progress.getViewCapability())) return;

            RockCrystalBuffer buf = WorldCacheManager.getOrLoadData(worldIn, WorldCacheManager.SaveKey.ROCK_CRYSTAL);
            ChunkPos pos = new ChunkPos(entityIn.getPosition());
            List<BlockPos> posList = buf.collectPositions(pos, 2);
            for (BlockPos rPos : posList) {
                BlockPos p = worldIn.getTopSolidOrLiquidBlock(rPos).up();
                double dstr = CelestialHandler.calcDaytimeDistribution(worldIn);
                if(dstr > 1E-4) {
                    PktParticleEvent pkt = new PktParticleEvent(PktParticleEvent.ParticleEventType.WAND_CRYSTAL_HIGHLIGHT, p.getX(), p.getY(), p.getZ());
                    PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) entityIn);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void highlightEffects(PktParticleEvent event) {
        BlockPos pos = event.getVec().toBlockPos();
        double x = pos.getX() + rand.nextFloat() * (rand.nextBoolean() ? 4 : -4);
        double y = pos.getY() + rand.nextFloat() * (rand.nextBoolean() ? 4 : -4);
        double z = pos.getZ() + rand.nextFloat() * (rand.nextBoolean() ? 4 : -4);
        double velX = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);
        double velY = rand.nextFloat() * 0.2F;
        double velZ = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);
        double dstr = CelestialHandler.calcDaytimeDistribution(Minecraft.getMinecraft().theWorld);
        for (int i = 0; i < 10; i++) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(x, y, z);
            particle.setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
            particle.motion(velX * (0.2 + 0.8 * rand.nextFloat()), velY * (0.2 + 0.8 * rand.nextFloat()), velZ * (0.2 + 0.8 * rand.nextFloat()));
            particle.scale(0.4F);
            particle.enableAlphaFade().setAlphaMultiplier((float) ((150 * dstr) / 255F));
        }

    }

    @Override
    public boolean needsSpecialHandling(World world, BlockPos at, EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public void onRightClick(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side, EnumHand hand, ItemStack stack) {
        IBlockState state = world.getBlockState(pos);
        Block b = state.getBlock();
        if(b instanceof IWandInteract) {
            ((IWandInteract) b).onInteract(world, pos, entityPlayer, side, entityPlayer.isSneaking());
            return;
        }
        IWandInteract wandTe = MiscUtils.getTileAt(world, pos, IWandInteract.class, true);
        if(wandTe != null) {
            wandTe.onInteract(world, pos, entityPlayer, side, entityPlayer.isSneaking());
        }
    }
}
