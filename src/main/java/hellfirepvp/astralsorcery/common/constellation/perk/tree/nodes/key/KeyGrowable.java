/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.CropHelper;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyGrowable
 * Created by HellFirePvP
 * Date: 20.07.2018 / 16:16
 */
public class KeyGrowable extends KeyPerk implements IPlayerTickPerk {

    private static int chanceToBonemeal = 3;
    private static int radius = 3;

    public KeyGrowable(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {
            @Override
            public void loadFromConfig(Configuration cfg) {
                chanceToBonemeal = cfg.getInt("Growth_Chance", getConfigurationSection(), chanceToBonemeal, 1, 1_000_000,
                        "Sets the chance (Random.nextInt(chance) == 0) to try to see if a random plant near the player gets bonemeal'd; the lower the more likely");
                radius = cfg.getInt("Radius", getConfigurationSection(), radius, 1, 16,
                        "Defines the radius around which the perk effect should apply around the player");
            }
        });
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if (side != Side.SERVER) {
            return;
        }
        float cChance = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, chanceToBonemeal);
        int chance = Math.max(MathHelper.floor(cChance), 1);
        if(rand.nextInt(chance) == 0) {
            float fRadius = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, radius);
            int rRadius = Math.max(MathHelper.floor(fRadius), 1);

            BlockPos pos = player.getPosition().add(
                    rand.nextInt(rRadius * 2) + 1 - rRadius,
                    rand.nextInt(rRadius * 2) + 1 - rRadius,
                    rand.nextInt(rRadius * 2) + 1 - rRadius);
            World w = player.getEntityWorld();
            CropHelper.GrowablePlant plant = CropHelper.wrapPlant(w, pos);
            PktParticleEvent pkt = null;
            if(plant != null) {
                if(plant.tryGrow(w, rand)) {
                    pkt = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_CROP_INTERACT, pos);
                }
            } else {
                IBlockState at = w.getBlockState(pos);
                    /*if(at.getBlock() instanceof IGrowable) {
                        if(((IGrowable) at.getBlock()).canUseBonemeal(w, rand, pos, at)) {
                            ((IGrowable) at.getBlock()).grow(w, rand, pos, at);
                            pkt = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_CROP_INTERACT, pos);
                        }
                    } else*/ if(at.getBlock() instanceof BlockDirt && at.getValue(BlockDirt.VARIANT).equals(BlockDirt.DirtType.DIRT)) {
                    if (w.setBlockState(pos, Blocks.GRASS.getDefaultState())) {
                        pkt = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_CROP_INTERACT, pos);
                    }
                }
            }
            if(pkt != null) {
                PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(w, pos, 16));
            }
        }
    }

}
