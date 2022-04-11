package my.com.codeplay.training.weatherapp_v2.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import my.com.codeplay.training.weatherapp_v2.R
import my.com.codeplay.training.weatherapp_v2.databinding.ActivityRecyclerviewBinding
import my.com.codeplay.training.weatherapp_v2.db.DatabaseManager
import my.com.codeplay.training.weatherapp_v2.db.entity.Weather


class HistoryActivity: AppCompatActivity() {
    private lateinit var binding: ActivityRecyclerviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Thread(
            object : Runnable {
                override fun run() {
                    val allWeatherData = DatabaseManager.getInstance(this@HistoryActivity)
                        .weatherDataDao().getAll()

                    if (allWeatherData.isNotEmpty()) {
                        this@HistoryActivity.runOnUiThread {
                            binding.recyclerView.adapter = ItemAdapter(allWeatherData)
                            binding.recyclerView.setHasFixedSize(true)
                        }
                    }
                }
            }
        ).start()
    }
}

class ItemAdapter(
    private val dataset: List<Weather>
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLocation: TextView = view.findViewById(R.id.item_location)
        val tvTemperature: TextView = view.findViewById(R.id.item_temperature)
        val tvWindSpeed: TextView = view.findViewById(R.id.item_windspeed)
        val tvHumidity: TextView = view.findViewById(R.id.item_humidity)
        val tvCloudiness: TextView = view.findViewById(R.id.item_cloudiness)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.tvLocation.text = item.location
        holder.tvTemperature.text = item.temperature
        holder.tvWindSpeed.text = item.windSpeed
        holder.tvHumidity.text = item.humidity
        holder.tvCloudiness.text = item.cloudiness
    }

    override fun getItemCount() = dataset.size
}