package com.example.administrator.sportmanager.admin.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.administrator.sportmanager.R;

public class PaymentDialogUtil {

    public interface OnPayConfirmListener {
        void onPayConfirmed(String channel, double amount);
    }

    /**
     * @param enableBalance 是否允许余额支付
     */
    public static void showPayDialog(Context context,
                                     String title,
                                     double defaultAmount,
                                     boolean showAmountInput,
                                     boolean enableBalance,
                                     OnPayConfirmListener listener) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_pay_select, null);

        EditText etAmount = view.findViewById(R.id.et_amount);
        RadioGroup rgPay = view.findViewById(R.id.rg_pay);

        RadioButton rbBalance = view.findViewById(R.id.rb_balance);
        rbBalance.setVisibility(enableBalance ? View.VISIBLE : View.GONE);

        if (showAmountInput) {
            etAmount.setText(String.valueOf(defaultAmount));
            etAmount.setVisibility(View.VISIBLE);
        } else {
            etAmount.setVisibility(View.GONE);
        }

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("确认支付", (dialog, which) -> {

                    int checkedId = rgPay.getCheckedRadioButtonId();
                    String channel;

                    if (checkedId == R.id.rb_wechat) channel = "微信支付";
                    else if (checkedId == R.id.rb_unionpay) channel = "银联";
                    else if (checkedId == R.id.rb_balance) channel = "余额支付";
                    else channel = "支付宝";

                    double amount = defaultAmount;
                    if (showAmountInput) {
                        String s = etAmount.getText().toString().trim();
                        try {
                            amount = Double.parseDouble(s);
                        } catch (Exception e) {
                            Toast.makeText(context, "请输入正确金额", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (amount <= 0) {
                            Toast.makeText(context, "金额必须大于0", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (listener != null) {
                        listener.onPayConfirmed(channel, amount);
                    }
                })
                .show();
    }
}
