package com.example.data

import kotlinx.coroutines.flow.Flow

class FarmRepository(private val farmDao: FarmDao) {

    // Animals
    val allAnimals: Flow<List<AnimalEntity>> = farmDao.getAllAnimals()

    suspend fun insertAnimal(animal: AnimalEntity): Long = farmDao.insertAnimal(animal)

    suspend fun deleteAnimalById(id: Int) = farmDao.deleteAnimalById(id)

    // Health Records
    val allHealthRecords: Flow<List<HealthRecordEntity>> = farmDao.getAllHealthRecords()

    fun getHealthRecordsForAnimal(animalId: Int): Flow<List<HealthRecordEntity>> =
        farmDao.getHealthRecordsForAnimal(animalId)

    suspend fun insertHealthRecord(record: HealthRecordEntity): Long =
        farmDao.insertHealthRecord(record)

    suspend fun deleteHealthRecord(id: Int) = farmDao.deleteHealthRecord(id)

    // Financials
    val allFinancials: Flow<List<FinancialEntity>> = farmDao.getAllFinancials()

    suspend fun insertFinancial(financial: FinancialEntity): Long =
        farmDao.insertFinancial(financial)

    suspend fun deleteFinancial(id: Int) = farmDao.deleteFinancial(id)

    // Inventory
    val allInventory: Flow<List<InventoryEntity>> = farmDao.getAllInventory()

    suspend fun insertInventory(inventory: InventoryEntity): Long =
        farmDao.insertInventory(inventory)

    suspend fun deleteInventory(id: Int) = farmDao.deleteInventory(id)

    // Employees
    val allEmployees: Flow<List<EmployeeEntity>> = farmDao.getAllEmployees()

    suspend fun insertEmployee(employee: EmployeeEntity): Long =
        farmDao.insertEmployee(employee)

    suspend fun deleteEmployee(id: Int) = farmDao.deleteEmployee(id)

    // Honey Batches
    val allHoneyBatches: Flow<List<HoneyBatchEntity>> = farmDao.getAllHoneyBatches()

    suspend fun insertHoneyBatch(batch: HoneyBatchEntity): Long =
        farmDao.insertHoneyBatch(batch)

    suspend fun deleteHoneyBatch(id: Int) = farmDao.deleteHoneyBatch(id)
}
