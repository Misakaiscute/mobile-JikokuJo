package com.jikokujo.schedule.data.repository

import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.RouteDetailed
import com.jikokujo.schedule.data.remote.ApiResult

class QueryablesRepositoryTestImpl: QueryableRepository {
    override lateinit var queryables: ApiResult<List<Queryable>>
    override lateinit var searchResult: ApiResult<List<RouteDetailed>>

    override suspend fun getQueryables() {
        queryables = ApiResult.Success(listOf(
            Queryable.Route(id = "001", name = "4-6-os villamos", type = 2),
            Queryable.Route(id = "002", name = "M3-mas metró", type = 3),
            Queryable.Route(id = "003", name = "73-mas trolibusz", type = 4),
            Queryable.Route(id = "004", name = "119E busz", type = 1),
            Queryable.Stop(idsAssociated = listOf("001", "002"), name = "Nyugati pályaudvar"),
            Queryable.Stop(idsAssociated = listOf("003", "004"), name = "Kálvin tér"),
            Queryable.Stop(idsAssociated = listOf("001", "002", "003", "004"), name = "Blaha Lujza tér"),
        ))
    }
    override suspend fun getRouteDetails(selected: Queryable) {
        return
    }
}