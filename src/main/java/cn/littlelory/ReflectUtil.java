package cn.littlelory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by littlelory on 2017/8/23.
 */
class ReflectUtil {

    private static final ReflectUtil INSTANCE = new ReflectUtil();

    static ReflectUtil getINSTANCE() {
        return INSTANCE;
    }

    private ReflectUtil() {
    }

    List<Field> getFieldsByAnnotation(Class<?> clz, Class<? extends Annotation> targetAnnotation) {
        Field[] allFields = clz.getDeclaredFields();

        return Arrays.stream(allFields)
                .filter(field -> field.getAnnotation(targetAnnotation) != null)
                .collect(Collectors.toList());
    }

    boolean hasAnnotation(Class<?> clz, Class<? extends Annotation> targetAnnotation) {
        return getFieldsByAnnotation(clz, targetAnnotation) != null;
    }

    <T extends Annotation> T getTypeAnnotation(Class<?> clz, Class<T> targetAnnotation) {
        return clz.getDeclaredAnnotation(targetAnnotation);
    }
}
