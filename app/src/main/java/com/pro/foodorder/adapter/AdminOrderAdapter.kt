package com.pro.foodorder.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pro.foodorder.R
import com.pro.foodorder.adapter.AdminOrderAdapter.AdminOrderViewHolder
import com.pro.foodorder.constant.Constant
import com.pro.foodorder.databinding.ItemAdminOrderBinding
import com.pro.foodorder.model.Order
import com.pro.foodorder.utils.DateTimeUtils.convertTimeStampToDate

class AdminOrderAdapter(
    private var mContext: Context?,
    private val mListOrder: List<Order>?,
    private val mIUpdateStatusListener: IUpdateStatusListener
) : RecyclerView.Adapter<AdminOrderViewHolder>() {

    private val disabledColor = mContext?.let { ContextCompat.getColor(it, R.color.black_overlay) } ?: 0 // Màu khi disable
    private val enabledColor = mContext?.let { ContextCompat.getColor(it, R.color.white) } ?: 0 // Màu khi enable

    interface IUpdateStatusListener {
        fun updateStatus(order: Order)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminOrderViewHolder {
        val itemAdminOrderBinding = ItemAdminOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdminOrderViewHolder(itemAdminOrderBinding)
    }

    override fun onBindViewHolder(holder: AdminOrderViewHolder, position: Int) {
        val order = mListOrder?.get(position) ?: return

        val isOrderDisabled = order.isCompleted // Đơn giản hóa logic: disable nếu order.isCompleted là true

        // Đặt màu nền dựa trên trạng thái disable
        if (isOrderDisabled) {
            holder.mItemAdminOrderBinding.layoutItem.setBackgroundColor(disabledColor)
            holder.mItemAdminOrderBinding.backgroundImageView.visibility = View.VISIBLE
        } else {
            holder.mItemAdminOrderBinding.layoutItem.setBackgroundColor(enabledColor)
            holder.mItemAdminOrderBinding.backgroundImageView.visibility = View.INVISIBLE
        }

        holder.mItemAdminOrderBinding.chbStatus.isEnabled = !isOrderDisabled  // Checkbox enable/disable

        holder.mItemAdminOrderBinding.chbStatus.isChecked = order.isCompleted
        holder.mItemAdminOrderBinding.tvId.text = order.id.toString()
        holder.mItemAdminOrderBinding.tvEmail.text = order.email
        holder.mItemAdminOrderBinding.tvName.text = order.name
        holder.mItemAdminOrderBinding.tvPhone.text = order.phone
        holder.mItemAdminOrderBinding.tvAddress.text = order.address
        holder.mItemAdminOrderBinding.tvMenu.text = order.foods
        holder.mItemAdminOrderBinding.tvDate.text = convertTimeStampToDate(order.id)
        val strAmount: String = "" + order.amount + Constant.CURRENCY
        holder.mItemAdminOrderBinding.tvTotalAmount.text = strAmount
        var paymentMethod = ""
        if (Constant.TYPE_PAYMENT_CASH == order.payment) {
            paymentMethod = Constant.PAYMENT_METHOD_CASH
        }
        holder.mItemAdminOrderBinding.tvPayment.text = paymentMethod

        // Loại bỏ listener cũ và đặt lại listener mới (chỉ khi không disable)
        holder.mItemAdminOrderBinding.chbStatus.setOnCheckedChangeListener(null)
        holder.mItemAdminOrderBinding.chbStatus.isChecked = order.isCompleted

        if (!isOrderDisabled) {
            holder.mItemAdminOrderBinding.chbStatus.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    AlertDialog.Builder(mContext)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn thay đổi trạng thái đơn hàng thành \"Đã thanh toán\" không?")
                        .setPositiveButton("Có") { dialog, _ ->
                            // Chỉ cập nhật nếu trạng thái thay đổi
                            if (isChecked != order.isCompleted) {
                                mIUpdateStatusListener.updateStatus(order)
                                holder.mItemAdminOrderBinding.layoutItem.setBackgroundColor(disabledColor)
                                holder.mItemAdminOrderBinding.backgroundImageView.visibility = View.VISIBLE
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton("Không") { dialog, _ ->
                            holder.mItemAdminOrderBinding.chbStatus.isChecked = false
                            dialog.dismiss()
                        }
                        .show()

                }
            }
        }
        //Đặt trạng thái cho các textview
        holder.mItemAdminOrderBinding.labelAddress.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.labelDate.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.labelEmail.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.labelId.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.labelMenu.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.labelName.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.labelPayment.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.labelPhone.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.labelTotalAmount.isEnabled = !isOrderDisabled

        holder.mItemAdminOrderBinding.tvAddress.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.tvDate.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.tvEmail.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.tvId.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.tvMenu.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.tvName.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.tvPayment.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.tvPhone.isEnabled = !isOrderDisabled
        holder.mItemAdminOrderBinding.tvTotalAmount.isEnabled = !isOrderDisabled
    }

    override fun getItemCount(): Int {
        return mListOrder?.size ?: 0
    }

    fun release() {
        mContext = null
    }


    class AdminOrderViewHolder(val mItemAdminOrderBinding: ItemAdminOrderBinding) :
        RecyclerView.ViewHolder(mItemAdminOrderBinding.root)
}