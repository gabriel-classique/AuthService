package com.xcvi.firebasesample

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xcvi.firebasesample.ui.theme.FirebaseSampleTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
                                                    navController.navigate(HomeScreen(uid = user.uid, email = user.email))
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
                                                    navController.navigate(HomeScreen(uid = user.uid, email = user.email))
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
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ){
                                Text(text = "Email: ${args.email}\nuid: ${args.uid}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Serializable
data object AuthScreen

@Serializable
data class HomeScreen(
    val uid: String,
    val email: String
)