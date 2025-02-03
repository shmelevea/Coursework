package org.example.ports

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.example.Warehouse
import org.example.items.*
import org.example.trucks.Truck
import kotlin.random.Random

class UnloadingPort(private val warehouse: Warehouse, private val portColor: String) {
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

    suspend fun processGoods(portName: String): List<Item> {
        val deliveredItems = mutableListOf<Item>()
        mutex.withLock {
            trucks.forEach { truck ->
                val category = Random.nextBoolean()
                var remainingCapacity = truck.getCapacity()
                println(getColorCodedOutput("###Входящий ${truck.getName()} Грузоподъемностью ${truck.getCapacity()}кг в $portName. Разгружаем...###"))
                var totalWeightLoaded = 0
                while (remainingCapacity > 0) {
                    val item = getRandomItem(category)
                    if (totalWeightLoaded + item.weight <= remainingCapacity) {
                        totalWeightLoaded += item.weight
                        remainingCapacity -= item.weight
                        println(getColorCodedOutput("Выгружаем ${item.name} весом ${item.weight}кг займет ${item.loadingTime}м"))
                        delay(item.loadingTime.toLong() * 1000)
                        deliveredItems.add(item)
                        warehouse.deliverItems(listOf(item))
                    } else {
                        break
                    }
                }
                printColorCodedMessage("Разгружено: $totalWeightLoaded кг в $portName")
            }
            trucks.clear()
        }
        return deliveredItems
    }

    private fun getRandomItem(foodCategory: Boolean): Item {
        val randomCategory = if (foodCategory) {
            FoodItemsCategory().items
        } else {
            listOf(
                SmallItemsCategory().items,
                MediumItemsCategory().items,
                BigItemsCategory().items
            ).random()
        }
        return randomCategory.random()
    }

    suspend fun deliverItemsToWarehouse(deliveredItems: List<Item>) {
        warehouse.deliverItems(deliveredItems)
    }

    private fun printColorCodedMessage(message: String) {
        val decoratedMessage =
            "#############################################################" +
                    "\n$message\n#############################################################"
        println(getColorCodedOutput(decoratedMessage))
    }
}