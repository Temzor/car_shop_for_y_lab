package ru.yaone.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация {@code Loggable} используется для обозначения методов,
 * классов, параметров или конструкторов, для которых требуется
 * включение логирования.
 *
 * <p>При добавлении этой аннотации к элементу программного кода,
 * соответствующая логика логирования применяется в процессе
 * выполнения приложения.</p>
 *
 * <p>Пример использования:</p>
 * <pre>
 * {@code
 * @Loggable("Запись в лог")
 * public void myMethod() {
 *     // реализация метода
 * }
 * }
 * </pre>
 *
 * <p>Аннотация присутствует в скомпилированном коде во время выполнения,
 * что позволяет использовать её в аспектах или других механизмах
 * обработки.</p>
 *
 * @author Ваше имя
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.PACKAGE})
public @interface Loggable {
    /**
     * Необходимое значение, описывающее, что именно должно
     * быть записано в лог. По умолчанию является пустой строкой.
     *
     * @return строка с описанием логируемой операции
     */
    String value() default "";
}