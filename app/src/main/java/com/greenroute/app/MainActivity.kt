package com.greenroute.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.greenroute.app.navigation.NavGraph
import com.greenroute.app.navigation.Routes
import com.greenroute.app.ui.components.BottomNavBar
import com.greenroute.app.ui.theme.GreenRouteTheme

/**
 * Main activity for the GreenRoute app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as GreenRouteApp

        setContent {
            GreenRouteTheme {
                GreenRouteApp(
                    routeRepository = app.routeRepository,
                    userRepository = app.userRepository
                )
            }
        }
    }
}

/**
 * Main composable for the GreenRoute app.
 */
@Composable
fun GreenRouteApp(
    routeRepository: com.greenroute.app.data.repository.RouteRepository,
    userRepository: com.greenroute.app.data.repository.UserRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.HOME

    // Routes where bottom nav should be visible
    val bottomNavRoutes = listOf(Routes.HOME, Routes.SAVED, Routes.RECENT)
    val showBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomNav) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Avoid building up a large stack of destinations
                            popUpTo(Routes.HOME) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            routeRepository = routeRepository,
            userRepository = userRepository,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
