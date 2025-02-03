package org.example.trucks.geneartion

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import org.example.Warehouse
import org.example.trucks.*
import kotlin.random.Random

class LoadTruckGenerator(private val warehouse: Warehouse) {
    private val channel = Channel<Truck>()

    suspend fun generateTruck() {
        delay(5000)
        while (true) {
            if (!warehouse.checkItems()) {
                break
            }
            val truck = when (Random.nextInt(2)) {
                0 -> SmallTruck()
                else -> MediumTruck()
            }
            channel.send(truck)
            delay(5000)
        }
        channel.close()
    }
    fun receiveTruck(): ReceiveChannel<Truck> = channel
}