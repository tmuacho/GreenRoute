package com.greenroute.app.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.greenroute.app.GreenRouteApp
import com.greenroute.app.data.repository.PlaceRepository
import com.greenroute.app.data.repository.RouteRepository
import com.greenroute.app.data.repository.UserRepository
import com.greenroute.app.ui.screens.detail.RouteDetailScreen
import com.greenroute.app.ui.screens.home.HomeScreen
import com.greenroute.app.ui.screens.profile.ProfileScreen
import com.greenroute.app.ui.screens.recent.RecentScreen
import com.greenroute.app.ui.screens.saved.SavedScreen
import com.greenroute.app.ui.screens.search.SearchScreen
import com.greenroute.app.viewmodel.*

object Routes {
    const val HOME = "home"
    const val SAVED = "saved"
    const val RECENT = "recent"
    const val SEARCH = "search"
    const val PROFILE = "profile"
    const val ROUTE_DETAIL = "route_detail"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    routeRepository: RouteRepository,
    userRepository: UserRepository,
    placeRepository: PlaceRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(routeRepository, userRepository)
            )
            HomeScreen(
                viewModel = viewModel,
                onSearchClick = { navController.navigate(Routes.SEARCH) },
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onRouteClick = { routeId -> navController.navigate("${Routes.ROUTE_DETAIL}/$routeId") }
            )
        }

        composable(Routes.PROFILE) {
            val viewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.provideFactory(userRepository)
            )
            ProfileScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.SAVED) {
            val viewModel: SavedViewModel = viewModel(
                factory = SavedViewModel.provideFactory(routeRepository)
            )
            SavedScreen(
                viewModel = viewModel,
                onRouteClick = { routeId -> navController.navigate("${Routes.ROUTE_DETAIL}/$routeId") }
            )
        }

        composable(Routes.RECENT) {
            val viewModel: RecentViewModel = viewModel(
                factory = RecentViewModel.provideFactory(routeRepository)
            )
            RecentScreen(
                viewModel = viewModel,
                onRouteClick = { routeId ->
                    navController.navigate("${Routes.ROUTE_DETAIL}/$routeId")
                }
            )
        }

        composable(Routes.SEARCH) {
            val context = LocalContext.current
            val app = context.applicationContext as GreenRouteApp
            val viewModel: SearchViewModel = viewModel(
                factory = SearchViewModel.provideFactory(
                    application = context.applicationContext as Application,
                    routeRepository = routeRepository,
                    userRepository = userRepository,
                    placeRepository = placeRepository,
                    directionsRepository = app.directionsRepository
                )
            )
            SearchScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToRecent = {
                    navController.navigate(Routes.RECENT) {
                        popUpTo(Routes.HOME)
                    }
                },
                onNavigateToDetail = { routeId ->
                    navController.navigate("${Routes.ROUTE_DETAIL}/$routeId")
                }
            )
        }

        composable(
            route = "${Routes.ROUTE_DETAIL}/{routeId}",
            arguments = listOf(navArgument("routeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getInt("routeId") ?: return@composable
            val viewModel: RouteDetailViewModel = viewModel(
                factory = RouteDetailViewModel.provideFactory(routeRepository, routeId)
            )
            RouteDetailScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
