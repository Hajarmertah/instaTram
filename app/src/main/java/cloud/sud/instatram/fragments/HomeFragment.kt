package cloud.sud.instatram.fragments

import android.os.Bundle
import androidx.lifecycle.Observer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import cloud.sud.instatram.HomeAdapter
import cloud.sud.instatram.HomeViewModel
import cloud.sud.instatram.R

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.stationData.observe(viewLifecycleOwner, Observer
        {val adapter = HomeAdapter(requireContext(), it)
            recyclerView.adapter = adapter
        })
        return view
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

    }


}