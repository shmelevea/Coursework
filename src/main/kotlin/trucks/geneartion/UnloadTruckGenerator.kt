package org.example.trucks.geneartion

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlin.random.Random
import org.example.trucks.*

class UnloadTruckGenerator(private val numberOfTrucksToGenerate: Int) {
    private val channel = Channel<Truck>()
    private var trucksGenerated = 0

    suspend fun generateTruck() {
        while (true) {
            if (trucksGenerated >= numberOfTrucksToGenerate) break
            val truck = when (Random.nextInt(3)) {
                0 -> SmallTruck()
                1 -> MediumTruck()
                else -> LargeTruck()
            }
            channel.send(truck)
            trucksGenerated++
            delay(30000)
        }
        channel.close()
        trucksGenerated = 0
    }

    fun receiveTruck(): ReceiveChannel<Truck> = channel
}