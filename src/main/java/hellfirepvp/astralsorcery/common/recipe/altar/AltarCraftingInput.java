/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipeInput;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileFocusRelay;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarCraftingInput
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarCraftingInput extends CustomRecipeInput {

    @Nullable
    private final TileAltar altar;
    @Nullable
    private final UUID playerUUID;
    @Nullable
    private final BaseConstellation focusConstellation;
    private final List<ItemStack> gridInputs = NonNullList.withSize(9, ItemStack.EMPTY);
    private final List<ItemStack> relayInputs = NonNullList.withSize(25, ItemStack.EMPTY);

    public AltarCraftingInput(@Nullable TileAltar altar, @Nullable UUID playerUUID, @Nullable BaseConstellation focusConstellation) {
        this.altar = altar;
        this.playerUUID = playerUUID;
        this.focusConstellation = focusConstellation;
    }

    public static AltarCraftingInput create(TileAltar altar, BlockGetter level, @Nullable UUID playerUUID) {
        BaseConstellation cst = altar.getTileData().getFocusedConstellation().orElse(null);
        AltarCraftingInput input = new AltarCraftingInput(altar, playerUUID, cst);

        InventoryView inv = altar.getTileData().getAltarInventory();
        for (int slot = 0; slot < input.gridInputs.size(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            input.gridInputs.set(slot, stack.copy());
        }

        Map<Integer, BlockPos> relayOffsets = TileAltar.getRelayGridOffsets();
        BlockPos altarPos = altar.getBlockPos();
        for (int relaySlot = 0; relaySlot < 25; relaySlot++) {
            BlockPos offset = relayOffsets.get(relaySlot);
            if (offset == null) continue;

            int finalRelaySlot = relaySlot;
            MiscUtil.getTileAt(level, altarPos.offset(offset), TileFocusRelay.class, true).ifPresent(relay -> {
                ItemStack relayStack = relay.getTileData().getInventory().getStackInSlot(0);
                if (!relayStack.isEmpty()) {
                    input.relayInputs.set(finalRelaySlot, relayStack.copy());
                }
            });
        }

        return input;
    }

    public static AltarCraftingInput createDisplay(@Nullable BaseConstellation focusConstellation, List<ItemStack> gridInputs, List<ItemStack> relayInputs) {
        AltarCraftingInput input = new AltarCraftingInput(null, null, focusConstellation);
        for (int i = 0; i < Math.min(input.gridInputs.size(), gridInputs.size()); i++) {
            input.gridInputs.set(i, gridInputs.get(i).copy());
        }
        for (int i = 0; i < Math.min(input.relayInputs.size(), relayInputs.size()); i++) {
            input.relayInputs.set(i, relayInputs.get(i).copy());
        }
        return input;
    }

    @Nullable
    public TileAltar getAltar() {
        return this.altar;
    }

    @Nullable
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public Optional<ServerPlayer> getCraftingServerPlayer() {
        if (this.getPlayerUUID() == null) return Optional.empty();
        return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer())
                .map(srv -> srv.getPlayerList().getPlayer(this.getPlayerUUID()));
    }

    public List<ItemStack> getGridInputs() {
        return Collections.unmodifiableList(this.gridInputs);
    }

    public List<ItemStack> getRelayInputs() {
        return Collections.unmodifiableList(this.relayInputs);
    }

    public Optional<BaseConstellation> getFocusConstellation() {
        return Optional.ofNullable(this.focusConstellation);
    }

    @Override
    public boolean isEmpty() {
        return this.getGridInputs().stream().allMatch(ItemStack::isEmpty) &&
                this.getRelayInputs().stream().allMatch(ItemStack::isEmpty);
    }
}
