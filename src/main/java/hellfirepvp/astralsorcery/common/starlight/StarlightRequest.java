package hellfirepvp.astralsorcery.common.starlight;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightRequest
 * Created by HellFirePvP
 * Date: 03.08.2016 / 10:05
 */
public class StarlightRequest {

    private final RequestType type;
    private final Constellation constellation;
    private final int amount;
    private final BlockPos originPos;

    private StarlightRequest(RequestType type, @Nonnull BlockPos originPos, @Nullable Constellation c, int amount) {
        this.type = type;
        this.constellation = c;
        this.amount = amount;
        this.originPos = originPos;
    }

    public RequestType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public BlockPos getOriginPos() {
        return originPos;
    }

    public Constellation getConstellation() {
        return constellation;
    }

    public static StarlightRequest createWorkbenchRequest(BlockPos workbenchPos, int amount) {
        return new StarlightRequest(RequestType.WORKBENCH, workbenchPos, null, amount);
    }

    public static enum RequestType {

        WORKBENCH

    }

}
