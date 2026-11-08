package com.vayulabs.ripple.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Home : Screen("home")
    object Breathe : Screen("breathe")
    object SensoryHub : Screen("sensory_hub")
    object FluidCanvas : Screen("sensory_fluid")
    object WorryStone : Screen("sensory_stone")
    object BubblePop : Screen("sensory_bubble")
    object Kaleidoscope : Screen("sensory_kaleidoscope")
    object ColorSort : Screen("sensory_colorsort")
    object Quotes : Screen("quotes")
    object Sounds : Screen("sounds")
    object ThoughtDissolver : Screen("thought_dissolver")
    object Grounding54321 : Screen("grounding_54321")
    object ZenSand : Screen("sensory_zensand")
    object SingingBowl : Screen("sensory_singingbowl")
}
