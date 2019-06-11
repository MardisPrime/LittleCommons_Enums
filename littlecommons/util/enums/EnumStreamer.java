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
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Supports repeated streaming of an enum's values without repeated array
 * creation via Enum.values().
 *
 * @author Simon Greening Christian
 * @param <E> the enum class to be streamed
 */
public final class EnumStreamer<E extends Enum<E>> {

    private final Set<E> iAllOf;

    /**
     * Make an EnumStreamer.
     *
     * @param enumClass a class that extends Enum
     *
     * @throws NullPointerException on enumClass
     * @throws IllegalArgumentException if enumClass is not an Enum subclass
     */
    public EnumStreamer(Class<E> enumClass) {
        if (enumClass == null) {
            throw new NullPointerException("enumClass");
        }
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException(
                    "enumClass is not an Enum subclass: "
                    + enumClass.getCanonicalName()
            );
        }
        iAllOf = Collections.unmodifiableSet(EnumSet.allOf(enumClass));

    }

    /**
     * A stream of the constants for the supported enum in the order that they
     * were declared.
     *
     * @return a stream of the constants for the supported enum
     */
    public Stream<E> stream() {
        return iAllOf.stream();
    }

    /**
     * A parallel stream of the constants for the supported enum.
     *
     * @return a parallel stream of the constants for the supported enum
     */
    public Stream<E> parallelStream() {
        return iAllOf.parallelStream();
    }

}
