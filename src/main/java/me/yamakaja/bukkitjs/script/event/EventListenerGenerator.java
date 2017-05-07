package me.yamakaja.bukkitjs.script.event;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.objectweb.asm.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by Yamakaja on 06.05.17.
 */
public class EventListenerGenerator {

    private static int serial = 0;

    private static ConcurrentHashMap<String, Class<?>> classes = new ConcurrentHashMap<>();

    private ASMClassLoader classLoader = new ASMClassLoader();

    public Listener makeListener(Consumer<Event> function, String event) {

        String className = "me/yamakaja/bukkitjs/script/event/EventHandler" + serial++;
        String eventClassName = event.startsWith("#") ? "org/bukkit/event/" + event.substring(1) : event;

        Class<?> clazz;

        if (classes.containsKey(eventClassName))
            clazz = classes.get(eventClassName);
        else {
            clazz = classLoader.createClass(className, generate(eventClassName, className));
            classes.put(eventClassName, clazz);
        }

        try {
            return (Listener) clazz.getConstructors()[0].newInstance(function);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] generate(String eventClass, String handlerClass) {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, handlerClass, null, "java/lang/Object", new String[]{"org/bukkit/event/Listener"});

        {
            fv = cw.visitField(ACC_PRIVATE, "function", "Ljava/util/function/Consumer;", "Ljava/util/function/Consumer<Lorg/bukkit/event/Event;>;", null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/util/function/Consumer;)V", "(Ljava/util/function/Consumer<Lorg/bukkit/event/Event;>;)V", null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, handlerClass, "function", "Ljava/util/function/Consumer;");
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitInsn(RETURN);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLocalVariable("this", "L" + handlerClass + ";", null, l0, l3, 0);
            mv.visitLocalVariable("function", "Ljava/util/function/Consumer;", "Ljava/util/function/Consumer<Lorg/bukkit/event/Event;>;", l0, l3, 1);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "onEvent", "(L" + eventClass + ";)V", null, null);
            {
                av0 = mv.visitAnnotation("Lorg/bukkit/event/EventHandler;", true);
                av0.visitEnd();
            }
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, handlerClass, "function", "Ljava/util/function/Consumer;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Consumer", "accept", "(Ljava/lang/Object;)V", true);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(RETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", "L" + handlerClass + ";", null, l0, l2, 0);
            mv.visitLocalVariable("e", "Lorg/bukkit/event/player/PlayerJoinEvent;", null, l0, l2, 1);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    private static class ASMClassLoader extends ClassLoader {

        public Class<?> createClass(String name, byte[] clazz) {
            return defineClass(name.replace('/', '.'), clazz, 0, clazz.length);
        }

    }

}
