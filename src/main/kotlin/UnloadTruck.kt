package org.example

import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.example.ports.UnloadingPort
import org.example.trucks.geneartion.UnloadTruckGenerator

class UnloadTruck(
    private val unloadingPort: UnloadingPort,
    private val unloadTruckGenerator: UnloadTruckGenerator,
    private val portName: String
) {

    suspend fun start() {
        coroutineScope {
            val job = coroutineScope {
                launch {
                    unloadTruckGenerator.generateTruck()
                }
                launch {
                    val channel = unloadTruckGenerator.receiveTruck()
                    channel.consumeEach { truck ->
                        unloadingPort.addTruck(truck)
                        launch {
                            val deliveredItems =
                                unloadingPort.processGoods(portName)
                            unloadingPort.deliverItemsToWarehouse(deliveredItems)
                        }
                    }
                }
            }
            job.join()
        }
    }
}