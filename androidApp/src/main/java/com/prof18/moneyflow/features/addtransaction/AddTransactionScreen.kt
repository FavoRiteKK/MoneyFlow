package com.prof18.moneyflow.features.addtransaction

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.prof18.moneyflow.R
import com.prof18.moneyflow.Screen
import com.prof18.moneyflow.features.addtransaction.components.DatePickerDialog
import com.prof18.moneyflow.features.addtransaction.components.IconTextClickableRow
import com.prof18.moneyflow.features.addtransaction.components.MFTextInput
import com.prof18.moneyflow.features.addtransaction.components.TransactionTypeChooser
import com.prof18.moneyflow.features.addtransaction.data.TransactionTypeRadioItem
import com.prof18.moneyflow.ui.components.MFTopBar
import com.prof18.moneyflow.ui.style.AppMargins
import com.prof18.moneyflow.ui.style.bigTextColor
import com.prof18.moneyflow.ui.style.textColor
import data.db.model.TransactionType

@Composable
fun AddTransactionScreen(
    navController: NavController,
    categoryName: String?,
    categoryId: Long?,
    @DrawableRes categoryIcon: Int?
) {

    val viewModel = viewModel<AddTransactionViewModel>(
        factory = AddTransactionViewModelFactory()
    )

    val (showDatePickerDialog, setShowedDatePickerDialog) = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MFTopBar(
                topAppBarText = "Add transaction",
                actionTitle = "Save",
                onBackPressed = {
                    navController.popBackStack()
                },
                onActionClicked = {
                    viewModel.addTransaction(categoryId!!)
                    navController.popBackStack()
                },
                actionEnabled = categoryId != null && viewModel.amountText.isNotEmpty()
            )
        },
        content = {
            Column {
                DatePickerDialog(
                    showDatePickerDialog,
                    setShowedDatePickerDialog,
                    onYearSelected = {
                        viewModel.setYearNumber(it)
                    },
                    onMonthSelected = {
                        viewModel.setMonthNumber(it)
                    },
                    onDaySelected = {
                        viewModel.setDayNumber(it)
                    },
                    onSave = {
                        viewModel.saveDate()
                    },

                    )
                TransactionTypeChooser(
                    possibleAnswerStringId = listOf(
                        TransactionTypeRadioItem(
                            R.string.transaction_type_income,
                            TransactionType.INCOME
                        ),
                        TransactionTypeRadioItem(
                            R.string.transaction_type_outcome,
                            TransactionType.OUTCOME
                        )
                    ),
                    answer = viewModel.selectedTransactionType,
                    onAnswerSelected = {
                        viewModel.selectedTransactionType = it
                    },
                    modifier = Modifier.padding(
                        start = AppMargins.regular,
                        end = AppMargins.regular,
                        top = AppMargins.regular
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        start = AppMargins.regular,
                        end = AppMargins.regular,
                        top = AppMargins.regular
                    )

                ) {


                    Spacer(Modifier.width(AppMargins.small))

                    MFTextInput(
                        text = viewModel.amountText,
                        textStyle = MaterialTheme.typography.h3.copy(color = bigTextColor()),
                        label = "0.00",
                        leadingIcon = {
                            // TODO: change based on the currency
                            Icon(
                                painter = painterResource(id = R.drawable.ic_euro_sign),
                                contentDescription = null,
                                tint = textColor()
                            )
                        },
                        onTextChange = {
                            viewModel.amountText = it
                        },
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                MFTextInput(
                    text = viewModel.descriptionText ?: "",
                    textStyle = MaterialTheme.typography.body1.copy(color = textColor()),
                    label = stringResource(id = R.string.description),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = null,
                            tint = textColor()
                        )
                    },
                    onTextChange = {
                        viewModel.descriptionText = it
                    },
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = AppMargins.regular,
                            end = AppMargins.regular,
                            top = AppMargins.medium
                        )
                )

                IconTextClickableRow(
                    onClick = {
                        navController.navigate("${Screen.CategoriesScreen.name}/true")
                    },
                    text = categoryName ?: stringResource(id = R.string.select_category),
                    iconId = categoryIcon ?: R.drawable.ic_question_circle,
                    isSomethingSelected = categoryName != null,
                    modifier = Modifier.padding(
                        start = AppMargins.regular,
                        end = AppMargins.regular,
                        top = AppMargins.medium
                    )
                )

                IconTextClickableRow(
                    onClick = {
                        setShowedDatePickerDialog(true)
                    },
                    text = viewModel.dateLabel ?: stringResource(id = R.string.today),
                    iconId = R.drawable.ic_calendar,
                    modifier = Modifier.padding(
                        start = AppMargins.regular,
                        end = AppMargins.regular,
                        top = AppMargins.medium,
                        bottom = AppMargins.regular
                    )
                )
            }
        }
    )
}