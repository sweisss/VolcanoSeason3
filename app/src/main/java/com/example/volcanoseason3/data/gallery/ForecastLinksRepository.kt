package com.example.volcanoseason3.data.gallery

class ForecastLinksRepository(
    private val dao: ForecastLinkDao
) {
    suspend fun insertForecastLink(link: ForecastLink) = dao.insert(link)
    suspend fun deleteForecastLink(link: ForecastLink) = dao.delete(link)
}