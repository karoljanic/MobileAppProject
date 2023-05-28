package org.mobileapp.service.enums

enum class ServiceAction(val str: String) {
    START_SERVICE("ACTION_START_SERVICE"),
    RESUME_SERVICE("ACTION_RESUME_SERVICE"),
    PAUSE_SERVICE("ACTION_PAUSE_SERVICE"),
    STOP_SERVICE("ACTION_STOP_SERVICE"),
}