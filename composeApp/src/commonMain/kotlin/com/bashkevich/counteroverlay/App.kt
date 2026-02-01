package com.bashkevich.counteroverlay

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.bashkevich.counteroverlay.di.coreModule
import com.bashkevich.counteroverlay.di.counterModule
import com.bashkevich.counteroverlay.di.platformModule
import com.bashkevich.counteroverlay.navigation.AddCounterDialogRoute
import com.bashkevich.counteroverlay.navigation.CounterDetailsRoute
import com.bashkevich.counteroverlay.navigation.CounterListRoute
import com.bashkevich.counteroverlay.navigation.platformSpecificRoutes
import com.bashkevich.counteroverlay.screens.addcounterdialog.AddCounterDialogScreen
import com.bashkevich.counteroverlay.screens.addcounterdialog.AddCounterDialogViewModel
import com.bashkevich.counteroverlay.screens.counterdetails.CounterDetailsScreen
import com.bashkevich.counteroverlay.screens.counterdetails.CounterDetailsViewModel
import com.bashkevich.counteroverlay.screens.counterlist.CounterListScreen
import com.bashkevich.counteroverlay.screens.counterlist.CounterListViewModel
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App(navController: NavHostController = rememberNavController()) {
    KoinMultiplatformApplication(config = KoinConfiguration {
        modules(
            coreModule,
            counterModule, platformModule
        )
    }) {
        MaterialTheme {
            NavHost(navController = navController, startDestination = CounterListRoute) {
                composable<CounterListRoute> {
                    val counterListViewModel = koinViewModel<CounterListViewModel>()
                    CounterListScreen(
                        viewModel = counterListViewModel,
                        onCounterClick = { counter ->
                            navController.navigate(
                                CounterDetailsRoute(
                                    counter.id
                                )
                            )
                        },
                        onCounterAdd = {
                            navController.navigate(AddCounterDialogRoute)
                        }
                    )
                }
                composable<CounterDetailsRoute> {
                    val counterDetailsViewModel = koinViewModel<CounterDetailsViewModel>()

                    CounterDetailsScreen(
                        viewModel = counterDetailsViewModel
                    )
                }
                dialog<AddCounterDialogRoute>(
                ) {
                    val addCounterDialogViewModel = koinViewModel<AddCounterDialogViewModel>()

                    AddCounterDialogScreen(
                        viewModel = addCounterDialogViewModel,
                        onDismissRequest = { navController.navigateUp() })
                }
                platformSpecificRoutes()
            }
        }
    }
}