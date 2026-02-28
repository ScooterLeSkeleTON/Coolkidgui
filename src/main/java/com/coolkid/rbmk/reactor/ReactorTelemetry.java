package com.coolkid.rbmk.reactor;

public record ReactorTelemetry(
        double thermalPower,
        double neutronFlux,
        double coreHeat,
        double steamPressure,
        double positiveVoidCoefficient,
        double fuelBurnup,
        double structuralIntegrity,
        boolean scrammed,
        ReactorOperationalMode mode) {
}
