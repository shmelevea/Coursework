package org.example.ports

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.example.Warehouse
import org.example.items.*
import org.example.trucks.*
import kotlin.random.Random

class LoadingPort(private val warehouse: Warehouse, private val portColor: String) {
    private val mutex = Mutex()
    private val trucks = mutableListOf<Truck>()

    suspend fun addTruck(truck: Truck) {
        mutex.withLock {
            trucks.add(truck)
        }
    }

    private fun getColorCodedOutput(message: String): String {
        return "$portColor$message${"\u001B[0m"}"
    }

    suspend fun processGoods(portName: String) {
        mutex.withLock {
            trucks.forEach { truck ->
                val remainingCapacity =
                    Random.nextInt((truck.getCapacity() * 0.9).toInt(), (truck.getCapacity() * 1.0).toInt())
                println(getColorCodedOutput("***Исходящий ${truck.getName()} Грузоподъемностью ${truck.getCapacity()}кг в $portName. Начинаем загрузку...***"))
                var totalWeightLoaded = 0
                val category = getRandomCategory()
                val startTime = System.currentTimeMillis()
                while (totalWeightLoaded < remainingCapacity) {
                    val availableItem = warehouse.getAvailableItems(category)
                    if (availableItem != null) {
                        if (totalWeightLoaded + availableItem.weight <= remainingCapacity) {
                            totalWeightLoaded += availableItem.weight
                            println(getColorCodedOutput("Загружаем ${availableItem.name} весом ${availableItem.weight}кг займет ${availableItem.loadingTime}м"))
                            warehouse.removeItem(availableItem)
                            delay(availableItem.loadingTime.toLong() * 1000)
                        } else {
                            break
                        }
                    } else {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - startTime >= 5000 && !warehouse.unloadPlus()) {
                            println(getColorCodedOutput("Превышено время ожидания товара..."))
                            break
                        }
                    }
                }
                if (totalWeightLoaded == 0) println(getColorCodedOutput("Товары требуемой категории исчерпаны $category"))
                else printColorCodedMessage("Загружено: $totalWeightLoaded кг в ${truck.getName()}, покидает склад...")

            }
            trucks.clear()
        }
    }

    private fun printColorCodedMessage(message: String) {
        val decoratedMessage =
            "*************************************************************" +
                    "\n$message\n*************************************************************"
        println(getColorCodedOutput(decoratedMessage))
    }

    private fun getRandomCategory(): ItemType {
        return listOf(
            ItemType.SMALL,
            ItemType.MEDIUM,
            ItemType.LARGE,
            ItemType.FOOD
        ).random()
    }
}