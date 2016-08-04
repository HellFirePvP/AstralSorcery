package hellfirepvp.astralsorcery.common.block.network;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.data.research.EnumGatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.block.ItemCollectorCrystal;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCollectorCrystal
 * Created by HellFirePvP
 * Date: 01.08.2016 / 12:58
 */
public class BlockCollectorCrystal extends BlockStarlightNetwork {

    private static AxisAlignedBB boxCrystal = new AxisAlignedBB(0.3, 0, 0.3, 0.7, 1, 0.7);

    public BlockCollectorCrystal() {
        super(Material.CORAL, MapColor.GRAY);
        setBlockUnbreakable();
        setResistance(20F);
        setHarvestLevel("pickaxe", 3);
        setSoundType(SoundType.GLASS);
        setLightLevel(1F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return boxCrystal;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        ItemStack stack = new ItemStack(itemIn);
        ItemCollectorCrystal.setConstellation(stack, Constellations.orion);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE, 100, 100));
        list.add(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        boolean shift = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
        CrystalProperties prop = CrystalProperties.getCrystalProperties(stack);
        Optional<Boolean> missing = CrystalProperties.addPropertyTooltip(prop, tooltip, shift);

        if(shift && missing.isPresent()) {
            EnumGatedKnowledge.ViewCapability cap = ResearchManager.clientProgress.getViewCapability();
            Constellation c = ItemCollectorCrystal.getConstellation(stack);
            if(c != null) {
                if(EnumGatedKnowledge.COLLECTOR_TYPE.canSee(cap) && !missing.get()) {
                    tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("crystal.collect.type") + " " + TextFormatting.BLUE + I18n.translateToLocal(c.getName()));
                }
            }
        }
    }

    /*@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            TileCollectorCrystal te = MiscUtils.getTileAt(worldIn, pos, TileCollectorCrystal.class);
            if(te != null) {
                playerIn.addChatMessage(new TextComponentString("PlayerMade: " + te.isPlayerMade()));
                playerIn.addChatMessage(new TextComponentString("Constellation: " + te.getTransmittingType().getName()));
                playerIn.addChatMessage(new TextComponentString("Can charge: " + te.canCharge()));
                playerIn.addChatMessage(new TextComponentString("Charge: " + te.getCharge()));
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
    }*/

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        TileCollectorCrystal te = MiscUtils.getTileAt(worldIn, pos, TileCollectorCrystal.class);
        if(te != null) {
            if(te.isPlayerMade()) {
                return 4.0F;
            }
        }
        return super.getBlockHardness(blockState, worldIn, pos);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if(placer == null || !(placer instanceof EntityPlayer)) return;
        TileCollectorCrystal te = MiscUtils.getTileAt(worldIn, pos, TileCollectorCrystal.class);
        if(te == null) return;

        Constellation c = ItemCollectorCrystal.getConstellation(stack);
        if(c == null) c = Constellations.bigDipper;
        te.onPlace(c, CrystalProperties.getCrystalProperties(stack), true);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return Lists.newArrayList();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCollectorCrystal();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileCollectorCrystal te = MiscUtils.getTileAt(world, pos, TileCollectorCrystal.class);
        if(te != null) {
            ItemStack stack = new ItemStack(this);
            CrystalProperties.applyCrystalProperties(stack, te.getCrystalProperties());
            ItemCollectorCrystal.setConstellation(stack, te.getConstellation());
            return stack;
        }
        return super.getPickBlock(world.getBlockState(pos), target, world, pos, player);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileCollectorCrystal te = MiscUtils.getTileAt(worldIn, pos, TileCollectorCrystal.class);
        if(te != null && !worldIn.isRemote) {
            PktParticleEvent event = new PktParticleEvent(PktParticleEvent.ParticleEventType.COLLECTOR_BURST,
                    pos.getX(), pos.getY(), pos.getZ());
            PacketChannel.CHANNEL.sendToAllAround(event, PacketChannel.pointFromPos(worldIn, pos, 32));
            TileCollectorCrystal.breakDamage(worldIn, pos);
        }
        super.breakBlock(worldIn, pos, state);
    }
}
