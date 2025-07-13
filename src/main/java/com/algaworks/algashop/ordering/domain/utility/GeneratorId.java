package com.algaworks.algashop.ordering.domain.utility;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;

import java.util.UUID;

public class GeneratorId {

    private static final TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator =
            Generators.timeBasedEpochRandomGenerator();

    private GeneratorId() {}

    public static UUID generateTimeBasedUUID() {
        return timeBasedEpochRandomGenerator.generate();
    }

}
