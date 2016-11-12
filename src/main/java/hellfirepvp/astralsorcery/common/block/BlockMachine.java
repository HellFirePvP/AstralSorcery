package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.item.base.IGrindable;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.IVariantTileProvider;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileGrindstone;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.SwordSharpenHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
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
    public String getHarvestTool(IBlockState state) {
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
        MachineType t = state.getValue(MACHINE_TYPE);
        switch (t) {
            case TELESCOPE:
                return new AxisAlignedBB(0, 0, 0, 1, 2, 1);
        }
        return super.getBoundingBox(state, source, pos);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(MACHINE_TYPE) != null;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (MachineType type : MachineType.values()) {
            list.add(type.asStack());
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        MachineType type = state.getValue(MACHINE_TYPE);
        if (type == null) return null;
        return type.provideTileEntity(world, state);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        MachineType type = state.getValue(MACHINE_TYPE);
        if (type == null || type != MachineType.GRINDSTONE) return;
        TileGrindstone tgr = MiscUtils.getTileAt(worldIn, pos, TileGrindstone.class, true);
        if(tgr == null || tgr.getGrindingItem() == null) return;
        ItemUtils.dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, tgr.getGrindingItem());
    }

    @Override
    public boolean onBlockActivated(World worldObj, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        MachineType type = state.getValue(MACHINE_TYPE);
        if (type == null) return true;
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        switch (type) {
            case TELESCOPE:
                if (player.worldObj.isRemote) {
                    AstralSorcery.proxy.openGui(CommonProxy.EnumGuiId.TELESCOPE, player, player.worldObj, posX, posY, posZ);
                }
                break;
            case GRINDSTONE:
                TileGrindstone tgr = MiscUtils.getTileAt(worldObj, pos, TileGrindstone.class, true);
                if(tgr != null) {
                    if(!worldObj.isRemote) {
                        ItemStack grind = tgr.getGrindingItem();
                        if(grind != null) {
                            if(player.isSneaking()) {
                                ItemUtils.dropItem(worldObj, posX + 0.5F, posY + 1F, posZ + 0.5F, grind);

                                tgr.setGrindingItem(null);
                            } else {
                                Item i = grind.getItem();
                                if(i instanceof IGrindable) {
                                    IGrindable.GrindResult result = ((IGrindable) i).grind(tgr, grind, rand);
                                    switch (result.getType()) {
                                        case SUCCESS:
                                            tgr.setGrindingItem(grind); //Update
                                            break;
                                        case ITEMCHANGE:
                                            tgr.setGrindingItem(result.getStack());
                                            break;
                                        case FAIL_BREAK_ITEM:
                                            tgr.setGrindingItem(null);
                                            worldObj.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.AMBIENT, 0.5F, worldObj.rand.nextFloat() * 0.2F + 0.8F);
                                            break;
                                    }
                                    tgr.playWheelEffect();
                                } else if(i instanceof ItemSword) {
                                    if(rand.nextInt(40) == 0) {
                                        SwordSharpenHelper.setSwordSharpened(grind);
                                    }
                                    tgr.playWheelEffect();
                                }
                            }
                        } else {
                            if(stack != null) {
                                Item trySet = stack.getItem();
                                if(trySet instanceof IGrindable && ((IGrindable) trySet).canGrind(tgr, stack)) {
                                    ItemStack toSet = stack.copy();
                                    toSet.stackSize = 1;
                                    tgr.setGrindingItem(toSet);
                                    worldObj.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.AMBIENT, 0.5F, worldObj.rand.nextFloat() * 0.2F + 0.8F);

                                    if(!player.isCreative()) {
                                        stack.stackSize--;
                                    }
                                } else if(trySet instanceof ItemSword && !SwordSharpenHelper.isSwordSharpened(stack)) {
                                    ItemStack toSet = stack.copy();
                                    toSet.stackSize = 1;
                                    tgr.setGrindingItem(toSet);
                                    worldObj.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.AMBIENT, 0.5F, worldObj.rand.nextFloat() * 0.2F + 0.8F);

                                    if(!player.isCreative()) {
                                        stack.stackSize--;
                                    }
                                }
                            }
                        }
                    } else {
                        ItemStack grind = tgr.getGrindingItem();
                        if(grind != null && !player.isSneaking()) {
                            Item i = grind.getItem();
                            if(i instanceof IGrindable) {
                                if(((IGrindable) i).canGrind(tgr, grind)) {
                                    for (int j = 0; j < 8; j++) {
                                        worldObj.spawnParticle(EnumParticleTypes.CRIT, posX + 0.5, posY + 0.75, posZ,
                                                (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3,
                                                (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3,
                                                (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3);
                                    }
                                }
                            } else if(SwordSharpenHelper.canBeSharpened(grind) && !SwordSharpenHelper.isSwordSharpened(grind)) {
                                for (int j = 0; j < 8; j++) {
                                    worldObj.spawnParticle(EnumParticleTypes.CRIT, posX + 0.5, posY + 0.75, posZ,
                                            (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3,
                                            (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3,
                                            (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3);
                                }
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
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighbor) {
        switch (state.getValue(MACHINE_TYPE)) {
            case TELESCOPE:
                if(world.isAirBlock(pos.up())) {
                    world.setBlockToAir(pos);
                }
                break;
        }
        super.neighborChanged(state, world, pos, neighbor);
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
    public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
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
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return super.getPickBlock(world.getBlockState(pos), target, world, pos, player); //Waila fix. wtf. why waila. why.
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
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
        return mt == null ? "null" : mt.getName();
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
