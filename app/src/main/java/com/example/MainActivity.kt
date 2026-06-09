package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.FarmViewModel
import com.example.ui.User
import com.example.ui.theme.MyApplicationTheme
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContainer()
            }
        }
    }
}

@Composable
fun MainContainer() {
    val viewModel: FarmViewModel = viewModel()
    val currentUser by viewModel.currentUser.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (currentUser == null) {
            LoginScreen(viewModel)
        } else {
            MainAppLayout(viewModel)
        }
    }
}

@Composable
fun LoginScreen(viewModel: FarmViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginError by viewModel.loginError.collectAsState()
    val language by viewModel.language.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f)
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Language Toggle at top-right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { viewModel.setLanguage(if (language == "en") "ur" else "en") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (language == "en") "اردو" else "English",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(if (LocalConfiguration.current.screenWidthDp > 600) 0.55f else 0.95f)
                .wrapContentHeight()
                .testTag("login_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Gold crest
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.secondary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(45.dp)
                    )
                }

                Text(
                    text = viewModel.t("app_title"),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = viewModel.t("app_tagline"),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(viewModel.t("username")) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(viewModel.t("password")) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                if (loginError != null) {
                    Text(
                        text = loginError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val success = viewModel.login(username, password)
                        if (success) {
                            Toast.makeText(context, "Log in Successful!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = viewModel.t("sign_in"),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Credentials Helper
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9F9F7), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Demo Logins Available:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "• Admin: admin / admin123\n• Manager: manager / manage123\n• Worker: worker / work123",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MainAppLayout(viewModel: FarmViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isWideScreen = LocalConfiguration.current.screenWidthDp > 600

    Scaffold(
        topBar = { TopFarmAppBar(viewModel) },
        bottomBar = {
            if (!isWideScreen) {
                BottomFarmNavBar(viewModel)
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isWideScreen) {
                SideFarmNavRail(viewModel)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                    },
                    label = "screenTransition"
                ) { screen ->
                    when (screen) {
                        "dashboard" -> DashboardScreen(viewModel)
                        "animals" -> AnimalsScreen(viewModel)
                        "financials" -> FinancialsScreen(viewModel)
                        "inventory" -> InventoryScreen(viewModel)
                        "employees" -> EmployeesScreen(viewModel)
                        "honey" -> HoneyScreen(viewModel)
                        "notifications" -> NotificationsScreen(viewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopFarmAppBar(viewModel: FarmViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val language by viewModel.language.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val activeAlertCount = alerts.size

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌿", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = viewModel.t("app_title"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = currentUser?.fullName?.let { "${viewModel.t("role")}: ${viewModel.t(currentUser?.role?.lowercase() ?: "")} (${it})" } ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        actions = {
            // Alerts notification icon
            Box(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .clickable { viewModel.setScreen("notifications") }
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Alerts",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                if (activeAlertCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-2).dp, y = 2.dp)
                            .size(16.dp)
                            .background(MaterialTheme.colorScheme.secondary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activeAlertCount.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Language Switcher
            IconButton(onClick = { viewModel.setLanguage(if (language == "en") "ur" else "en") }) {
                Text(
                    text = if (language == "en") "UR" else "EN",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            // Logout Button
            IconButton(onClick = { viewModel.logout() }) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

@Composable
fun SideFarmNavRail(viewModel: FarmViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    NavigationRail(
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.width(96.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            listOf(
                NavMenuItem("dashboard", Icons.Default.Home, "dashboard"),
                NavMenuItem("animals", Icons.Default.Face, "animals"),
                NavMenuItem("financials", Icons.Default.ShoppingCart, "financials"),
                NavMenuItem("inventory", Icons.Default.Settings, "inventory"),
                NavMenuItem("employees", Icons.Default.Person, "employees"),
                NavMenuItem("honey", Icons.Default.Build, "honey")
            ).forEach { item ->
                NavigationRailItem(
                    selected = currentScreen == item.id,
                    onClick = { viewModel.setScreen(item.id) },
                    icon = { Icon(item.icon, contentDescription = viewModel.t(item.labelKey)) },
                    label = {
                        Text(
                            text = viewModel.t(item.labelKey),
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                )
            }
        }
    }
}

@Composable
fun BottomFarmNavBar(viewModel: FarmViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        listOf(
            NavMenuItem("dashboard", Icons.Default.Home, "dashboard"),
            NavMenuItem("animals", Icons.Default.Face, "animals"),
            NavMenuItem("financials", Icons.Default.ShoppingCart, "financials"),
            NavMenuItem("inventory", Icons.Default.Settings, "inventory"),
            NavMenuItem("employees", Icons.Default.Person, "employees"),
            NavMenuItem("honey", Icons.Default.Build, "honey")
        ).forEach { item ->
            NavigationBarItem(
                selected = currentScreen == item.id,
                onClick = { viewModel.setScreen(item.id) },
                icon = { Icon(item.icon, contentDescription = viewModel.t(item.labelKey)) },
                label = {
                    Text(
                        text = viewModel.t(item.labelKey),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            )
        }
    }
}

data class NavMenuItem(val id: String, val icon: ImageVector, val labelKey: String)

// ======================== MODULES IMPLEMENTATIONS ========================

@Composable
fun DashboardScreen(viewModel: FarmViewModel) {
    val animalsList by viewModel.animals.collectAsState()
    val financialsList by viewModel.financials.collectAsState()
    val inventoriesList by viewModel.inventories.collectAsState()
    val alertsList by viewModel.alerts.collectAsState()

    // Aggregate values
    val totalAnimals = animalsList.size
    val breedingStock = animalsList.count { it.status == "Breeding" || it.status == "Pregnant" }
    val maleAnimals = animalsList.count { it.gender == "Male" && it.status != "Kids/Babies" }
    val femaleAnimals = animalsList.count { it.gender == "Female" && it.status != "Kids/Babies" }
    val kidsBabies = animalsList.count { it.status == "Kids/Babies" }
    val pregnantAnimals = animalsList.count { it.status == "Pregnant" }
    val forSale = animalsList.count { it.status == "For Sale" }

    // Financial accounting for current month
    val curCal = Calendar.getInstance()
    val df = DecimalFormat("#,###")
    val dfFloat = DecimalFormat("#,###.##")

    val (monthlyExpenses, monthlyRevenue) = remember(financialsList) {
        var exp = 0.0
        var rev = 0.0
        for (f in financialsList) {
            // we can simplify by accumulating all histories
            if (f.type == "Expense") exp += f.amount else rev += f.amount
        }
        Pair(exp, rev)
    }

    val netProfit = monthlyRevenue - monthlyExpenses

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "${viewModel.t("dashboard")} - Executive Analytics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Numerical metrics panel grid
        item {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(110.dp),
                modifier = Modifier
                    .height(if (LocalConfiguration.current.screenWidthDp > 600) 130.dp else 260.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { MetricCard(viewModel.t("total_animals"), totalAnimals.toString(), Icons.Default.Face, MaterialTheme.colorScheme.primary) }
                item { MetricCard(viewModel.t("breeding_stock"), breedingStock.toString(), Icons.Default.Favorite, Color(0xFFF3F0E6)) }
                item { MetricCard(viewModel.t("male_animals"), maleAnimals.toString(), Icons.Default.Star, Color(0xFFFCFAF2)) }
                item { MetricCard(viewModel.t("female_animals"), femaleAnimals.toString(), Icons.Default.Favorite, Color(0xFFF3F0E6)) }
                item { MetricCard(viewModel.t("kids_babies"), kidsBabies.toString(), Icons.Default.Face, Color(0xFFFCFAF2)) }
                item { MetricCard(viewModel.t("pregnant_animals"), pregnantAnimals.toString(), Icons.Default.Favorite, Color(0xFFF3F0E6)) }
                item { MetricCard(viewModel.t("for_sale"), forSale.toString(), Icons.Default.ShoppingCart, MaterialTheme.colorScheme.secondary) }
            }
        }

        // Financial balance overview card with gold gradient background
        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Financial Snapshot (All Time)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "PKR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(viewModel.t("monthly_expenses"), fontSize = 12.sp, color = Color.Gray)
                            Text(
                                "Rs. ${df.format(monthlyExpenses)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(viewModel.t("monthly_revenue"), fontSize = 12.sp, color = Color.Gray)
                            Text(
                                "Rs. ${df.format(monthlyRevenue)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2E7D32)
                            )
                        }
                        Column(modifier = Modifier.weight(1.2f)) {
                            Text(viewModel.t("net_profit"), fontSize = 12.sp, color = Color.Gray)
                            Text(
                                "Rs. ${df.format(netProfit)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = if (netProfit >= 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Draw beautifully custom financial Canvas chart
                    Text(
                        text = "Cashflow Trend (Revenue vs Expenses)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(Color(0xFFF3F0E6), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val maxVal = maxOf(monthlyRevenue, monthlyExpenses, 1.0).toFloat()
                            val width = size.width
                            val height = size.height

                            // Draw baseline
                            drawLine(
                                color = Color.LightGray,
                                start = Offset(0f, height - 10f),
                                end = Offset(width, height - 10f),
                                strokeWidth = 1f
                            )

                            // Revenue candle (Green)
                            val revHeight = (monthlyRevenue.toFloat() / maxVal) * (height - 20)
                            drawRect(
                                color = Color(0xFF4CAF50),
                                topLeft = Offset(width * 0.25f - 25f, height - 10f - revHeight),
                                size = Size(50f, revHeight)
                            )

                            // Expenses candle (Red)
                            val expHeight = (monthlyExpenses.toFloat() / maxVal) * (height - 20)
                            drawRect(
                                color = Color(0xFFEF5350),
                                topLeft = Offset(width * 0.75f - 25f, height - 10f - expHeight),
                                size = Size(50f, expHeight)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 16.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Revenues", fontSize = 10.sp, color = Color.Gray)
                            Text("Total Expenses", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        // Live alert box previews
        if (alertsList.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.t("notifications"),
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = { viewModel.setScreen("notifications") }) {
                        Text("View All (${alertsList.size})", fontSize = 12.sp)
                    }
                }

                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFFF3F3)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val alert = alertsList.first()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = Color.Red,
                            modifier = Modifier.size(28.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (viewModel.language.value == "en") alert.titleEn else alert.titleUr,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = if (viewModel.language.value == "en") alert.descEn else alert.descUr,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // GPS Complex Farm Locations
        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = viewModel.t("gps_pos"),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Central Thal Range Compound: 32.2031° N, 71.5309° E\nWanher Head Office Complex: 31.5204° N, 74.3587° E",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, score: String, icon: ImageVector, surfaceColor: Color) {
    val isPrimaryColored = (surfaceColor == MaterialTheme.colorScheme.primary)
    val isSecondaryColored = (surfaceColor == MaterialTheme.colorScheme.secondary)
    
    val iconColor = when {
        isPrimaryColored -> MaterialTheme.colorScheme.secondary
        isSecondaryColored -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primary
    }
    
    val textColor = when {
        isPrimaryColored -> Color.White
        isSecondaryColored -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primary
    }
    
    val labelColor = when {
        isPrimaryColored -> Color.White.copy(alpha = 0.8f)
        isSecondaryColored -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        else -> Color(0xFF7A7565) // CharcoalMuted
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .background(surfaceColor, RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = if (isPrimaryColored || isSecondaryColored) Color.Transparent else Color(0xFFE8E4D9),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = score,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = textColor,
                    fontSize = 24.sp
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AnimalsScreen(viewModel: FarmViewModel) {
    val animalsList by viewModel.animals.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedSpecieFilter by remember { mutableStateOf("All") }
    var activeAnimalEdit by remember { mutableStateOf<AnimalEntity?>(null) }
    var activeAnimalDetail by remember { mutableStateOf<AnimalEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredList = animalsList.filter {
        (selectedSpecieFilter == "All" || it.species == selectedSpecieFilter) &&
                (it.name.contains(searchQuery, ignoreCase = true) ||
                        it.rfid.contains(searchQuery, ignoreCase = true) ||
                        it.breed.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        floatingActionButton = {
            if (currentUser != null && currentUser?.role != "Worker") {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Animal")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = viewModel.t("animals"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(viewModel.t("search_holder")) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("animal_search"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Species scroll horizontal bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Goat", "Sheep", "Cow", "Buffalo").forEach { sp ->
                    FilterChip(
                        selected = selectedSpecieFilter == sp,
                        onClick = { selectedSpecieFilter = sp },
                        label = { Text(sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Real animals list
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No animal records found matching criteria.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList) { animal ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { activeAnimalDetail = animal },
                            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Simulated species graphic avatar
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when(animal.species) {
                                                "Goat" -> Icons.Default.Face
                                                "Cow" -> Icons.Default.Home
                                                else -> Icons.Default.Face
                                            },
                                            contentDescription = animal.species,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = animal.name.ifEmpty { "Unnamed Animal" },
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = "${animal.species} • ${animal.breed}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = "Tag: ${animal.rfid}",
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                when(animal.status) {
                                                    "Pregnant" -> Color(0xFFFFF3CD)
                                                    "Healthy" -> Color(0xFFD4EDDA)
                                                    "For Sale" -> Color(0xFFCCE5FF)
                                                    else -> Color(0xFFE2E3E5)
                                                },
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = animal.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when(animal.status) {
                                                "Pregnant" -> Color(0xFF856404)
                                                "Healthy" -> Color(0xFF155724)
                                                "For Sale" -> Color(0xFF004085)
                                                else -> Color(0xFF383D41)
                                            }
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${animal.weight} kg",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Animal Details Dialog Module
    activeAnimalDetail?.let { animal ->
        AnimalDetailsDialog(
            animal = animal,
            viewModel = viewModel,
            onDismiss = { activeAnimalDetail = null },
            onEdit = {
                activeAnimalDetail = null
                activeAnimalEdit = animal
            }
        )
    }

    // Animal Registration / Edit Form Dialog
    if (showAddDialog || activeAnimalEdit != null) {
        AnimalFormDialog(
            animal = activeAnimalEdit,
            viewModel = viewModel,
            onDismiss = {
                showAddDialog = false
                activeAnimalEdit = null
            }
        )
    }
}

@Composable
fun AnimalDetailsDialog(
    animal: AnimalEntity,
    viewModel: FarmViewModel,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    val healthList by viewModel.healthRecords.collectAsState()
    val personalHealth = healthList.filter { it.animalId == animal.id }
    val currentUser by viewModel.currentUser.collectAsState()
    var displaySection by remember { mutableStateOf("basic") } // basic, breeding, health, growth
    
    // Growth series mapping
    val growthTimeline = remember(animal.growthHistory) {
        val list = mutableListOf<Pair<String, Double>>()
        if (animal.growthHistory.isNotEmpty()) {
            animal.growthHistory.split(",").forEach { item ->
                try {
                    val parts = item.split(":")
                    if (parts.size == 2) {
                        list.add(Pair(parts[0], parts[1].toDouble()))
                    }
                } catch (e: Exception) {}
            }
        }
        list
    }

    var showInvoiceView by remember { mutableStateOf(false) }
    var showQRCodeView by remember { mutableStateOf(false) }
    var shareText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header block
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = animal.name.ifEmpty { "Animal Specs" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons row (Edit, Invoice, Share QR)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (currentUser != null && currentUser?.role != "Worker") {
                        Button(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", fontSize = 11.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = { showQRCodeView = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("QR Code", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { showInvoiceView = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Invoice", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val msg = "Wanher Farms Livestock Profile\n-----------\nID: ${animal.rfid}\nName: ${animal.name}\nSpecies: ${animal.species}\nBreed: ${animal.breed}\nWeight: ${animal.weight}kg\nStatus: ${animal.status}"
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, msg)
                                type = "text/plain"
                                `package` = "com.whatsapp"
                            }
                            try {
                                context.startActivity(sendIntent)
                            } catch (ex: Exception) {
                                Toast.makeText(context, "WhatsApp is not installed on this device.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", fontSize = 9.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scroll tabs selector (Basic, Breeding, Health, Growth Chart)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("basic", "breeding", "health", "growth").forEach { tab ->
                        FilterChip(
                            selected = displaySection == tab,
                            onClick = { displaySection = tab },
                            label = { Text(tab.uppercase()) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = Color.DarkGray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sliding views container
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (displaySection) {
                        "basic" -> {
                            ProfileDetailRow("RFID Tag:", animal.rfid)
                            ProfileDetailRow("Species:", animal.species)
                            ProfileDetailRow("Breed:", animal.breed)
                            ProfileDetailRow("Gender:", animal.gender)
                            ProfileDetailRow("Color:", animal.color)
                            ProfileDetailRow("Weight:", "${animal.weight} kg")
                            ProfileDetailRow("Date of Birth:", animal.dateOfBirth)
                            ProfileDetailRow("Calculated Age:", viewModel.calculateAge(animal.dateOfBirth))
                            ProfileDetailRow("Current Status:", animal.status)
                            ProfileDetailRow("Purchase Cost:", "Rs. ${animal.purchasePrice}")
                            ProfileDetailRow("Transport Cost:", "Rs. ${animal.transportCost}")
                            ProfileDetailRow("Seller Name:", animal.sellerName)
                            ProfileDetailRow("Seller Contact:", animal.sellerContact)
                            ProfileDetailRow("Notes:", animal.notes ?: "N/A")
                        }
                        "breeding" -> {
                            ProfileDetailRow("Sire ID:", animal.sireId ?: "N/A")
                            ProfileDetailRow("Dam ID:", animal.damId ?: "N/A")
                            ProfileDetailRow("Expected Delivery:", animal.expectedDeliveryDate ?: "N/A")
                            ProfileDetailRow("Breeding History:", "Logged naturally inside farm logs. Under gestation.")
                        }
                        "health" -> {
                            // Sub-section option to ADD therapeutic record
                            var showAddRecordForm by remember { mutableStateOf(false) }
                            var rectype by remember { mutableStateOf("Vaccination") }
                            var recdate by remember { mutableStateOf("2026-06-09") }
                            var recdetail by remember { mutableStateOf("") }
                            var reccost by remember { mutableStateOf("") }

                            if (currentUser != null && currentUser?.role != "Worker") {
                                Button(
                                    onClick = { showAddRecordForm = !showAddRecordForm },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Text(if (showAddRecordForm) "Hide Health Logger" else "Log Medical Incident")
                                }
                            }

                            if (showAddRecordForm) {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text("Log Medicine / Vaccination", fontWeight = FontWeight.Bold)
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            FilterChip(selected = rectype == "Vaccination", onClick = { rectype = "Vaccination" }, label = { Text("Vaccine") })
                                            FilterChip(selected = rectype == "Deworming", onClick = { rectype = "Deworming" }, label = { Text("Deworm") })
                                            FilterChip(selected = rectype == "Treatment", onClick = { rectype = "Treatment" }, label = { Text("Treatment") })
                                        }
                                        OutlinedTextField(value = recdate, onValueChange = { recdate = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                                        OutlinedTextField(value = recdetail, onValueChange = { recdetail = it }, label = { Text("Treatment Details") }, modifier = Modifier.fillMaxWidth())
                                        OutlinedTextField(value = reccost, onValueChange = { reccost = it }, label = { Text("Medicine Cost (PKR)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                                        
                                        Button(onClick = {
                                            viewModel.addHealthRecord(
                                                HealthRecordEntity(
                                                    animalId = animal.id,
                                                    type = rectype,
                                                    date = recdate,
                                                    details = recdetail,
                                                    cost = reccost.toDoubleOrNull() ?: 0.0
                                                )
                                            )
                                            showAddRecordForm = false
                                        }, modifier = Modifier.align(Alignment.End)) {
                                            Text("Save Medical Log")
                                        }
                                    }
                                }
                            }

                            if (personalHealth.isEmpty()) {
                                Text("No medical history reported.", color = Color.Gray)
                            } else {
                                personalHealth.forEach { record ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF9F6))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text(record.type, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                                Text(record.date, fontSize = 11.sp, color = Color.Gray)
                                            }
                                            Text(record.details, fontSize = 13.sp, color = Color.DarkGray)
                                            if (record.cost > 0.0) {
                                                Text("Cost: Rs. ${record.cost}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "growth" -> {
                            Text("Monthly Progressive Weight Tracking Plot", fontWeight = FontWeight.Bold)
                            
                            if (growthTimeline.isEmpty()) {
                                Text("No historic body mass tracking entries.", color = Color.Gray)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .background(Color(0xFFF3F0E6), RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val maxVal = (growthTimeline.maxOfOrNull { it.second } ?: 100.0).toFloat()
                                        val minVal = (growthTimeline.minOfOrNull { it.second } ?: 0.0).toFloat()
                                        val span = maxOf(maxVal - minVal, 1.0f)
                                        
                                        val width = size.width
                                        val height = size.height
                                        val ptCount = growthTimeline.size

                                        if (ptCount >= 2) {
                                            val xStep = width / (ptCount - 1)
                                            val path = androidx.compose.ui.graphics.Path()

                                            for (idx in 0 until ptCount) {
                                                val x = idx * xStep
                                                val weightVal = growthTimeline[idx].second.toFloat()
                                                val y = height - ((weightVal - minVal) / span) * (height - 20) - 10f

                                                if (idx == 0) {
                                                    path.moveTo(x, y)
                                                } else {
                                                    path.lineTo(x, y)
                                                }
                                                // Drwa small circle indices
                                                drawCircle(color = Color(0xFFC5A02B), radius = 6f, center = Offset(x, y))
                                            }

                                            drawPath(
                                                path = path,
                                                color = Color(0xFF1F4E3D),
                                                style = Stroke(width = 5f)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                growthTimeline.forEach { pt ->
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(pt.first, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        Text("${pt.second} kg", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Invoice generation dialog
    if (showInvoiceView) {
        Dialog(onDismissRequest = { showInvoiceView = false }) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("WANHER FARMS PVT LTD", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                    Text("Official Animal Valuation / Invoice Reciept", style = MaterialTheme.typography.labelSmall)
                    
                    HorizontalDivider()

                    ProfileDetailRow("Invoice Number:", "INV-2026-${animal.id}")
                    ProfileDetailRow("Animal Model/RFID:", animal.rfid)
                    ProfileDetailRow("Subspecie/Breed:", "${animal.species} • ${animal.breed}")
                    ProfileDetailRow("Acquisition Cost:", "Rs. ${animal.purchasePrice}")
                    ProfileDetailRow("Transport Freight:", "Rs. ${animal.transportCost}")
                    
                    val estVal = animal.purchasePrice * 1.35
                    ProfileDetailRow("Market Valuation Model:", "Rs. ${DecimalFormat("#,###").format(estVal)}")
                    
                    HorizontalDivider()
                    
                    Text("Authorized digital signature of Aazar Salam & Co. generated successfully.", fontSize = 10.sp, color = Color.Gray)

                    Button(
                        onClick = { showInvoiceView = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Print / Close")
                    }
                }
            }
        }
    }

    // QR Code display dialog
    if (showQRCodeView) {
        Dialog(onDismissRequest = { showQRCodeView = false }) {
            ElevatedCard(
                modifier = Modifier
                    .fillModifierCompact()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("ANIMAL RFID QR CODE", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Tag: ${animal.rfid}", style = MaterialTheme.typography.bodySmall)

                    // Draw unique visual dummy QR Code structure using standard canvas grid lines
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .background(Color.White)
                            .border(2.dp, Color.Black)
                            .padding(8.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Draw corner anchor blocks
                            drawRect(Color.Black, topLeft = Offset(0f, 0f), size = Size(35f, 35f))
                            drawRect(Color.White, topLeft = Offset(5f, 5f), size = Size(25f, 25f))
                            drawRect(Color.Black, topLeft = Offset(10f, 10f), size = Size(15f, 15f))

                            drawRect(Color.Black, topLeft = Offset(size.width - 35f, 0f), size = Size(35f, 35f))
                            drawRect(Color.White, topLeft = Offset(size.width - 30f, 5f), size = Size(25f, 25f))
                            drawRect(Color.Black, topLeft = Offset(size.width - 25f, 10f), size = Size(15f, 15f))

                            drawRect(Color.Black, topLeft = Offset(0f, size.height - 35f), size = Size(35f, 35f))
                            drawRect(Color.White, topLeft = Offset(5f, size.height - 30f), size = Size(25f, 25f))
                            drawRect(Color.Black, topLeft = Offset(10f, size.height - 25f), size = Size(15f, 15f))

                            // Draw dummy data bits
                            for (i in 0..10) {
                                drawRect(
                                    color = if ((i * 7) % 3 == 0) Color.Black else Color.Transparent,
                                    topLeft = Offset(40f + (i * 8f), 45f + (i * 2f)),
                                    size = Size(10f, 10f)
                                )
                                drawRect(
                                    color = if ((i * 4) % 2 == 0) Color.Black else Color.Transparent,
                                    topLeft = Offset(20f + (i * 5f), 75f + (i * 4f)),
                                    size = Size(8f, 14f)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { showQRCodeView = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.Medium, color = Color.Gray, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.Bold, color = Color.DarkGray, fontSize = 13.sp)
    }
}

@Composable
fun AnimalFormDialog(
    animal: AnimalEntity?,
    viewModel: FarmViewModel,
    onDismiss: () -> Unit
) {
    var rfid by remember { mutableStateOf(animal?.rfid ?: "") }
    var name by remember { mutableStateOf(animal?.name ?: "") }
    var species by remember { mutableStateOf(animal?.species ?: "Goat") }
    var breed by remember { mutableStateOf(animal?.breed ?: "") }
    var gender by remember { mutableStateOf(animal?.gender ?: "Female") }
    var color by remember { mutableStateOf(animal?.color ?: "") }
    var weight by remember { mutableStateOf(animal?.weight?.toString() ?: "") }
    var dob by remember { mutableStateOf(animal?.dateOfBirth ?: "2024-05-12") }
    var status by remember { mutableStateOf(animal?.status ?: "Healthy") }
    var purchasePrice by remember { mutableStateOf(animal?.purchasePrice?.toString() ?: "") }
    var transportCost by remember { mutableStateOf(animal?.transportCost?.toString() ?: "") }
    var sellerName by remember { mutableStateOf(animal?.sellerName ?: "") }
    var sellerContact by remember { mutableStateOf(animal?.sellerContact ?: "") }
    var notes by remember { mutableStateOf(animal?.notes ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = if (animal == null) "Register Animal Profile" else "Update Animal Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(value = rfid, onValueChange = { rfid = it }, label = { Text("RFID Tag Number / Barcode") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name / Nickname") }, modifier = Modifier.fillMaxWidth())
                    
                    // Species Selector
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Species:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        listOf("Goat", "Sheep", "Cow", "Buffalo").forEach { sp ->
                            FilterChip(selected = species == sp, onClick = { species = sp }, label = { Text(sp) })
                        }
                    }

                    OutlinedTextField(value = breed, onValueChange = { breed = it }, label = { Text("Breed (e.g. Sahiwal, Kamori)") }, modifier = Modifier.fillMaxWidth())
                    
                    // Gender selection
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Gender:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        listOf("Male", "Female").forEach { g ->
                            FilterChip(selected = gender == g, onClick = { gender = g }, label = { Text(g) })
                        }
                    }

                    OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = dob, onValueChange = { dob = it }, label = { Text("Date of Birth (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                    
                    // Health status
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Status:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        listOf("Healthy", "Pregnant", "Sick", "For Sale").forEach { s ->
                            FilterChip(selected = status == s, onClick = { status = s }, label = { Text(s) })
                        }
                    }

                    OutlinedTextField(value = purchasePrice, onValueChange = { purchasePrice = it }, label = { Text("Purchase Price (PKR)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = transportCost, onValueChange = { transportCost = it }, label = { Text("Transportation Cost (PKR)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = sellerName, onValueChange = { sellerName = it }, label = { Text("Seller Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sellerContact, onValueChange = { sellerContact = it }, label = { Text("Seller Contact") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Special Notes") }, modifier = Modifier.fillMaxWidth())
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            viewModel.saveAnimal(
                                AnimalEntity(
                                    id = animal?.id ?: 0,
                                    rfid = rfid,
                                    name = name,
                                    species = species,
                                    breed = breed,
                                    gender = gender,
                                    color = color,
                                    weight = weight.toDoubleOrNull() ?: 0.0,
                                    dateOfBirth = dob,
                                    status = status,
                                    purchasePrice = purchasePrice.toDoubleOrNull() ?: 0.0,
                                    transportCost = transportCost.toDoubleOrNull() ?: 0.0,
                                    sellerName = sellerName,
                                    sellerContact = sellerContact,
                                    notes = notes,
                                    growthHistory = animal?.growthHistory ?: "2026-06-09:${weight.toDoubleOrNull() ?: 0.0}"
                                )
                            )
                            onDismiss()
                        }
                    ) {
                        Text("Save Profile")
                    }
                }
            }
        }
    }
}

@Composable
fun FinancialsScreen(viewModel: FarmViewModel) {
    val financialsList by viewModel.financials.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (currentUser != null && currentUser?.role != "Worker") {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Log Transaction")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = viewModel.t("financials"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (currentUser?.role == "Worker") {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F8))) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Red)
                        Text(viewModel.t("role_restricted"), color = Color.Red, fontSize = 12.sp)
                    }
                }
            }

            // Interactive ledger logs list
            if (financialsList.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No transactions logged in cashflow tracker.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(financialsList) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                if (item.type == "Expense") Color(0xFFFFF0F0) else Color(0xFFEDFBF0),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (item.type == "Expense") Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                            contentDescription = item.type,
                                            tint = if (item.type == "Expense") Color.Red else Color(0xFF4CAF50),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(item.category, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(item.description, fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(item.date, fontSize = 11.sp, color = Color.LightGray)
                                    }
                                }

                                Text(
                                    text = "${if (item.type == "Expense") "-" else "+"} Rs. ${DecimalFormat("#,###").format(item.amount)}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = if (item.type == "Expense") Color.Red else Color(0xFF388E3C)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            var transType by remember { mutableStateOf("Expense") }
            var transCategory by remember { mutableStateOf("Feed") }
            var transAmount by remember { mutableStateOf("") }
            var transDate by remember { mutableStateOf("2026-06-09") }
            var transDetail by remember { mutableStateOf("") }
            var contact by remember { mutableStateOf("") }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Record Finance Entries", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FilterChip(selected = transType == "Expense", onClick = { transType = "Expense" }, label = { Text("Expense") })
                        FilterChip(selected = transType == "Revenue", onClick = { transType = "Revenue" }, label = { Text("Revenue") })
                    }

                    OutlinedTextField(value = transAmount, onValueChange = { transAmount = it }, label = { Text("Amount (PKR)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = transDate, onValueChange = { transDate = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = transDetail, onValueChange = { transDetail = it }, label = { Text("Transaction details") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact Client/Supplier") }, modifier = Modifier.fillMaxWidth())

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.addFinancial(
                                    FinancialEntity(
                                        type = transType,
                                        category = transCategory,
                                        amount = transAmount.toDoubleOrNull() ?: 0.0,
                                        date = transDate,
                                        description = transDetail,
                                        contactName = contact
                                    )
                                )
                                showAddDialog = false
                            }
                        ) {
                            Text("Record Transaction")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryScreen(viewModel: FarmViewModel) {
    val inventoriesList by viewModel.inventories.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (currentUser != null && currentUser?.role != "Worker") {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = viewModel.t("inventory"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (inventoriesList.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No inventory tracked in system stocks.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(inventoriesList) { item ->
                        val isDanger = item.quantity <= item.minStockAlert
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (isDanger) Color(0xFFFFF0F0) else Color.White),
                            border = BorderStroke(1.dp, if (isDanger) Color.Red else Color(0xFFEEEEEE))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(item.itemName, fontWeight = FontWeight.Bold, color = if (isDanger) Color.Red else Color.Unspecified)
                                    Text("Category: ${item.category}", fontSize = 11.sp, color = Color.Gray)
                                    Text("Supplier: ${item.supplierName} (${item.supplierContact})", fontSize = 11.sp, color = Color.Gray)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${item.quantity} ${item.unit}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = if (isDanger) Color.Red else MaterialTheme.colorScheme.primary
                                    )
                                    if (isDanger) {
                                        Text(viewModel.t("low_inventory_alert"), color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            var itemName by remember { mutableStateOf("") }
            var category by remember { mutableStateOf("Feed") }
            var quantity by remember { mutableStateOf("") }
            var unit by remember { mutableStateOf("kg") }
            var minStock by remember { mutableStateOf("") }
            var supplierName by remember { mutableStateOf("") }
            var supplierContact by remember { mutableStateOf("") }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Add Stockpile Goods", fontWeight = FontWeight.Bold)

                    OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = { Text("Item Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Volume Quantity") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit of Measurement (e.g. kg, vials)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = minStock, onValueChange = { minStock = it }, label = { Text("Min Stock Alert Threshold") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = supplierName, onValueChange = { supplierName = it }, label = { Text("Supplier Business Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = supplierContact, onValueChange = { supplierContact = it }, label = { Text("Supplier Call Contact") }, modifier = Modifier.fillMaxWidth())

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("Close")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.addInventory(
                                    InventoryEntity(
                                        itemName = itemName,
                                        category = category,
                                        quantity = quantity.toDoubleOrNull() ?: 0.0,
                                        unit = unit,
                                        minStockAlert = minStock.toDoubleOrNull() ?: 0.0,
                                        supplierName = supplierName,
                                        supplierContact = supplierContact
                                    )
                                )
                                showAddDialog = false
                            }
                        ) {
                            Text("Enroll Goods")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeesScreen(viewModel: FarmViewModel) {
    val employeesList by viewModel.employees.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (currentUser != null && currentUser?.role == "Admin") {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Employee")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = viewModel.t("employees"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (employeesList.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No payroll employees found.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(employeesList) { emp ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(emp.name, fontWeight = FontWeight.Bold)
                                        Text("Position: ${emp.role}", fontSize = 11.sp, color = Color.Gray)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                when(emp.attendanceToday) {
                                                    "Present" -> Color(0xFFD4EDDA)
                                                    "Absent" -> Color(0xFFF8D7DA)
                                                    else -> Color(0xFFFFF3CD)
                                                },
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = emp.attendanceToday,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when(emp.attendanceToday) {
                                                "Present" -> Color(0xFF155724)
                                                "Absent" -> Color(0xFF721C24)
                                                else -> Color(0xFF856404)
                                            }
                                        )
                                    }
                                }

                                Text(emp.performanceNotes, fontSize = 12.sp, color = Color.Gray)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Attendance toggle buttons
                                    Button(
                                        onClick = { viewModel.updateEmployeeAttendance(emp, "Present") },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Present", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.updateEmployeeAttendance(emp, "Absent") },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Absent", fontSize = 11.sp)
                                    }

                                    if (currentUser?.role == "Admin") {
                                        Button(
                                            onClick = {
                                                viewModel.recordSalaryPayout(emp)
                                                Toast.makeText(viewModel.getApplication(), "Payout Success Logged in ledger!", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                            modifier = Modifier.weight(1.2f)
                                        ) {
                                            Text("Wages", fontSize = 11.sp, color = Color.DarkGray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            var name by remember { mutableStateOf("") }
            var role by remember { mutableStateOf("Laborer") }
            var salary by remember { mutableStateOf("") }
            var notes by remember { mutableStateOf("") }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Enroll Employee Profile", fontWeight = FontWeight.Bold)

                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role Position") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = salary, onValueChange = { salary = it }, label = { Text("Base monthly Salary (PKR)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Duty Notes") }, modifier = Modifier.fillMaxWidth())

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.addEmployee(
                                    EmployeeEntity(
                                        name = name,
                                        role = role,
                                        salary = salary.toDoubleOrNull() ?: 0.0,
                                        performanceNotes = notes
                                    )
                                )
                                showAddDialog = false
                            }
                        ) {
                            Text("Enroll")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HoneyScreen(viewModel: FarmViewModel) {
    val honeyBatchesList by viewModel.honeyBatches.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var showAddBatchDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (currentUser != null && currentUser?.role != "Worker") {
                FloatingActionButton(
                    onClick = { showAddBatchDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Log Batch")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "${viewModel.t("honey")} - Sidr & Acacia Stocks",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (honeyBatchesList.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No production batches listed yet.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(honeyBatchesList) { batch ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Batch ID: ${batch.batchNumber}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(batch.size, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                Text("Yield production date: ${batch.productionDate} • Expiry: ${batch.expiryDate}", fontSize = 11.sp, color = Color.Gray)
                                Text("Retail price: Rs. ${batch.salePrice} • Wholesale cost: Rs. ${batch.costPerUnit}", fontSize = 11.sp, color = Color.Gray)

                                LinearProgressIndicator(
                                    progress = { 
                                        val total = maxOf(batch.quantity, 1)
                                        (batch.salesCount.toFloat() / total.toFloat()) 
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = Color(0xFFC5A02B),
                                    trackColor = Color(0xFFF4EDD5)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Sales progress: ${batch.salesCount} / ${batch.quantity} Jars", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    
                                    if (currentUser != null && currentUser?.role != "Worker" && batch.salesCount < batch.quantity) {
                                        Button(
                                            onClick = {
                                                viewModel.recordHoneySale(batch, 5, "Distributor Outlet Lahore")
                                                Toast.makeText(viewModel.getApplication(), "5 Bottles sold successfully!", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text("Sell 5 Jars", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddBatchDialog) {
        Dialog(onDismissRequest = { showAddBatchDialog = false }) {
            var batchNo by remember { mutableStateOf("") }
            var size by remember { mutableStateOf("500g") }
            var qty by remember { mutableStateOf("") }
            var cost by remember { mutableStateOf("") }
            var salePrice by remember { mutableStateOf("") }
            var distributor by remember { mutableStateOf("") }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Register Honey Output Batch", fontWeight = FontWeight.Bold)

                    OutlinedTextField(value = batchNo, onValueChange = { batchNo = it }, label = { Text("Batch Number Identifier") }, modifier = Modifier.fillMaxWidth())
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FilterChip(selected = size == "250g", onClick = { size = "250g" }, label = { Text("250g") })
                        FilterChip(selected = size == "500g", onClick = { size = "500g" }, label = { Text("500g") })
                        FilterChip(selected = size == "1kg", onClick = { size = "1kg" }, label = { Text("1kg") })
                    }

                    OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("Output Jars count") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Unit production cost (PKR)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = salePrice, onValueChange = { salePrice = it }, label = { Text("Customer Sales Price (PKR)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = distributor, onValueChange = { distributor = it }, label = { Text("Distributor Contract name") }, modifier = Modifier.fillMaxWidth())

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddBatchDialog = false }) {
                            Text("Dismiss")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.addHoneyBatch(
                                    HoneyBatchEntity(
                                        batchNumber = batchNo,
                                        productionDate = "2026-06-09",
                                        expiryDate = "2028-06-09",
                                        quantity = qty.toIntOrNull() ?: 0,
                                        size = size,
                                        costPerUnit = cost.toDoubleOrNull() ?: 0.0,
                                        salePrice = salePrice.toDoubleOrNull() ?: 0.0,
                                        distributorName = distributor
                                    )
                                )
                                showAddBatchDialog = false
                            }
                        ) {
                            Text("Enroll")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(viewModel: FarmViewModel) {
    val alertsList by viewModel.alerts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = viewModel.t("notifications"),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        if (alertsList.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("Zero active livestock or warehouse alert flags recorded.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(alertsList) { alert ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when(alert.severity) {
                                "Severe" -> Color(0xFFFFEBEE)
                                "High" -> Color(0xFFFFF3E0)
                                else -> Color(0xFFE8F5E9)
                            }
                        ),
                        border = BorderStroke(
                            1.dp,
                            when(alert.severity) {
                                "Severe" -> Color.Red
                                "High" -> Color(0xFFE65100)
                                else -> Color(0xFF2E7D32)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = when(alert.type) {
                                    "Inventory" -> Icons.Default.Settings
                                    "Pregnancy" -> Icons.Default.Favorite
                                    else -> Icons.Default.Warning
                                },
                                contentDescription = alert.type,
                                tint = when(alert.severity) {
                                    "Severe" -> Color.Red
                                    "High" -> Color(0xFFE65100)
                                    else -> Color(0xFF2E7D32)
                                },
                                modifier = Modifier.size(24.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (viewModel.language.value == "en") alert.titleEn else alert.titleUr,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )
                                Text(
                                    text = if (viewModel.language.value == "en") alert.descEn else alert.descUr,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.DarkGray.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Custom design helpers
fun Modifier.fillModifierCompact(): Modifier = this.fillMaxWidth(0.95f)
