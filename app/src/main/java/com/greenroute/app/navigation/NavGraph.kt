package com.greenroute.app.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.greenroute.app.data.repository.RouteRepository
import com.greenroute.app.data.repository.UserRepository
import com.greenroute.app.ui.screens.home.HomeScreen
import com.greenroute.app.ui.screens.profile.ProfileScreen
import com.greenroute.app.ui.screens.recent.RecentScreen
import com.greenroute.app.ui.screens.saved.SavedScreen
import com.greenroute.app.ui.screens.search.SearchScreen
import com.greenroute.app.viewmodel.*

/**
 * Navigation routes for the app.
 */
object Routes {
    const val HOME = "home"
    const val SAVED = "saved"
    const val RECENT = "recent"
    const val SEARCH = "search"
    const val PROFILE = "profile"
}

/**
 * Navigation graph for the GreenRoute app.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    routeRepository: RouteRepository,
    userRepository: UserRepository,
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
                onProfileClick = { navController.navigate(Routes.PROFILE) }
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
            SavedScreen(viewModel = viewModel)
        }

        composable(Routes.RECENT) {
            val viewModel: RecentViewModel = viewModel(
                factory = RecentViewModel.provideFactory(routeRepository)
            )
            RecentScreen(viewModel = viewModel)
        }

        composable(Routes.SEARCH) {
            val context = LocalContext.current
            val viewModel: SearchViewModel = viewModel(
                factory = SearchViewModel.provideFactory(
                    application = context.applicationContext as Application,
                    routeRepository = routeRepository,
                    userRepository = userRepository
                )
            )
            SearchScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToRecent = {
                    navController.navigate(Routes.RECENT) {
                        // Pop up to home to avoid back stack loop
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }
    }
}
