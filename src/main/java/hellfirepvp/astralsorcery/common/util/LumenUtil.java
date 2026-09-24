/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenRequestChain;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
//Inspired by utility of FluidUtil
public class LumenUtil {

    // Returns the lumen stack that was actually drained & successfully filled from the chain source
    public static LumenStack tryChainTransfer(ILumenHandler destination, Level level, LumenRequestChain transferChain, LumenStack transfer, ILumenHandler.Action action) {
        ILumenHandler handler = level.getCapability(ILumenHandler.BLOCK, transferChain.getEndNode().getPos(), null);
        if (handler == null) return LumenStack.EMPTY;
        return tryLumenTransfer(destination, handler, transfer, action);
    }

    // Returns the lumen stack that was actually drained & successfully filled.
    public static LumenStack tryLumenTransfer(ILumenHandler destination, ILumenHandler source, LumenStack transfer, ILumenHandler.Action action) {
        LumenStack drainable = source.drain(transfer, ILumenHandler.Action.SIMULATE);
        if (!drainable.isEmpty() && drainable.isSameLumen(transfer)) {
            return doTransfer(destination, source, drainable, action);
        }
        return LumenStack.EMPTY;
    }

    private static LumenStack doTransfer(ILumenHandler destination, ILumenHandler source, LumenStack transfer, ILumenHandler.Action action) {
        int fillable = destination.fill(transfer, ILumenHandler.Action.SIMULATE);
        if (fillable <= 0) return LumenStack.EMPTY;

        LumenStack toDrain = transfer.copyWithAmount(fillable);
        if (action.isSimulate()) return toDrain;
        LumenStack drained = source.drain(toDrain, ILumenHandler.Action.EXECUTE);
        if (drained.isEmpty()) return LumenStack.EMPTY;
        return toDrain.copyWithAmount(destination.fill(drained, ILumenHandler.Action.EXECUTE));
    }

    public static LumenStack fill(ILumenHandler destination, LumenStack transfer, ILumenHandler.Action action) {
        int fillable = destination.fill(transfer, ILumenHandler.Action.SIMULATE);
        if (fillable <= 0) return LumenStack.EMPTY;
        if (action.isSimulate()) return transfer.copyWithAmount(fillable);
        return transfer.copyWithAmount(destination.fill(transfer, ILumenHandler.Action.EXECUTE));
    }

    public static LumenStack fillItem(ItemStack destination, LumenStack transfer, ILumenHandler.Action action) {
        if (destination.isEmpty()) return LumenStack.EMPTY;
        ILumenHandler handler = destination.getCapability(ILumenHandler.ITEM, null);
        if (handler == null) handler = StoredLumenComponent.getAsHandlerAccess(destination);
        if (handler == null) return LumenStack.EMPTY;
        return fill(handler, transfer, action);
    }

    public static LumenStack drain(ILumenHandler source, LumenStack transfer, ILumenHandler.Action action) {
        if (transfer.isEmpty()) return LumenStack.EMPTY;
        LumenStack drainable = source.drain(transfer, ILumenHandler.Action.SIMULATE);
        if (!drainable.isEmpty() && drainable.isSameLumen(transfer)) {
            if (action.isSimulate()) return drainable;
            return source.drain(transfer, ILumenHandler.Action.EXECUTE);
        }
        return LumenStack.EMPTY;
    }

    public static LumenStack drainItem(ItemStack source, LumenStack transfer, ILumenHandler.Action action) {
        if (source.isEmpty()) return LumenStack.EMPTY;
        ILumenHandler handler = source.getCapability(ILumenHandler.ITEM, null);
        if (handler == null) handler = StoredLumenComponent.getAsHandlerAccess(source);
        if (handler == null) return LumenStack.EMPTY;
        return drain(handler, transfer, action);
    }

    public static class Storage {

        public static void setStoredLumen(ItemStack stack, LumenStack lumen) {
            StoredLumenComponent cmp = stack.getOrDefault(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY);
            cmp = cmp.updateLumenStack(lumen.getLumen(), lumen.getAmount(), lumen.getAmount());
            stack.set(DataComponentsAS.STORED_LUMEN, cmp);
        }
    }
}
