package ru.ifmo;

import ru.ifmo.fnds.LinearMemory;
import ru.ifmo.fnds.OriginalVersion;

/**
 *
 */
public class FastNonDominatedSortingDeb {
    private FastNonDominatedSortingDeb() {}

    private static final NonDominatedSortingFactory ORIGINAL_FACTORY = OriginalVersion::new;
    private static final NonDominatedSortingFactory LINEAR_MEMORY_FACTORY = LinearMemory::new;

    public static NonDominatedSortingFactory getOriginalImplementation() {
        return ORIGINAL_FACTORY;
    }

    public static NonDominatedSortingFactory getLinearMemoryImplementation() {
        return LINEAR_MEMORY_FACTORY;
    }
}
