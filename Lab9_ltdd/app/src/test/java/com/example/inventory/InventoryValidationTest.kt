package com.example.inventory

import com.example.inventory.data.Item
import com.example.inventory.ui.item.ItemDetails
import com.example.inventory.ui.item.ItemDetailsUiState
import com.example.inventory.ui.item.ItemUiState
import com.example.inventory.ui.item.toItem
import com.example.inventory.ui.item.toItemDetails
import com.example.inventory.ui.item.toItemUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InventoryValidationTest {

    private fun validateInput(uiState: ItemDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && price.isNotBlank() && quantity.isNotBlank()
        }
    }

    @Test
    fun itemValidation_validInput_returnsTrue() {
        val validDetails = ItemDetails(name = "Keyboard", price = "50.0", quantity = "10")
        assertTrue(validateInput(validDetails))
    }

    @Test
    fun itemValidation_blankName_returnsFalse() {
        val invalidDetails = ItemDetails(name = "", price = "50.0", quantity = "10")
        assertFalse(validateInput(invalidDetails))
    }

    @Test
    fun itemValidation_blankPrice_returnsFalse() {
        val invalidDetails = ItemDetails(name = "Keyboard", price = "", quantity = "10")
        assertFalse(validateInput(invalidDetails))
    }

    @Test
    fun itemValidation_blankQuantity_returnsFalse() {
        val invalidDetails = ItemDetails(name = "Keyboard", price = "50.0", quantity = "")
        assertFalse(validateInput(invalidDetails))
    }

    @Test
    fun itemDetails_toItem_convertsCorrectly() {
        val details = ItemDetails(id = 5, name = "Mouse", price = "25.5", quantity = "15")
        val item = details.toItem()
        assertEquals(5, item.id)
        assertEquals("Mouse", item.name)
        assertEquals(25.5, item.price, 0.0)
        assertEquals(15, item.quantity)
    }

    @Test
    fun itemDetails_toItem_invalidPriceAndQuantity_fallbacksToZero() {
        val details = ItemDetails(id = 1, name = "Test", price = "abc", quantity = "xyz")
        val item = details.toItem()
        assertEquals(0.0, item.price, 0.0)
        assertEquals(0, item.quantity)
    }

    @Test
    fun item_toItemDetails_convertsCorrectly() {
        val item = Item(id = 1, name = "Laptop", price = 999.99, quantity = 3)
        val details = item.toItemDetails()
        assertEquals("1", details.id.toString())
        assertEquals("Laptop", details.name)
        assertEquals("999.99", details.price)
        assertEquals("3", details.quantity)
    }

    @Test
    fun sellItem_logic_reducesQuantityWhenGreaterThanZero() {
        val item = Item(id = 1, name = "Headphones", price = 100.0, quantity = 5)
        val updatedItem = if (item.quantity > 0) item.copy(quantity = item.quantity - 1) else item
        assertEquals(4, updatedItem.quantity)
    }

    @Test
    fun sellItem_logic_doesNotReduceWhenZero() {
        val item = Item(id = 1, name = "Headphones", price = 100.0, quantity = 0)
        val updatedItem = if (item.quantity > 0) item.copy(quantity = item.quantity - 1) else item
        assertEquals(0, updatedItem.quantity)
    }

    @Test
    fun itemDetailsUiState_outOfStockFlag() {
        val inStockItem = Item(id = 1, name = "Desk", price = 200.0, quantity = 2)
        val outOfStockItem = Item(id = 2, name = "Chair", price = 50.0, quantity = 0)

        val inStockUiState = ItemDetailsUiState(
            outOfStock = inStockItem.quantity <= 0,
            itemDetails = inStockItem.toItemDetails()
        )
        val outOfStockUiState = ItemDetailsUiState(
            outOfStock = outOfStockItem.quantity <= 0,
            itemDetails = outOfStockItem.toItemDetails()
        )

        assertFalse(inStockUiState.outOfStock)
        assertTrue(outOfStockUiState.outOfStock)
    }
}

