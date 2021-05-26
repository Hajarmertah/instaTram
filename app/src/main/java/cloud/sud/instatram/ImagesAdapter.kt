package cloud.sud.instatram

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import cloud.sud.instatram.application.ImApplication
import cloud.sud.instatram.databinding.EditPostedImageBinding
import cloud.sud.instatram.databinding.ItemImagesLayoutBinding
import cloud.sud.instatram.databinding.UpdateImageTitleBinding
import cloud.sud.instatram.model.ImageDAO
import cloud.sud.instatram.model.ImageEntity
import com.bumptech.glide.Glide

class ImagesAdapter( private  val fragment: Fragment): RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    private var pics :List<ImageEntity> = listOf()


    private val mPicViewModel: ImageViewModel by fragment.requireActivity().viewModels {
        ImageViewModelFactory((fragment.requireActivity().application as ImApplication).repository)
    }
    class ViewHolder(view: ItemImagesLayoutBinding): RecyclerView.ViewHolder(view.root){
        //holds the textView that will add each item to
        val ivPic =view.ivStationPic
        val tvTitle =view.tvPicTitle
        val tvDate =view.tvPicDate
        val editBtn = view.btnEditPostedPic


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemImagesLayoutBinding=  ItemImagesLayoutBinding.inflate(LayoutInflater.from(fragment.context),parent,false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pic=pics[position]
        Glide.with(fragment)
            .load(pic.image)
            .into(holder.ivPic)
        holder.tvTitle.text =pic.title
        holder.tvDate.text =pic.date
        holder.editBtn.setOnClickListener{
            val  dialog = Dialog(fragment.requireActivity())
            val mBinding:EditPostedImageBinding= EditPostedImageBinding.inflate(fragment.layoutInflater)
            dialog.setContentView(mBinding.root)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawableResource(R.color.white_black)

            mBinding.deletePostedPic.setOnClickListener {
                AlertDialog.Builder(fragment.requireContext())
                    .setTitle(fragment.getString((R.string.confirm_delete_pic_title)))
                    .setMessage(fragment.getString(R.string.confirm_delete_pic))
                    .setPositiveButton(fragment.getString(R.string.keep)){iDialog, _ ->
                        dialog.dismiss()
                        iDialog.dismiss()
                    }
                    .setNegativeButton(fragment.getString(R.string.lbl_delete)){ iDialog, _->
                        mPicViewModel.deletePic(pic)
                        dialog.dismiss()
                        iDialog.dismiss()
                        Toast.makeText(fragment.requireContext(),fragment.getString(R.string.pic_deleted),
                            Toast.LENGTH_LONG).show()
                    }.show()
                    .let {
                        it.setCanceledOnTouchOutside(false)
                        it.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        it.window?.setBackgroundDrawableResource(R.color.white_black)}
            }


            mBinding.editPostedPic.setOnClickListener{viewEP->
                dialog.dismiss()
                val eDialog= Dialog(fragment.requireActivity())
                val eBinding:UpdateImageTitleBinding= UpdateImageTitleBinding.inflate(fragment.layoutInflater)
                eDialog.setContentView(eBinding.root)

                eBinding.updateTitle.setText(pic.title)
                eDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,350)
                eDialog.window?.setBackgroundDrawableResource(R.color.white_black)
                eDialog.window?.setGravity(Gravity.BOTTOM)
                eDialog.setCanceledOnTouchOutside(false)
                showKeyboard(viewEP)

                eBinding.ivCancelUpdate.setOnClickListener{
                    hideKeyboard(it)
                    AlertDialog.Builder(fragment.requireContext())
                        .setTitle(fragment.getString((R.string.cancel_update_title)))
                        .setMessage(fragment.getString(R.string.cancel_update_message))
                        .setPositiveButton(fragment.getString(R.string.keep_editing)){iDialog, _ ->
                            iDialog.dismiss()
                        }
                        .setNegativeButton(fragment.getString(R.string.discard_update)){ iDialog, _->
                            eDialog.dismiss()
                            iDialog.dismiss()
                            Toast.makeText(fragment.requireContext(),fragment.getString(R.string.canceled),
                                Toast.LENGTH_SHORT).show()
                        }.show()
                        .let {ti->
                            ti.setCanceledOnTouchOutside(false)
                            ti.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            ti.window?.setBackgroundDrawableResource(R.color.white_black)}
                }

                eBinding.ivSendUpdate.setOnClickListener{view->
                    val  updatedTitle=eBinding.updateTitle.text.toString().trim{it<= ' '}
                    val id=pic.id
                    when{
                        TextUtils.isEmpty(updatedTitle)->{
                            Toast.makeText(fragment.requireContext(), fragment.getString(R.string.no_title), Toast.LENGTH_SHORT).show()
                        }else->{
                        hideKeyboard(view)
                        val  updatedPic: ImageDAO.ImUpdate=ImageDAO.ImUpdate(
                            id,
                            updatedTitle
                        )
                        mPicViewModel.updatePicTitle(updatedPic)
                        Toast.makeText(fragment.requireContext(),fragment.getString(R.string.title_updated),
                            Toast.LENGTH_SHORT).show()
                        eDialog.dismiss()
                    }
                    }
                }
                eDialog.show()
            }
            dialog.show()


        }

    }
    override fun getItemCount(): Int {
        return pics.size
    }
    fun picsList(list:List<ImageEntity>){
        pics =list
        notifyDataSetChanged()
    }

    private fun hideKeyboard(view: View){
        val im=view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(view.windowToken,0)
    }
    private fun showKeyboard(view: View){
        val im=view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.toggleSoftInput(InputMethodManager.SHOW_FORCED,0)
    }

}