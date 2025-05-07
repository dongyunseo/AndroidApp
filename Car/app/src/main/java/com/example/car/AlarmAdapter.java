package com.example.car;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<AlarmItem> alarmList;

    public AlarmAdapter(List<AlarmItem> alarmList) {
        this.alarmList = alarmList;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        AlarmItem alarm = alarmList.get(position);
        //  Log.d("AlarmAdapter", "바인딩 데이터: " + alarm.toString());

        holder.carFullName.setText(alarm.getFullModelName());

        String price = alarm.getPrice();
        if (price != null && !price.isEmpty()) {
            holder.price.setText(price + " 만원");
        } else {
            holder.price.setText("가격 정보 없음");
        }

        String regDate = alarm.getRegDate();
        if (regDate != null && !regDate.isEmpty()) {
            holder.regDate.setText("알림 등록 시간: " + regDate);
        } else {
            holder.regDate.setText("등록일 없음");
        }

        // alertCount 표시
        int count = alarm.getAlertCount();
        if (count > 0) {
            holder.alertCount.setText("새로운 최저가 : 🔔 " + count + "건 새 매물");
            holder.alertCount.setVisibility(View.VISIBLE);
        } else {
            holder.alertCount.setVisibility(View.GONE);
        }

        holder.alertCount.setOnClickListener(v -> {
            // Intent로 페이지 이동
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, AlarmCheck.class); // 이동할 액티비티 클래스
            intent.putExtra("modelName", alarm.getFullModelName()); // 예: 모델명 전달
            intent.putExtra("userId", "로그인한 사용자 ID"); // 필요 시 전달

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView carFullName, price, regDate, alertCount;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            carFullName = itemView.findViewById(R.id.fullModelName);
            price = itemView.findViewById(R.id.price);
            regDate = itemView.findViewById(R.id.regDate);
            alertCount = itemView.findViewById(R.id.alertCount); // 추가됨
        }
    }
}
