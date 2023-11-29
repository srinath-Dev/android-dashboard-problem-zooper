package zuper.dev.android.dashboard.navigation

sealed class Screen(val route: String) {
    object Dashboard: Screen(route = "dashboard")
    object Jobs: Screen(route = "jobs")
}