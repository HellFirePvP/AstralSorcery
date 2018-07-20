/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.type.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyStoneEnrichment
 * Created by HellFirePvP
 * Date: 17.07.2018 / 20:10
 */
public class KeyStoneEnrichment extends KeyPerk implements IPlayerTickPerk {

    private static final BlockStateCheck stoneCheck = new CleanStoneCheck();

    private static int enrichmentRadius = 3;
    private static int chanceToEnrich = 70;

    public KeyStoneEnrichment(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {
            @Override
            public void loadFromConfig(Configuration cfg) {
                enrichmentRadius = cfg.getInt("Effect_Radius", getConfigurationSection(), enrichmentRadius, 1, 35,
                        "Defines the radius where a random position to generate a ore at is searched");
                chanceToEnrich = cfg.getInt("Chance_To_CreateOre", getConfigurationSection(), chanceToEnrich, 2, 4_000_000,
                        "Sets the chance (Random.nextInt(chance) == 0) to try to see if a random stone next to the player should get turned into an ore; the lower the more likely");
            }
        });
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if (side == Side.SERVER) {
            float modChance = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, chanceToEnrich);
            if(rand.nextInt(Math.round(Math.max(modChance, 1))) == 0) {
                float enrRad = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, enrichmentRadius);
                Vector3 vec = Vector3.atEntityCenter(player).add(
                        (rand.nextFloat() * enrRad * 2) - enrRad,
                        (rand.nextFloat() * enrRad * 2) - enrRad,
                        (rand.nextFloat() * enrRad * 2) - enrRad);
                BlockPos pos = vec.toBlockPos();
                if(stoneCheck.isStateValid(player.getEntityWorld(), pos, player.getEntityWorld().getBlockState(pos))) {
                    ItemStack blockStack = OreTypes.AEVITAS_ORE_PERK.getRandomOre(rand);
                    if(!blockStack.isEmpty()) {
                        IBlockState state = ItemUtils.createBlockState(blockStack);
                        if(state != null) {
                            player.getEntityWorld().setBlockState(pos, state);
                        }
                    }
                }
            }
        }
    }

    private static class CleanStoneCheck implements BlockStateCheck {

        @Override
        public boolean isStateValid(World world, BlockPos pos, IBlockState state) {
            return state.getBlock() == Blocks.STONE && state.getValue(BlockStone.VARIANT).equals(BlockStone.EnumType.STONE);
        }

    }

}
