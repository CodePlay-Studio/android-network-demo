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
//        1732602 to "Jerantut",
//        1732637 to "Maran",
//        1732663 to "Raub",
//        1732687 to "Batu Pahat",
//        1732698 to "Parit Raja",
//        1732706 to "Pekan Nenas",
//        1732711 to "Pontian Kechil",
//        1732721 to "Kampung Pasir Gudang Baru",
//        1732738 to "Kota Tinggi",
//        1732740 to "Kampung Seelong",
//        1732741 to "Taman Senai",
//        1732742 to "Kulai",
//        1732745 to "Skudai",
//        1732752 to "Johor Bahru",
//        1732811 to "Kluang",
//        1732814 to "Yong Peng",
//        1732826 to "Mersing",
//        1732846 to "Segamat",
//        1732857 to "Tangkak",
//        1732869 to "Muar",
//        1732871 to "Bakri",
//        1732877 to "Labis",
//        1732884 to "Chaah",
//        1732891 to "Kuala Selangor",
//        1732892 to "Batang Berjuntai",
//        1732893 to "Batu Arang",
//        1732903 to "Shah Alam",
//        1732905 to "Klang",
//        1732945 to "Cukai",
//        1733023 to "Kuala Lipis",
        1733035 to "Melaka",
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
        1733049 to "Johor"
    )
    private lateinit var binding: ActivityRecyclerviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.apply {
            val selectedCityId = intent.getIntExtra(EXTRA_CITY_ID, 0)
            adapter = CityAdapter(cities, selectedCityId) { id, _ ->
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(EXTRA_CITY_ID, id)
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
        private val items: List<Pair<Int, String>>,
        private val curSelection: Int = 0,
        private val callback : (id: Int, name: String) -> Unit) :
        RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

        inner class CityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val cityNameText: TextView = view.findViewById(android.R.id.text1)
            init {
                val outValue = TypedValue()
                view.context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                view.setBackgroundResource(outValue.resourceId)
                view.setOnClickListener {
                    val (id, name) = items[adapterPosition]
                    callback(id, name)
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