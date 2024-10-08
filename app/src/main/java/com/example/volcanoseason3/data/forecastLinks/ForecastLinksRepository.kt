package com.example.volcanoseason3.data.forecastLinks

class ForecastLinksRepository(
    private val dao: ForecastLinkDao
) {
    suspend fun insertForecastLink(link: ForecastLink) = dao.insert(link)
    suspend fun deleteForecastLink(link: ForecastLink) = dao.delete(link)
    suspend fun updateForecastLink(link: ForecastLink) = dao.update(link)
    fun getAllForecastLinks() = dao.getAllLinks()
}