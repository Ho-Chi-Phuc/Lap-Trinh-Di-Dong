package com.example.lab8.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lab8.ui.screens.ForgotPasswordScreen
import com.example.lab8.ui.screens.HomeScreen
import com.example.lab8.ui.screens.SignInScreen
import com.example.lab8.ui.screens.SignUpScreen
import com.example.lab8.ui.viewmodel.AuthViewModel

/** Xóa toàn bộ back stack rồi mở [target] - nhấn Back sẽ thoát app, không quay lại màn hình cũ. */
private fun NavHostController.navigateAndClearAll(target: Screen) {
    navigate(target.route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // viewModel() ở đây gắn với Activity, nên các màn hình dùng chung một AuthViewModel.
    val authViewModel: AuthViewModel = viewModel()
    val state by authViewModel.uiState.collectAsState()

    // Màn hình đầu tiên phụ thuộc vào phiên đăng nhập THẬT của Firebase (currentUser).
    // remember: chỉ quyết định một lần khi mở app.
    val startDestination = remember {
        if (authViewModel.isLoggedIn) Screen.Home.route else Screen.SignIn.route
    }

    // Bảo vệ Home: nếu mất phiên đăng nhập (đăng xuất, tài khoản bị xóa...) thì quay về Đăng nhập.
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    LaunchedEffect(state.isLoggedIn, currentRoute) {
        if (!state.isLoggedIn && currentRoute == Screen.Home.route) {
            navController.navigateAndClearAll(Screen.SignIn)
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.SignIn.route) {
            SignInScreen(
                state = state,
                onSignIn = { email, password ->
                    authViewModel.signIn(email, password) {
                        // Chỉ chạy khi Firebase xác thực thành công.
                        navController.navigateAndClearAll(Screen.Home)
                    }
                },
                onGoToSignUp = {
                    authViewModel.clearMessage()
                    navController.navigate(Screen.SignUp.route) { launchSingleTop = true }
                },
                onGoToForgotPassword = {
                    authViewModel.clearMessage()
                    navController.navigate(Screen.ForgotPassword.route) { launchSingleTop = true }
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                state = state,
                onSignUp = { email, password, confirm ->
                    authViewModel.signUp(email, password, confirm) {
                        // Đăng ký xong -> về màn hình Đăng nhập (thông báo thành công hiện ở đó).
                        navController.navigateAndClearAll(Screen.SignIn)
                    }
                },
                onBackToSignIn = {
                    authViewModel.clearMessage()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                state = state,
                onSendReset = { email -> authViewModel.sendPasswordReset(email) },
                onBackToSignIn = {
                    authViewModel.clearMessage()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            // Mỗi lần vào Home, tải lại thông tin người dùng từ server (cập nhật emailVerified).
            LaunchedEffect(Unit) { authViewModel.refreshUser() }

            HomeScreen(
                state = state,
                onSignOut = {
                    authViewModel.signOut() // gọi FirebaseAuth.signOut()
                    navController.navigateAndClearAll(Screen.SignIn)
                },
                onRecheckVerification = { authViewModel.refreshUser(showResult = true) },
                onResendVerification = { authViewModel.resendVerificationEmail() }
            )
        }
    }
}
