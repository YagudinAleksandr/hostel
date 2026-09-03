package uz.backend.manager.mapper;

import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Маппер сущностей в DTO по совпадению имён.
 * <p>
 * Читает свойства источника (record-компоненты или геттеры) и передаёт их в конструктор
 * целевого DTO, сопоставляя по имени параметра. План маппинга строится один раз на пару
 * типов и кэшируется.
 * <p>
 * Работает только в сторону "сущность -&gt; DTO". Создание и изменение сущностей идёт
 * через доменные методы агрегатов, а не через маппер.
 *
 * @author Aleksandr Yagudin
 */
@Component
public class MapperBase {

    /**
     * Пара типов "источник - цель"
     */
    private record TypePair(Class<?> source, Class<?> target) {
    }

    /**
     * План маппинга: конструктор цели и геттеры источника в порядке параметров конструктора
     */
    private record Plan(Constructor<?> constructor, List<Method> readers) {
    }

    /**
     * Планы маппинга по парам типов
     */
    private final Map<TypePair, Plan> plans = new ConcurrentHashMap<>();

    /**
     * Геттеры по типам источников
     */
    private final Map<Class<?>, Map<String, Method>> readers = new ConcurrentHashMap<>();

    /**
     * Ручные конвертеры для пар типов, которые по именам не сходятся
     */
    private final Map<TypePair, Function<Object, Object>> converters = new ConcurrentHashMap<>();

    /**
     * Конвертация объекта в DTO
     *
     * @param source  источник
     * @param dtoType тип Data Transfer Object
     * @param <D>     тип Data Transfer Object
     * @return Data Transfer Object или {@code null}, если источник пуст
     */
    public <D> D toDto(Object source, Class<D> dtoType) {
        if (source == null) {
            return null;
        }

        TypePair pair = new TypePair(source.getClass(), dtoType);

        Function<Object, Object> converter = converters.get(pair);
        if (converter != null) {
            return dtoType.cast(converter.apply(source));
        }

        Plan plan = plans.computeIfAbsent(pair, this::buildPlan);
        Parameter[] parameters = plan.constructor().getParameters();
        Object[] arguments = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Object value = read(plan.readers().get(i), source);
            arguments[i] = convert(value, parameters[i].getType(), parameters[i].getParameterizedType());
        }

        try {
            return dtoType.cast(plan.constructor().newInstance(arguments));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Не удалось создать " + dtoType.getName(), e);
        }
    }

    /**
     * Конвертация коллекции объектов в список DTO
     *
     * @param sources источники
     * @param dtoType тип Data Transfer Object
     * @param <D>     тип Data Transfer Object
     * @return список Data Transfer Object
     */
    public <D> List<D> toDtoList(Collection<?> sources, Class<D> dtoType) {
        if (sources == null) {
            return List.of();
        }
        List<D> list = new ArrayList<>(sources.size());
        for (Object source : sources) {
            list.add(toDto(source, dtoType));
        }
        return list;
    }

    /**
     * Регистрация ручного конвертера для пары типов. Нужна там, где имена свойств
     * не совпадают, например {@code Address.zip} и {@code AddressDto.zipCode}.
     *
     * @param sourceType тип источника
     * @param targetType тип цели
     * @param converter  функция конвертации
     * @param <S>        тип источника
     * @param <D>        тип цели
     */
    @SuppressWarnings("unchecked")
    public <S, D> void register(Class<S> sourceType, Class<D> targetType, Function<S, D> converter) {
        TypePair pair = new TypePair(sourceType, targetType);
        converters.put(pair, source -> converter.apply((S) source));
        plans.remove(pair);
    }

    /**
     * Построение плана маппинга. Падает сразу, если какому-то параметру конструктора цели
     * не нашлось свойства источника.
     *
     * @param pair пара типов
     * @return план маппинга
     */
    private Plan buildPlan(TypePair pair) {
        Map<String, Method> sourceReaders = readers(pair.source());
        Constructor<?> constructor = constructorOf(pair.target());

        List<Method> planReaders = new ArrayList<>();
        for (Parameter parameter : constructor.getParameters()) {
            Method reader = sourceReaders.get(parameter.getName());
            if (reader == null) {
                throw new IllegalStateException("Нет свойства %s в %s для параметра %s.%s"
                        .formatted(parameter.getName(), pair.source().getSimpleName(),
                                pair.target().getSimpleName(), parameter.getName()));
            }
            planReaders.add(reader);
        }
        return new Plan(constructor, planReaders);
    }

    /**
     * Выбор конструктора цели: у record - канонический, у обычного класса - самый длинный
     *
     * @param type тип цели
     * @return конструктор
     */
    private Constructor<?> constructorOf(Class<?> type) {
        Constructor<?> found = null;
        for (Constructor<?> constructor : type.getConstructors()) {
            if (found == null || constructor.getParameterCount() > found.getParameterCount()) {
                found = constructor;
            }
        }
        if (found == null || found.getParameterCount() == 0) {
            throw new IllegalStateException("У " + type.getName() + " нет публичного конструктора с параметрами");
        }
        if (!found.getParameters()[0].isNamePresent()) {
            throw new IllegalStateException("Для " + type.getName() + " нужен флаг компиляции -parameters");
        }
        return found;
    }

    /**
     * Сбор геттеров источника: record-компоненты либо методы getXxx/isXxx
     *
     * @param type тип источника
     * @return геттеры по именам свойств
     */
    private Map<String, Method> readers(Class<?> type) {
        return readers.computeIfAbsent(type, source -> {
            Map<String, Method> found = new LinkedHashMap<>();
            if (source.isRecord()) {
                for (RecordComponent component : source.getRecordComponents()) {
                    found.put(component.getName(), component.getAccessor());
                }
                return found;
            }
            for (Method method : source.getMethods()) {
                if (method.getParameterCount() != 0 || method.getReturnType() == void.class) {
                    continue;
                }
                if (Modifier.isStatic(method.getModifiers()) || method.getDeclaringClass() == Object.class) {
                    continue;
                }
                String name = method.getName();
                boolean bool = method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class;
                if (name.startsWith("get") && name.length() > 3) {
                    found.put(decapitalize(name.substring(3)), method);
                } else if (bool && name.startsWith("is") && name.length() > 2) {
                    found.put(decapitalize(name.substring(2)), method);
                }
            }
            return found;
        });
    }

    /**
     * Чтение свойства источника
     *
     * @param reader геттер
     * @param source источник
     * @return значение свойства
     */
    private Object read(Method reader, Object source) {
        try {
            return reader.invoke(source);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Не удалось прочитать " + reader, e);
        }
    }

    /**
     * Приведение значения к типу параметра конструктора цели
     *
     * @param value       значение
     * @param targetType  тип параметра
     * @param genericType тип параметра с дженериками, нужен для коллекций
     * @return значение нужного типа
     */
    private Object convert(Object value, Class<?> targetType, Type genericType) {
        if (value == null) {
            return null;
        }

        // коллекции проверяем первыми: List подходит под isInstance,
        // но элементы внутри всё равно надо переложить в DTO
        if (value instanceof Collection<?> items && Collection.class.isAssignableFrom(targetType)) {
            Class<?> itemType = elementType(genericType);
            List<Object> list = new ArrayList<>(items.size());
            for (Object item : items) {
                list.add(itemType == null ? item : convert(item, itemType, itemType));
            }
            return list;
        }

        if (targetType.isInstance(value) || targetType.isPrimitive()) {
            return value;
        }

        Function<Object, Object> converter = converters.get(new TypePair(value.getClass(), targetType));
        if (converter != null) {
            return converter.apply(value);
        }

        return toDto(value, targetType);
    }

    /**
     * Тип элемента коллекции
     *
     * @param genericType тип с дженериками
     * @return тип элемента или {@code null}, если он не определён
     */
    private Class<?> elementType(Type genericType) {
        if (genericType instanceof ParameterizedType parameterized
                && parameterized.getActualTypeArguments().length == 1
                && parameterized.getActualTypeArguments()[0] instanceof Class<?> item) {
            return item;
        }
        return null;
    }

    /**
     * Первая буква имени в нижний регистр
     *
     * @param name имя
     * @return имя свойства
     */
    private static String decapitalize(String name) {
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}
