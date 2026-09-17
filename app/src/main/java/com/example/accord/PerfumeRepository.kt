package com.example.accord

object PerfumeRepository {
    suspend fun fetchAllPerfumes(): List<Perfume> {
        val all = mutableListOf<Perfume>()
        var offset = 0
        val pageSize = 50
        var total = Int.MAX_VALUE

        while (offset < total) {
            val page = RetrofitInstance.api.getPerfumes(limit = pageSize, offset = offset)
            total = page.total
            if (page.perfumes.isEmpty()) break
            all += page.perfumes
            offset += page.perfumes.size
        }
        return all
    }
}
