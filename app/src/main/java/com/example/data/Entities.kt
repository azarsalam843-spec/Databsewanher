package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rfid: String = "",
    val name: String = "",
    val species: String = "Goat", // Goat, Sheep, Cow, Buffalo, etc.
    val breed: String = "",
    val gender: String = "Female", // Male, Female
    val color: String = "",
    val weight: Double = 0.0,
    val dateOfBirth: String = "",
    val status: String = "Healthy", // Healthy, Pregnant, Sick, For Sale, Kids/Babies, Sold
    val purchaseDate: String = "",
    val purchasePrice: Double = 0.0,
    val sellerName: String = "",
    val sellerContact: String = "",
    val transportCost: Double = 0.0,
    val expectedDeliveryDate: String? = null,
    val sireId: String? = null,
    val damId: String? = null,
    val lastDewormingDate: String? = null,
    val growthHistory: String = "", // e.g. "2026-01-01:12.5,2026-02-01:14.2"
    val imageUrl: String? = null,
    val notes: String? = null
)

@Entity(tableName = "health_records")
data class HealthRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val animalId: Int,
    val type: String, // Vaccination, Deworming, Treatment, Vet Visit
    val date: String,
    val details: String,
    val cost: Double = 0.0
)

@Entity(tableName = "financials")
data class FinancialEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // Expense, Revenue
    val category: String, // Feed, Animal Purchase, Labor, Vet Cost, Animal Sale, Honey Sale, Milk Sale, Other
    val amount: Double,
    val date: String,
    val description: String,
    val contactName: String? = null
)

@Entity(tableName = "inventories")
data class InventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemName: String,
    val category: String, // Feed, Medicine, Vaccine, Equipment, Branding, Packaging
    val quantity: Double,
    val unit: String, // kg, vials, units, bottles
    val minStockAlert: Double,
    val supplierName: String = "",
    val supplierContact: String = ""
)

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val role: String, // Admin, Manager, Laborer, Vet
    val attendanceToday: String = "Pending", // Present, Absent, Pending
    val salary: Double = 0.0,
    val performanceNotes: String = ""
)

@Entity(tableName = "honey_batches")
data class HoneyBatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val batchNumber: String,
    val productionDate: String,
    val expiryDate: String,
    val quantity: Int, // Number of bottles
    val size: String, // 250g, 500g, 1kg
    val costPerUnit: Double,
    val salePrice: Double,
    val salesCount: Int = 0,
    val distributorName: String? = null
)
