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

    static Field getFieldByAnnotation(Class<?> clz, Class<? extends Annotation> targetAnnotation) {
        List<Field> fields = getFieldsByAnnotation(clz, targetAnnotation);
        if (fields.size() > 1)
            throw new MultiFieldException("there are multiple fields associated by annotation["+targetAnnotation+"].");

        return fields.get(0);
    }

    static List<Field> getFieldsByAnnotation(Class<?> clz, Class<? extends Annotation> targetAnnotation) {
        Field[] allFields = clz.getDeclaredFields();

        return Arrays.stream(allFields)
                .filter(field -> field.getAnnotation(targetAnnotation) != null)
                .collect(Collectors.toList());
    }

    static boolean hasTypeAnnotation(Class<?> clz, Class<? extends Annotation> targetAnnotation) {
        return getTypeAnnotation(clz, targetAnnotation) != null;
    }



    static <T extends Annotation> T getTypeAnnotation(Class<?> clz, Class<T> targetAnnotation) {
        return clz.getDeclaredAnnotation(targetAnnotation);
    }
}
