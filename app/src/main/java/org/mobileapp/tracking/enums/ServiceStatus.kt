package org.mobileapp.tracking.enums


enum class ServiceStatus {
    IS_RUNNING,
    IS_NOT_RUNNING,
    IS_PAUSED;

    companion object {
        fun fromOrdinal(ordinal: Int) = ServiceStatus.values().first { it.ordinal == ordinal }
    }
}