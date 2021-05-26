package cloud.sud.instatram.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import cloud.sud.instatram.ImageViewModel
import cloud.sud.instatram.ImageViewModelFactory
import cloud.sud.instatram.ImagesAdapter
import cloud.sud.instatram.activities.ImagesActivity.Companion.STATION_ID_KEY
import cloud.sud.instatram.application.ImApplication
import cloud.sud.instatram.databinding.ImageFragmentBinding

class ImageFragment : Fragment() {

    companion object {
        fun newInstance() = ImageFragment()
    }





    // private lateinit var viewModel: picViewModel
    private lateinit var mBinding : ImageFragmentBinding
    private val mPicViewModel: ImageViewModel by viewModels{
        ImageViewModelFactory((requireActivity().application as ImApplication).repository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = ImageFragmentBinding.inflate(inflater,container,false)


        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.rvPicsList.layoutManager= GridLayoutManager(requireActivity(),1)
        val bundle=this.arguments

        val myId =bundle?.getString(STATION_ID_KEY)!!
        val imagesAdapter= ImagesAdapter(this@ImageFragment)

        mBinding.rvPicsList.adapter=imagesAdapter


        mPicViewModel.allPicList(myId).observe(viewLifecycleOwner){
                pics->
            pics.let {

                if (it.isNotEmpty()){
                    mBinding.rvPicsList.visibility= View.VISIBLE
                    mBinding.noPicAddedYet.visibility= View.GONE
                    imagesAdapter.picsList(it)
                }else{
                    mBinding.rvPicsList.visibility= View.GONE
                    mBinding.noPicAddedYet.visibility= View.VISIBLE
                }


                /*  for(item in it ){

                  }*/
            }
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //  viewModel = ViewModelProvider(this).get(picViewModel::class.java)
        // TODO: Use the ViewModel
    }

}