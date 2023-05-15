package org.example.sharing.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Helper-класс, содержащий вспомогательные методы для использования в связке с SpringFramework BeanUtils
 *
 * @see BeanUtils
 */
@UtilityClass
public class BeanUtilsHelper {

    /**
     * Метод возвращает массив из имен игнорируемых полей объекта.
     * В этот список попадают все null-поля, а так же дополнительные поля, которые были указаны.
     * (Возможны проблемы с глубоким копированием и просмотром полей при использовании {@link BeanUtils}
     *
     * @param source        - объект источник, имена полей которого необходимо получить
     * @param ignoredFields - дополнительный список игнорируемых полей
     * @return String[] - массив имен неинициализированных полей объекта
     */
    public static String[] getIgnoredPropertyNames(@NotNull Object source, String... ignoredFields) {
        var wrappedSource = new BeanWrapperImpl(source);
        var foundFields = Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .collect(toList());
        foundFields.addAll(Arrays.asList(ignoredFields));
        return foundFields.toArray(new String[0]);
    }
}
