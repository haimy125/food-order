package com.pro.foodorder.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.pro.foodorder.ControllerApplication
import com.pro.foodorder.R
import com.pro.foodorder.activity.MainActivity
import com.pro.foodorder.constant.GlobalFunction.hideSoftKeyboard
import com.pro.foodorder.constant.GlobalFunction.showToastMessage
import com.pro.foodorder.databinding.FragmentFeedbackBinding
import com.pro.foodorder.model.Feedback
import com.pro.foodorder.prefs.DataStoreManager.Companion.user
import com.pro.foodorder.utils.StringUtil.isEmpty

class FeedbackFragment : BaseFragment() {

    private var mFragmentFeedbackBinding: FragmentFeedbackBinding? = null
    private lateinit var edtName: EditText
    private lateinit var edtPhone: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mFragmentFeedbackBinding = FragmentFeedbackBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        edtName = mFragmentFeedbackBinding!!.edtName
        edtPhone = mFragmentFeedbackBinding!!.edtPhone

        //Load user info
        loadUserInfo()

        mFragmentFeedbackBinding!!.tvSendFeedback.setOnClickListener { onClickSendFeedback() }

        return mFragmentFeedbackBinding!!.root
    }

    private fun onClickSendFeedback() {
        if (activity == null) {
            return
        }
        val activity = activity as MainActivity?
        val strName = mFragmentFeedbackBinding!!.edtName.text.toString()
        val strPhone = mFragmentFeedbackBinding!!.edtPhone.text.toString()
        val strEmail = mFragmentFeedbackBinding!!.edtEmail.text.toString()
        val strComment = mFragmentFeedbackBinding!!.edtComment.text.toString()
        when {
            isEmpty(strName) -> {
                showToastMessage(activity, getString(R.string.name_require))
            }
            isEmpty(strComment) -> {
                showToastMessage(activity, getString(R.string.comment_require))
            }
            else -> {
                saveUserInfo()
                activity!!.showProgressDialog(true)
                val feedback = Feedback(strName, strPhone, strEmail, strComment)
                ControllerApplication[getActivity()!!].feedbackDatabaseReference
                        .child(System.currentTimeMillis().toString())
                        .setValue(feedback) { _: DatabaseError?, _: DatabaseReference? ->
                            activity.showProgressDialog(false)
                            sendFeedbackSuccess()
                        }
            }
        }
    }

    private fun sendFeedbackSuccess() {
        hideSoftKeyboard(activity!!)
        showToastMessage(activity, getString(R.string.send_feedback_success))
//        mFragmentFeedbackBinding!!.edtName.setText("")
//        mFragmentFeedbackBinding!!.edtPhone.setText("")
        mFragmentFeedbackBinding!!.edtComment.setText("")
    }
    // Hàm lưu dữ liệu vào SharedPreferences
    private fun saveUserInfo() {
        val name = edtName.text.toString().trim()
        val phone = edtPhone.text.toString().trim()
//        val sharedPreferences = getSharedPreferences("FeedbackPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("name", name)
            putString("phone", phone)
            apply() // hoặc commit() nếu bạn muốn đồng bộ
        }
    }
    // Hàm lấy dữ liệu đã lưu và tự động điền vào EditText
    private fun loadUserInfo() {
//        val sharedPreferences = getSharedPreferences("FeedbackPrefs", Context.MODE_PRIVATE)
        mFragmentFeedbackBinding!!.edtEmail.setText(user!!.email)

        val savedName = sharedPreferences.getString("name", "")
        val savedPhone = sharedPreferences.getString("phone", "")
        edtName.setText(savedName)
        edtPhone.setText(savedPhone)
    }

    override fun initToolbar() {
        if (activity != null) {
            (activity as MainActivity?)!!.setToolBar(false, getString(R.string.feedback))
        }
    }
}