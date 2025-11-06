    package com.example.healthtracker

    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.compose.runtime.*
    import androidx.lifecycle.viewmodel.compose.viewModel
    import com.example.healthtracker.databese.AppDatabase
    import com.example.healthtracker.Repositrory.UserRepository
    import com.example.healthtracker.Viewmodel.LoginViewModel
    import com.example.healthtracker.Viewmodel.LoginViewModelFactory
    import com.example.healthtracker.Viewmodel.SignupViewModel
    import com.example.healthtracker.Viewmodel.SignupViewModelFactory
    import com.example.healthtracker.ui.theme.HealthTrackerTheme

    // Navigation Destinations
    sealed class Destination
    object DestinationLogin : Destination()
    object DestinationSignup : Destination()
    object DestinationHabit : Destination() // nouvel écran

    class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContent {
                HealthTrackerTheme {

                    // ✅ Initialisation Room + Repository
                    val database = AppDatabase.getDatabase(this)
                    val repository = UserRepository(database.userDao())

                    // ✅ Initialisation ViewModels
                    val signupViewModel: SignupViewModel = viewModel(factory = SignupViewModelFactory(
                        repository
                    )
                    )
                    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(
                        repository
                    )
                    )

                    // ✅ Navigation state
                    val backStack = remember { mutableStateListOf<Destination>(DestinationLogin) }

                    when (backStack.last()) {
                        is DestinationLogin ->
                            LoginScreen(
                                viewModel = loginViewModel,
                                onNavigateToSignup = { backStack.add(DestinationSignup) },
                                onLoginSuccess = { backStack.add(DestinationHabit) }
                            )

                        is DestinationSignup ->
                            SignupScreen(
                                viewModel = signupViewModel,
                                onBack = { backStack.removeLastOrNull() },
                                onNavigateToSignin = { backStack.removeLastOrNull() }
                            )

                        is DestinationHabit ->
                            HabitScreen()
                    }
                }
            }
        }


    }
