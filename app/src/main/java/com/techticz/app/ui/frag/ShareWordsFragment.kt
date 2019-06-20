package com.techticz.app.ui.frag

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.techticz.dietcalendar.R;
import kotlinx.android.synthetic.main.fragment_share_words.*
import timber.log.Timber

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    ImagePickerFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [ShareWordsFragment.Listener].
 */
class ShareWordsFragment : BottomSheetDialogFragment(), FacebookCallback<Sharer.Result> {
    override fun onCancel() {
        Timber.d("Post sharing cancelled!")
        pb_spreading_word.visibility = View.GONE
    }

    override fun onSuccess(result: Sharer.Result?) {
        Timber.d("Post shared success")
        pb_spreading_word.visibility = View.GONE
        b_spread_words.visibility = View.GONE
        b_claim_reward.visibility = View.VISIBLE
        tv_share_word_title.text = "Thank you!"
        tv_spread_word_description.text = "We appriciate that you have shared words about us with your friends and families."
        tv_reward.text = "Congratulations! \u270c\n Reward is unlocked."
    }

    override fun onError(error: FacebookException?) {
        Timber.d("Post sharing error")
        pb_spreading_word.visibility = View.GONE
        tv_reward.text = "Sharing post failed"
    }

    private var mListener: Listener? = null
    lateinit var shareDialog : ShareDialog
    lateinit var callbackManager: CallbackManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_share_words, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        FacebookSdk.sdkInitialize(context)
        callbackManager = CallbackManager.Factory.create();
        shareDialog =  ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager,  this);
        mListener?.rewardProductView()?.let{
            fl_reward_product.addView(it)
        }
        b_spread_words.setOnClickListener { spreadWords() }
        b_claim_reward.setOnClickListener {
            mListener?.onRewardClaimed()
            dismiss()
        }
    }

    private fun spreadWords() {
        //onSuccess(null)
        pb_spreading_word.visibility = View.VISIBLE
        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            val linkContent = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.sillens.shapeupclub"))
                    .build()
            shareDialog.show(linkContent)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as Listener
        } else {
            mListener = context as Listener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    //----------------------------------------------------------------


    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    //----------------------------------------------------------------


    interface Listener {
        fun onRewardClaimed()
        fun couldNotSpreadWord()
        fun rewardProductView():View?
    }



    companion object {


        fun newInstance(): ShareWordsFragment =
                ShareWordsFragment()

    }
}
