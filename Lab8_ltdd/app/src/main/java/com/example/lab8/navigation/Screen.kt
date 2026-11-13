package com.example.lab8.navigation

/** Danh sách các màn hình và đường dẫn (route) tương ứng. */
sealed class Screen(val route: String) {
    data object SignIn : Screen("sign_in")
    data object SignUp : Screen("sign_up")
    data object ForgotPassword : Screen("forgot_password")
    data object Home : Screen("home")
}
