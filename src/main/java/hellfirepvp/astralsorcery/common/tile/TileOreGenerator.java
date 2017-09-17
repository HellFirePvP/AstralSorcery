/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerIO;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileOreGenerator
 * Created by HellFirePvP
 * Date: 20.07.2017 / 18:37
 */
public class TileOreGenerator extends TileEntitySynchronized {

    private static boolean generatingOre = false;

    private boolean structural = false;
    private int remainingGuaranteed = 0;

    public static void createStructuralTile(World world, BlockPos pos) {
        TileOreGenerator gen = new TileOreGenerator();
        gen.structural = true;
        gen.remainingGuaranteed = ConfigEntryMultiOre.guaranteedOres;
        gen.setWorld(world);
        gen.setPos(pos);
        gen.validate();
        Chunk ch = world.getChunkFromBlockCoords(pos);
        ch.getTileEntityMap().put(pos, gen);
        world.setTileEntity(pos, gen);
        gen.updateContainingBlockInfo();
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
        if(world.isRemote) return false;
        if(generatingOre) return false;
        if(newState.getBlock().getClass().equals(oldState.getBlock().getClass())) {
            return false; //Uhm... shit like redstone ore and stuff.
        }
        if(remainingGuaranteed > 0) {
            if(world instanceof WorldServer) {
                BlockCustomOre.allowCrystalHarvest = true;
                MiscUtils.breakBlockWithoutPlayer((WorldServer) world, pos, oldState, false, false);
                BlockCustomOre.allowCrystalHarvest = false;
            }
            generatingOre = true;
            IBlockState state;
            if(ConfigEntryMultiOre.oreChance == 0 || rand.nextInt(ConfigEntryMultiOre.oreChance) != 0) {
                state = Blocks.STONE.getDefaultState();
            } else {
                if(rand.nextInt(200) == 0) {
                    state = BlocksAS.customOre.getDefaultState().withProperty(BlockCustomOre.ORE_TYPE, BlockCustomOre.OreType.ROCK_CRYSTAL);
                } else {
                    ItemStack stack = OreTypes.getRandomOre(rand, !ConfigEntryMultiOre.allowModdedOres);
                    state = ItemUtils.createBlockState(stack);
                    if(state == null) {
                        state = Blocks.STONE.getDefaultState();
                    }
                }
            }
            world.setBlockState(pos, state);
            if(structural) {
                PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.GEN_STRUCTURE, pos);
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, pos, 32));
            }
            generatingOre = false;
            remainingGuaranteed--;
            markForUpdate();
            return false;
        }
        return true;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.structural = compound.getBoolean("struct");
        this.remainingGuaranteed = compound.getInteger("remaining");

        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            EventHandlerIO.generatorQueue.add(this);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("struct", this.structural);
        compound.setInteger("remaining", this.remainingGuaranteed);
    }

    @SideOnly(Side.CLIENT)
    public static void playGenerateStructureEffect(PktParticleEvent pktParticleEvent) {
        BlockPos pos = pktParticleEvent.getVec().toBlockPos();
        for (int i = 0; i < 40 + rand.nextInt(30); i++) {
            Vector3 particlePos = new Vector3(
                    pos.getX() - 3 + rand.nextFloat() * 7,
                    pos.getY() - 3 + rand.nextFloat() * 7,
                    pos.getZ() - 3 + rand.nextFloat() * 7
            );
            Vector3 dir = particlePos.clone().subtract(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).normalize().divide(-30);
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(particlePos.getX(), particlePos.getY(), particlePos.getZ());
            p.motion(dir.getX(), dir.getY(), dir.getZ()).setAlphaMultiplier(1F).setMaxAge(rand.nextInt(40) + 20);
            p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(0.2F + rand.nextFloat() * 0.1F).setColor(Constellations.armara.getConstellationColor());
        }
    }

    public static class ConfigEntryMultiOre extends ConfigEntry {

        public static final ConfigEntryMultiOre instance = new ConfigEntryMultiOre();

        private static boolean allowModdedOres = true;
        private static int guaranteedOres = 550;
        private static int chanceDespawn = 100;
        private static int oreChance = 2;

        private ConfigEntryMultiOre() {
            super(Section.MACHINERY, "multi-ore");
        }

        @Override
        public String getConfigurationSection() {
            return super.getConfigurationSection() + "." + getKey();
        }

        @Override
        public void loadFromConfig(Configuration cfg) {
            allowModdedOres = cfg.getBoolean("allowModdedOres", getConfigurationSection(), allowModdedOres, "Set this to true to also allow several modded ores to spawn");
            guaranteedOres = cfg.getInt("guaranteedBlocks", getConfigurationSection(), guaranteedOres, 0, Integer.MAX_VALUE, "This value defines how often the block can be broken and will 100% respawn again.");
            chanceDespawn = cfg.getInt("chanceDespawn", getConfigurationSection(), chanceDespawn, 1, Integer.MAX_VALUE, "This value defines how high the chance is after 'guaranteedBlocks' has been reached that the block-respawner despawns. The higher this number, the more unlikely it is to despawn.");
            oreChance = cfg.getInt("oreChance", getConfigurationSection(), oreChance, 0, Integer.MAX_VALUE, "This defines how often an ore will be generated instead of a stone. The higher the number the more rare. Set to 0 to have it never generate ore, only stone.");
        }

    }

}
