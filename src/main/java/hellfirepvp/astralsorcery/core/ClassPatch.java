package hellfirepvp.astralsorcery.core;

import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;

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

    public byte[] transform(byte[] bytes) {
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);

        try {
            patch(node);
        } catch (ASMTransformationException exc) {
            throw new ASMTransformationException("Failed to apply ASM Transformation ClassPatch " + getClass().getSimpleName().toUpperCase(), exc);
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        bytes = writer.toByteArray();

        if(writeAsClassFile) {
            try {
                File f = new File("C:/ASTestClasses/" + getClass().getSimpleName() + ".class");
                f.getParentFile().mkdirs();
                f.createNewFile();
                FileOutputStream out = new FileOutputStream(f);
                out.write(bytes);
                out.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bytes;
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

}
