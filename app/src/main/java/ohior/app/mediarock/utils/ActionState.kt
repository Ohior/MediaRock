package ohior.app.mediarock.utils

sealed class ActionState {
    data object Loading : ActionState()
    data object Success : ActionState()
    data class Fail(val message: String) : ActionState()
    data object None : ActionState()
}