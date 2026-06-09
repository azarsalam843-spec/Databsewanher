package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {
    // Animals
    @Query("SELECT * FROM animals ORDER BY id DESC")
    fun getAllAnimals(): Flow<List<AnimalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimal(animal: AnimalEntity): Long

    @Query("DELETE FROM animals WHERE id = :id")
    suspend fun deleteAnimalById(id: Int)

    // Health Records
    @Query("SELECT * FROM health_records WHERE animalId = :animalId ORDER BY date DESC")
    fun getHealthRecordsForAnimal(animalId: Int): Flow<List<HealthRecordEntity>>

    @Query("SELECT * FROM health_records ORDER BY date DESC")
    fun getAllHealthRecords(): Flow<List<HealthRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(record: HealthRecordEntity): Long

    @Query("DELETE FROM health_records WHERE id = :id")
    suspend fun deleteHealthRecord(id: Int)

    // Financials
    @Query("SELECT * FROM financials ORDER BY date DESC")
    fun getAllFinancials(): Flow<List<FinancialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinancial(financial: FinancialEntity): Long

    @Query("DELETE FROM financials WHERE id = :id")
    suspend fun deleteFinancial(id: Int)

    // Inventory
    @Query("SELECT * FROM inventories ORDER BY itemName ASC")
    fun getAllInventory(): Flow<List<InventoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventory: InventoryEntity): Long

    @Query("DELETE FROM inventories WHERE id = :id")
    suspend fun deleteInventory(id: Int)

    // Employees
    @Query("SELECT * FROM employees ORDER BY name ASC")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity): Long

    @Query("DELETE FROM employees WHERE id = :id")
    suspend fun deleteEmployee(id: Int)

    // Honey Batches
    @Query("SELECT * FROM honey_batches ORDER BY productionDate DESC")
    fun getAllHoneyBatches(): Flow<List<HoneyBatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoneyBatch(batch: HoneyBatchEntity): Long

    @Query("DELETE FROM honey_batches WHERE id = :id")
    suspend fun deleteHoneyBatch(id: Int)
}
