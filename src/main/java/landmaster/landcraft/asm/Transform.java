package landmaster.landcraft.asm;

import java.util.*;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import net.minecraft.launchwrapper.*;
import net.minecraftforge.fml.common.*;

public class Transform implements IClassTransformer {
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (transformedName.startsWith("landmaster.landcraft")) { // Only LandCraft classes
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(bytes);
			classReader.accept(classNode, 0);
			if (classNode.visibleAnnotations != null) {
				classNode.visibleAnnotations.stream()
					.filter(node -> node.desc.equals("Llandmaster/landcraft/util/OptionalM;"))
					.forEach(node -> {
						@SuppressWarnings("unchecked")
						List<String> modids = (List<String>) node.values.get(1);
						@SuppressWarnings("unchecked")
						List<String> stripped = (List<String>) node.values.get(3);
						if (!modids.stream().anyMatch(Loader::isModLoaded)) {
							System.out.println(String.format(
									"Stripping interfaces %s of class %s since none of the mods %s are loaded",
									Arrays.toString(stripped.toArray()), transformedName, Arrays.toString(modids.toArray())));
							classNode.interfaces.removeIf(stripped::contains);
						}
					});
			}
			
			if (classNode.methods != null) {
				Iterator<MethodNode> methodIt = classNode.methods.iterator();
				while (methodIt.hasNext()) {
					MethodNode methodNode = methodIt.next();
					boolean doStrip = methodNode.visibleAnnotations != null && methodNode.visibleAnnotations.stream()
						.filter(node -> node.desc.equals("Llandmaster/landcraft/util/OptionalM;"))
						.anyMatch(node -> {
							@SuppressWarnings("unchecked")
							List<String> modids = (List<String>) node.values.get(1);
							return !modids.stream().anyMatch(Loader::isModLoaded);
						});
					if (doStrip) {
						System.out.println(String.format(
								"Stripping method %s of class %s",
								methodNode.name, transformedName));
						methodIt.remove();
					}
				}
			}
			
			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			classNode.accept(classWriter);
			return classWriter.toByteArray();
		}
		
		return bytes;
	}
	
}
