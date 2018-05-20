package com.rekeningrijden.simulation.entities

import com.rekeningrijden.europe.dtos.TransLocationDto
import com.rekeningrijden.simulation.car.CarImpl
import com.rekeningrijden.simulation.simulation.CarSimulator
import com.rekeningrijden.simulation.simulation.MessageProducer
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.concurrent.TimeUnit

data class Journey(
    private val carSimulator: CarSimulator,
    private val messageProducer: MessageProducer,
    private val carImpl: CarImpl,
    private var route: Route
) : Thread() {
    private val dateTimeNowIso8601UTC: String
        get() {
            return Instant.now().toString()
        }

    override fun run() {
        while (!route.isRouteDriven) {
            val sr = findSubRouteThatIsNotDrivenYet()

            var indexesTravelled = 0
            while (!sr.isSubRouteDriven) {
                val coor = sr.getNextCoordinateAtIndex(indexesTravelled)
                indexesTravelled++

                if (coor == null) break

                val dto = TransLocationDto(
                    coor.latitude.toString(),
                    coor.longitude.toString(),
                    dateTimeNowIso8601UTC,
                    carImpl.id.toString(),
                    carImpl.country
                )
                messageProducer.sendTransLocation(sr.countryCode, dto)
                logger.debug("Lat: " + coor.latitude + " - Lon: " + coor.longitude)

                Thread.sleep(1000)
            }

            if (route.isRouteDriven) {
                logger.debug("Thread sleeping for 15 minutes")
                TimeUnit.MINUTES.sleep(15)
                this.route = carSimulator.newRoute
            }
        }
    }

    private fun findSubRouteThatIsNotDrivenYet(): SubRoute {
        val srs = route.subRoutes
        for (sr in srs) {
            if (!sr.isSubRouteDriven) {
                return sr
            }
        }
        route.isRouteDriven = true
        return srs[srs.size - 1]
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Journey::class.java)
    }
}
