package uz.xia.taxigo.data.remote.model.nomination

import com.squareup.moshi.Json

class NominationResponse(
    @field:Json(name = "display_name")
    val displayName: String?
)