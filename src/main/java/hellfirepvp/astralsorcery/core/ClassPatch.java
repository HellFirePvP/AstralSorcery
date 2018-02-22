/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.core;

import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClassPatch
 * Created by HellFirePvP
 * Date: 04.08.2016 / 00:39
 */
public abstract class ClassPatch {

    public boolean writeAsClassFile = false;

    private final String className;//, classNameObf;

    public ClassPatch(String className) {
        this.className = className;
    }

    public void transform(ClassNode node) {
        try {
            patch(node);
        } catch (ASMTransformationException exc) {
            throw new ASMTransformationException("Failed to applyServer ASM Transformation ClassPatch " + getClass().getSimpleName().toUpperCase(), exc);
        }
    }

    public abstract void patch(ClassNode cn);

    public String getClassName() {
        return className;
    }

    @Nonnull
    public MethodNode getMethodLazy(ClassNode cn, String deobf, String obf) {
        for (MethodNode m : cn.methods) {
            if (m.name.equals(obf) || m.name.equals(deobf)) {
                return m;
            }
        }
        FMLLog.info("[AstralTransformer] Find method will fail. Printing all methods as debug...");
        for (MethodNode found : cn.methods) {
            FMLLog.info("Method: mame=" + found.name + ", desc=" + found.desc + ", signature=" + found.signature);
        }
        throw new ASMTransformationException("Could not find method: " + deobf + "/" + obf);
    }

    @Nonnull
    public MethodNode getMethod(ClassNode cn, String deobf, String obf, String sig) {
        for (MethodNode m : cn.methods) {
            if ((m.name.equals(obf) || m.name.equals(deobf)) && m.desc.equals(sig)) {
                return m;
            }
        }
        FMLLog.info("[AstralTransformer] Find method will fail. Printing all methods as debug...");
        for (MethodNode found : cn.methods) {
            FMLLog.info("Method: mame=" + found.name + ", desc=" + found.desc + ", signature=" + found.signature);
        }
        throw new ASMTransformationException("Could not find method: " + deobf + "/" + obf);
    }
    @Nonnull
    public AbstractInsnNode findNthInstruction(MethodNode mn, int n, int opCode) {
        return findNthInstructionAfter(mn, n, 0, opCode);
    }

    @Nonnull
    public AbstractInsnNode findNthInstructionAfter(MethodNode mn, int n, int startingIndex, int opCode) {
        AbstractInsnNode node = findFirstInstructionAfter(mn, startingIndex, opCode);
        int currentIndex = mn.instructions.indexOf(node) + 1;
        for (int i = 0; i <= (n - 1); i++) {
            node = findFirstInstructionAfter(mn, currentIndex, opCode);
            currentIndex = mn.instructions.indexOf(node) + 1;
        }
        return node;
    }

    @Nonnull
    public AbstractInsnNode findFirstInstruction(MethodNode mn, int opCode) {
        return findFirstInstructionAfter(mn, 0, opCode);
    }

    @Nonnull
    public AbstractInsnNode findFirstInstructionAfter(MethodNode mn, int startingIndex, int opCode) {
        for (int i = startingIndex; i < mn.instructions.size(); i++) {
            AbstractInsnNode ain = mn.instructions.get(i);
            if (ain.getOpcode() == opCode)
                return ain;
        }
        throw new ASMTransformationException("Couldn't find Instruction with opcode " + opCode);
    }

    public static int peekFirstInstructionAfter(MethodNode mn, int startingIndex, int opCode) {
        for (int i = startingIndex; i < mn.instructions.size(); i++) {
            AbstractInsnNode ain = mn.instructions.get(i);
            if (ain.getOpcode() == opCode)
                return i;
        }
        return -1;
    }

    @Nonnull
    public static MethodInsnNode getFirstMethodCallAfter(MethodNode mn, String owner, String nameDeobf, String nameObf, String sig, int startingIndex) {
        for (int i = startingIndex; i < mn.instructions.size(); i++) {
            AbstractInsnNode ain = mn.instructions.get(i);
            if (ain instanceof MethodInsnNode) {
                MethodInsnNode min = (MethodInsnNode)ain;
                if (min.owner.equals(owner) && (min.name.equals(nameDeobf) || min.name.equals(nameObf)) && min.desc.equals(sig)) {
                    return min;
                }
            }
        }
        throw new ASMTransformationException("Couldn't find method Instruction: owner=" + owner + " nameDeobf=" + nameDeobf + " nameObf=" + nameObf + " signature=" + sig);
    }

    public static int peekFirstMethodCallAfter(MethodNode mn, String owner, String nameDeobf, String nameObf, String sig, int startingIndex) {
        for (int i = startingIndex; i < mn.instructions.size(); i++) {
            AbstractInsnNode ain = mn.instructions.get(i);
            if (ain instanceof MethodInsnNode) {
                MethodInsnNode min = (MethodInsnNode)ain;
                if (min.owner.equals(owner) && (min.name.equals(nameDeobf) || min.name.equals(nameObf)) && min.desc.equals(sig)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Nonnull
    public static MethodInsnNode getFirstMethodCall(MethodNode mn, String owner, String nameDeobf, String nameObf, String sig) {
        return getNthMethodCallAfter(mn, owner, nameDeobf, nameObf, sig, 0, 0);
    }

    @Nonnull
    public static MethodInsnNode getNthMethodCall(MethodNode mn, String owner, String nameDeobf, String nameObf, String sig, int n) {
        return getNthMethodCallAfter(mn, owner, nameDeobf, nameObf, sig, n, 0);
    }

    @Nonnull
    public static MethodInsnNode getNthMethodCallAfter(MethodNode mn, String owner, String nameDeobf, String nameObf, String sig, int n, int startingIndex) {
        MethodInsnNode node = getFirstMethodCallAfter(mn, owner, nameDeobf, nameObf, sig, startingIndex);
        int currentIndex = mn.instructions.indexOf(node) + 1;
        for (int i = 0; i <= (n - 1); i++) {
            node = getFirstMethodCallAfter(mn, owner, nameDeobf, nameObf, sig, currentIndex);
            currentIndex = mn.instructions.indexOf(node) + 1;
        }
        return node;
    }

    @Nonnull
    public static LabelNode getFirstLabelBefore(MethodNode mn, int startIndex) {
        for (int i = startIndex; i >= 0; i--) {
            AbstractInsnNode isn = mn.instructions.get(i);
            if(isn instanceof LabelNode) return (LabelNode) isn;
        }
        throw new ASMTransformationException("Couldn't find LabelNode before index " + startIndex);
    }

    @Nonnull
    public static LabelNode getNextLabelAfter(MethodNode mn, int startIndex) {
        for (int i = startIndex; i < mn.instructions.size(); i++) {
            AbstractInsnNode isn = mn.instructions.get(i);
            if(isn instanceof LabelNode) return (LabelNode) isn;
        }
        throw new ASMTransformationException("Couldn't find LabelNode after index " + startIndex);
    }

}
