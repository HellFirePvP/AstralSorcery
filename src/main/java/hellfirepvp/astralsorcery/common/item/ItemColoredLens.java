package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.tick.TickManager;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.network.packet.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.potion.PotionCheatDeath;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalLens;
import hellfirepvp.astralsorcery.common.util.CropHelper;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import hellfirepvp.astralsorcery.common.util.data.TickTokenizedMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemColoredLens
 * Created by HellFirePvP
 * Date: 29.11.2016 / 12:35
 */
public class ItemColoredLens extends Item implements ItemDynamicColor {

    public ItemColoredLens() {
        setMaxStackSize(16);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (ColorType ct : ColorType.values()) {
            subItems.add(new ItemStack(itemIn, 1, ct.getMeta()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if(stack != null && stack.getItem() != null && stack.getItem() instanceof ItemColoredLens) {
            int dmg = stack.getItemDamage();
            if(dmg >= 0 && dmg < ColorType.values().length) {
                tooltip.add(I18n.format("item.ItemColoredLens.effect." + ColorType.values()[dmg].name().toLowerCase() + ".name"));
            }
        }
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            ItemStack inHand = playerIn.getHeldItem(EnumHand.MAIN_HAND);
            ColorType type = null;
            if(inHand != null && inHand.getItem() != null && inHand.getItem() instanceof ItemColoredLens) {
                int dmg = inHand.getItemDamage();
                if(dmg >= 0 && dmg < ColorType.values().length) {
                    type = ColorType.values()[dmg];
                }
            }
            if(type != null) {
                TileCrystalLens lens = MiscUtils.getTileAt(worldIn, pos, TileCrystalLens.class, true);
                if(lens != null) {
                    ColorType oldType = lens.setLensColor(type);
                    SoundHelper.playSoundAround(Sounds.clipSwitch, worldIn, pos, 0.8F, 1.5F);
                    if(oldType != null) {
                        ItemUtils.dropItem(worldIn, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, oldType.asStack());
                    }
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public int getColorForItemStack(ItemStack stack, int tintIndex) {
        int dmg = stack.getItemDamage();
        if(dmg < 0 || dmg >= ColorType.values().length) return 0xFFFFFFFF;
        return ColorType.values()[dmg].colorRGB;
    }

    public static enum ColorType {

        FIRE   (TargetType.ENTITY, 0xFF5711, 0.05F),
        BREAK  (TargetType.BLOCK,  0xD4FF00, 0.10F),
        GROW   (TargetType.BLOCK,  0x00D736, 0.40F),
        DAMAGE (TargetType.ENTITY, 0x767676, 0.60F),
        REGEN  (TargetType.ENTITY, 0xA13085, 0.90F),
        NIGHT  (TargetType.ENTITY, 0x008EAE, 0.25F);

        private static final Map<Integer, TickTokenizedMap<BlockPos, BreakEntry>> breakMap = new HashMap<>();

        public final int colorRGB;
        public final Color wrappedColor;
        private final float flowReduction;
        private final TargetType type;

        private ColorType(TargetType type, int colorRGB, float flowReduction) {
            this.type = type;
            this.colorRGB = colorRGB;
            this.wrappedColor = new Color(colorRGB);
            this.flowReduction = flowReduction;
        }

        public TargetType getType() {
            return type;
        }

        public float getFlowReduction() {
            return flowReduction;
        }

        public String getUnlocalizedName() {
            return name().toLowerCase();
        }

        public ItemStack asStack() {
            return new ItemStack(ItemsAS.coloredLens, 1, getMeta());
        }

        public int getMeta() {
            return ordinal();
        }

        public void onLivingEntityInBeam(EntityLivingBase living, float percStrength) {
            switch (this) {
                case FIRE:
                    if(living.getEntityWorld().rand.nextFloat() > percStrength) return;
                    living.setFire(1);
                    break;
                case DAMAGE:
                    if(living.getEntityWorld().rand.nextFloat() > percStrength) return;
                    living.attackEntityFrom(CommonProxy.dmgSourceStellar, 0.6F);
                    break;
                case REGEN:
                    if(living.getEntityWorld().rand.nextFloat() > percStrength) return;
                    living.heal(0.6F);
                    break;
                case NIGHT:
                    if(living.getEntityWorld().rand.nextFloat() > percStrength) return;
                    living.addPotionEffect(new PotionEffect(Potion.getPotionById(16), 300, 0, true, true));
                    break;
            }
        }

        public void onBlockOccupyingBeam(World world, BlockPos at, IBlockState state, float percStrength) {
            switch (this) {
                case BREAK:
                    float hardness = state.getBlockHardness(world, at);
                    if(hardness < 0) return;
                    hardness *= 10F;
                    addProgress(world, at, hardness, percStrength);
                    PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.EffectType.BEAM_BREAK, at);
                    PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(world, at, 16));
                    break;
                case GROW:
                    if(world.rand.nextFloat() > percStrength) return;
                    CropHelper.GrowablePlant plant = CropHelper.wrapPlant(world, at);
                    if(plant != null) {
                        plant.tryGrow(world, world.rand);
                        PktParticleEvent packet = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_CROP_INTERACT, at);
                        PacketChannel.CHANNEL.sendToAllAround(packet, PacketChannel.pointFromPos(world, at, 16));
                    }
                    break;
                /*case HARVEST:
                    if(world.rand.nextFloat() > percStrength) return;
                    CropHelper.HarvestablePlant harvest = CropHelper.wrapHarvestablePlant(world, at);
                    if(harvest != null) {
                        harvest.tryGrow(world, world.rand);
                        if(harvest.canHarvest(world)) {
                            List<ItemStack> drops = harvest.harvestDropsAndReplant(world, world.rand, 4);
                            for (ItemStack st : drops) {
                                ItemUtils.dropItemNaturally(world, at.getX() + 0.5, at.getY() + 0.5, at.getZ() + 0.5, st);
                            }
                        }
                        PktParticleEvent packet = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_CROP_INTERACT, at);
                        PacketChannel.CHANNEL.sendToAllAround(packet, PacketChannel.pointFromPos(world, at, 16));
                    }
                    break;*/
            }
        }

        private void addProgress(World world, BlockPos pos, float expectedHardness, float percStrength) {
            TickTokenizedMap<BlockPos, BreakEntry> map = breakMap.get(world.provider.getDimension());
            if(map == null) {
                map = new TickTokenizedMap<>(TickEvent.Type.SERVER);
                TickManager.getInstance().register(map);
                breakMap.put(world.provider.getDimension(), map);
            }

            BreakEntry breakProgress = map.get(pos);
            if(breakProgress == null) {
                breakProgress = new BreakEntry(expectedHardness, world, pos, world.getBlockState(pos));
                map.put(pos, breakProgress);
            }

            breakProgress.breakProgress -= percStrength;
            breakProgress.idleTimeout = 0;
        }

        @SideOnly(Side.CLIENT)
        public static void blockBreakAnimation(PktPlayEffect pktPlayEffect) {
            World w = Minecraft.getMinecraft().world;
            RenderingUtils.playBlockBreakParticles(pktPlayEffect.pos, w.getBlockState(pktPlayEffect.pos));
        }

    }

    private static class BreakEntry implements TickTokenizedMap.TickMapToken<Float> {

        private float breakProgress;
        private final World world;
        private final BlockPos pos;
        private final IBlockState expected;

        private int idleTimeout;

        public BreakEntry(@Nonnull Float value, World world, BlockPos at, IBlockState expectedToBreak) {
            this.breakProgress = value;
            this.world = world;
            this.pos = at;
            this.expected = expectedToBreak;
        }

        @Override
        public int getRemainingTimeout() {
            return (breakProgress <= 0 || idleTimeout >= 20) ? 0 : 1;
        }

        @Override
        public void tick() {
            idleTimeout++;
        }

        @Override
        public void onTimeout() {
            if(breakProgress > 0) return;

            IBlockState nowAt = world.getBlockState(pos);
            if(nowAt.getBlock().equals(expected.getBlock()) && nowAt.getBlock().getMetaFromState(nowAt) == expected.getBlock().getMetaFromState(expected)) {
                List<ItemStack> drops = nowAt.getBlock().getDrops(world, pos, nowAt, 5);
                world.setBlockToAir(pos);
                PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.EffectType.BEAM_BREAK, pos);
                PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(world, pos, 16));
                for (ItemStack stack : drops) {
                    ItemUtils.dropItemNaturally(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }

        @Override
        public Float getValue() {
            return breakProgress;
        }

    }

    //Respectively only Entity-checks or only block-checks will be done.
    public static enum TargetType {

        ENTITY, BLOCK

    }

}
