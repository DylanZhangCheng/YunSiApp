package com.lehua.tablet0306.utils;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lehua.tablet0306.R;

import java.util.List;

public class PushAdapter extends BaseAdapter {

    private List<PushMessage> messageList;
    private Context context;
    private String photoUrl;
    private ViewHolder viewHolder;

    public PushAdapter(List<PushMessage> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.bracelet_push_item, null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvBrief = (TextView) convertView.findViewById(R.id.tv_brief);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_push_date);
            viewHolder.imgPhoto = (ImageView) convertView.findViewById(R.id.img_photo);
            viewHolder.imgCollect = (ImageView) convertView.findViewById(R.id.iv_collect);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final PushMessage pushMessage = messageList.get(position);
        viewHolder.tvTitle.setText(pushMessage.getTitle());

        String brief_all = " ";
        if(pushMessage.getBrief().length()<50) {
            brief_all = pushMessage.getBrief().substring(0,pushMessage.getBrief().length()) + "..." + "<font color='#87CEEB'>查看详情>></font>";
        }
        else{
            brief_all = pushMessage.getBrief().substring(0,50) + "..." + "<font color='#87CEEB'>查看详情>></font>";
        }
        viewHolder.tvBrief.setText(Html.fromHtml(brief_all));
        viewHolder.tvDate.setText(pushMessage.getDate());
        if(pushMessage.getCollected() == 1){
            viewHolder.imgCollect.setImageResource(R.drawable.collect2_yes);
        }else {
            viewHolder.imgCollect.setImageResource(R.drawable.collect2_no);
        }

        photoUrl = "http://zhuji.qrsyb.cn/one/downloadFile.spe?dtype=PostgresXL&mode=html&fileid=" + pushMessage.getPhoto();

        MethodUtils.loadImage(context, viewHolder.imgPhoto,photoUrl);

        return convertView;
    }

    private static class ViewHolder {
        TextView tvTitle, tvBrief, tvDate;
        ImageView imgPhoto,imgCollect;
    }
}
