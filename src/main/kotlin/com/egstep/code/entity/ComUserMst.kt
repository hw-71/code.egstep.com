package com.egstep.code.entity

import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "com_user_mst")
data class ComUserMst(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userId: String = "",
    var email: String = "",
    var userName: String = "",
    var userTypeCd: String = "",
    var userStatCd: String = "",
    var useYn: String = "",
    var createUserId: String? = null,
    var createDt: OffsetDateTime? = null,
    var updateUserId: String? = null,
    var updateDt: OffsetDateTime? = null,
    var lastLoginDt : OffsetDateTime? = null
)


