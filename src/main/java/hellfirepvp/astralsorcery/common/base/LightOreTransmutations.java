/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightOreTransmutations
 * Created by HellFirePvP
 * Date: 30.01.2017 / 12:30
 */
public class LightOreTransmutations {

    private static List<Transmutation> registeredTransmutations = new LinkedList<>();

    public static void init() {
        registerTransmutation(new Transmutation(Blocks.COAL_BLOCK.getDefaultState(),         Blocks.OBSIDIAN.getDefaultState(),     400.0D));
        registerTransmutation(new Transmutation(Blocks.SAND.getDefaultState(),               Blocks.CLAY.getDefaultState(),         200.0D));
        registerTransmutation(new Transmutation(Blocks.DIAMOND_ORE.getDefaultState(),        Blocks.EMERALD_ORE.getDefaultState(), 3000.0D));
        registerTransmutation(new Transmutation(Blocks.NETHER_WART_BLOCK.getDefaultState(),  Blocks.SOUL_SAND.getDefaultState(),    200.0D));
        registerTransmutation(new Transmutation(Blocks.PUMPKIN.getDefaultState(),            Blocks.CAKE.getDefaultState(),        2000.0D));
        registerTransmutation(new Transmutation(Blocks.SEA_LANTERN.getDefaultState(),        Blocks.LAPIS_BLOCK.getDefaultState(),  200.0D));
    }

    public static void registerTransmutation(Transmutation tr) {
        for (Transmutation t : registeredTransmutations) {
            if(t.input.equals(tr.input)) {
                AstralSorcery.log.warn("Tried to register Transmutation that has the same input as an already existing one.");
                return;
            }
        }
        if(tr.input == null) {
            AstralSorcery.log.warn("Tried to register Transmutation with null input - Skipping!");
            return;
        }
        if(tr.input.getBlock().equals(Blocks.IRON_ORE) || tr.input.getBlock().equals(Blocks.CRAFTING_TABLE)) {
            AstralSorcery.log.warn("Cannot register Transmutation of iron ore/workbench -> something. By default occupied by the transmutation to starmetal or general crafting which is handled differently.");
            return;
        }
        if(tr.output == null) {
            AstralSorcery.log.warn("Tried to register Transmutation with null output - Skipping!");
            return;
        }
        registeredTransmutations.add(tr);
    }

    @Nullable
    public static Transmutation searchForTransmutation(IBlockState tryStateIn) {
        for (Transmutation tr : registeredTransmutations) {
            if(tr.input.equals(tryStateIn)) return tr;
        }
        return null;
    }

    public static class Transmutation {

        public final IBlockState input;
        public final IBlockState output;
        public final double cost;

        public Transmutation(IBlockState input, IBlockState output, double cost) {
            this.input = input;
            this.output = output;
            this.cost = cost;
        }
    }

}
