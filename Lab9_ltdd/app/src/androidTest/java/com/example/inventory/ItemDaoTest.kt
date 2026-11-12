package com.example.inventory

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventory.data.InventoryDatabase
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ItemDaoTest {
    private lateinit var itemDao: ItemDao
    private lateinit var inventoryDatabase: InventoryDatabase

    private val item1 = Item(1, "Apples", 10.0, 20)
    private val item2 = Item(2, "Bananas", 15.0, 97)

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        inventoryDatabase = Room.inMemoryDatabaseBuilder(context, InventoryDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        itemDao = inventoryDatabase.itemDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        inventoryDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsItemIntoDB() = runBlocking {
        itemDao.insert(item1)
        val allItems = itemDao.getAllItems().first()
        assertEquals(item1, allItems[0])
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
        itemDao.insert(item1)
        itemDao.insert(item2)
        val allItems = itemDao.getAllItems().first()
        assertEquals(item1, allItems[0])
        assertEquals(item2, allItems[1])
    }

    @Test
    @Throws(Exception::class)
    fun daoGetItem_returnsItemFromDB() = runBlocking {
        itemDao.insert(item1)
        val item = itemDao.getItem(1).first()
        assertEquals(item1, item)
    }

    @Test
    @Throws(Exception::class)
    fun daoUpdateItems_updatesItemsInDB() = runBlocking {
        itemDao.insert(item1)
        itemDao.update(Item(1, "Apples", 12.0, 25))
        val updatedItem = checkNotNull(itemDao.getItem(1).first())
        assertEquals(12.0, updatedItem.price, 0.0)
        assertEquals(25, updatedItem.quantity)
    }

    @Test
    @Throws(Exception::class)
    fun daoDeleteItems_deletesItemsFromDB() = runBlocking {
        itemDao.insert(item1)
        itemDao.insert(item2)
        itemDao.delete(item1)
        val allItems = itemDao.getAllItems().first()
        assertEquals(1, allItems.size)
        assertEquals(item2, allItems[0])
    }

    @Test
    @Throws(Exception::class)
    fun daoSellItem_reducesQuantityByOne() = runBlocking {
        itemDao.insert(item1)
        val currentItem = checkNotNull(itemDao.getItem(1).first())
        if (currentItem.quantity > 0) {
            itemDao.update(currentItem.copy(quantity = currentItem.quantity - 1))
        }
        val updatedItem = checkNotNull(itemDao.getItem(1).first())
        assertEquals(19, updatedItem.quantity)
    }

    @Test
    @Throws(Exception::class)
    fun daoSellItem_cannotReduceBelowZero() = runBlocking {
        val zeroItem = Item(3, "Oranges", 5.0, 0)
        itemDao.insert(zeroItem)
        val currentItem = checkNotNull(itemDao.getItem(3).first())
        if (currentItem.quantity > 0) {
            itemDao.update(currentItem.copy(quantity = currentItem.quantity - 1))
        }
        val afterSellItem = checkNotNull(itemDao.getItem(3).first())
        assertEquals(0, afterSellItem.quantity)
    }
}
