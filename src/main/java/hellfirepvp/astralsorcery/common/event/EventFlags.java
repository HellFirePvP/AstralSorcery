/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventFlags
 * Created by HellFirePvP
 * Date: 12.01.2020 / 15:12
 */
public class EventFlags {

    //Attack based stuff
    public static BooleanFlag SWEEP_ATTACK = new BooleanFlag(false);
    public static BooleanFlag LIGHTNING_ARC = new BooleanFlag(false);
    public static BooleanFlag MANTLE_DISCIDIA_ADDED = new BooleanFlag(false);

    //Block breaking
    public static BooleanFlag CHAIN_MINING = new BooleanFlag(false);
    public static BooleanFlag MINING_SIZE_BREAK = new BooleanFlag(false);
    public static BooleanFlag CHECK_BREAK_SPEED = new BooleanFlag(false);
    public static BooleanFlag CHECK_UNDERWATER_BREAK_SPEED = new BooleanFlag(false);
    public static BooleanFlag PLAY_BLOCK_BREAK_EFFECTS = new BooleanFlag(false);

    //Rendering stuff
    public static BooleanFlag SKY_RENDERING = new BooleanFlag(false);
    public static BooleanFlag GUI_CLOSING = new BooleanFlag(false);

    public static class BooleanFlag {

        private boolean originalState;
        private boolean flag;

        private BooleanFlag(boolean flag) {
            this.flag = flag;
            this.originalState = flag;
        }

        public boolean isSet() {
            return originalState != flag;
        }

        public void executeWithFlag(Runnable run) {
            if (originalState == flag) {
                flag = !flag;
                try {
                    run.run();
                } finally {
                    flag = !flag;
                }
            }
        }
    }
}
