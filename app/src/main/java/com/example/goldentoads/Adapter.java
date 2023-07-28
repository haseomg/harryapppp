package com.example.goldentoads;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private String TAG = "어댑터";
    private Context mContext;
    private ArrayList<Data> mArrayList;

    public Adapter(Context context, ArrayList<Data> arrayList){

        this.mArrayList = arrayList;
        this.mContext = context;
    }

    public void setArrayList(ArrayList<Data> arrayList) {
        this.mArrayList = arrayList;
    }

    //리스트의 각 항목을 이루는 디자인(xml)을 적용.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate (R.layout.item, parent, false);
        ViewHolder vh = new ViewHolder (view);
        return vh;

    }

//리스트에 각 항목에 들어갈 데이터를 지정
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        Data data = mArrayList.get (position);
        holder.tv_item.setText (data.getItem ());

        DecimalFormat decimalFormat = new DecimalFormat("#,###원");
        String price = data.getPrice();
        String formattedPrice = "";

        if(price != null){
            try {
                formattedPrice = decimalFormat.format(Long.parseLong(price));
            }catch (NumberFormatException e){
                Log.e(TAG, "NumberFormatException : "+e.getMessage() );
            }
        }


        holder.tv_price.setText (formattedPrice);
        holder.typePosition = data.getTypePosition();
        holder.categoryPosition = data.getCategoryPosition();



        if(data.getTypePosition()==0){

            holder.iv_up.setVisibility(View.VISIBLE);
            holder.iv_down.setVisibility(View.GONE);

        } else if (data.getTypePosition()==1) {

            holder.iv_up.setVisibility(View.GONE);
            holder.iv_down.setVisibility((View.VISIBLE));

        }


    }

    //화면에 보여줄 데이터의 갯수를 반환.
    @Override
    public int getItemCount() {
//        Log.d (TAG, "getItemCount: "+mArrayList.size ());
        return mArrayList.size ();
    }



    //아이템 클릭 리스너 인터페이스
    interface OnItemClickListener{
        void onItemClick(View v, int position); //뷰와 포지션값
        void onEditClick(View v, int position); //수정
        void onDeleteClick(View v, int position); // 삭제

    }
    //리스너 객체 참조 변수
    private OnItemClickListener mListener = null;

    //리스너 객체 참조를 어댑터에 전달 메서드
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

//뷰홀더 객체에 저장되어 화면에 표시되고, 필요에 따라 생성 또는 재활용 된다.
    public class ViewHolder extends RecyclerView.ViewHolder {
    TextView tv_item, tv_price;
    ImageButton btn_change, btn_delete;
    ImageView iv_up, iv_down;
    int typePosition, categoryPosition;

    public ViewHolder(@NonNull View itemView) {

        super(itemView);
        this.tv_item = itemView.findViewById(R.id.item);
        this.tv_price = itemView.findViewById(R.id.price);
        this.btn_change = itemView.findViewById(R.id.btn_change);
        this.btn_delete = itemView.findViewById(R.id.btn_delete);
        this.iv_up = itemView.findViewById(R.id.up);
        this.iv_down = itemView.findViewById(R.id.down);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Log.d(TAG, "아이템뷰 너 나옴?");
                Log.d(TAG, "position 값 : "+position);
                Log.d(TAG, "" +
                        "값 : "+mListener);
                if(position!=RecyclerView.NO_POSITION){
                    if(mListener!=null){
                        mListener.onItemClick(v,position);

                    }
                }

            }
        });

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Log.d(TAG, "체인지버튼 너 나옴?");
                Log.d(TAG, "position 값 : "+position);
                Log.d(TAG, "mListener값 : "+mListener);
                if(position!=RecyclerView.NO_POSITION){
                    if(mListener!=null){
                        mListener.onEditClick(v, position);
                    }
                }
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Log.d(TAG, "딜리트 버튼 너 나옴?");
                Log.d(TAG, "position 값 : "+position);
                Log.d(TAG, "mListener값 : "+mListener);
                if(position!=RecyclerView.NO_POSITION){
                    if(mListener!=null){
                        mListener.onDeleteClick(v, position);

                    }
                }
            }
        });
    }



}


}
