package my.com.codeplay.training.weatherapp_v2

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import my.com.codeplay.training.weatherapp_v2.databinding.ActivityRecyclerviewBinding

class CityListActivity : AppCompatActivity() {
    private val cities = listOf(
        /*1733035 to "Melaka",
        1733036 to "Terengganu",
        1733037 to "Selangor",
        1733038 to "Sarawak",
        1733039 to "Sabah",
        1733040 to "Perlis",
        1733041 to "Perak",
        1733042 to "Pahang",
        1733043 to "Negeri Sembilan",
        1733044 to "Kelantan",
        1733046 to "Kuala Lumpur",
        1733047 to "Pulau Pinang",
        1733048 to "Kedah",
        1733049 to "Johor"*/
        City(1733035, "Melaka", 102.25f, 2.25f),
        City(1733036, "Terengganu", 103.0f, 5.0f),
        City(1733037, "Selangor", 101.5f, 3.16667f),
        City(1733038, "Sarawak", 113.5f, 2.5f),
        City(1733039, "Sabah", 117.0f, 5.5f),
        City(1733040, "Perlis", 100.25f, 6.5f),
        City(1733041, "Perak", 101.0f, 5.0f),
        City(1733042, "Pahang", 102.75f, 3.5f),
        City(1733043, "Negeri Sembilan", 102.166672f, 2.75f),
        City(1733044, "Kelantan", 102.0f, 5.33333f),
        City(1733046, "Kuala Lumpur", 101.686531f, 3.14309f),
        City(1733047, "Pulau Pinang", 100.258476f, 5.37677f),
        City(1733048, "Kedah", 100.666672f, 6.0f),
        City(1733049, "Johor", 103.5f, 2.0f),
    )
    private lateinit var binding: ActivityRecyclerviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.apply {
            val selectedCityId = intent.getIntExtra(EXTRA_CITY_ID, 0)
            adapter = CityAdapter(cities, selectedCityId) { city -> //id, _ ->
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(EXTRA_CITY_ID, city.id) // id)
                    putExtra(EXTRA_CITY_LON, city.lon)
                    putExtra(EXTRA_CITY_LAT, city.lat)
                })
                finish()
            }
            addItemDecoration(
                DividerItemDecoration(
                    this@CityListActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
            setHasFixedSize(true)
        }
    }

    private class CityAdapter(
        //private val items: List<Pair<Int, String>>,
        private val items: List<City>,
        private val curSelection: Int = 0,
        //private val callback : (id: Int, name: String) -> Unit
        private val callback : (city: City) -> Unit
    ) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

        inner class CityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val cityNameText: TextView = view.findViewById(android.R.id.text1)
            init {
                val outValue = TypedValue()
                view.context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                view.setBackgroundResource(outValue.resourceId)
                view.setOnClickListener {
                    //val (id, name) = items[adapterPosition]
                    //callback(id, name)

                    callback(items[adapterPosition])
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return CityViewHolder(view)
        }

        override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
            val (id, name) = items[position]
            with(holder.cityNameText) {
                typeface = if (curSelection == id) {
                    setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_baseline_check_circle_24,
                        0
                    )
                    Typeface.create(typeface, Typeface.BOLD)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    Typeface.create(typeface, Typeface.NORMAL)
                }
                text = name
            }
        }

        override fun getItemCount() = items.size
    }
}