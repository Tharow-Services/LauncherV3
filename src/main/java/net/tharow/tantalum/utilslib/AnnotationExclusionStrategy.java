package net.tharow.tantalum.utilslib;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class AnnotationExclusionStrategy implements ExclusionStrategy {
    private final Class<?> typeToSkip;

    AnnotationExclusionStrategy(Class<?> typeToSkip){
        this.typeToSkip = typeToSkip;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return fieldAttributes.getAnnotation(DoNotSerialize.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return (aClass == typeToSkip);
    }
}
