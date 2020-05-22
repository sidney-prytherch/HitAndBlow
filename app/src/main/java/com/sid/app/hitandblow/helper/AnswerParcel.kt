package com.sid.app.hitandblow.helper

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class AnswerParcel(val answer: IntArray, val won:Boolean): Parcelable