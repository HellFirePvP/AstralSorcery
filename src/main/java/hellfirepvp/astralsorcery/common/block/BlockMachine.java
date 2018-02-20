/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.IVariantTileProvider;
import hellfirepvp.astralsorcery.common.tile.TileGrindstone;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.auxiliary.SwordSharpenHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStoneMachine
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:11
 */
public class BlockMachine extends BlockContainer implements BlockCustomName, BlockVariants {

    private static final Random rand = new Random();

    public static PropertyEnum<MachineType> MACHINE_TYPE = PropertyEnum.create("machine", MachineType.class);

    public BlockMachine() {
        super(Material.BARRIER, MapColor.AIR);
        setHardness(3.0F);
        setSoundType(SoundType.STONE);
        setResistance(25.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        IBlockState state = world.getBlockState(pos);
        switch (state.getValue(MACHINE_TYPE)) {
            case TELESCOPE:
                RenderingUtils.playBlockBreakParticles(pos.up(), Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE));
            case GRINDSTONE:
                RenderingUtils.playBlockBreakParticles(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE));
                break;
        }
        return false;
    }

    @Override
    public String getHarvestTool(IBlockState state) {
        if(!(state.getBlock() instanceof BlockMachine)) {
            return super.getHarvestTool(state);
        }
        MachineType t = state.getValue(MACHINE_TYPE);
        switch (t) {
            case TELESCOPE:
                return "axe";
            case GRINDSTONE:
                return "pickaxe";
        }
        return super.getHarvestTool(state);
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        if(!(state.getBlock() instanceof BlockMachine)) {
            return super.getSoundType(state, world, pos, entity);
        }
        MachineType t = state.getValue(MACHINE_TYPE);
        switch (t) {
            case TELESCOPE:
                return SoundType.WOOD;
            case GRINDSTONE:
                return SoundType.STONE;
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if(!(state.getBlock() instanceof BlockMachine)) {
            return super.getBoundingBox(state, source, pos);
        }
        MachineType t = state.getValue(MACHINE_TYPE);
        switch (t) {
            case TELESCOPE:
                return new AxisAlignedBB(0, 0, 0, 1, 2, 1);
        }
        return super.getBoundingBox(state, source, pos);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (MachineType type : MachineType.values()) {
            list.add(type.asStack());
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        MachineType type = state.getValue(MACHINE_TYPE);
        return type.provideTileEntity(world, state);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        MachineType type = state.getValue(MACHINE_TYPE);
        if (type != MachineType.GRINDSTONE) return;
        TileGrindstone tgr = MiscUtils.getTileAt(worldIn, pos, TileGrindstone.class, true);
        if(tgr == null || tgr.getGrindingItem().isEmpty()) return;
        ItemUtils.dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, tgr.getGrindingItem());
    }

    public boolean handleSpecificActivateEvent(PlayerInteractEvent.RightClickBlock event) {
        EnumHand hand = event.getHand();
        World world = event.getWorld();
        EntityPlayer player = event.getEntityPlayer();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        MachineType type = state.getValue(MACHINE_TYPE);
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        switch (type) {
            case TELESCOPE:
                if (player.world.isRemote) {
                    AstralSorcery.proxy.openGui(CommonProxy.EnumGuiId.TELESCOPE, player, player.world, posX, posY, posZ);
                }
                break;
            case GRINDSTONE:
                TileGrindstone tgr = MiscUtils.getTileAt(world, pos, TileGrindstone.class, true);
                if(tgr != null) {
                    if(!world.isRemote) {
                        ItemStack grind = tgr.getGrindingItem();
                        if(!grind.isEmpty()) {
                            if(player.isSneaking()) {
                                player.inventory.placeItemBackInInventory(world, grind);

                                tgr.setGrindingItem(ItemStack.EMPTY);
                            } else {
                                GrindstoneRecipe recipe = GrindstoneRecipeRegistry.findMatchingRecipe(grind);
                                if(recipe != null) {
                                    GrindstoneRecipe.GrindResult result = recipe.grind(grind);
                                    switch (result.getType()) {
                                        case SUCCESS:
                                            tgr.setGrindingItem(grind); //Update
                                            break;
                                        case ITEMCHANGE:
                                            tgr.setGrindingItem(result.getStack());
                                            break;
                                        case FAIL_BREAK_ITEM:
                                            tgr.setGrindingItem(ItemStack.EMPTY);
                                            world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.2F + 0.8F);
                                            break;
                                    }
                                    tgr.playWheelEffect();
                                } else if(SwordSharpenHelper.canBeSharpened(grind)) {
                                    if(rand.nextInt(40) == 0) {
                                        SwordSharpenHelper.setSwordSharpened(grind);
                                    }
                                    tgr.playWheelEffect();
                                }
                            }
                        } else {
                            ItemStack stack = player.getHeldItem(hand);

                            if(!stack.isEmpty()) {
                                GrindstoneRecipe recipe = GrindstoneRecipeRegistry.findMatchingRecipe(stack);
                                if(recipe != null) {
                                    ItemStack toSet = stack.copy();
                                    toSet.setCount(1);
                                    tgr.setGrindingItem(toSet);
                                    world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.2F + 0.8F);

                                    if(!player.isCreative()) {
                                        stack.setCount(stack.getCount() - 1);
                                    }
                                } else if(SwordSharpenHelper.canBeSharpened(stack) && !SwordSharpenHelper.isSwordSharpened(stack)) {
                                    ItemStack toSet = stack.copy();
                                    toSet.setCount(1);
                                    tgr.setGrindingItem(toSet);
                                    world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.2F + 0.8F);

                                    if(!player.isCreative()) {
                                        stack.setCount(stack.getCount() - 1);
                                    }
                                } else if(player.isSneaking()) {
                                    return false;
                                }
                            }
                        }
                    } else {
                        ItemStack grind = tgr.getGrindingItem();
                        if(!grind.isEmpty()) {
                            GrindstoneRecipe recipe = GrindstoneRecipeRegistry.findMatchingRecipe(grind);
                            if(recipe != null) {
                                for (int j = 0; j < 8; j++) {
                                    world.spawnParticle(EnumParticleTypes.CRIT, posX + 0.5, posY + 0.8, posZ + 0.4,
                                            (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3,
                                            (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3,
                                            (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3);
                                }
                            } else if(SwordSharpenHelper.canBeSharpened(grind) && !SwordSharpenHelper.isSwordSharpened(grind)) {
                                for (int j = 0; j < 8; j++) {
                                    world.spawnParticle(EnumParticleTypes.CRIT, posX + 0.5, posY + 0.8, posZ + 0.4,
                                            (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3,
                                            (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3,
                                            (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3);
                                }
                            } else if(player.isSneaking()) {
                                return false;
                            }
                        }
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        switch (state.getValue(MACHINE_TYPE)) {
            case TELESCOPE:
                worldIn.setBlockState(pos.up(), BlocksAS.blockStructural.getDefaultState().withProperty(BlockStructural.BLOCK_TYPE, BlockStructural.BlockType.TELESCOPE_STRUCT));
                break;
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        switch (state.getValue(MACHINE_TYPE)) {
            case TELESCOPE:
                if(world.isAirBlock(pos.up())) {
                    world.setBlockToAir(pos);
                }
                break;
        }
        super.neighborChanged(state, world, pos, blockIn, fromPos);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        if(!(world instanceof World)) {
            super.onNeighborChange(world, pos, neighbor);
            return;
        }
        IBlockState state = world.getBlockState(pos);
        switch (state.getValue(MACHINE_TYPE)) {
            case TELESCOPE:
                if(world.isAirBlock(pos.up())) {
                    ((World) world).setBlockToAir(pos);
                }
                break;
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < MachineType.values().length ? getDefaultState().withProperty(MACHINE_TYPE, MachineType.values()[meta]) : getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        MachineType type = state.getValue(MACHINE_TYPE);
        return type == null ? 0 : type.ordinal();
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isTranslucent(IBlockState state) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, MACHINE_TYPE);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        MachineType mt = getStateFromMeta(meta).getValue(MACHINE_TYPE);
        return mt.getName();
    }

    @Override
    public List<IBlockState> getValidStates() {
        List<IBlockState> ret = new LinkedList<>();
        for (MachineType type : MachineType.values()) {
            ret.add(getDefaultState().withProperty(MACHINE_TYPE, type));
        }
        return ret;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(MACHINE_TYPE).getName();
    }

    public static enum MachineType implements IStringSerializable, IVariantTileProvider {

        TELESCOPE((world, pos) -> new TileTelescope()),
        GRINDSTONE((world, pos) -> new TileGrindstone());

        private final IVariantTileProvider provider;

        private MachineType(IVariantTileProvider provider) {
            this.provider = provider;
        }

        @Override
        public TileEntity provideTileEntity(World world, IBlockState state) {
            return provider.provideTileEntity(world, state);
        }

        public int getMeta() {
            return ordinal();
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.blockMachine, 1, getMeta());
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        @Override
        public String toString() {
            return getName();
        }
    }

}
