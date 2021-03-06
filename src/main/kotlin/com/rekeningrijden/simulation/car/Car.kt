package com.rekeningrijden.simulation.car

import java.util.UUID

interface Car {
    /**
     * The tracker ID which every motorized vehicle in Europe should have. Each
     * vehicle may only have one tracker and old trackers may not be
     * recommissioned.
     */
    val id: UUID

    /**
     * The
     * [ISO 3166-1 alpha-2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2)
     * country code which indicates which country the car originates from.
     */
    val country: String

    /**
     * The speed at which a car travels in _kilometers per hour_.
     */
    val speed: Double
}
