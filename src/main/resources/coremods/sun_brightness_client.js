function initializeCoreMod() {
    return {
        'set_player_field': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.World',
                'methodName': 'getSunBrightnessBody',
                'methodDesc': '(F)F'
            },
            'transformer': function(method) {
                print('[AstralSorcery] Adding \'sun_brightness_client\' ASM patch...');

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var fReturn = ASMAPI.findFirstInstructionAfter(method, Opcodes.FRETURN, 0);

                while (fReturn !== null) {
                    method.instructions.insertBefore(fReturn, new VarInsnNode(Opcodes.ALOAD, 0));
                    method.instructions.insertBefore(fReturn, ASMAPI.buildMethodCall(
                        'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                        'overrideSunBrightnessClient',
                        "(FLnet/minecraft/world/World;)F",
                        ASMAPI.MethodType.STATIC));

                    fReturn = ASMAPI.findFirstInstructionAfter(method, Opcodes.FRETURN, method.instructions.indexOf(fReturn) + 2);
                }

                print('[AstralSorcery] Added \'sun_brightness_client\' ASM patch!');
                return method;
            }
        }
    }
}