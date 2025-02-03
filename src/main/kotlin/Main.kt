package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.example.ports.LoadingPort
import org.example.ports.UnloadingPort
import org.example.trucks.geneartion.*

suspend fun main() {

    val warehouse = Warehouse()

    val unloadingPort1 = UnloadingPort(warehouse, "\u001B[33m")
    val unloadingPort2 = UnloadingPort(warehouse, "\u001B[31m")
    val loadingPort1 = LoadingPort(warehouse, "\u001B[34;3m")
    val loadingPort2 = LoadingPort(warehouse, "\u001B[32;3m")

    val unloadTruckGenerator1 = UnloadTruckGenerator(2)
    val unloadTruckGenerator2 = UnloadTruckGenerator(3)
    val loadTruckGenerator1 = LoadTruckGenerator(warehouse)
    val loadTruckGenerator2 = LoadTruckGenerator(warehouse)

    val unloadTruck1 = UnloadTruck(unloadingPort1, unloadTruckGenerator1, "Желтый порт")
    val unloadTruck2 = UnloadTruck(unloadingPort2, unloadTruckGenerator2, "Красный порт")
    val loadTruck1 = LoadTruck(loadingPort1, loadTruckGenerator1, "Синий порт")
    val loadTruck2 = LoadTruck(loadingPort2, loadTruckGenerator2, "Зеленый порт")

    coroutineScope {
        launch {
            unloadTruck1.start()
        }
        launch {
            unloadTruck2.start()
        }
        launch {
            loadTruck1.start()
        }
        launch {
            loadTruck2.start()
        }
    }
}