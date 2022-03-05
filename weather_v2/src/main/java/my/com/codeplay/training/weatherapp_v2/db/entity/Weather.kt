package my.com.codeplay.training.weatherapp_v2.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Weather(
    @ColumnInfo(name = "location") val location: String?,
    @ColumnInfo(name = "temperature") val temperature: String?,
    @ColumnInfo(name = "windSpeed") val windSpeed: String?,
    @ColumnInfo(name = "humidity") val humidity: String?,
    @ColumnInfo(name = "cloudiness") val cloudiness: String?,
    @ColumnInfo(name = "iconId") val iconId: Int?
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}
