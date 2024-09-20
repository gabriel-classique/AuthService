package com.xcvi.firebasesample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.xcvi.firebasesample.ui.theme.FirebaseSampleTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FirebaseSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = AuthScreen){
                        composable<AuthScreen> {
                            val viewModel: AuthViewModel = hiltViewModel()

                            if(viewModel.user != null){
                                val user = viewModel.user!!
                                navController.clearAndNavigate(HomeScreen(uid = user.uid, email = user.email))
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                TextField(value = viewModel.email, onValueChange = { viewModel.onEmailInput(it) })
                                TextField(value = viewModel.password, onValueChange = { viewModel.onPasswordInput(it) })
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    TextButton(
                                        onClick = {
                                            viewModel.register(
                                                onSuccess = { user ->
                                                    navController.clearAndNavigate(HomeScreen(uid = user.uid, email = user.email))
                                                    Toast.makeText(applicationContext, "Successful", Toast.LENGTH_LONG).show()
                                                },
                                                onFailure = {
                                                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                                                }
                                            )
                                        }
                                    ) {
                                        Text(text = "Register")
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.login(
                                                onSuccess = { user ->
                                                    navController.clearAndNavigate(HomeScreen(uid = user.uid, email = user.email))
                                                    Toast.makeText(applicationContext, "Successful", Toast.LENGTH_LONG).show()
                                                },
                                                onFailure = {
                                                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                                                }
                                            )
                                        }
                                    ) {
                                        Text(text = "Login")
                                    }
                                }
                            }
                        }
                        composable<HomeScreen> {

                            val args = it.toRoute<HomeScreen>()
                            val viewModel: HomeViewModel = hiltViewModel()

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ){

                                Text(text = "Email: ${args.email}\nuid: ${args.uid}")
                                Button(
                                    onClick = {
                                        viewModel.saveData {
                                            Toast.makeText(applicationContext, "SUCCESS", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Text(text = "Save")
                                }
                                Button(
                                    onClick = {
                                        viewModel.getData {
                                            Toast.makeText(applicationContext,it.content , Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Text(text = "Get")
                                }
                                Button(
                                    onClick = {
                                        navController.clearAndNavigate(AuthScreen)
                                        viewModel.logout()
                                    }
                                ) {
                                    Text(text = "Logout")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


fun <R : Any> NavHostController.clearAndNavigate(route: R) {
    navigate(route = route) {
        popUpTo(graph.startDestinationId) { inclusive = true }
    }
    graph.setStartDestination(route)
}


@Serializable
data object AuthScreen

@Serializable
data class HomeScreen(
    val uid: String,
    val email: String
)