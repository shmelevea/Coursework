package org.example

import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.example.ports.LoadingPort
import org.example.trucks.geneartion.LoadTruckGenerator

class LoadTruck(
    private val loadingPort: LoadingPort,
    private val truckGenerator: LoadTruckGenerator,
    private val portName: String
) {

    suspend fun start() {
        coroutineScope {
            val job = coroutineScope {
                launch {
                    truckGenerator.generateTruck()
                }
                launch {
                    val channel = truckGenerator.receiveTruck()
                    channel.consumeEach { truck ->
                        loadingPort.addTruck(truck)
                        launch {
                            loadingPort.processGoods(portName)
                        }
                    }
                }
            }
            job.join()
        }
    }
}