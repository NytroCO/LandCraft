package landmaster.landcraft.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import net.minecraft.launchwrapper.*;
import net.minecraftforge.fml.common.asm.transformers.deobf.*;

public class Transform implements IClassTransformer {
	private static String mapMethod(String owner, MethodNode node, boolean obf) {
		return mapMethod(owner, node.name, node.desc, obf);
	}
	
	private static String mapMethod(String owner, String name, String desc, boolean obf) {
		return obf ? FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc) : name;
	}
	
	/*private static String mapClass(String name, boolean obf) {
		return obf ? FMLDeobfuscatingRemapper.INSTANCE.map(name) : name;
	}*/
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		boolean obf = !name.equals(transformedName);
		
		if (transformedName.equals("net.minecraft.entity.EntityLiving")) {
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(bytes);
			classReader.accept(classNode, 0);
			
			classNode.methods.stream()
			.filter(methodNode -> mapMethod(name, methodNode, obf).equals("func_184640_d"))
			.forEach(methodNode -> {
				LoadingPlugin.coreLog.info("Patching func_184640_d (getSlotForItemStack)");
				
				InsnList insns = new InsnList();
				insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
				insns.add(new InsnNode(Opcodes.DUP));
				LabelNode label = new LabelNode();
				insns.add(new JumpInsnNode(Opcodes.IFNULL, label));
				insns.add(new InsnNode(Opcodes.DUP));
				insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "func_77973_b", "()Lnet/minecraft/item/Item;", false));
				insns.add(new TypeInsnNode(Opcodes.INSTANCEOF, "landmaster/landcraft/item/ItemLandmastersWings"));
				insns.add(new JumpInsnNode(Opcodes.IFEQ, label));
				insns.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/inventory/EntityEquipmentSlot", "CHEST", "Lnet/minecraft/inventory/EntityEquipmentSlot;"));
				insns.add(new InsnNode(Opcodes.ARETURN));
				insns.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[] {"net/minecraft/item/ItemStack"}));
				insns.add(label);
				insns.add(new InsnNode(Opcodes.POP));
				
				methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), insns);
			});
			
			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			classNode.accept(classWriter);
			return classWriter.toByteArray();
		}
		
		return bytes;
	}
	
}
