package com.pro.foodorder.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pro.foodorder.ControllerApplication
import com.pro.foodorder.R
import com.pro.foodorder.adapter.OrderAdapter
import com.pro.foodorder.databinding.ActivityOrderHistoryBinding
import com.pro.foodorder.model.Order
import com.pro.foodorder.prefs.DataStoreManager.Companion.user
import java.util.*

class OrderHistoryActivity : BaseActivity() {

    private var mActivityOrderHistoryBinding: ActivityOrderHistoryBinding? = null
    private var mListOrder: MutableList<Order>? = null
    private var mOrderAdapter: OrderAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityOrderHistoryBinding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(mActivityOrderHistoryBinding!!.root)
        initToolbar()
        initView()
        getListOrders()
    }

    private fun initToolbar() {
        mActivityOrderHistoryBinding!!.toolbar.imgBack.visibility = View.VISIBLE
        mActivityOrderHistoryBinding!!.toolbar.imgCart.visibility = View.GONE
        mActivityOrderHistoryBinding!!.toolbar.tvTitle.text = getString(R.string.order_history)
        mActivityOrderHistoryBinding!!.toolbar.imgBack.setOnClickListener { onBackPressed() }
    }

    private fun initView() {
        val linearLayoutManager = LinearLayoutManager(this)
        mActivityOrderHistoryBinding!!.rcvOrderHistory.layoutManager = linearLayoutManager
    }

    private fun getListOrders() {
        ControllerApplication[this].bookingDatabaseReference
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (mListOrder != null) {
                            mListOrder!!.clear()
                        } else {
                            mListOrder = ArrayList()
                        }
                        for (dataSnapshot in snapshot.children) {
                            val order = dataSnapshot.getValue(Order::class.java)
                            if (order != null) {
                                val strEmail = user!!.email
                                if (strEmail.equals(order.email, ignoreCase = true)) {
                                    mListOrder!!.add(0, order)
                                }
                            }
                        }
                        // Sắp xếp danh sách theo thuộc tính isCompleted: false sẽ đứng trước true
                        mListOrder!!.sortBy { if (it.isCompleted) 1 else 0 }

                        mOrderAdapter = OrderAdapter(this@OrderHistoryActivity, mListOrder)
                        mActivityOrderHistoryBinding!!.rcvOrderHistory.adapter = mOrderAdapter
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mOrderAdapter != null) {
            mOrderAdapter!!.release()
        }
    }
}