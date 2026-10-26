package com.example.fastfoodorderingapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderViewModel : ViewModel() {

    private val _order = MutableLiveData(Order())
    val order: LiveData<Order> get() = _order

    fun updateMainMeal(meal: String?) {
        _order.value = _order.value?.copy(mainMeal = meal)
    }

    fun updateSideDishes(sides: List<String>) {
        _order.value = _order.value?.copy(sideDishes = sides)
    }

    fun updateDrink(drink: String?) {
        _order.value = _order.value?.copy(drink = drink)
    }

    fun resetOrder() {
        _order.value = Order()
    }
}
