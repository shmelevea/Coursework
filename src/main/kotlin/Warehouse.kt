package org.example

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.example.items.*
import java.util.concurrent.TimeUnit

class Warehouse {
    private val mutex = Mutex()
    private val items = mutableListOf<Item>()
    private var lastDeliveryTime: Long = 0

    suspend fun deliverItems(deliveredItems: List<Item>) {
        mutex.withLock {
            items.addAll(deliveredItems)
            lastDeliveryTime = System.currentTimeMillis()
        }
    }

    suspend fun removeItem(item: Item) {
        mutex.withLock {
            items.remove(item)
        }
    }

    suspend fun getAvailableItems(category: ItemType): Item? {
        return mutex.withLock {
            val availableItem = items.firstOrNull { it.type == category }
            availableItem?.let {
                items.remove(it)
            }
            availableItem
        }
    }

    fun checkItems(): Boolean {
        return items.isNotEmpty()
    }

    fun unloadPlus(): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeDiffSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTime - lastDeliveryTime)
        return timeDiffSeconds <= 30
    }
}