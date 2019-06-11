/*
 * Copyright 2019 Simon Greening Christian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package littlecommons.util.enums;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Implementations of {@link java.util.stream.Collector} that accumulate enums
 * into unmodifiable Sets and Maps backed by EnumSets and EnumMaps.
 */
public final class EnumCollectors {

    /**
     * Collects enums into an unmodifiable Set.
     *
     * @param <E>       the type of enums to be collected
     * @param enumClass the class of the enums to be collected
     *
     * @return an unmodifiable Set backed by an EnumSet
     */
    public static <E extends Enum<E>> Collector<E, EnumSet<E>, Set<E>> toSet(
            Class<E> enumClass) {
        return new EnumSetCollector<>(enumClass);
    }

    /**
     * Collects enums as keys into an unmodifiable Map with values derived from
     * a supplied function.
     *
     * @param <E>        the type of enums to be collected
     * @param <V>        the type of the value objects
     * @param enumClass  the class of the enums to be collected
     * @param valueMaker a Function that makes the map values
     *
     * @return an unmodifiable Map backed by an EnumMap
     */
    public static <E extends Enum<E>, V> Collector<E, EnumMap<E, V>, Map<E, V>> toMap(
            Class<E> enumClass, Function<E, V> valueMaker) {
        return new EnumMapCollector<>(enumClass, valueMaker);
    }

    private static <E> Class<E> throwIfNotEnumClass(Class<E> enumClass) {
        if (enumClass == null) {
            throw new NullPointerException("enumClass");
        }
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException(
                    "enumClass is not an Enum subclass: "
                    + enumClass.getCanonicalName()
            );
        }
        return enumClass;
    }

    /**
     *
     * @author Simon Greening Christian
     * @param <E> the type of the enum to be supported
     */
    private static class EnumSetCollector<E extends Enum<E>>
            implements Collector<E, EnumSet<E>, Set<E>> {

        private final Class<E> iEnumClass;

        EnumSetCollector(Class<E> enumClass) {
            iEnumClass = throwIfNotEnumClass(enumClass);
        }

        @Override
        public BiConsumer<EnumSet<E>, E> accumulator() {
            return (s, e) -> s.add(e);
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }

        @Override
        public BinaryOperator<EnumSet<E>> combiner() {
            return (s, t) -> {
                s.addAll(t);
                return s;
            };
        }

        @Override
        public Function<EnumSet<E>, Set<E>> finisher() {
            return s -> s.isEmpty()
                    ? Collections.emptySet()
                    : Collections.unmodifiableSet(s);
        }

        @Override
        public Supplier<EnumSet<E>> supplier() {
            return () -> EnumSet.noneOf(iEnumClass);
        }

    }

    /**
     *
     * @author Simon Greening Christian
     * @param <E> the type of the enum to be supported
     */
    private static class EnumMapCollector<E extends Enum<E>, T>
            implements Collector<E, EnumMap<E, T>, Map<E, T>> {

        private final Class<E> iEnumClass;
        private final Function<E, T> iValueMaker;

        EnumMapCollector(Class<E> enumClass, Function<E, T> valueMaker) {
            iEnumClass = throwIfNotEnumClass(enumClass);
            if (valueMaker == null) {
                throw new NullPointerException("valueMaker");
            }
            iValueMaker = valueMaker;
        }

        @Override
        public BiConsumer<EnumMap<E, T>, E> accumulator() {
            return (m, e) -> m.put(e, iValueMaker.apply(e));
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }

        @Override
        public BinaryOperator<EnumMap<E, T>> combiner() {
            return (m, n) -> {
                m.putAll(n);
                return m;
            };
        }

        @Override
        public Function<EnumMap<E, T>, Map<E, T>> finisher() {
            return m -> m.isEmpty()
                    ? Collections.emptyMap()
                    : Collections.unmodifiableMap(m);
        }

        @Override
        public Supplier<EnumMap<E, T>> supplier() {
            return () -> new EnumMap<>(iEnumClass);
        }

    }

}
