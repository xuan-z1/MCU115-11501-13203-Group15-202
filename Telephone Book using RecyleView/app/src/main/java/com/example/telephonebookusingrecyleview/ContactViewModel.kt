package com.example.telephonebookusingrecyleview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactViewModel : ViewModel() {

    private val _allContacts = MutableLiveData<MutableList<Contact>>()

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _filteredContacts = MutableLiveData<List<Contact>>()
    val filteredContacts: LiveData<List<Contact>> = _filteredContacts

    init {
        val initialList = mutableListOf(
            Contact(name = "Johnny", phone = "0988312345", email = "johnnyhong@gmail.com"),
            Contact(name = "Jessica", phone = "0981911934", email = "jessicahuang@gmail.com"),
            Contact(name = "Alex Chen", phone = "0912345678", email = "alex.chen@gmail.com"),
            Contact(name = "Bob Lin", phone = "0923456789", email = "bob.lin@yahoo.com"),
            Contact(name = "Charlie Wang", phone = "0934567890", email = "charlie.wang@outlook.com"),
            Contact(name = "David Lee", phone = "0945678901", email = "david.lee@gmail.com"),
            Contact(name = "Emma Chang", phone = "0956789012", email = "emma.chang@gmail.com"),
            Contact(name = "Frank Wu", phone = "0967890123", email = "frank.wu@hotmail.com"),
            Contact(name = "Grace Liu", phone = "0978901234", email = "grace.liu@gmail.com"),
            Contact(name = "Henry Yang", phone = "0989012345", email = "henry.yang@gmail.com"),
            Contact(name = "Ivy Huang", phone = "0990123456", email = "ivy.huang@gmail.com"),
            Contact(name = "Jack Tsai", phone = "0911223344", email = "jack.tsai@gmail.com"),
            Contact(name = "Kevin Kao", phone = "0922334455", email = "kevin.kao@gmail.com"),
            Contact(name = "Lily Chou", phone = "0933445566", email = "lily.chou@gmail.com"),
            Contact(name = "Michael Ho", phone = "0944556677", email = "michael.ho@gmail.com"),
            Contact(name = "Nina Hsieh", phone = "0955667788", email = "nina.hsieh@gmail.com"),
            Contact(name = "Oliver Hsu", phone = "0966778899", email = "oliver.hsu@gmail.com"),
            Contact(name = "Peggy Sung", phone = "0977889900", email = "peggy.sung@gmail.com"),
            Contact(name = "Queen Fan", phone = "0988990011", email = "queen.fan@gmail.com"),
            Contact(name = "Ryan Yeh", phone = "0912112233", email = "ryan.yeh@gmail.com"),
            Contact(name = "Sophia Lu", phone = "0923223344", email = "sophia.lu@gmail.com"),
            Contact(name = "Tom Cheng", phone = "0934334455", email = "tom.cheng@gmail.com"),
            Contact(name = "Una Chiang", phone = "0945445566", email = "una.chiang@gmail.com"),
            Contact(name = "Victor Ku", phone = "0956556677", email = "victor.ku@gmail.com"),
            Contact(name = "Wendy Peng", phone = "0967667788", email = "wendy.peng@gmail.com"),
            Contact(name = "Xavier Teng", phone = "0978778899", email = "xavier.teng@gmail.com"),
            Contact(name = "Yvonne Lai", phone = "0989889900", email = "yvonne.lai@gmail.com"),
            Contact(name = "Zack Lo", phone = "0919001122", email = "zack.lo@gmail.com"),
            Contact(name = "Amber Yu", phone = "0920112233", email = "amber.yu@gmail.com"),
            Contact(name = "Brian Pan", phone = "0931223344", email = "brian.pan@gmail.com"),
            Contact(name = "Cynthia Tang", phone = "0942334455", email = "cynthia.tang@gmail.com"),
            Contact(name = "Daniel Su", phone = "0953445566", email = "daniel.su@gmail.com"),
        )
        _allContacts.value = initialList
        applyFilter()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    fun addContact(contact: Contact) {
        val currentList = _allContacts.value ?: mutableListOf()
        currentList.add(0, contact)
        _allContacts.value = currentList
        applyFilter()
    }

    fun deleteContact(contact: Contact) {
        val currentList = _allContacts.value ?: return
        currentList.removeAll { it.id == contact.id }
        _allContacts.value = currentList
        applyFilter()
    }

    private fun applyFilter() {
        val query = _searchQuery.value?.trim().orEmpty()
        val currentList = _allContacts.value.orEmpty()
        if (query.isEmpty()) {
            _filteredContacts.value = currentList.toList()
        } else {
            _filteredContacts.value = currentList.filter { contact ->
                contact.name.contains(query, ignoreCase = true) ||
                        contact.phone.contains(query, ignoreCase = true) ||
                        contact.email.contains(query, ignoreCase = true)
            }
        }
    }
}
