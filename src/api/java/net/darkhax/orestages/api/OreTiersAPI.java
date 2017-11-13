package net.darkhax.orestages.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Tuple;

public final class OreTiersAPI {

    /**
     * A list of all relevant blockstates.
     */
    private static final List<IBlockState> RELEVANT_STATES = new ArrayList<>();

    /**
     * A map which links block states to their stage key.
     */
    public static final Map<IBlockState, Tuple<String, IBlockState>> STATE_MAP = new HashMap<>();

    /**
     * A map of all the replacement state ids.
     */
    public static final Map<String, String> REPLACEMENT_IDS = new HashMap<>();

    /**
     * Adds a replacement for a block state.
     *
     * @param stage The stage to add the replacement to.
     * @param original The original block.
     * @param originalMeta The original block meta.
     * @param replacement The replacement block.
     * @param replacementMeta The replacement block meta.
     */
    public static void addReplacement (@Nonnull String stage, @Nonnull Block original, int originalMeta, @Nonnull Block replacement, int replacementMeta) {

        addReplacement(stage, original.getStateFromMeta(originalMeta), replacement.getStateFromMeta(replacementMeta));
    }

    /**
     * Adds a replacement for a block state.
     *
     * @param stage The stage to add the replacement to.
     * @param original The original block.
     * @param replacement The block to replace it with.
     */
    public static void addReplacement (@Nonnull String stage, @Nonnull Block original, @Nonnull Block replacement) {

        addReplacement(stage, original.getDefaultState(), replacement.getDefaultState());
    }

    /**
     * Adds a replacement for a block state.
     *
     * @param stage The stage to add the replacement to.
     * @param original The original block state.
     * @param replacement The state to replace it with.
     */
    public static void addReplacement (@Nonnull String stage, @Nonnull IBlockState original, @Nonnull IBlockState replacement) {

        STATE_MAP.put(original, new Tuple<>(stage, replacement));

        addRelevantState(original);
        addRelevantState(replacement);

        REPLACEMENT_IDS.put(original.getBlock().getRegistryName().toString(), replacement.getBlock().getRegistryName().toString());
    }

    /**
     * Removes a replacement state.
     *
     * @param state The state to remove.
     */
    public static void removeReplacement (IBlockState state) {

        STATE_MAP.remove(state);
    }

    /**
     * Checks if a state has a replacement for it.
     *
     * @param stage The stage to check.
     * @param state The state to check for.
     * @return Whether or not the state has a replacement.
     */
    public static boolean hasReplacement (@Nonnull IBlockState state) {

        return STATE_MAP.containsKey(state);
    }

    /**
     * Unlocks a stage for a player.
     *
     * @param player The player to unlock the stage for.
     * @param stage The stage to unlock.
     */
    //public static void unlockStage (@Nonnull EntityPlayer player, @Nonnull String stage) {
    //    PlayerDataHandler.getStageData(player).unlockStage(stage);
    //}

    /**
     * Locks a stage for a player.
     *
     * @param player The player to lock the stage for.
     * @param stage The stage to lcok.
     */
    //public static void lockStage (@Nonnull EntityPlayer player, @Nonnull String stage) {
    //    PlayerDataHandler.getStageData(player).lockStage(stage);
    //}

    /**
     * Checks if a player has a stage.
     *
     * @param player The player to check.
     * @param stage The stage to check for.
     * @return Whether or not the player has the stage.
     */
    public static boolean hasStage (@Nonnull EntityPlayer player, @Nonnull String stage) {
        return false;
        //return PlayerDataHandler.getStageData(player).hasUnlockedStage(stage);
    }

    /**
     * Gets a set of all states to be replaced/wrapped.
     *
     * @return A set of all the states to replace/wrap.
     */
    public static Set<IBlockState> getStatesToReplace () {

        return STATE_MAP.keySet();
    }

    /**
     * Gets a list of all the relevant blockstates. This is used internally for getting the list of
     * models to wrap. See
     * {@link net.darkhax.orestages.OreTiersEventHandler#onModelBake(net.minecraftforge.client.event.ModelBakeEvent)}.
     *
     * @return A List of all the relevant states.
     */
    public static List<IBlockState> getRelevantStates () {

        return RELEVANT_STATES;
    }

    /**
     * Gets stage info from a blockstate.
     *
     * @param state The blockstate to get stage info for.
     * @return The stage info for the passed state.
     */
    @Nullable
    public static Tuple<String, IBlockState> getStageInfo (@Nonnull IBlockState state) {

        return STATE_MAP.get(state);
    }

    /**
     * Used internally add a relevant blockstate stage. Just a wrapper to prevent duplicate
     * entries.
     *
     * @param state The blockstate to add.
     */
    private static void addRelevantState (@Nonnull IBlockState state) {

        if (!RELEVANT_STATES.contains(state)) {

            RELEVANT_STATES.add(state);
        }
    }
}
