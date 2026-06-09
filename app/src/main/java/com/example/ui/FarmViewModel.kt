package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FarmViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = FarmRepository(database.farmDao())

    // Language State: "en" for English, "ur" for Urdu
    private val _language = MutableStateFlow("en")
    val language: StateFlow<String> = _language.asStateFlow()

    // Authentication State
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // Navigation State (Active Screen module)
    private val _currentScreen = MutableStateFlow("dashboard")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // DB Observation Flows
    val animals = repository.allAnimals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val healthRecords = repository.allHealthRecords.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val financials = repository.allFinancials.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val inventories = repository.allInventory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val employees = repository.allEmployees.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val honeyBatches = repository.allHoneyBatches.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected items for display / edit
    val selectedAnimal = MutableStateFlow<AnimalEntity?>(null)

    // Alert system state
    private val _alerts = MutableStateFlow<List<FarmAlert>>(emptyList())
    val alerts: StateFlow<List<FarmAlert>> = _alerts.asStateFlow()

    // Localization Dictionary
    private val localizations = mapOf(
        "en" to mapOf(
            "app_title" to "Wanher Farms Private Limited",
            "app_tagline" to "Enterprise Livestock & Honey Management System",
            "dashboard" to "Executive Dashboard",
            "animals" to "Livestock Database",
            "financials" to "Financial Ledger",
            "inventory" to "Inventory & Warehousing",
            "employees" to "Employee Management",
            "honey" to "Honey Business Module",
            "notifications" to "Alerts & Notifications",
            "username" to "Username",
            "password" to "Password",
            "login" to "Secure Login",
            "logout" to "Logout",
            "sign_in" to "Sign In",
            "role" to "User Role",
            "admin" to "Administrator",
            "manager" to "Farm Manager",
            "worker" to "Field Operator",
            "total_animals" to "Total Animals",
            "breeding_stock" to "Breeding Stock",
            "male_animals" to "Male Animals",
            "female_animals" to "Female Animals",
            "kids_babies" to "Kids / Babies",
            "pregnant_animals" to "Pregnant Animals",
            "for_sale" to "Animals for Sale",
            "monthly_expenses" to "Monthly Expenses",
            "monthly_revenue" to "Monthly Revenue",
            "net_profit" to "Net Profit / Loss",
            "recent_purchases" to "Recent Purchases",
            "recent_sales" to "Recent Sales",
            "upcoming_vaccinations" to "Upcoming Vaccinations",
            "low_inventory_alert" to "Low Inventory Alert",
            "add_animal" to "Register Animal",
            "animal_id" to "Animal ID",
            "rfid" to "RFID / Tag Number",
            "name" to "Name / Nickname",
            "species" to "Species",
            "breed" to "Breed",
            "gender" to "Gender",
            "color" to "Color",
            "weight" to "Weight (kg)",
            "dob" to "Date of Birth",
            "age" to "Calculated Age",
            "status" to "Health Status",
            "purchase_price" to "Purchase Price",
            "transport_cost" to "Transport Cost",
            "seller_name" to "Seller Name",
            "seller_contact" to "Seller Contact",
            "save" to "Save Details",
            "delete" to "Delete Record",
            "cancel" to "Cancel",
            "expected_delivery" to "Expected Delivery",
            "number_kids" to "Number of Kids Born",
            "breeding_records" to "Breeding Records",
            "health_records" to "Health & Vaccinations",
            "add_health" to "Add Health Incident",
            "add_financial" to "Record Transaction",
            "add_item" to "Add Inventory Item",
            "add_employee" to "Enroll Employee",
            "add_batch" to "Log Honey Batch",
            "invoice" to "Generate Invoice",
            "share" to "Share on WhatsApp",
            "gps_pos" to "GPS Farm Location",
            "rs" to "Rs.",
            "en" to "English",
            "ur" to "Urdu",
            "p_l_statement" to "Profit & Loss",
            "honey_batches" to "Honey Batches",
            "honey_products" to "Honey Products",
            "batch_no" to "Batch Number",
            "prod_date" to "Production Date",
            "exp_date" to "Expiry Date",
            "total_bottles" to "Total Bottles",
            "sold_bottles" to "Sold Bottles",
            "msrp" to "MSRP (Retail Price)",
            "unit_cost" to "Unit Cost",
            "attendance" to "Today's Attendance",
            "payout" to "Record Payout",
            "search_holder" to "Search RFID, species or breed...",
            "unauthorized" to "Access Denied: Admin or Manager clearance required.",
            "role_restricted" to "Field operators have Read-Only permissions for financials."
        ),
        "ur" to mapOf(
            "app_title" to "وانہر فارمز پرائیویٹ لمیٹڈ",
            "app_tagline" to "مویشیوں اور شہد کی پیداوار کا مربوط انتظام",
            "dashboard" to "ایگزیکٹو ڈیش بورڈ",
            "animals" to "مویشیوں کا ڈیٹا بیس",
            "financials" to "مالیاتی حساب کتاب",
            "inventory" to "انوینٹری اور گودام",
            "employees" to "ملازمین کا انتظام",
            "honey" to "شہد کا کاروبار",
            "notifications" to "انتباہات اور نوٹیفیکیشنز",
            "username" to "صارف کا نام",
            "password" to "پاس ورڈ",
            "login" to "محفوظ لاگ ان",
            "logout" to "لاگ آؤٹ",
            "sign_in" to "داخل ہوں",
            "role" to "صارف کا عہدہ",
            "admin" to "ایڈمنسٹریٹر",
            "manager" to "فارم مینیجر",
            "worker" to "فیلڈ آپریٹر",
            "total_animals" to "کل جانور",
            "breeding_stock" to "افزائشِ نسل کا سٹاک",
            "male_animals" to "نر جانور",
            "female_animals" to "مادہ جانور",
            "kids_babies" to "بچے / میمنے",
            "pregnant_animals" to "حاملہ جانور",
            "for_sale" to "فروخت کے لیے دستیاب",
            "monthly_expenses" to "ماہانہ اخراجات",
            "monthly_revenue" to "ماہانہ آمدنی",
            "net_profit" to "خالص منافع / نقصان",
            "recent_purchases" to "حالیہ خریداریاں",
            "recent_sales" to "حالیہ فروخت",
            "upcoming_vaccinations" to "آنے والی ویکسینیشن",
            "low_inventory_alert" to "کم اسٹاک کا انتباہ",
            "add_animal" to "جانور کا اندراج کریں",
            "animal_id" to "جانور کا نمبر (ID)",
            "rfid" to "ٹیگ نمبر / RFID",
            "name" to "نام / عرفیت",
            "species" to "مخلوق",
            "breed" to "نسل",
            "gender" to "جنس",
            "color" to "رنگ",
            "weight" to "وزن (کلوگرام)",
            "dob" to "تاریخِ پیدائش",
            "age" to "حساب شدہ عمر",
            "status" to "صحت کی حالت",
            "purchase_price" to "قیمتِ خرید",
            "transport_cost" to "کرایہ / ٹرانسپورٹ",
            "seller_name" to "بیچنے والے کا نام",
            "seller_contact" to "بیچنے والے کا رابطہ",
            "save" to "محفوظ کریں",
            "delete" to "خارج کریں",
            "cancel" to "منسوخ کریں",
            "expected_delivery" to "متوقع زچگی",
            "number_kids" to "پیدا ہونے والے بچے",
            "breeding_records" to "افزائشِ نسل کا ریکارڈ",
            "health_records" to "صحت اور ویکسین",
            "add_health" to "علاج درج کریں",
            "add_financial" to "لین دین درج کریں",
            "add_item" to "انوینٹری آئٹم شامل کریں",
            "add_employee" to "ملازم درج کریں",
            "add_batch" to "شہد کا نیا بیچ درج کریں",
            "invoice" to "انوائس بنائیں",
            "share" to "واٹس ایپ شیئر",
            "gps_pos" to "فارم کا جغرافیائی مقام",
            "rs" to "روپے",
            "en" to "انگریزی",
            "ur" to "اردو",
            "p_l_statement" to "منافع اور نقصان",
            "honey_batches" to "شہد کے بیچز",
            "honey_products" to "شہد کی مصنوعات",
            "batch_no" to "بیچ نمبر",
            "prod_date" to "پیداوار کی تاریخ",
            "exp_date" to "خاتمے کی تاریخ",
            "total_bottles" to "کل بوتلیں",
            "sold_bottles" to "فروخت شدہ بوتلیں",
            "msrp" to "پرچون قیمت (MSRP)",
            "unit_cost" to "لاگت فی یونٹ",
            "attendance" to "آج کی حاضری",
            "payout" to "تنخواہ کی ادائیگی",
            "search_holder" to "ٹیگ، مخلوق یا نسل تلاش کریں...",
            "unauthorized" to "رسائی ممنوع ہے: ایڈمن یا مینیجر کی اجازت درکار ہے۔",
            "role_restricted" to "فیلڈ آپریٹرز کو مالیاتی حساب کتاب میں تبدیلی کا اختیار نہیں ہے۔"
        )
    )

    init {
        // Pre-populate with beautiful, detailed Agricultural seed data if empty
        viewModelScope.launch {
            animals.collectLatest { list ->
                if (list.isEmpty()) {
                    injectRealisticSeedData()
                }
            }
        }
        // Run alert updater periodic loop
        viewModelScope.launch {
            combine(animals, healthRecords, inventories) { a, h, i ->
                generateFarmAlerts(a, h, i)
            }.collect { generatedAlerts ->
                _alerts.value = generatedAlerts
            }
        }
    }

    // Locale helper
    fun t(key: String): String {
        return localizations[_language.value]?.get(key) ?: key
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    // Role-based auth simulation
    fun login(user: String, pass: String): Boolean {
        _loginError.value = null
        val dbUser = when {
            user.equals("admin", ignoreCase = true) && pass == "admin123" ->
                User("admin", "Aazar Salam", "Admin", "azarsalam2005@gmail.com")
            user.equals("manager", ignoreCase = true) && pass == "manage123" ->
                User("manager", "Muhammad Bilal", "Manager", "bilal.manager@wanher.com")
            user.equals("worker", ignoreCase = true) && pass == "work123" ->
                User("worker", "Sajid Khan", "Worker", "sajid.worker@wanher.com")
            else -> null
        }
        if (dbUser != null) {
            _currentUser.value = dbUser
            _currentScreen.value = "dashboard"
            return true
        } else {
            _loginError.value = if (_language.value == "en") "Invalid credentials" else "غلط صارف کا نام یا پاس ورڈ"
            return false
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentScreen.value = "dashboard"
    }

    // Auto-calculate animal age
    fun calculateAge(dobStr: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dob = sdf.parse(dobStr) ?: return "-"
            val diffMs = System.currentTimeMillis() - dob.time
            val diffDays = diffMs / (1000 * 60 * 60 * 24)
            val months = diffDays / 30
            val years = months / 12
            val remainingMonths = months % 12
            when {
                years > 0 -> if (_language.value == "en") "$years y $remainingMonths m" else "$years سال $remainingMonths ماہ"
                months > 0 -> if (_language.value == "en") "$months months" else "$months ماہ"
                else -> if (_language.value == "en") "$diffDays days" else "$diffDays دن"
            }
        } catch (e: Exception) {
            "-"
        }
    }

    // CRUD - Animal
    fun saveAnimal(animal: AnimalEntity) {
        viewModelScope.launch {
            repository.insertAnimal(animal)
        }
    }

    fun deleteAnimal(id: Int) {
        viewModelScope.launch {
            repository.deleteAnimalById(id)
        }
    }

    // CRUD - Health Records
    fun addHealthRecord(record: HealthRecordEntity) {
        viewModelScope.launch {
            repository.insertHealthRecord(record)
            // If there is code associated with a financial expense for treatment, log it!
            if (record.cost > 0.0) {
                repository.insertFinancial(
                    FinancialEntity(
                        type = "Expense",
                        category = "Vet Cost",
                        amount = record.cost,
                        date = record.date,
                        description = "Medical treatment: ${record.type} for Animal id #${record.animalId}"
                    )
                )
            }
        }
    }

    fun deleteHealthRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteHealthRecord(id)
        }
    }

    // CRUD - Financials
    fun addFinancial(financial: FinancialEntity) {
        viewModelScope.launch {
            repository.insertFinancial(financial)
        }
    }

    fun deleteFinancial(id: Int) {
        viewModelScope.launch {
            repository.deleteFinancial(id)
        }
    }

    // CRUD - Inventory
    fun addInventory(inventory: InventoryEntity) {
        viewModelScope.launch {
            repository.insertInventory(inventory)
        }
    }

    fun deleteInventory(id: Int) {
        viewModelScope.launch {
            repository.deleteInventory(id)
        }
    }

    // CRUD - Employee
    fun addEmployee(employee: EmployeeEntity) {
        viewModelScope.launch {
            repository.insertEmployee(employee)
        }
    }

    fun updateEmployeeAttendance(employee: EmployeeEntity, status: String) {
        viewModelScope.launch {
            repository.insertEmployee(employee.copy(attendanceToday = status))
        }
    }

    fun deleteEmployee(id: Int) {
        viewModelScope.launch {
            repository.deleteEmployee(id)
        }
    }

    fun recordSalaryPayout(employee: EmployeeEntity) {
        viewModelScope.launch {
            repository.insertFinancial(
                FinancialEntity(
                    type = "Expense",
                    category = "Labor",
                    amount = employee.salary,
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                    description = "Monthly Salary payout for ${employee.name} (${employee.role})"
                )
            )
        }
    }

    // CRUD - Honey
    fun addHoneyBatch(batch: HoneyBatchEntity) {
        viewModelScope.launch {
            repository.insertHoneyBatch(batch)
            // Log batch production as an expense
            val totalCost = batch.costPerUnit * batch.quantity
            repository.insertFinancial(
                FinancialEntity(
                    type = "Expense",
                    category = "Other",
                    amount = totalCost,
                    date = batch.productionDate,
                    description = "Honey Batch ${batch.batchNumber} Production Cost"
                )
            )
        }
    }

    fun recordHoneySale(batch: HoneyBatchEntity, quantitySold: Int, client: String) {
        viewModelScope.launch {
            if (batch.salesCount + quantitySold <= batch.quantity) {
                val updated = batch.copy(salesCount = batch.salesCount + quantitySold)
                repository.insertHoneyBatch(updated)
                val saleRevenue = batch.salePrice * quantitySold
                repository.insertFinancial(
                    FinancialEntity(
                        type = "Revenue",
                        category = "Honey Sale",
                        amount = saleRevenue,
                        date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                        description = "Sold $quantitySold bottles from Honey Batch ${batch.batchNumber} to $client",
                        contactName = client
                    )
                )
            }
        }
    }

    fun deleteHoneyBatch(id: Int) {
        viewModelScope.launch {
            repository.deleteHoneyBatch(id)
        }
    }

    // Seed injector with professional agricultural data
    private suspend fun injectRealisticSeedData() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today = Calendar.getInstance()

        fun minusDays(days: Int): String {
            val c = today.clone() as Calendar
            c.add(Calendar.DAY_OF_YEAR, -days)
            return sdf.format(c.time)
        }

        fun plusDays(days: Int): String {
            val c = today.clone() as Calendar
            c.add(Calendar.DAY_OF_YEAR, days)
            return sdf.format(c.time)
        }

        // 1. Animals
        val animalsSeed = listOf(
            AnimalEntity(
                rfid = "WF-COW-101",
                name = "Sahiwal Gold",
                species = "Cow",
                breed = "Sahiwal Purebred",
                gender = "Female",
                color = "Reddish Brown",
                weight = 420.0,
                dateOfBirth = "2021-03-10",
                status = "Pregnant",
                purchaseDate = "2023-01-15",
                purchasePrice = 350000.0,
                sellerName = "Sahiwal Model Farm",
                sellerContact = "+92-300-1234567",
                transportCost = 15000.0,
                expectedDeliveryDate = plusDays(35),
                growthHistory = "2026-03-01:405.0,2026-04-01:412.0,2026-05-01:420.0",
                notes = "High-yield milk producer, average 18 liters per day."
            ),
            AnimalEntity(
                rfid = "WF-COW-102",
                name = "Cholistani King",
                species = "Cow",
                breed = "Cholistani",
                gender = "Male",
                color = "White with Black spots",
                weight = 580.0,
                dateOfBirth = "2020-05-12",
                status = "Healthy",
                purchaseDate = "2023-02-10",
                purchasePrice = 450000.0,
                sellerName = "Bhawalpur Livestock Dept",
                sellerContact = "+92-301-7654321",
                transportCost = 20000.0,
                growthHistory = "2026-03-01:562.0,2026-04-01:573.0,2026-05-01:580.0",
                notes = "Primary breeding bull with outstanding genetic profile."
            ),
            AnimalEntity(
                rfid = "WF-GOT-201",
                name = "Kamori Queen",
                species = "Goat",
                breed = "Kamori",
                gender = "Female",
                color = "Dark Brown with gold ears",
                weight = 62.0,
                dateOfBirth = "2023-06-20",
                status = "Pregnant",
                purchaseDate = "2024-02-12",
                purchasePrice = 75000.0,
                sellerName = "Mirpurkhas Goat Market",
                sellerContact = "+92-333-8889911",
                transportCost = 5000.0,
                expectedDeliveryDate = plusDays(12),
                growthHistory = "2026-03-01:58.0,2026-04-01:60.5,2026-05-01:62.0",
                notes = "Exceptional ear-length and dapple skin pattern."
            ),
            AnimalEntity(
                rfid = "WF-GOT-202",
                name = "Beetal Prince",
                species = "Goat",
                breed = "Beetal (Faisalabadi)",
                gender = "Male",
                color = "Jet Black",
                weight = 94.0,
                dateOfBirth = "2022-11-05",
                status = "Healthy",
                purchaseDate = "2024-01-10",
                purchasePrice = 120000.0,
                sellerName = "Lyallpur Stud Farms",
                sellerContact = "+92-321-4433221",
                transportCost = 6000.0,
                growthHistory = "2026-03-01:91.0,2026-04-01:92.5,2026-05-01:94.0",
                notes = "High frame rate and stellar libido records."
            ),
            AnimalEntity(
                rfid = "WF-GOT-203",
                name = "Laili",
                species = "Goat",
                breed = "Beetal",
                gender = "Female",
                color = "Creamy White",
                weight = 45.0,
                dateOfBirth = "2025-05-01",
                status = "Kids/Babies",
                purchaseDate = "2025-10-01",
                purchasePrice = 30000.0,
                sellerName = "Kasur Goat Yard",
                transportCost = 2000.0,
                growthHistory = "2026-03-01:38.0,2026-04-01:41.5,2026-05-01:45.0",
                notes = "Born triplet kid, rapidly gaining weight."
            ),
            AnimalEntity(
                rfid = "WF-BUF-301",
                name = "Neeli Beauty",
                species = "Buffalo",
                breed = "Nili Ravi",
                gender = "Female",
                color = "Black",
                weight = 650.0,
                dateOfBirth = "2019-09-18",
                status = "Healthy",
                purchaseDate = "2022-05-14",
                purchasePrice = 480000.0,
                sellerName = "Okara Buffalo Breeders",
                sellerContact = "+92-344-5555123",
                transportCost = 18000.0,
                growthHistory = "2026-03-01:640.0,2026-04-01:645.0,2026-05-01:650.0",
                notes = "Generates 22 liters of thick high-fat milk daily."
            ),
            AnimalEntity(
                rfid = "WF-GOT-204",
                name = "Shano",
                species = "Goat",
                breed = "Kamori",
                gender = "Female",
                color = "Brown spots",
                weight = 48.0,
                dateOfBirth = "2024-01-22",
                status = "For Sale",
                purchaseDate = "2024-08-01",
                purchasePrice = 50000.0,
                sellerName = "Locally bred",
                transportCost = 0.0,
                growthHistory = "2026-03-01:44.0,2026-04-01:46.0,2026-05-01:48.0",
                notes = "Listed for Eid-ul-Adha marketplace."
            )
        )

        val insertedIds = mutableListOf<Long>()
        for (a in animalsSeed) {
            val id = repository.insertAnimal(a)
            insertedIds.add(id)
        }

        // 2. Health Records
        if (insertedIds.size >= 4) {
            repository.insertHealthRecord(
                HealthRecordEntity(
                    animalId = insertedIds[0].toInt(),
                    type = "Vaccination",
                    date = minusDays(20),
                    details = "FMD (Foot and Mouth Disease) Vaccine injection.",
                    cost = 2500.0
                )
            )
            repository.insertHealthRecord(
                HealthRecordEntity(
                    animalId = insertedIds[2].toInt(),
                    type = "Deworming",
                    date = minusDays(5),
                    details = "Albendazole oral drench administered.",
                    cost = 450.0
                )
            )
            repository.insertHealthRecord(
                HealthRecordEntity(
                    animalId = insertedIds[5].toInt(),
                    type = "Vet Visit",
                    date = minusDays(12),
                    details = "Regular pregnancy ultrasound verification, successful.",
                    cost = 4000.0
                )
            )
        }

        // 3. Financials
        val financialsSeed = listOf(
            FinancialEntity(type = "Expense", category = "Feed", amount = 85000.0, date = minusDays(14), description = "Purchase of 25 bags of high-protein feed concentrate"),
            FinancialEntity(type = "Expense", category = "Labor", amount = 65000.0, date = minusDays(28), description = "Monthly wages for staff farm labor"),
            FinancialEntity(type = "Expense", category = "Equipment", amount = 42000.0, date = minusDays(40), description = "Automated fly catcher and electric milk dispenser nozzle kits"),
            FinancialEntity(type = "Revenue", category = "Milk Sale", amount = 145000.0, date = minusDays(7), description = "Weekly dispatch of 800 liters of fresh buffalo buffalo milk sold to Nest Fresh"),
            FinancialEntity(type = "Revenue", category = "Animal Sale", amount = 180000.0, date = minusDays(3), description = "Sold active Kamori breeding buck to royal farm owner Lahore", contactName = "Malik Asif"),
            FinancialEntity(type = "Revenue", category = "Honey Sale", amount = 58000.0, date = minusDays(1), description = "Supplied 40 glass jars of organic sidr honey to Gourmet Bakers", contactName = "Gourmet Bakers Lahore")
        )
        for (f in financialsSeed) {
            repository.insertFinancial(f)
        }

        // 4. Inventory
        val inventorySeed = listOf(
            InventoryEntity(itemName = "Wandal Protein Concentrates", category = "Feed", quantity = 340.0, unit = "kg", minStockAlert = 100.0, supplierName = "Cargill Feed Mills Ltd", supplierContact = "+92-042-1111002"),
            InventoryEntity(itemName = "Albendazole Dewormer Liquid", category = "Medicine", quantity = 1.2, unit = "liters", minStockAlert = 2.0, supplierName = "ICI Pakistan Health", supplierContact = "+92-300-4444555"),
            InventoryEntity(itemName = "FMD BioVacc Vaccines", category = "Vaccine", quantity = 48.0, unit = "vials", minStockAlert = 15.0, supplierName = "National Veterinary Labs", supplierContact = "+92-51-123499"),
            InventoryEntity(itemName = "Sidr Brand Honey Bottles (500g)", category = "Packaging", quantity = 150.0, unit = "bottles", minStockAlert = 50.0, supplierName = "Gunj Packagings Gujranwala", supplierContact = "+92-55-908021"),
            InventoryEntity(itemName = "Gold Wanher Branding Labels", category = "Branding", quantity = 250.0, unit = "units", minStockAlert = 100.0, supplierName = "Faisalabad Sticker Print", supplierContact = "+92-41-456321")
        )
        for (i in inventorySeed) {
            repository.insertInventory(i)
        }

        // 5. Employees
        val employeeSeed = listOf(
            EmployeeEntity(name = "Muhammad Bilal", role = "Manager", attendanceToday = "Present", salary = 75000.0, performanceNotes = "Exceptional management skills, keeps livestock metrics precise daily."),
            EmployeeEntity(name = "Sajid Khan", role = "Laborer", attendanceToday = "Present", salary = 35000.0, performanceNotes = "Very disciplined in distributing pasture feed allocations."),
            EmployeeEntity(name = "Dr. Yasir Shah", role = "Vet", attendanceToday = "Pending", salary = 120000.0, performanceNotes = "Excellent reproductive diagnosis rates.")
        )
        for (e in employeeSeed) {
            repository.insertEmployee(e)
        }

        // 6. Honey Batches
        val honeySeeds = listOf(
            HoneyBatchEntity(batchNumber = "WH-SIDR-2026-01", productionDate = minusDays(18), expiryDate = plusDays(365 * 2), quantity = 120, size = "500g", costPerUnit = 450.0, salePrice = 1450.0, salesCount = 40, distributorName = "Punjab Organic Co. Lahore"),
            HoneyBatchEntity(batchNumber = "WH-ACACIA-2026-02", productionDate = minusDays(2), expiryDate = plusDays(365 * 2), quantity = 80, size = "1kg", costPerUnit = 750.0, salePrice = 2400.0, salesCount = 0, distributorName = "Isloo Super Mart")
        )
        for (h in honeySeeds) {
            repository.insertHoneyBatch(h)
        }
    }

    // Auto alert generation pipeline
    private fun generateFarmAlerts(
        animalsList: List<AnimalEntity>,
        healthList: List<HealthRecordEntity>,
        inventoryList: List<InventoryEntity>
    ): List<FarmAlert> {
        val alertList = mutableListOf<FarmAlert>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today = Date()

        // 1. Low inventory alerts
        for (inv in inventoryList) {
            if (inv.quantity <= inv.minStockAlert) {
                alertList.add(
                    FarmAlert(
                        titleEn = "Low Stock: ${inv.itemName}",
                        titleUr = "کم اسٹاک: ${inv.itemName}",
                        descEn = "Available: ${inv.quantity} ${inv.unit}. Reorder soon from ${inv.supplierName}.",
                        descUr = "دستیاب مقدار: ${inv.quantity} ${inv.unit}۔ سپلائر سے فوری رابطہ کریں۔",
                        type = "Inventory",
                        severity = "High"
                    )
                )
            }
        }

        // 2. Upcoming Pregnancy milestones
        for (a in animalsList) {
            if (a.status == "Pregnant" && !a.expectedDeliveryDate.isNullOrEmpty()) {
                try {
                    val expDate = sdf.parse(a.expectedDeliveryDate)
                    if (expDate != null) {
                        val diffDays = (expDate.time - today.time) / (1000 * 60 * 60 * 24)
                        if (diffDays in 0..15) {
                            alertList.add(
                                FarmAlert(
                                    titleEn = "Kidding Milestone Alert for ${a.name} (${a.rfid})",
                                    titleUr = "زچگی کا الرٹ: ${a.name} (${a.rfid})",
                                    descEn = "Kidding expected in $diffDays days ($a.expectedDeliveryDate). Prepare dry birth stall.",
                                    descUr = "متوقع زچگی $diffDays دنوں میں ($a.expectedDeliveryDate) ہے۔ زچگی خانہ تیار کریں۔",
                                    type = "Pregnancy",
                                    severity = "Severe"
                                )
                            )
                        }
                    }
                } catch (e: Exception) {}
            }
        }

        // 3. Vaccination schedules
        // If an animal has no vaccination logs, alert that FMD vaccines are pending
        for (a in animalsList) {
            val personalRecords = healthList.filter { it.animalId == a.id && it.type == "Vaccination" }
            if (personalRecords.isEmpty() && a.status != "Sold") {
                alertList.add(
                    FarmAlert(
                        titleEn = "Vaccine Required: ${a.name} (${a.rfid})",
                        titleUr = "ویکسینیشن درکار: ${a.name} (${a.rfid})",
                        descEn = "No active vaccination entry found. Recommended scheduling FMD vaccine.",
                        descUr = "کوئی ویکسینیشن ریکارڈ نہیں ملا۔ فزیکل چیک اپ اور حفاظتی ٹیکے شیڈول کریں۔",
                        type = "Health",
                        severity = "Medium"
                    )
                )
            }
        }

        return alertList
    }
}

// User representation helpers
data class User(
    val username: String,
    val fullName: String,
    val role: String, // Admin, Manager, Worker
    val email: String
)

// Alert representation helpers
data class FarmAlert(
    val titleEn: String,
    val titleUr: String,
    val descEn: String,
    val descUr: String,
    val type: String, // Inventory, Pregnancy, Health
    val severity: String // Severe, High, Medium, Low
)
