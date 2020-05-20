function initializeCoreMod() {
    return {
        'allow_entity_interaction': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.network.play.ServerPlayNetHandler',
                'methodName': 'func_147340_a',
                'methodDesc': '(Lnet/minecraft/network/play/client/CUseEntityPacket;)V'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'allow_entity_interaction\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');

                var instanceOfChain = ASMAPI.findFirstInstructionAfter(method, Opcodes.INSTANCEOF, 0);

                while (instanceOfChain !== null) {
                    if (instanceOfChain.desc === 'net/minecraft/entity/item/ItemEntity') {
                        var instanceOfExpOrb = ASMAPI.findFirstInstructionAfter(method, Opcodes.INSTANCEOF, method.instructions.indexOf(instanceOfChain) + 1);
                        if (instanceOfExpOrb !== null && instanceOfExpOrb.desc === 'net/minecraft/entity/item/ExperienceOrbEntity') {
                            // Label jump that jumps to the continuing code, after the disconnect-kick block
                            var jumpInsnNode = ASMAPI.findFirstInstructionAfter(method, Opcodes.IF_ACMPNE, method.instructions.indexOf(instanceOfChain));
                            if (jumpInsnNode !== null) {
                                var loadEntity = method.instructions.get(method.instructions.indexOf(instanceOfChain) - 1);

                                method.instructions.insertBefore(loadEntity, new VarInsnNode(Opcodes.ALOAD, 3));
                                method.instructions.insertBefore(loadEntity, new TypeInsnNode(Opcodes.INSTANCEOF, 'hellfirepvp/astralsorcery/common/entity/InteractableEntity'));
                                method.instructions.insertBefore(loadEntity, new JumpInsnNode(Opcodes.IFNE, jumpInsnNode.label));
                            }
                        }
                    }
                    instanceOfChain = ASMAPI.findFirstInstructionAfter(method, Opcodes.INSTANCEOF, method.instructions.indexOf(instanceOfChain) + 2);
                }

                ASMAPI.log('INFO', 'Added \'allow_entity_interaction\' ASM patch!');
                return method;
            }
        }
    }
}