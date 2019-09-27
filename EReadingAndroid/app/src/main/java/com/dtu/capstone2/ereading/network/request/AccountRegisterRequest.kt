package com.dtu.capstone2.ereading.network.request

import com.google.gson.annotations.SerializedName

/**
 * Create by Nguyen Van Phuc on 4/2/19
 */
data class AccountRegisterRequest(@SerializedName("username") val userName: String,
                                  val password: String,
                                  @SerializedName("password_confirm") val passwordConfirm: String,
                                  val email: String)
