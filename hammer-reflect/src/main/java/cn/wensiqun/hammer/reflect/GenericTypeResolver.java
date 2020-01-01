package cn.wensiqun.hammer.reflect;

import javax.annotation.Nonnull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

public class GenericTypeResolver {

    private static final List<Class> EMPTY = Collections.emptyList();

    @Nonnull
    private Class targetType;

    public GenericTypeResolver(@Nonnull Class targetType) {
        this.targetType = targetType;
    }

    @Nonnull
    public List<Class> resolve(@Nonnull Class implClass) {
        return resolve0(implClass, implClass.getTypeParameters());
    }

    private List<Class> resolve0(@Nonnull Class implClass, @Nonnull Type[] genericTypes) {
        if (Object.class.equals(implClass)) {
            return EMPTY;
        }
        if (implClass.equals(targetType)) {
            List<Class> results = new ArrayList<>(genericTypes.length);
            for (Type genericType : genericTypes) {
                results.add(getActualType(genericType));
            }
            return results;
        }
        Map<String, Integer> typeParamPosMap = new HashMap<>();
        TypeVariable<Class>[] typeVariables = implClass.getTypeParameters();
        for (int i = 0; i < typeVariables.length; i++) {
            typeParamPosMap.put(typeVariables[i].getName(), i);
        }
        if (targetType.isInterface()) {
            Type[] genericInterfaces = implClass.getGenericInterfaces();
            Class[] interfaces = implClass.getInterfaces();
            for (int i = 0; i < genericInterfaces.length; i++) {
                List<Class> results = resolveParent(interfaces[i], genericInterfaces[i], genericTypes, typeParamPosMap);
                if (results != EMPTY) {
                    return results;
                }
            }
        }
        Class superclass = implClass.getSuperclass();
        if (superclass == null) {
            return EMPTY;
        }
        Type genericSuperClass = implClass.getGenericSuperclass();
        return resolveParent(superclass, genericSuperClass, genericTypes, typeParamPosMap);
    }

    private List<Class> resolveParent(@Nonnull Class clazz,
                                      @Nonnull Type genericType,
                                      @Nonnull final Type[] genericTypes,
                                      @Nonnull final Map<String, Integer> typeParameterPosMap) {
        if (genericType instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
            Type[] newGenericTypes = new Type[actualTypeArguments.length];
            for (int i = 0; i < actualTypeArguments.length; i++) {
                Type actualTypeArg = actualTypeArguments[i];
                if (actualTypeArg instanceof TypeVariable) {
                    Integer pos = typeParameterPosMap.get(((TypeVariable) actualTypeArg).getName());
                    newGenericTypes[i] = genericTypes[pos];
                } else {
                    newGenericTypes[i] = actualTypeArg;
                }
            }
            return resolve0(clazz, newGenericTypes);
        } else {
            return resolve0(clazz, clazz.getTypeParameters());
        }
    }

    private Class getActualType(Type type) {
        if (type instanceof ParameterizedType) {
            return getActualType(((ParameterizedType) type).getActualTypeArguments()[0]);
        } else if (type instanceof TypeVariable) {
            Type[] bounds = ((TypeVariable) type).getBounds();
            if (bounds.length > 0) {
                return getActualType(bounds[0]);
            } else {
                return Object.class;
            }
        } else if (type instanceof Class) {
            return (Class) type;
        } else {
            return Object.class;
        }
    }

}