package top.apricityx.workshop

enum class WorkshopBrowseSortOption(
    val browseSortValue: String,
    val actualSortValue: String,
    val supportsTimeWindow: Boolean,
) {
    MostPopular(
        browseSortValue = "trend",
        actualSortValue = "trend",
        supportsTimeWindow = true,
    ),
    MostRecent(
        browseSortValue = "mostrecent",
        actualSortValue = "mostrecent",
        supportsTimeWindow = false,
    ),
    LastUpdated(
        browseSortValue = "lastupdated",
        actualSortValue = "lastupdated",
        supportsTimeWindow = false,
    ),
    MostSubscribed(
        browseSortValue = "totaluniquesubscribers",
        actualSortValue = "totaluniquesubscribers",
        supportsTimeWindow = false,
    ),
}

enum class WorkshopBrowseTimeWindow(
    val daysValue: Int,
) {
    Today(1),
    OneWeek(7),
    ThirtyDays(30),
    ThreeMonths(90),
    SixMonths(180),
    OneYear(365),
    AllTime(-1),
}

fun WorkshopBrowseSortOption.displayName(): String =
    when (this) {
        WorkshopBrowseSortOption.MostPopular -> "Most Popular"
        WorkshopBrowseSortOption.MostRecent -> "Recently Published"
        WorkshopBrowseSortOption.LastUpdated -> "Recently Updated"
        WorkshopBrowseSortOption.MostSubscribed -> "Most Subscribed"
    }

fun WorkshopBrowseTimeWindow.displayName(): String =
    when (this) {
        WorkshopBrowseTimeWindow.Today -> "Today"
        WorkshopBrowseTimeWindow.OneWeek -> "This Week"
        WorkshopBrowseTimeWindow.ThirtyDays -> "30 Days"
        WorkshopBrowseTimeWindow.ThreeMonths -> "3 Months"
        WorkshopBrowseTimeWindow.SixMonths -> "6 Months"
        WorkshopBrowseTimeWindow.OneYear -> "1 Year"
        WorkshopBrowseTimeWindow.AllTime -> "All Time"
    }
