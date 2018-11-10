/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.core.patch.helper;

import hellfirepvp.astralsorcery.core.ClassPatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchEntityLivingBaseWaterSlowDown
 * Created by HellFirePvP
 * Date: 22.09.2018 / 10:17
 */
public class PatchEntityLivingBaseWaterSlowDown extends ClassPatch {

    public PatchEntityLivingBaseWaterSlowDown() {
        super("net.minecraft.entity.EntityLivingBase");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethod(cn, "getWaterSlowDown", "func_189749_co", "()F");
        int index = 0;
        while ((index = peekFirstInstructionAfter(mn, index, Opcodes.FRETURN)) != -1) {
            AbstractInsnNode fRet = mn.instructions.get(index);
            mn.instructions.insertBefore(fRet, new VarInsnNode(Opcodes.ALOAD, 0)); //thisEntity
            mn.instructions.insertBefore(fRet, new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "hellfirepvp/astralsorcery/common/event/listener/EventHandlerCapeEffects",
                    "getWaterSlowDown",
                    "(FLnet/minecraft/entity/EntityLivingBase;)F",
                    false));
            index = mn.instructions.indexOf(fRet) + 1;
        }
    }
}
