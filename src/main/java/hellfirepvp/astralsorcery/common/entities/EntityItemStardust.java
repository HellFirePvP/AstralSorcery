package hellfirepvp.astralsorcery.common.entities;

import hellfirepvp.astralsorcery.common.block.fluid.FluidBlockLiquidStarlight;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityItemStardust
 * Created by HellFirePvP
 * Date: 14.09.2016 / 17:42
 */
public class EntityItemStardust extends EntityItem {

    private static final AxisAlignedBB boxCraft = new AxisAlignedBB(-0.6, -0.2, -0.6, 0.6, 0.2, 0.6);

    public static final int TOTAL_MERGE_TIME = 60 * 20;
    private int inertMergeTick = 0;

    public EntityItemStardust(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityItemStardust(World worldIn) {
        super(worldIn);
    }

    public EntityItemStardust(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        checkMergeConditions();
    }

    private void checkMergeConditions() {
        if(worldObj.isRemote) {
            if(canCraft()) {
                spawnCraftingParticles();
            }
        } else {
            if(canCraft()) {
                inertMergeTick++;
                if(inertMergeTick >= TOTAL_MERGE_TIME) {
                    buildCelestialCrystals();
                }
            } else {
                inertMergeTick = 0;
            }
        }
    }

    private void buildCelestialCrystals() {
        PacketChannel.CHANNEL.sendToAllAround(new PktParticleEvent(PktParticleEvent.ParticleEventType.CELESTIAL_CRYSTAL_FORM, posX, posY, posZ),
                PacketChannel.pointFromPos(worldObj, getPosition(), 64));

        worldObj.setBlockState(getPosition(), BlocksAS.celestialCrystals.getDefaultState());
        getEntityItem().stackSize--;
        List<Entity> foundItems = worldObj.getEntitiesInAABBexcluding(this, boxCraft.offset(posX, posY, posZ).expandXyz(0.1), EntityUtils.selectItem(ItemsAS.rockCrystal));
        if(foundItems.size() > 0) {
            EntityItem ei = (EntityItem) foundItems.get(0);
            ItemStack stack = ei.getEntityItem();
            stack.stackSize--;
            if(stack.stackSize <= 0) {
                ei.setDead();
            } else {
                ei.setEntityItemStack(stack);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnCraftingParticles() {

    }

    @SideOnly(Side.CLIENT)
    public static void spawnFormationParticles(PktParticleEvent event) {

    }

    private boolean canCraft() {
        BlockPos at = getPosition();
        IBlockState state = worldObj.getBlockState(at);
        if(!(state.getBlock() instanceof FluidBlockLiquidStarlight)) {
            return false;
        }
        if(!((FluidBlockLiquidStarlight) state.getBlock()).isSourceBlock(worldObj, at)) {
            return false;
        }
        state = worldObj.getBlockState(at.down());
        if(!state.isSideSolid(worldObj, at.down(), EnumFacing.UP)) {
            return false;
        }
        List<Entity> foundItems = worldObj.getEntitiesInAABBexcluding(this, boxCraft.offset(posX, posY, posZ), EntityUtils.selectItem(ItemsAS.rockCrystal));
        return foundItems.size() > 0;
    }

}
