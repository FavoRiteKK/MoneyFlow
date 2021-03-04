package com.prof18.moneyflow.features.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.prof18.moneyflow.Screen
import com.prof18.moneyflow.features.home.components.HeaderNavigator
import com.prof18.moneyflow.features.home.components.HomeRecap
import com.prof18.moneyflow.ui.components.Loader
import com.prof18.moneyflow.ui.components.TransactionCard
import com.prof18.moneyflow.ui.style.AppMargins
import presentation.home.HomeModel
import timber.log.Timber


@Composable
fun HomeScreen(navController: NavController) {

    val homeViewModel = viewModel<HomeViewModel>(
        factory = HomeViewModelFactory()
    )

    val homeModel by homeViewModel.homeLiveData.observeAsState()


    when (homeModel) {
        is HomeModel.Loading -> Loader()
        is HomeModel.HomeState -> {

            val homeState = (homeModel as HomeModel.HomeState)

            Column(modifier = Modifier.padding(AppMargins.small)) {

                TextButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        navController.navigate(Screen.AddTransactionScreen.name)
                    }) {
                    Text("Add transaction")
                }
                HomeRecap(homeState.balanceRecap)
                HeaderNavigator()

                if (homeState.latestTransactions.isEmpty()) {

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "¯\\_(ツ)_/¯",
                                modifier = Modifier
                                    .padding(bottom = AppMargins.small),
                                style = MaterialTheme.typography.h6
                            )

                            Text(
                                "Your wallet is emptyyy",
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }
                } else {


                    LazyColumn {
                        items(homeState.latestTransactions) { transaction ->
                            val (showTransactionMenu, setShowTransactionMenu) = remember {
                                mutableStateOf(
                                    false
                                )
                            }
                            // TODO: fix dropdown menu
/*
                            DropdownMenu(
                                toggle = {
                                    TransactionCard(
                                        transaction = transaction,
                                        onClick = {
                                            Timber.d("onClick")
                                        },
                                        onLongPress = {
                                            setShowTransactionMenu(true)
                                        })
                                    Divider()
                                },
                                expanded = showTransactionMenu,
                                onDismissRequest = { setShowTransactionMenu(false) },
                            ) {
                                DropdownMenuItem(onClick = {
                                    homeViewModel.deleteTransaction(transaction.id)
                                    setShowTransactionMenu(false)
                                }) {
                                    Text("Delete")
                                }
                            }*/
                        }
                    }
                }
            }
        }
        is HomeModel.Error -> Text("Something wrong here!")
    }
}

